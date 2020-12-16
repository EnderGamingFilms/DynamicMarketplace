package me.endergamingfilms.dynamicmarketplace;

import me.endergamingfilms.dynamicmarketplace.commands.CommandManager;
import me.endergamingfilms.dynamicmarketplace.database.MySQL;
import me.endergamingfilms.dynamicmarketplace.database.SQLGetter;
import me.endergamingfilms.dynamicmarketplace.gui.CollectorGUI;
import me.endergamingfilms.dynamicmarketplace.gui.InventoryListener;
import me.endergamingfilms.dynamicmarketplace.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;

public final class DynamicMarketplace extends JavaPlugin implements Listener {
    public final CommandManager cmdManager = new CommandManager(this);
    public final FileManager fileManager = new FileManager(this);
    public final MessageUtils messageUtils = new MessageUtils(this);
    public final Responses respond = new Responses(this);
    public final MarketData marketData = new MarketData(this);
    public final Calculations calculations = new Calculations(this);
    public final Operations operations = new Operations(this);
    public final MySQL database = new MySQL(this);
    public final SQLGetter standing = new SQLGetter(this);
    //    public final GuiManager guiManager = new GuiManager(this);
    public final CollectorGUI collectorGUI = new CollectorGUI(this);
    private BukkitTask sqlTimeOutPrevention;
    public Economy economy;

    @Override
    public void onEnable() {
        //Load Files
        messageUtils.log(MessageUtils.LogLevel.INFO, "&9Loading config files.");
        loadFiles();

        // Setup Economy
        messageUtils.log(MessageUtils.LogLevel.INFO, "&9Loading vault economy.");
        if (!setupEconomy())
            return; // There was an error enabling the economy hooks

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
        if (fileManager.collectorIsEnabled) {
            new InventoryListener(this);
            collectorGUI.initialize();
        }

        // MySQL
        setupSQL();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Plugin) this);
        fileManager.saveMaterialData();
        database.disconnect();
        try {
            this.sqlTimeOutPrevention.cancel();
        } catch (NullPointerException ignored) {
        }

        super.onDisable();
    }

    public void loadFiles() {
        fileManager.setup();
    }

    private void setupSQL() {
        database.init();
        if (database.isEnabled()) {
            try {
                database.connect();
            } catch (ClassNotFoundException | SQLException e) {
                messageUtils.log(MessageUtils.LogLevel.INFO, "&cDatabase failed to connect.");
                e.printStackTrace();
            }

            if (database.isConnected()) {
                messageUtils.log(MessageUtils.LogLevel.INFO, "&aDatabase successfully connected.");
                this.sqlTimeOutPrevention = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                    @Override
                    public void run() {
                        standing.createTable();
                    }
                }, 5 * 20L, (60 * 5) * 20L);
                // Register Listeners in Main Class (PlayerJoinEvent)
                this.getServer().getPluginManager().registerEvents(this, this);
            }
        } else {
            messageUtils.log(MessageUtils.LogLevel.INFO, "&cDatabase not enabled.");
        }
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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (!standing.playerExists(player.getUniqueId()))
                    standing.createPlayer(player);
            }
        }.runTaskAsynchronously(this);
    }
}
