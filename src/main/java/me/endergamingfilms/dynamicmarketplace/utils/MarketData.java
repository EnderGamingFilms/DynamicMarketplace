package me.endergamingfilms.dynamicmarketplace.utils;

import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketData {
    private static DynamicMarketplace plugin;
    private Map<Material, MarketItem> marketMap = new HashMap<>();
    private List<String> blacklist;

    public MarketData(@NotNull final DynamicMarketplace instance) {
        plugin = instance;
    }

    /**
     * This object is what stores all data about a single item in the market
     */
    public static class MarketItem {
        private double amount;
        private double basePrice;
        private Material material;
        private String friendlyName;
        private String recipe;

        public MarketItem(Material material, String friendlyName, double amount) {
            this.amount = amount;
            this.material = material;
            this.friendlyName = friendlyName;
        }

        public MarketItem(Material material, String friendlyName, double amount, String recipe) {
            this.amount = amount;
            this.material = material;
            this.friendlyName = friendlyName;
            this.recipe = recipe;
        }

        public double getBuyPrice(double purchaseAmount) {
            return round(purchaseAmount * this.basePrice * getTax(), 2);
        }

        public double getBuyPrice() {
            return getBuyPrice(1);
        }

        public double getBasePrice() {
            return basePrice;
        }

        public double getSellPrice(int purchaseAmount) {
            return round(purchaseAmount * this.basePrice * 1 / getTax(), 2);
        }

        public double getSellPrice() {
            return getSellPrice(1);
        }

        public double getAmount() {
            return amount;
        }

        public String getFriendly() {
            return friendlyName;
        }

        public Material getMaterial() {
            return material;
        }

        public String getRecipe() {
            return recipe;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public void setBasePrice(double buyPrice) {
            this.basePrice = buyPrice;
        }

        public void setFriendly(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public void setMaterial(Material material) {
            this.material = material;
        }

        public void setRecipe(String recipe) {
            this.recipe = recipe;
        }

        public boolean hasRecipe() {
            return recipe != null;
        }
    }

    private static double adjustBuy(String item, double amount) {
        return plugin.calculations.calcPrice(item, amount);
    }

    /**
     * This is to relay config data to MarketItems
     *
     * @param value  Value to be rounded
     * @param places places to round input to (#.00)
     * @return double - QuantityScalar found in config.yml /papi parse me %market_buy_rabbit_foot,1%
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * This is to relay config data to MarketItems
     *
     * @return double - Tax found in config.yml
     */
    private static double getTax() {
        return plugin.fileManager.tax;
    }

    /**
     * Use this to retrieve an item from storage
     *
     * @param material org.bukkit.Material
     * @param silent   Run this function without causing console messages
     * @return MarketItem - Used to access item's market information
     */
    public MarketItem getItem(Material material, boolean silent) {
        if (contains(material, silent))
            return marketMap.get(material);
        return new MarketItem(Material.AIR, "", 0);
    }

    // TODO: Clean up all the overloaded getItems and reduce copy/pasted code
    public MarketItem getItem(ItemStack itemStack) {
        return getItem(itemStack.getType(), false);
    }

    /**
     * Use this to retrieve an item from storage
     *
     * @param material org.bukkit.Material
     * @param silent   Run this function without causing console messages
     * @return MarketItem - Used to access item's market information
     */
    public MarketItem getItem(String material, boolean silent) {
        if (resolveItem(material)) {
            if (!silent) {
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to get incorrect material from market storage. &3(" + material + ")");
            }
            return new MarketItem(Material.AIR, "", 0);
        }
        final Material resolved = Material.matchMaterial(material);
        return getItem(resolved, silent);
    }

    /**
     * Use this to retrieve an item from storage
     *
     * @param material org.bukkit.Material
     * @return MarketItem - Used to access item's market information
     */
    public MarketItem getItem(Material material) {
        if (contains(material)) {
            return getItem(material, false);
        } else {
            plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to obtain Item data that was not in market storage &3(" + material.getKey().getKey() + ")");
            return new MarketItem(Material.AIR, "", 0);
        }
    }

    /**
     * Use this to retrieve an item from storage
     *
     * @param material org.bukkit.Material
     * @return MarketItem - Used to access item's market information
     */
    public MarketItem getItem(String material) {
        if (resolveItem(material)) {
            plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to get incorrect material from market storage. &3(" + material + ")");
            return new MarketItem(Material.AIR, "", 0);
        }
        final Material resolved = Material.matchMaterial(material);
        return getItem(resolved, false);
    }

    /**
     * Use this to place a new item into storage
     *
     * @param material org.bukkit.Material
     * @param amount   Amount of item in market
     */
    public void putItem(Material material, double amount) {
        marketMap.put(material, new MarketItem(material, plugin.messageUtils.capitalize(material.getKey().getKey()), amount));
    }


    public void putItem(String materialName, int amount, String recipe) {
        if (resolveItem(materialName)) {
            plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to put incorrect material into market storage. &3(" + materialName + ")");
            return;
        }

        final Material material = Material.matchMaterial(materialName);


        marketMap.put(material, new MarketItem(material, plugin.messageUtils.capitalize(material.getKey().getKey()), amount, recipe));
    }

    /**
     * Use this to place a new item into storage
     *
     * @param materialName (String) org.bukkit.Material
     * @param amount       Amount of item in market
     */
    public void putItem(String materialName, double amount) {
        if (resolveItem(materialName)) {
            plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to put incorrect material into market storage. &3(" + materialName + ")");
            return;
        }

        final Material material = Material.matchMaterial(materialName);

        putItem(material, amount);
    }

    /**
     * Use this to check if an item is in market storage
     *
     * @param material org.bukkit.Material
     * @return boolean - Will return true if the material was found
     */
    public boolean contains(Material material) {
        if (!marketMap.containsKey(material)) {
            plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to obtain Item data that was not in market storage &3(" + material.getKey().getKey() + ")");
            return false;
        }
        return true;
    }

    public boolean contains(Material material, boolean silent) {
        if (!marketMap.containsKey(material)) {
            if (!silent) {
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to obtain Item data that was not in market storage &3(" + material.getKey().getKey() + ")");
            }
            return false;
        }
        return true;
    }

    /**
     * Use this to check if an item is in market storage
     *
     * @param materialName (String) org.bukkit.Material
     * @return boolean - Will return true if the material was found
     */
    public boolean contains(String materialName) {
        if (resolveItem(materialName)) {
            plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to put incorrect material into market storage. &3(" + materialName + ")");
            return false;
        }

        final Material material = Material.matchMaterial(materialName);

        if (!marketMap.containsKey(material)) {
            plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to obtain Item data that was not in market storage &3(" + material.getKey().getKey() + ")");
            return false;
        }
        return true;
    }

    /**
     * Use this to check if an item is in market storage
     *
     * @param materialName (String) org.bukkit.Material
     * @param silent       Run this function without causing console messages
     * @return boolean - Will return true if the material was found
     */
    public boolean contains(String materialName, boolean silent) {
        if (resolveItem(materialName)) {
            if (!silent) {
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to put incorrect material into market storage. &3(" + materialName + ")");
            }
            return false;
        }

        final Material material = Material.matchMaterial(materialName);

        if (!marketMap.containsKey(material)) {
            if (!silent) {
                plugin.messageUtils.log(MessageUtils.LogLevel.WARNING, "&eTried to obtain Item data that was not in market storage &3(" + material.getKey().getKey() + ")");
            }
            return false;
        }
        return true;
    }

    /**
     * Use this to check if an item is in market storage
     *
     * @param material (String) org.bukkit.Material
     * @return boolean - Will return true if the input material is correct
     */
    public boolean resolveItem(String material) {
        assert material != null;
        final Material resolved = Material.matchMaterial(material);
        return resolved == null;
    }

    /**
     * Use this to check if an item is in market storage
     *
     * @return MarketData - Will return the entire market map (shouldn't be used often)
     */
    public Map<Material, MarketItem> getDataMap() {
        return this.marketMap;
    }

    /**
     * Use this to get market item blacklist
     *
     * @return List - Will return a list of blacklisted items (can be null)
     */
    public List<String> getBlacklist() {
        return this.blacklist;
    }

    public void setBlacklist(List<String> newBlacklist) {
        this.blacklist = newBlacklist;
    }

    /**
     * Use this to check item against item blacklist
     *
     * @param arg - minecraft item
     * @return boolean - Will check if the arg is on the blacklist
     */
    public boolean checkAgainstBlacklist(String arg) {
        if (blacklist.contains("craftables")) {
            if (getItem(arg).hasRecipe()) {
                return true;
            }
        }

        for (String item : blacklist) {
            if (arg.toLowerCase().contains(item))
                return true;
        }
        return false;
    }
}
