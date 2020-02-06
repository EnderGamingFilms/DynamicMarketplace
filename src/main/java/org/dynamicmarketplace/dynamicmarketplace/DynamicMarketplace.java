package org.dynamicmarketplace.dynamicmarketplace;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class DynamicMarketplace extends JavaPlugin {

    public static Economy economy = null;

    @Override
    public void onEnable() {

        // Setup Economy
        if ( ! setupEconomy() ){ throwError (); }
        SaveData.init();

        // Register commands
        this.getCommand("dynmarket").setExecutor(new CommandClass(this));
        this.getCommand("sellall").setExecutor(new CommandClass(this));
        this.getCommand("sell").setExecutor(new CommandClass(this));
        this.getCommand("sellhand").setExecutor(new CommandClass(this));
        this.getCommand("buy").setExecutor(new CommandClass(this));
        this.getCommand("infohand").setExecutor(new CommandClass(this));
        this.getCommand("iteminfo").setExecutor(new CommandClass(this));

        // Register PlaceHolderAPI hook
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderHook().register();
        }
    }

    public static void throwError () {
        System.out.println("[*** Dynamic Market Error ***] Do you have an economy installed with vault? ");
        Bukkit.shutdown();
    }

    private boolean setupEconomy () {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration( net.milkbowl.vault.economy.Economy.class);
        if ( economyProvider != null )
            economy = economyProvider.getProvider();
        return ( economy != null );
    }

}
