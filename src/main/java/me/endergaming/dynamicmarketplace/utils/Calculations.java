package me.endergaming.dynamicmarketplace.utils;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.jetbrains.annotations.NotNull;

public class Calculations {
    private final DynamicMarketplace plugin;

    public Calculations(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    public void calcPrice(String item) {
        calcPrice(item, 1);
    }

    public double calcPrice(String item, double amount) {
        double price = 0.0;
        if (plugin.marketData.getItem(item, plugin.fileManager.debug).hasRecipe()) {
            price += calcRecipe(plugin.marketData.getItem(item, plugin.fileManager.debug).getRecipe());
            // Set buyPrice of current passed in item
            plugin.marketData.getItem(item).setBasePrice(price);
        }
        // Basic Price Calculations
        if (plugin.marketData.getItem(item, plugin.fileManager.debug).getAmount() > 0) {
            price += calcBasic(item, amount);
        } else {
            price *= amount;
        }
        return price;
    }

    public double calcBasic(String item, double amount) {
        MarketData.MarketItem newItem = plugin.marketData.getItem(item, !plugin.fileManager.debug);
        // Calculate Base purchase price
        newItem.setBasePrice(((1/newItem.getAmount()) * plugin.fileManager.scalar) * 10);
        // Return adjusted price
        return newItem.getBuyPrice(amount);
    }

    private double calcRecipe(String recipe) {
        double price = 0.0;
        double multiplier= 1.0;
        String[] splitString;
        if (recipe.contains(",")) { // If recipe has multiple parts and needs to be parsed
            String[] parts = recipe.split(",");
            for (int i = 0; i < parts.length; ++i) {//String s : parts) {
                // Replace beginning " " in the parts[i]
                parts[i] = parts[i].trim();
                splitString = parts[i].split(" ");
                // Catch multipliers
                switch (splitString[0]) {
                    case "crafting":
                        multiplier = plugin.fileManager.multiplierCraft;
                        break;
                    case "smelting":
                        multiplier = plugin.fileManager.multiplierSmelt;
                        break;
                    case "growing":
                        multiplier = plugin.fileManager.multiplierGrow;
                        break;
                    default:
                        // Calculate Price
                        price += (splitString.length > 1) ? calcPrice(splitString[0], Double.parseDouble(splitString[1])) : 0.0;
                }
            }
        } else { // If the recipe consists of only a single item
            // Remove the beginning " "
            recipe = recipe.trim();
            splitString = recipe.split(" ");
            price += (splitString.length > 1) ? calcPrice(splitString[0], Double.parseDouble(splitString[1])) : 0.0;
        }
        return price * multiplier;
    }
}
