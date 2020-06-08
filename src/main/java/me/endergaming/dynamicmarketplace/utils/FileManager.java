package me.endergaming.dynamicmarketplace.utils;


import org.jetbrains.annotations.NotNull;
import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.text.DecimalFormat;

import static me.endergaming.dynamicmarketplace.utils.Responses.*;

public class FileManager {
    private final DynamicMarketplace plugin;
    public DecimalFormat df = new DecimalFormat("#.00");

    public FileManager(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    /** |-------------- Settings --------------| */
    public int scalar;
    public double tax;
    public double multiplierCraft;
    public double multiplierSmelt;
    public double multiplierGrow;
    public boolean collectorIsEnabled;
    public double collectorDefTax;
    public boolean collectorHasStanding;
    public String[] blacklist;
    public boolean debug;
    //------------------------------------------

    /** |-------------- Files --------------| */
    private FileConfiguration config;
    private FileConfiguration messages;
    private File configFile;
    private File messageFile;
    private File materialsFile;
    private File recipesFile;
    //------------------------------------------

    public void setup() {
        setupConfig();
        // Load settings
        reloadSettings();
        setupMessages();
        setupMaterials();
        setupRecipes();
    }

    /** |-------------- Config.yml --------------| */
    public void setupConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eConfig.yml did not exist so one was created");
                plugin.saveResource("config.yml", true);
            } catch (Exception e) {
                plugin.messageUtils.log(MessageUtils.LogLevel.SEVERE, "&cThere was an issue creating Config.yml");
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadSettings() {
        this.scalar = plugin.messageUtils.grabConfig("QuantityScalar", INT);
        this.tax = plugin.messageUtils.grabConfig("Tax", DOUBLE);
        this.multiplierCraft = plugin.messageUtils.grabConfig("Multipliers.Growing", DOUBLE);
        this.multiplierGrow = plugin.messageUtils.grabConfig("Multipliers.Smelting", DOUBLE);
        this.multiplierSmelt = plugin.messageUtils.grabConfig("Multipliers.Crafting", DOUBLE);
        this.collectorIsEnabled = plugin.messageUtils.grabConfig("TheCollector.Enabled", BOOLEAN);
        this.collectorDefTax = plugin.messageUtils.grabConfig("TheCollector.Tax", DOUBLE);
        this.collectorHasStanding = plugin.messageUtils.grabConfig("TheCollector.Standing.Enabled", BOOLEAN);
        this.blacklist = plugin.messageUtils.grabConfig("Purchase-Blacklist", LIST).toArray(new String[0]);
        this.debug = plugin.messageUtils.grabConfig("Debug", BOOLEAN);
    }
    //------------------------------------------

    /** |-------------- Message.yml --------------| */
    public void setupMessages() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        messageFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messageFile.exists()) {
            try {
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eMessages.yml did not exist so one was created");
                plugin.saveResource("messages.yml", true);
            } catch (Exception e) {
                plugin.messageUtils.log(MessageUtils.LogLevel.SEVERE, "&cThere was an issue creating Messages.yml");
            }
        }
        messages = YamlConfiguration.loadConfiguration(messageFile);
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public File getMessagesFile() {
        return messageFile;
    }

    public void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messageFile);
        plugin.messageUtils.prefix = messages.getString("prefix");
    }
    //------------------------------------------

    /** |-------------- Materials.yml --------------| */
    public void setupMaterials() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        materialsFile = new File(plugin.getDataFolder(), "materials.yml");

        if (!materialsFile.exists()) {
            try {
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eMaterials.yml did not exist so one was created");
                plugin.saveResource("materials.yml", true);
            } catch (Exception e) {
                plugin.messageUtils.log(MessageUtils.LogLevel.SEVERE, "&cThere was an issue creating Materials.yml");
            }
        }

        // Read in file
        readMaterialData();
    }

    public void readMaterialData() {
        try {
            // Setup BufferedReader
            BufferedReader br = new BufferedReader(new FileReader(materialsFile));

            // Read line by line
            String line;

            // Go thought the file grabbing line by line
            while ((line = br.readLine()) != null) {
                String[] item = line.split("(: )");
                plugin.marketData.putItem(item[0], Double.parseDouble(item[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //------------------------------------------


    /** |-------------- Recipes.yml --------------| */
    public void setupRecipes() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        recipesFile = new File(plugin.getDataFolder(), "recipes.yml");

        if (!recipesFile.exists()) {
            try {
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eRecipes.yml did not exist so one was created");
                plugin.saveResource("recipes.yml", true);
            } catch (Exception e) {
                plugin.messageUtils.log(MessageUtils.LogLevel.SEVERE, "&cThere was an issue creating Recipes.yml");
            }
        }
    }

    public void readRecipeData() {
    }
    //------------------------------------------

    public void reloadAll() {
        // Stage 1
        reloadMessages();
        // Stage 2
        reloadConfig();
        reloadSettings();
        // Stage 3
        readMaterialData();
        // Stage 4
        readRecipeData();
    }
}
