package me.endergaming.dynamicmarketplace.utils;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.ShopOpperations;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerInteractions {

    private static String prefix = ChatColor.GREEN + "[Market] " + ChatColor.WHITE;

    private static void sendPlayer(Player player, String message) {

        message = message.replaceAll("\\[([^]]*)\\]", ChatColor.RED + "$1" + ChatColor.WHITE);
        message = message.replaceAll("\\(([^)]*)\\)", ChatColor.GREEN + "$1" + ChatColor.WHITE);
        message = message.replaceAll("\\{([^}]*)}", ChatColor.YELLOW + "$1" + ChatColor.WHITE);

        player.sendMessage(prefix + message);

    }

    public static void collectorEmpty(Player player) {
        player.sendMessage(prefix + ChatColor.YELLOW + "You sold nothing to The Collector... Good job.");
    }

    public static void collectorFailed(Player player) {
        player.sendMessage(prefix + ChatColor.YELLOW + "Sorry, your transaction was canceled.");
    }

    public static void collectorSuccess(Player player) {
        player.sendMessage(prefix + ChatColor.GRAY + ChatColor.BOLD + "The Collector " + ChatColor.RESET + ChatColor.YELLOW + "has sold your items! " + collectorTax(player));
    }

    public static String collectorTax(Player player) {
//        if (player.hasPermission("dynamark.tax.premium")) {
//            return "You get 60% of the profits.";
//        } else {
//            return "You get 60% of the profits.";
//        }
        int percent = (int) (100* DynamicMarketplace.getInstance().operationsManager.getTax());
        return ("You get " + percent + "% of the profits.");
    }

    public static void noPermission(Player player) {
        player.sendMessage(ChatColor.RED + "Insufficient Permissions.");
    }

    public void collectorInvalidPlayer(CommandSender sender) {
        sender.sendMessage(prefix + ChatColor.YELLOW + "The player you entered is offline or invalid.");
    }

    public static void nonPlayer(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "[Market] " + ChatColor.WHITE + "Only players can run this command.");
    }

    public static void pluginReloaded(Player player) {
        player.sendMessage(ChatColor.GREEN + "[Market] Plugin has been reloaded");
    }

    public static void buyFailedCost(Player player, double cost, double balance) {
        sendPlayer(player, String.format("You cannot afford the purchase, cost: ($%.4f), your balance: ($%.2f)", cost, balance));
    }

    public static void buyFailedQuantity(Player player, String item, int amount) {
        sendPlayer(player, String.format("You cannot buy (%d) of (%s), there are not that many left in the shop", amount, capitalize(item)));
    }

    public static void buySuccess(Player player, String item, int amount, double sale) {
        sendPlayer(player, String.format("Bought (%d) (%s) for ($%.2f)", amount, item, sale));
    }

    public static void buyFailedSpace(Player player, String item, int amount, double sale) {
        sendPlayer(player, String.format("Inventory space limited! Only bought (%d) (%s) for ($%.2f)", amount, capitalize(item), sale));
    }

    public static void noItemInHand(Player player) {
        sendPlayer(player, String.format("You are not holding an item to sell"));
    }

    public static void hasName(Player player) {
        sendPlayer(player, String.format("You cannot sell an item with a custom name"));
    }

    public static void sellFailedQuantity(Player player, String item, int amount, double sale) {
        sendPlayer(player, String.format("You dont have that much (%s), sold the (%d) you did have for ($%.2f)", capitalize(item), amount, sale));
    }

    public static void sellFailedQuantity(Player player, String item, int amount) {
        sendPlayer(player, String.format("You dont have that much (%s), sold the (%d) you did have.", capitalize(item), amount));
    }

    public static void sellSuccess(Player player, String item, int amount, double sale) {
        sendPlayer(player, String.format("Sold (%d) (%s) for ($%.2f)", amount, capitalize(item), sale));
    }

    public static void itemInfo(Player player, String item, double amount, double[] costs) {
        sendPlayer(player, "-------------------------------");
        sendPlayer(player, String.format("{Info for item:} (%s) . . .", capitalize(item)));
        if (amount > 0) {
            sendPlayer(player, String.format("This item is a basic material"));
            sendPlayer(player, String.format("There are (%.0f) in the market", amount - 1));
        } else
            sendPlayer(player, String.format("This item is a crafted material"));
        if (costs[0] < 0)
            sendPlayer(player, String.format("1x  -                Sell: (%.2f)", costs[1]));
        else
            sendPlayer(player, String.format("1x  - Buy: (%.2f) Sell: (%.2f)", costs[0], costs[1]));
        if (costs[2] < 0)
            sendPlayer(player, String.format("64x -                Sell: (%.2f)", costs[3]));
        else
            sendPlayer(player, String.format("64x - Buy: (%.2f) Sell: (%.2f)", costs[2], costs[3]));
        sendPlayer(player, "-------------------------------");
    }

    public static void itemInvalid(Player player, String item) {
        sendPlayer(player, String.format("No item of name (%s) exists, ask an admin to add it to the market", capitalize(item)));
    }

    public static void inputInvalid(Player player, String input) {
        sendPlayer(player, String.format("Input number \'(%s)\' is not valid", input));
    }

    public static void invalidSellEnchant(Player player) {
        sendPlayer(player, "Cannot sell enchanted items");
    }

    public static void invalidSellDamaged(Player player) {
        sendPlayer(player, "Cannot sell damaged items");
    }

    public static void itemCost(Player player, String name, int amount, String price) {
        player.sendMessage(ChatColor.GREEN + "[Market] " + amount + " " + capitalize(name) + ChatColor.WHITE + " currently costs " + ChatColor.GREEN + price);
    }

    public static void getHelp(Player player) {
        player.sendMessage(ChatColor.BLUE + "--------------------" + ChatColor.GREEN + " Dynamic Market " + ChatColor.BLUE + "--------------------");
        player.sendMessage(ChatColor.YELLOW + "/market reload " + ChatColor.WHITE + "- Reloads config");
        player.sendMessage(ChatColor.YELLOW + "/market cost <item> <amount> " + ChatColor.WHITE + "- Gets the cost of an item");
        player.sendMessage(ChatColor.YELLOW + "/market collector [playerName] " + ChatColor.WHITE + "- Opens the collector gui");
        player.sendMessage(ChatColor.YELLOW + "/buy <item> <amount> " + ChatColor.WHITE + "- Buy an item from the market");
        player.sendMessage(ChatColor.YELLOW + "/sell <item> <amount> " + ChatColor.WHITE + "- Sells an item to the market");
        player.sendMessage(ChatColor.YELLOW + "/sellall <item> " + ChatColor.WHITE + "- Sells all of an item in your inventory");
        player.sendMessage(ChatColor.YELLOW + "/iteminfo <item> " + ChatColor.WHITE + "- Get info on a specific item");
        player.sendMessage(ChatColor.YELLOW + "/sellhand " + ChatColor.WHITE + "- Sells the item in your hand and that amount");
    }

    public static void getHelp(Player player, String cmd) {
        switch (cmd) {
            case "collector":
                player.sendMessage(ChatColor.YELLOW + "/market collector [playerName] " + ChatColor.WHITE + "- Opens the collector gui");
                break;
            case "buy":
                player.sendMessage(ChatColor.YELLOW + "/buy <item> <amount> " + ChatColor.WHITE + "- Buy an item from the market");
                break;
            case "sell":
                player.sendMessage(ChatColor.YELLOW + "/sell <item> <amount> " + ChatColor.WHITE + "- Sells an item to the market");
                break;
            case "sellall":
                player.sendMessage(ChatColor.YELLOW + "/sellall <item> " + ChatColor.WHITE + "- Sells all of an item in your inventory");
                break;
            case "iteminfo":
                player.sendMessage(ChatColor.YELLOW + "/iteminfo <item> " + ChatColor.WHITE + "- Get info on a specific item");
                break;
            case "cost":
                player.sendMessage(ChatColor.YELLOW + "/market cost <item> <amount> " + ChatColor.WHITE + "- Gets the cost of an item");
                break;
            default:
                System.out.println(ChatColor.RED + "[Market-Err] We made it to a place we should never end up in. (CARROT)");
        }
    }

    public static String getHelp(String cmd) {
        switch (cmd) {
            case "collector":
                return ChatColor.YELLOW + "/market collector [playerName] " + ChatColor.WHITE + "- Opens the collector gui";
            case "buy":
                return ChatColor.YELLOW + "/buy <item> <amount> " + ChatColor.WHITE + "- Buy an item from the market";
            case "sell":
                return ChatColor.YELLOW + "/sell <item> <amount> " + ChatColor.WHITE + "- Sells an item to the market";
            case "sellall":
                return ChatColor.YELLOW + "/sellall <item> " + ChatColor.WHITE + "- Sells all of an item in your inventory";
            case "iteminfo":
                return ChatColor.YELLOW + "/iteminfo <item> " + ChatColor.WHITE + "- Get info on a specific item";
            default:
                System.out.println(ChatColor.RED + "[Market-Err] We made it to a place we should never end up in. (PLUM)");
        }
        return null;
    }

    public static String capitalize(String input) { // https://www.spigotmc.org/threads/how-to-get-a-user-friendly-item-name.373484/
        StringBuilder output = new StringBuilder();
        for (String s : input.split("_")) {
            output.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
        }
        return output.substring(0, output.length() - 1);
    }
}
