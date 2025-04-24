package me.sialim.iuteamssialim;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TeamManager {
    private final Map<UUID, String> playerTeams = new HashMap<>();
    private final Set<String> validTeams = Set.of("red", "blue");
    private final File file;
    private final IUTeamsSialim plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TeamManager(IUTeamsSialim plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "teams.json");
        load();
    }

    public boolean joinTeam(UUID uuid, String team) {
        if (!validTeams.contains(team.toLowerCase())) return false;
        if (playerTeams.containsKey(uuid)) return false;

        int current = getTeamCount(team);
        int other = getTeamCount(getOtherTeam(team));
        if (current > other) return false;

        playerTeams.put(uuid, team.toLowerCase());
        save();
        return true;
    }

    public String getTeam(UUID uuid) {
        return playerTeams.get(uuid);
    }

    public boolean isOnTeam(UUID uuid) {
        return playerTeams.containsKey(uuid);
    }

    public int getTeamCount(String team) {
        return (int) playerTeams.values().stream()
                .filter(t -> t.equalsIgnoreCase(team))
                .count();
    }

    private String getOtherTeam(String team) {
        return team.equalsIgnoreCase("red") ? "blue" : "red";
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(playerTeams, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<UUID, String>>() {}.getType();
            Map<UUID, String> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                playerTeams.clear();
                playerTeams.putAll(loaded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
