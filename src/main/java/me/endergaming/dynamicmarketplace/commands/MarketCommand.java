package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MarketCommand extends BaseCommand {
    private final DynamicMarketplace plugin;

    public MarketCommand(String command, @NotNull final DynamicMarketplace instance) {
        super(command);
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Only allow player to run command - Exception == collector <player>
        if (!(sender instanceof Player)) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("collector")) {
                    // Collector Command
                    if (args.length == 2) {
                        plugin.cmdManager.collectorCmd.runFromConsole(sender, args); // For console-only
                    } else {
                        plugin.messageUtils.send(sender, plugin.respond.getHelp(label));
                    }
                } else {
                    plugin.messageUtils.send(sender, plugin.respond.nonPlayer());
                }
            }
            return true;
        }

        // Player run commands
        Player player = (Player) sender;

        // Return help menu if no args are passed
        if (args.length == 0) {
            plugin.messageUtils.send(player, plugin.respond.getHelp(player));
            return true;
        }

        if (args[0].toLowerCase().matches("reload|rl")) {
            plugin.cmdManager.reloadCmd.run(player);

        } else if (args[0].toLowerCase().matches("load")) {
            plugin.cmdManager.reloadCmd.load(player);
        } else if (args[0].equalsIgnoreCase("collector")) {
            plugin.cmdManager.collectorCmd.runFromPlayer(player, args);
        } else if (args[0].equalsIgnoreCase("missing")) {
            if (player.hasPermission("market.*")) {
                player.sendMessage(plugin.respond.genMissingFile(plugin.fileManager.outputMissingMats()));
            } else {
                player.sendMessage(plugin.respond.noPerms());
            }
        } else if (args[0].equalsIgnoreCase("buy")) {
            plugin.cmdManager.buyCmd.run(player, args, args[0]);
        } else if (args[0].equalsIgnoreCase("test")) { // |------------- CURRENT TEST COMMAND -------------|
            if (!player.hasPermission("market.*")) return false;
            // Stuff Here
            if (args.length > 2) {
                plugin.marketData.getItem(args[1]).setAmount(Double.parseDouble(args[2]));
                player.sendMessage(plugin.messageUtils.colorize("&7Set amount of &3" + plugin.marketData.getItem(args[1]).getFriendly() + "&7 to&3 " + args[2]));
            } else {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("reload")) {
                        plugin.fileManager.calcItemData();
                        plugin.marketData.getDataMap().forEach((k, v) -> System.out.println("TEST | All Prices (" +
                                v.getMaterial() + "): " + v.getBuyPrice()));
                    } else if (args[1].equalsIgnoreCase("amount")) {
                        player.sendMessage(plugin.messageUtils.colorize("&eAmount in Market: &6" +
                                plugin.marketData.getItem(player.getItemInHand().getType().toString()).getAmount()));
                    } else if (args[1].equalsIgnoreCase("load")) {
                        plugin.fileManager.readMaterialData();
                    } else if (args[1].equalsIgnoreCase("missing")) {
                        plugin.fileManager.outputMissingMats();
                    } else if (args[1].equalsIgnoreCase("help")) {
                        plugin.messageUtils.send(player, plugin.respond.getHelp(player));
                    } else if (args[1].equalsIgnoreCase("sellall")) {
                        plugin.operations.makeSale(player, player.getInventory(), plugin.operations.COLLECTOR);
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        plugin.operations.removeFromInventory(player.getInventory(), player.getInventory().getContents(), Material.APPLE, 16);
                    } else if (args[1].equalsIgnoreCase("update")) {
                        plugin.fileManager.calcItemData();
                        player.sendMessage(plugin.messageUtils.colorize("&eUpdated Price: &a") +
                                plugin.marketData.getItem(player.getItemInHand().getType().toString())
                                        .getSellPrice());
                        plugin.fileManager.saveMaterialData();
                        player.sendMessage(plugin.messageUtils.colorize("&7(&c!&7) &dMarket data saved."));
                    }
                } else {
                    player.sendMessage(plugin.messageUtils.colorize("&cNothing happened..."));
                    return false;
                }
            }
        } else if (args[0].equalsIgnoreCase("sell")) {
            plugin.cmdManager.sellCmd.run(player, args, args[0]);
        } else if (args[0].equalsIgnoreCase("worth")) {
            plugin.cmdManager.worthCmd.run(player);
        } else if (args[0].equalsIgnoreCase("sellhand")) {
            plugin.cmdManager.sellHandCmd.run(player);
        } else if (args[0].toLowerCase().matches("sellall")) {
            plugin.cmdManager.sellAllCmd.run(player, args);
        } else if (args[0].toLowerCase().matches("iteminfo|info|item")) {
            plugin.cmdManager.itemInfoCmd.run(player, args, args[0]);
        } else {
            plugin.messageUtils.send(player, plugin.respond.getHelp(player));
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
