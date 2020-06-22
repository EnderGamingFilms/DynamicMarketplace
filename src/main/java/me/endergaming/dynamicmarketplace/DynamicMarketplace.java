package me.endergaming.dynamicmarketplace;

import me.endergaming.dynamicmarketplace.commands.*;
import me.endergaming.dynamicmarketplace.gui.CollectorGUI;
import me.endergaming.dynamicmarketplace.gui.InventoryListener;
import me.endergaming.dynamicmarketplace.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class DynamicMarketplace extends JavaPlugin {
    public final CommandManager cmdManager = new CommandManager(this);
    public final FileManager fileManager = new FileManager(this);
    public final MessageUtils messageUtils = new MessageUtils(this);
    public final Responses respond = new Responses(this);
    public final MarketData marketData = new MarketData(this);
    public final Calculations calculations = new Calculations(this);
    public final Operations operations = new Operations(this);
    //    public final GuiManager guiManager = new GuiManager(this);
    public final CollectorGUI collectorGUI = new CollectorGUI(this);
    public Economy economy;

    @Override
    public void onEnable() {
        //Load Files
        messageUtils.log(MessageUtils.LogLevel.INFO, "&9Loading config files.");
        loadFiles();

        // Setup Economy
        messageUtils.log(MessageUtils.LogLevel.INFO, "&9Loading vault economy.");
        setupEconomy();

        // Register commands
        messageUtils.log(MessageUtils.LogLevel.INFO, "&9Loading plugin commands.");
        cmdManager.registerCommands();

        // Register PlaceHolderAPI hook
        messageUtils.log(MessageUtils.LogLevel.INFO, "&9Loading plugin hooks.");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            messageUtils.log(MessageUtils.LogLevel.INFO, "&9Plugin hooks successfully loaded.");
//            new PlaceholderHook().register();
            new HookPlaceholderAPI(this).register();
        } else {
            messageUtils.log(MessageUtils.LogLevel.WARNING, "&9Unable to load hooks.");
        }

        // Register Listeners
        new InventoryListener(this);
        collectorGUI.initialize();
    }

    @Override
    public void onDisable() {
        fileManager.saveMaterialData();
        messageUtils.log(MessageUtils.LogLevel.INFO, "&aSaved all material amount to file.");
        super.onDisable();
    }

    public void registerCommand(BaseCommand command) {
        command.register();
    }

    public void loadFiles() {
        fileManager.setup();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        } else {
            // Disable this plugin if vault economy is not installed
            messageUtils.log(MessageUtils.LogLevel.SEVERE, "&cThis plugin depends on Vault Economy, and will not work without it...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        return (economy != null);
    }
}
