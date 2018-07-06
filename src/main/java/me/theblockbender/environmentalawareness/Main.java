package me.theblockbender.environmentalawareness;

import me.theblockbender.environmentalawareness.listener.SaplingBlockListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public Economy economy;

    public void onEnable() {
        saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().severe(" | Unable to load the EnvironmentalAwareness plugin.");
            getLogger().severe(" | Missing dependency: Vault");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerListeners();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new SaplingBlockListener(this), this);
    }
}
