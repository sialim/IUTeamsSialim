package me.sialim.iuteamssialim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PartyManager {
    private IUTeamsSialim plugin;
    private final Map<UUID, Party> parties = new HashMap<>();
    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, Set<UUID>> pendingInvites = new HashMap<>();

    public PartyManager(IUTeamsSialim plugin) {
        file = new File(plugin.getDataFolder(), "parties.json");
        if (!file.exists()) {
            file.mkdirs();
        }
        loadParties();
    }

    public boolean createParty(UUID owner) {
        if (parties.containsKey(owner)) return false;
        Party party = new Party(owner);
        parties.put(owner, party);
        saveParties();
        return true;
    }

    public boolean joinParty(UUID owner, UUID uuid) {
        Party party = parties.get(owner);
        if (party == null) return false;
        party.addMember(uuid);
        saveParties();
        return true;
    }

    public void leaveParty(UUID uuid) {
        UUID ownerToRemove = null;

        for (Map.Entry<UUID, Party> entry : parties.entrySet()) {
            Party party = entry.getValue();

            if (party.getOwner().equals(uuid)) {
                ownerToRemove = entry.getKey();
                break;
            } else if (party.isMember(uuid)) {
                party.removeMember(uuid);
                break;
            }
        }

        if (ownerToRemove != null) {
            parties.remove(ownerToRemove);
        }

        saveParties();
    }

    public Party getPartyOf(UUID uuid) {
        for (Party p : parties.values()) {
            if (p.isMember(uuid)) return p;
        }
        return null;
    }

    public boolean isInParty(UUID uuid) {
        return getPartyOf(uuid) != null;
    }

    public void saveParties() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(parties, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadParties() {
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<UUID, Party>>() {}.getType();
            Map<UUID, Party> loaded = gson.fromJson(reader, type);
            if (loaded != null) parties.putAll(loaded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean invitePlayer(UUID owner, UUID target) {
        if (!parties.containsKey(owner)) return false;
        pendingInvites.computeIfAbsent(target, k -> new HashSet<>()).add(owner);
        return true;
    }

    public boolean hasInvite(UUID target, UUID owner) {
        return pendingInvites.getOrDefault(target, Set.of()).contains(owner);
    }

    public boolean acceptInvite(UUID target, UUID owner) {
        if (!hasInvite(target, owner)) return false;
        joinParty(owner, target);
        pendingInvites.get(target).remove(owner);
        return true;
    }

    public boolean denyInvite(UUID target, UUID owner) {
        if (!hasInvite(target, owner)) return false;
        pendingInvites.get(target).remove(owner);
        return true;
    }
}
