package me.endergaming.dynamicmarketplace.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.database.SQLGetter;
import org.bukkit.entity.Player;

public class HookPlaceholderAPI extends PlaceholderExpansion {
    private final DynamicMarketplace plugin;

    public HookPlaceholderAPI(final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    @Override
    public String getIdentifier() {
        return "market";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        MarketData.MarketItem item;
        String str;
        String[] splitStr;

        if (params.startsWith("buy_")) {
            str = parseAgs(params); // Remove prefix from string
            if (str.matches("([A-z])+?(_)?.+")) { // Parse proper materials
                splitStr = str.split(",");
                if (plugin.marketData.contains(splitStr[0], true) && splitStr.length > 1) {
                    item = plugin.marketData.getItem(splitStr[0], true);
                    if (splitStr[1].matches("^\\d+$")) {// Only parse positive integers
                        int amount = Integer.parseInt(splitStr[1]);
                        double price = (item.getBuyPrice(amount));
                        return plugin.economy.format(price);
                    }
                }
            }
        } else if (params.startsWith("sell_")) {
            str = parseAgs(params); // Remove prefix from string
            if (str.matches("([A-z])+?(_)?.+")) { // Parse proper materials
                splitStr = str.split(",");
                if (plugin.marketData.contains(splitStr[0], true) && splitStr.length > 1) {
                    item = plugin.marketData.getItem(splitStr[0], true);
                    if (splitStr[1].matches("^\\d+$")) {// Only parse positive integers
                        int amount = Integer.parseInt(splitStr[1]);
                        double price = (item.getSellPrice(amount));
                        return plugin.economy.format(price);
                    }
                }
            }
        } else if (params.startsWith("friendly_")) {
            str = parseAgs(params); // Remove prefix from string
            if (plugin.marketData.contains(str, true)) { // Parse proper materials
                item = plugin.marketData.getItem(str, true);
                return item.getFriendly();
            }
        } else if (params.startsWith("amount_")) {
            str = parseAgs(params); // Remove prefix from string
            if (plugin.marketData.contains(str, true)) { // Parse proper materials
                item = plugin.marketData.getItem(str, true);
                return String.valueOf(item.getAmount());
            }
        } else if (params.equalsIgnoreCase("hand")) {
            if (plugin.marketData.contains(player.getItemInHand().getType().getKey().getKey(), true)) {
                item = plugin.marketData.getItem(player.getItemInHand().getType(), true);
                return plugin.economy.format(item.getBuyPrice(player.getItemInHand().getAmount()));
            }
            return "$0.00";
        } else if (params.equalsIgnoreCase("standing")) {
            if (plugin.fileManager.collectorHasStanding) {
                return String.valueOf(plugin.standing.getStanding(player.getUniqueId()));
            } else {
                return String.valueOf(SQLGetter.defaultStanding);
            }
        }
        // Return null if nothing else
        return null;
    }

    public String parseAgs(String args){
        String str = "";
        if (args.startsWith("buy_")) {
            str = args.replaceFirst("buy_", "");
        } else if (args.startsWith("sell_")) {
            str = args.replaceFirst("sell_", "");
        } else if (args.startsWith("friendly_")) {
            str = args.replaceFirst("friendly_", "");
        } else if (args.startsWith("amount_")) {
            str = args.replaceFirst("amount_", "");
        }
        return str;
    }
}
