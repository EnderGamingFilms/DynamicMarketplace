package me.endergaming.dynamicmarketplace;

import me.endergaming.dynamicmarketplace.gui.CollectorGUI;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClass implements CommandExecutor {
    private DynamicMarketplace plugin;

    public CommandClass(DynamicMarketplace plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        if (!(sender instanceof Player)) { // Command for console to run
//                if (args.length == 2) {
//                    if (args[0].equalsIgnoreCase("collector")) {
//                        Player playerNew = Bukkit.getPlayerExact(args[1]);
//                        if (playerNew != null && playerNew.isValid()) {
//                            playerNew.openInventory(CollectorGUI.GUI(playerNew));
//                        } else {
//                            plugin.interactionManager.collectorInvalidPlayer(sender);
//                        }
//                    }
//                    return true;
//                }
//                sender.sendMessage(ChatColor.GREEN + "[Market] " + ChatColor.WHITE + "Only players can run this command.");
//                return false;
//        }
//
//        Player player = (Player) sender;
//
//        // In-Game Commands
//        if (args[0].equalsIgnoreCase("collector") && sender.hasPermission("market.command.collector")) {
//            if (args.length >= 2) { // If sender runs /market collector <somePlayer>
//                Player playerNew = Bukkit.getPlayerExact(args[1]);
//                if (playerNew != null && playerNew.isValid()) {
//                    playerNew.openInventory(CollectorGUI.GUI(playerNew));
//                } else {
//                    plugin.interactionManager.collectorInvalidPlayer(sender);
//                }
//            } else { // If sender runs /market collector
//                player.openInventory(CollectorGUI.GUI(player));
//            }
//            return true;
//        }
//
//        if ( command.getName().equals("buy") && player.hasPermission("market.command.buy")) {
//            if (args.length == 1) {
//                PlayerInteractions.getHelp(player, command.getName());
//                return true;
//            }
//            if (SaveData.validItem(args[0], player)) {
//                int amount = (args.length == 1) ? 1 : castInt(args[1], player);
//                if (amount > 0) {
//                    ShopOpperations.buy(player, args[0], amount);
//                    SaveData.saveMarket();
//                }
//            } else {
//                PlayerInteractions.itemInvalid( player, args[0] );
//            }
//        } else if ( command.getName().equals("sell") && player.hasPermission("market.command.sell")) {
//            if (args.length == 0) {
//                PlayerInteractions.getHelp(player, command.getName());
//                return true;
//            }
//            if (SaveData.validItem(args[0], player)) {
//                int amount = (args.length == 1) ? 1 : castInt(args[1], player);
//                if (amount > 0) {
//                    ShopOpperations.sellItems(player, args[0], amount, false);
//                    SaveData.saveMarket();
//                }
//            } else {
//                PlayerInteractions.itemInvalid( player, args[0] );
//            }
//        } else if ( command.getName().equals("sellall") && player.hasPermission("market.command.sellall")) {
//            if (args.length == 0) {
//                PlayerInteractions.getHelp(player, command.getName());
//                return true;
//            }
//            if (SaveData.validItem(args[0], player)) {
//                ShopOpperations.sellItems(player, args[0], 1, true);
//                SaveData.saveMarket();
//            } else {
//                PlayerInteractions.itemInvalid( player, args[0] );
//            }
//        }
//
//        if ( command.getName().equals("sellhand") && player.hasPermission("market.command.sellhand")) {
//            ShopOpperations.sellHand( player );
//            SaveData.saveMarket();
//            return true;
//        }
//
//
//        if ( command.getName().equals("iteminfo")) {
//            if ( args.length == 0 )  {
//                PlayerInteractions.getHelp(player,command.getName());
//                return true;
//        }
//            if (SaveData.validItem(args[0], player)) {
//                ShopOpperations.itemInfo( player, args[0] );
//            } else {
//                PlayerInteractions.itemInvalid( player, args[0] );
//            }
//            return true;
//        }
//
//        if ( command.getName().equals("infohand")){
//            ShopOpperations.infoHand( player );
//            return true;
//        }
//
//        // Command /market
//        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
//            PlayerInteractions.getHelp(player);
//            return true;
//        }
//
//        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("market.reload")) {
//            SaveData.reloadALL();
//            player.sendMessage(ChatColor.GREEN + "[Market] Plugin has been reloaded");
//        }else if (args[0].equalsIgnoreCase("cost")) {
//            if (args.length == 1) return false;
//            if (SaveData.validItem(args[1], player)) {
//                int amount = (args.length == 2) ? 1 : castInt(args[2], player);
//                if (amount > 0) {
//                    ShopOpperations.cost(player, args[1], amount);
//                }
//            } else {
//                PlayerInteractions.itemInvalid(player, args[1]);
//            }
//        }
//
//        return true;
//    }
//
//    private static int castInt (String str, Player player) {
//        if (str.matches("[0-9]+")) {
//            int number = Integer.parseInt(str);
//            if ( number < 1000 )
//                return  number;
//        }
//        PlayerInteractions.inputInvalid(player, str);
        return  false;
    }
}