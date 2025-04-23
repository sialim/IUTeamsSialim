package me.sialim.iuteamssialim;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Party {
    private UUID owner;
    private Set<UUID> members;

    public Party(UUID owner) {
        this.owner = owner;
        this.members = new HashSet<>();
        this.members.add(owner);
    }
    public UUID getOwner() { return owner; }
    public Set<UUID> getMembers() { return members; }
    public boolean isMember(UUID uuid) { return members.contains(uuid); }

    public void addMember(UUID uuid) { members.add(uuid); }
    public void removeMember(UUID uuid) { members.remove(uuid); }
}