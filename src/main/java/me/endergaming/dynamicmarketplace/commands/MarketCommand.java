package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarketCommand extends BaseCommand {
    private static DynamicMarketplace instance = DynamicMarketplace.getInstance();
    public MarketCommand(String command) {
        super(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Only allow player to run command - Exception == collector <player>
        if (!(sender instanceof Player)) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("collector")) {
                    // Collector Command
                    if (args.length == 2) {
                        CollectorCommand.runFromConsole(sender, args); // For console-only
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + "/market collector [playerName] " + ChatColor.WHITE + "- Opens the collector gui");
                    }
                } else {
                    PlayerInteractions.nonPlayer(sender);
                }
            }
            return true;
        }
        // Player run commands
        Player player = (Player) sender;

        // Return help menu if no args are passed
        if (args.length == 0) {
            PlayerInteractions.getHelp(player);
            return true;
        }

        if (args[0].toLowerCase().matches("help|hlp|h|plz")) {
            PlayerInteractions.getHelp(player);
        } else if (args[0].toLowerCase().matches("reload|rl")) {
            ReloadCommand.run(player);
        } else if (args[0].equalsIgnoreCase("collector")) {
            CollectorCommand.runFromPlayer(player, args);
        } else if (args[0].equalsIgnoreCase("missing")) {
            if (player.hasPermission("market.*")) {
                player.sendMessage(instance.respond.genMissingFile(instance.fileManager.outputMissingMats()));
            } else {
                player.sendMessage(instance.respond.noPerms());
            }
        } else if (args[0].equalsIgnoreCase("buy")) {
            BuyCommand.run(player, args, args[0]);
        } else if (args[0].equalsIgnoreCase("test")) { // |------------- CURRENT TEST COMMAND -------------|
            // Stuff Here
            if (args.length > 2) {
                instance.marketData.getItem(args[1]).setAmount(Double.parseDouble(args[2]));
                player.sendMessage(instance.messageUtils.colorize("&7Set amount of &3" + instance.marketData.getItem(args[1]).getFriendly() + "&7 to&3 " + args[2]));
            } else {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("reload")) {
                        instance.fileManager.calcItemData();
                        instance.marketData.getDataMap().forEach((k, v) -> System.out.println("TEST | All Prices (" +
                                v.getMaterial() + "): " + v.getBuyPrice()));
                    } else if (args[1].equalsIgnoreCase("amount")) {
                        player.sendMessage(instance.messageUtils.colorize("&eAmount in Market: &6" +
                                instance.marketData.getItem(player.getItemInHand().getType().toString()).getAmount()));
                    } else if (args[1].equalsIgnoreCase("load")) {
                        instance.fileManager.readMaterialData();
                    } else if (args[1].equalsIgnoreCase("missing")) {
                        instance.fileManager.outputMissingMats();
                    } else if (args[1].equalsIgnoreCase("sellall")) {
                        instance.operations.makeSale(player, player.getInventory(), instance.operations.COLLECTOR);
                    }  else if (args[1].equalsIgnoreCase("remove")) {
                        instance.operations.removeFromInventory(player.getInventory(), player.getInventory().getContents(), Material.APPLE, 16);
                    } else if (args[1].equalsIgnoreCase("update")) {
                        instance.fileManager.calcItemData();
                        player.sendMessage(instance.messageUtils.colorize("&eUpdated Price: &a") +
                                instance.marketData.getItem(player.getItemInHand().getType().toString())
                                        .getSellPrice());
                        instance.fileManager.saveMaterialData();
                        player.sendMessage(instance.messageUtils.colorize("&7(&c!&7) &dMarket data saved."));
                    }
                } else {
                    player.sendMessage(instance.messageUtils.colorize("&cNothing happened..."));
                    return false;
                }
            }
        } else if (args[0].equalsIgnoreCase("sell")) {
            SellCommand.run(player, args, args[0]);
        } else if (args[0].equalsIgnoreCase("cost")) {
            CostCommand.run(player, args);
        } else if (args[0].equalsIgnoreCase("sellhand")) {
            SellHandCommand.run(player);
        } else if (args[0].toLowerCase().matches("sellall")) {
            SellAllCommand.run(player, args, args[0]);
        } else if (args[0].toLowerCase().matches("iteminfo|info|item")) {
            ItemInfoCommand.run(player, args, args[0]);
        } else if (args[0].toLowerCase().matches("settax|tax")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.YELLOW + "/market settax <amount> " + ChatColor.WHITE + "- Set collector player profit");
                return false;
            }
            if (player.hasPermission("market.command.settax")) {
                if (args[1].matches("^[1-9][0-9]?$|^100$")) {
                    instance.operationsManager.setTax(Double.parseDouble(args[1]));
                    player.sendMessage(DynamicMarketplace.getInstance().messageUtils.format("&cThis action is only temporary. Will reset after restart!"));
                } else {
                    player.sendMessage(ChatColor.GREEN + "[Market] " + ChatColor.WHITE + "Please enter a percentage from 1-100");
                }
            }
        } else {
            PlayerInteractions.getHelp(player);
            return false;
        }

        return true;
    }

    /* @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completes = null;
        //TODO command completion
        //market reload
        if (args.length == 1) {
            completes = Arrays.asList("reload", "...");
            if (args[0].length() > 0)
                completes.removeIf(s -> !s.startsWith(args[0]));
        }
        return completes;
    } */
}
