package org.dynamicmarketplace.dynamicmarketplace;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerInteractions {

    private static String prefix = ChatColor.GREEN + "[Market] " + ChatColor.WHITE;

    private static void sendPlayer ( Player player, String message ) {

        message = message.replaceAll("\\[([^]]*)\\]", ChatColor.RED + "$1" + ChatColor.WHITE);
        message = message.replaceAll("\\(([^)]*)\\)", ChatColor.GREEN + "$1" + ChatColor.WHITE);
        message = message.replaceAll("\\{([^}]*)}", ChatColor.YELLOW + "$1" + ChatColor.WHITE);

        player.sendMessage(prefix + message);

    }

    public static void buyFailedCost (Player player, double cost, double balance) {
        sendPlayer ( player, String.format ("You cannot afford the purchase, cost: ($%.4f), your balance: ($%.2f)", cost, balance));
    }
    public static void buyFailedQuantity (Player player, String item, int amount) {
        sendPlayer ( player, String.format ("You cannot buy (%d) of (%s), there are not that many left in the shop", amount, item));
    }
    public static void buySuccess ( Player player, String item, int amount, double sale ){
        sendPlayer( player, String.format( "Bought (%d) (%s) for ($%.2f)", amount, item, sale));
    }
    public  static void buyFailedSpace ( Player player, String item, int amount, double sale ){
        sendPlayer( player, String.format( "Inventory space limited! Only bought (%d) (%s) for ($%.2f)", amount, item, sale));
    }

    public static void noItemInHand ( Player player){
        sendPlayer ( player, String.format ("You are not holding an item to sell") );
    }

    public static void hasName (Player player) {
        sendPlayer ( player, String.format ("You cannot sell an item with a custom name"));
    }

    public static void sellFailedQuantity ( Player player, String item, int amount, double sale ){
        sendPlayer ( player, String.format ("You dont have that much (%s), sold the (%d) you did have for ($%.2f)", item, amount, sale));
    }
    public static void sellFailedQuantity ( Player player, String item, int amount){
        sendPlayer ( player, String.format ("You dont have that much (%s), sold the (%d) you did have.", item, amount));
    }
    public static void sellSuccess ( Player player, String item, int amount, double sale ){
        sendPlayer ( player, String.format ("Sold (%d) (%s) for ($%.2f)", amount, item, sale));
    }

    public static void itemInfo ( Player player, String item, double amount, double[] costs ){
        sendPlayer( player, "-------------------------------");
        sendPlayer(player, String.format("{Info for item:} (%s) . . .", item));
        if ( amount > 0 ) {
            sendPlayer(player, String.format("This item is a basic material"));
            sendPlayer(player, String.format("There are (%.0f) in the market", amount-1));
        }
        else
            sendPlayer(player, String.format("This item is a crafted material"));
        if (costs[0] < 0)
            sendPlayer( player, String.format("1x  -                Sell: (%.2f)", costs[1]));
        else
            sendPlayer( player, String.format("1x  - Buy: (%.2f) Sell: (%.2f)", costs[0], costs[1]));
        if (costs[2] < 0)
            sendPlayer( player, String.format("64x -                Sell: (%.2f)", costs[3]));
        else
            sendPlayer( player, String.format("64x - Buy: (%.2f) Sell: (%.2f)", costs[2], costs[3]));
        sendPlayer( player, "-------------------------------");
    }

    public static void itemInvalid ( Player player, String item){
        sendPlayer( player, String.format("No item of name (%s) exists, ask an admin to add it to the market", item ));
    }

    public static void inputInvalid ( Player player, String input ){
        sendPlayer( player, String.format("Input number \'(%s)\' is not valid", input ));
    }

    public static void invalidSellEnchant ( Player player ){
        sendPlayer( player, "Cannot sell enchanted items");
    }
    public static void invalidSellDamaged ( Player player ){
        sendPlayer( player, "Cannot sell damaged items");
    }
    public static void itemCost ( Player player, String name, int amount, double price ){
        sendPlayer( player, String.format("(%d) (%s) currently costs (%.2f)", amount, name, price ));
    }

    public static void sendHelp(Player player) {
        player.sendMessage(ChatColor.BLUE + "---------------------" + ChatColor.GREEN + " Dynamic Market " + ChatColor.BLUE + "---------------------");
        player.sendMessage(ChatColor.YELLOW + "/market reload " + ChatColor.WHITE + "- Reloads config");
        player.sendMessage(ChatColor.YELLOW + "/market cost <item> <amount> " + ChatColor.WHITE + "- Gets the cost of an item");
        player.sendMessage(ChatColor.YELLOW + "/buy <item> <amount> " + ChatColor.WHITE + "- Buy an item from the market");
        player.sendMessage(ChatColor.YELLOW + "/sell <item> <amount> " + ChatColor.WHITE + "- Sells an item to the market");
        player.sendMessage(ChatColor.YELLOW + "/sellall <item> " + ChatColor.WHITE + "- Sells all of an item in your inventory");
        player.sendMessage(ChatColor.YELLOW + "/iteminfo <item> " + ChatColor.WHITE + "- Get info on a specific item");
        player.sendMessage(ChatColor.YELLOW + "/sellhand " + ChatColor.WHITE + "- Sells the item in your hand and that amount");
        player.sendMessage(ChatColor.YELLOW + "/infohand " + ChatColor.WHITE + "- Check the current price of the held item");
    }

    public static void sendHelp(Player player, String str) {
        switch (str) {
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
            default:
                System.out.println(ChatColor.RED + "[Market-Err] We made it to a place we should never end up in. (CARROT)");
        }
    }
}
