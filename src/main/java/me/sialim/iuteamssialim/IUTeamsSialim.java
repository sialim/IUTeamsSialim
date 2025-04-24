package me.sialim.iuteamssialim;

import org.bukkit.plugin.java.JavaPlugin;

public final class IUTeamsSialim extends JavaPlugin {
    public PartyManager partyManager;
    public PartyCommand partyCommand;
    public TeamManager teamManager;
    public TeamCommand teamCommand;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        partyManager = new PartyManager(this);
        teamManager = new TeamManager(this);

        partyCommand = new PartyCommand(this);
        teamCommand = new TeamCommand(this);

        getCommand("party").setExecutor(partyCommand);
        getCommand("party").setTabCompleter(partyCommand);

        getCommand("team").setExecutor(teamCommand);
    }

    @Override
    public void onDisable() {
        partyManager.saveParties();
    }
}
