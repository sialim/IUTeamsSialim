package me.sialim.iuteamssialim;

import org.bukkit.command.CommandExecutor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyCommand implements CommandExecutor {
    private PartyManager manager;

    public PartyCommand(PartyManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player p)) return false;
        if (args.length < 1) return false;
        UUID uuid = p.getUniqueId();

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (manager.isInParty(uuid)) {
                    p.sendMessage(ChatColor.RED + "You are already in a party!");
                    return false;
                }
                Party party = new Party(uuid);
            }
            case "leave" -> {
                if (!manager.isInParty(uuid)) {
                    p.sendMessage(ChatColor.RED + "You aren't in a party!");
                    return false;
                }
                String partyOwnerUsername = Bukkit.getPlayer(manager.getPartyOf(uuid).getOwner()).getName();
                manager.leaveParty(uuid);
                p.sendMessage("Left " + partyOwnerUsername + "'s party.");
            }
            case "invite" -> {
                if (args.length < 2) return false;
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    p.sendMessage(ChatColor.RED + "Player not found.");
                    return false;
                }

                Party party = manager.getPartyOf(uuid);
                if (party == null || !party.getOwner().equals(uuid)) {
                    p.sendMessage(ChatColor.RED + "You must be a party owner to invite.");
                    return false;
                }

                if (manager.invitePlayer(uuid, target.getUniqueId())) {
                    p.sendMessage(ChatColor.GREEN + "Successfully invited " + target.getName());
                    target.sendMessage("You were invited to " + p.getName() + "'s party.");
                    target.sendMessage("Type /party accept " + p.getName() + " to join their party.");
                } else {
                    p.sendMessage(ChatColor.RED + "Failed to send invite.");
                }
                return true;
            }
            case "accept" -> {
                if (args.length < 2) return false;
                OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);
                UUID ownerUUID = owner.getUniqueId();
                if (manager.acceptInvite(uuid, ownerUUID)) {
                    p.sendMessage(ChatColor.GREEN + "You joined the party!");
                } else {
                    p.sendMessage(ChatColor.RED + "No invite from that player.");
                }
            }

            case "deny" -> {
                if (args.length < 2) return false;
                OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);
                UUID ownerUUID = owner.getUniqueId();
                if (manager.denyInvite(uuid, ownerUUID)) {
                    p.sendMessage(ChatColor.GREEN + "Invite denied.");
                } else {
                    p.sendMessage(ChatColor.RED + "No invite from that player.");
                }
                return true;
            }
        }
        return false;
    }
}

