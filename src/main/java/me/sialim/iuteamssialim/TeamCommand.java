package me.sialim.iuteamssialim;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamCommand implements TabExecutor {
    private IUTeamsSialim plugin;
    private TeamManager manager;
    public TeamCommand(IUTeamsSialim plugin) {
        this.plugin = plugin;
        this.manager = plugin.teamManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("join")) {
            p.sendMessage("Usage: /team join <red|blue>");
            return true;
        }

        String desiredTeam = args[1].toLowerCase();

        if (!List.of("red", "blue").contains(desiredTeam)) {
            p.sendMessage(ChatColor.RED + "Invalid team. Use red or blue.");
            return true;
        }

        if (manager.isOnTeam(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "You are already on a team.");
            return true;
        }

        int desiredTeamCount = manager.getTeamCount(desiredTeam);
        int otherTeamCount = manager.getTeamCount(
                desiredTeam.equals("red") ? "blue" : "red"
        );

        if (desiredTeamCount > otherTeamCount) {
            p.sendMessage(ChatColor.RED + "Cannot join that team — it would be unbalanced.");
            return true;
        }

        if (manager.joinTeam(p.getUniqueId(), desiredTeam)) {
            p.sendMessage(ChatColor.GREEN + "You joined the " + desiredTeam + " team!");
        } else {
            p.sendMessage(ChatColor.RED + "Failed to join team.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }
}
