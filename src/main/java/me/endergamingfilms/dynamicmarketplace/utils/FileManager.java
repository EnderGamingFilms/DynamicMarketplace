package me.endergamingfilms.dynamicmarketplace.utils;


import com.sun.org.apache.xml.internal.security.algorithms.Algorithm;
import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class FileManager {
    public static final boolean BOOLEAN = false;
    public static final int INT = 0;
    public static final double DOUBLE = 0.0;
    public static final String STRING = "";
    public static final String[] LIST = new String[0];
    private final DynamicMarketplace plugin;
    public DecimalFormat df = new DecimalFormat("#.00");

    public FileManager(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    /**
     * |-------------- Settings --------------|
     */
    public int scalar;
    public double tax;
    public double multiplierCraft;
    public double multiplierSmelt;
    public double multiplierGrow;
    public boolean mySQLEnabled;
    public boolean collectorIsEnabled;
    public double collectorDefTax;
    public boolean collectorHasStanding;
    public boolean debug;
    public String algorithm;
    public double easyAdjustment;
    //------------------------------------------

    /**
     * |-------------- Files --------------|
     */
    private FileConfiguration config;
    private FileConfiguration messages;
    private File configFile;
    private File messageFile;
    public File materialsFile;
    private File recipesFile;
    //------------------------------------------

    public void setup() {
        setupConfig();
        // Load settings
        reloadSettings();
        // Load everything else
        setupMessages();
        setupMaterials();
        setupRecipes();
        readMaterialData();
        readRecipeData();
        // Generate pricing
        calcItemData();
    }

    /**
     * |-------------- Config.yml --------------|
     */
    public void setupConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                plugin.saveResource("config.yml", true);
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eConfig.yml did not exist so one was created");
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
        this.algorithm = plugin.messageUtils.grabConfig("Algorithm", STRING);
        this.easyAdjustment = plugin.messageUtils.grabConfig("EasyAdjustment", DOUBLE);
        this.multiplierCraft = plugin.messageUtils.grabConfig("Multipliers.Growing", DOUBLE);
        this.multiplierGrow = plugin.messageUtils.grabConfig("Multipliers.Smelting", DOUBLE);
        this.multiplierSmelt = plugin.messageUtils.grabConfig("Multipliers.Crafting", DOUBLE);
        this.mySQLEnabled = plugin.messageUtils.grabConfig("MySQL.Enabled", BOOLEAN);
        this.collectorIsEnabled = plugin.messageUtils.grabConfig("TheCollector.Enabled", BOOLEAN);
        this.collectorDefTax = plugin.messageUtils.grabConfig("TheCollector.Tax", DOUBLE);
        this.collectorHasStanding = plugin.messageUtils.grabConfig("TheCollector.Standing.Enabled", BOOLEAN);
        plugin.marketData.setBlacklist(plugin.messageUtils.grabConfig("Item-Blacklist", LIST));
        this.debug = plugin.messageUtils.grabConfig("Debug", BOOLEAN);
    }
    //------------------------------------------

    /**
     * |-------------- Message.yml --------------|
     */
    public void setupMessages() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        messageFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messageFile.exists()) {
            try {
                plugin.saveResource("messages.yml", true);
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eMessages.yml did not exist so one was created");
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

    /**
     * |-------------- Materials.yml --------------|
     */
    public void setupMaterials() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        materialsFile = new File(plugin.getDataFolder(), "materials.yml");

        if (!materialsFile.exists()) {
            try {
                plugin.saveResource("materials.yml", true);
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eMaterials.yml did not exist so one was created");
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
                plugin.marketData.putItem(item[0], MarketData.round(Double.parseDouble(item[1]) * this.easyAdjustment, 2));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMaterialData() {
        Map<Integer, String> dataMap = new HashMap<>();
        try {
            Material material;
            // Setup BufferedReader
            BufferedReader br = new BufferedReader(new FileReader(materialsFile));

            // Read line by line
            String line;

            // Go thought the file grabbing line by line
            for (int key = 0; (line = br.readLine()) != null; ++key) {
                String[] item = line.split("(: )");
                dataMap.put(key, item[0]);
            }
            MarketData.MarketItem item;

            // Print data to file
            FileWriter fileWriter = new FileWriter(materialsFile, false);
            for (int i = 0; i < dataMap.size(); ++i) {
                item = plugin.marketData.getItem(dataMap.get(i));
                fileWriter.write(String.format("%s: %s\n", item.getMaterial().getKey().getKey(), item.getAmount()));
            }

            plugin.messageUtils.log(MessageUtils.LogLevel.INFO, "&aSaved all material amount to file.");
            // Close files
            fileWriter.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean outputMissingMats() {
        File listFile = new File(plugin.getDataFolder(), "unresolved.yml");
        try {
            listFile.createNewFile();
            plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eUnresolved.yml has been successfully created");
        } catch (Exception e) {
            plugin.messageUtils.log(MessageUtils.LogLevel.SEVERE, "&cThere was an issue creating Unresolved.yml");
            return false;
        }
        try {
        FileWriter fileWriter = new FileWriter(listFile, false);
        fileWriter.write("# These items are not inside materials.yml or recipes.yml, please add them if you want to generate prices\n");
        for (Material m : Material.values()) {
            if (!plugin.marketData.contains(m, true)) {
                fileWriter.write(String.format("%s\n", m.getKey().getKey()));
            }
        }
        fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //------------------------------------------


    /**
     * |-------------- Recipes.yml --------------|
     */
    public void setupRecipes() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        recipesFile = new File(plugin.getDataFolder(), "recipes.yml");

        if (!recipesFile.exists()) {
            try {
                plugin.saveResource("recipes.yml", true);
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eRecipes.yml did not exist so one was created");
            } catch (Exception e) {
                plugin.messageUtils.log(MessageUtils.LogLevel.SEVERE, "&cThere was an issue creating Recipes.yml");
            }
        }
    }

    public void readRecipeData() {
        try {
            // Setup BufferedReader
            BufferedReader br = new BufferedReader(new FileReader(recipesFile));

            // Read line by line
            String line;

            // Go thought the file grabbing line by line
            List<String> items;
            while ((line = br.readLine()) != null) {
                String[] item = line.split("(: )");
                plugin.marketData.putItem(item[0], -1, item[1]);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * |-------------- General Market Functions --------------|
     */
    public void loadAll() {
        // Stage 1 - Reload messages.yml
        reloadMessages();
        // Stage 2 - Reload config.yml and load settings into plugin
        reloadConfig();
        reloadSettings();
        // Stage 3 - Re-read all data from materials.yml & recipes.yml
        readMaterialData();
        readRecipeData();
        // Stage 5 - Re-calculate buy & sell prices for all items
        calcItemData();
    }

    public void calcItemData() {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.marketData.getDataMap().forEach((k, v) -> plugin.calculations.calcPrice(v.getMaterial().getKey().getKey()));
            }
        }.runTaskAsynchronously(plugin);
    }

//    public void reloadAllContainingItem(Material material) {
//        for (Map.Entry<Material, MarketData.MarketItem> entry : plugin.marketData.getDataMap().entrySet()) {
//            if (entry.getValue() == null) continue;
//
//            MarketData.MarketItem item = entry.getValue();
//            if (item.getRecipe() != null && item.getRecipe().contains(material.getKey().getKey())) {
//                // Re-Calculate Prices
//                plugin.calculations.calcPrice(item.getMaterial().getKey().getKey());
//                // Recursively update items that require other items that were previously updated
//                reloadAllContainingItem(item.getMaterial());
//            }
//        }
//    }
    //------------------------------------------
}
