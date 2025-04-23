package me.sialim.iuteamssialim;

import org.bukkit.plugin.java.JavaPlugin;

public final class IUTeamsSialim extends JavaPlugin {
    private PartyManager partyManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.partyManager = new PartyManager(this);
        getCommand("party").setExecutor(new PartyCommand(partyManager));
    }

    @Override
    public void onDisable() {
        partyManager.saveParties();
    }
}
