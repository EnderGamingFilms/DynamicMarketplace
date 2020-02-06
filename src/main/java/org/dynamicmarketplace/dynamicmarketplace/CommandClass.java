package org.dynamicmarketplace.dynamicmarketplace;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class CommandClass implements CommandExecutor {
    private DynamicMarketplace plugin;

    public CommandClass(DynamicMarketplace plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Allow only players to run commands in-game
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GREEN + "[Market] " + ChatColor.WHITE + "Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;

        // Try to buy an items
        if ( command.getName().equals("buy") && player.hasPermission("dynamark.buy")) {
            if (args.length == 0) {
                PlayerInteractions.sendHelp(player, command.getName());
                return true;
            }
            if (SaveData.validItem(args[0], player)) {
                int amount = (args.length == 1) ? 1 : castInt(args[1], player);
                if (amount > 0) {
                    ShopOpperations.buy(player, args[0], amount);
                    SaveData.saveMarket();
                }
            } else {
                PlayerInteractions.itemInvalid( player, args[0] );
            }
        } else if ( command.getName().equals("sell") && player.hasPermission("dynamark.sell")) {
            if (args.length == 0) {
                PlayerInteractions.sendHelp(player, command.getName());
                return true;
            }
            if (SaveData.validItem(args[0], player)) {
                int amount = (args.length == 1) ? 1 : castInt(args[1], player);
                if (amount > 0) {
                    ShopOpperations.sellItems(player, args[0], amount, false);
                    SaveData.saveMarket();
                }
            } else {
                PlayerInteractions.itemInvalid( player, args[0] );
            }
        } else if ( command.getName().equals("sellall") && player.hasPermission("dynamark.sellall")) {
            if (args.length == 0) {
                PlayerInteractions.sendHelp(player, command.getName());
                return true;
            }
            if (SaveData.validItem(args[0], player)) {
                ShopOpperations.sellItems(player, args[0], 1, true);
                SaveData.saveMarket();
            } else {
                PlayerInteractions.itemInvalid( player, args[0] );
            }
        }

        if ( command.getName().equals("sellhand") && player.hasPermission("dynamark.sellhand")) {
            ShopOpperations.sellHand( player );
            SaveData.saveMarket();
            return true;
        }

        if ( command.getName().equals("iteminfo")) {
            if ( args.length == 0 )  {
                PlayerInteractions.sendHelp(player,command.getName());
                return true;
        }
            if (SaveData.validItem(args[0], player)) {
                ShopOpperations.itemInfo( player, args[0] );
            } else {
                PlayerInteractions.itemInvalid( player, args[0] );
            }
            return true;
        }

        if ( command.getName().equals("infohand")){
            ShopOpperations.infoHand( player );
            return true;
        }

        // Command /market
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            PlayerInteractions.sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("dynamark.reload")) {
            SaveData.reloadALL();
            player.sendMessage(ChatColor.GREEN + "[Market] Plugin has been reloaded");
        }else if (args[0].equalsIgnoreCase("cost")) {
            if (args.length == 0) return false;
            if (SaveData.validItem(args[1], player)) {
                int amount = (args.length == 2) ? 1 : castInt(args[2], player);
                if (amount > 0) {
                    ShopOpperations.cost(player, args[1], amount);
                }
            } else {
                PlayerInteractions.itemInvalid(player, args[1]);
            }
        }

        return true;
    }

    private static int castInt ( String str, Player player ) {
        if ( str.matches("[0-9]+" ) ) {
            int number = Integer.parseInt(str);
            if ( number < 1000 )
                return  number;
        }
        PlayerInteractions.inputInvalid( player, str);
        return -1;
    }

}