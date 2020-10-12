package me.endergamingfilms.dynamicmarketplace.commands;

import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SellAllCommand extends BaseCommand {
    private final DynamicMarketplace plugin;
    private Material itemToSell;

    public SellAllCommand(String command, @NotNull final DynamicMarketplace instance) {
        super(command);
        this.plugin = instance;
    }

    public void run(Player player, String[] args) {
        if (!player.hasPermission("market.command.sellall")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        // Command Actions
        args = String.join(" ", args).replace("sellall ", "").split(" ");
        if (Arrays.toString(args).contains("sellall")) {
            if (plugin.marketData.contains(player.getItemInHand().getType(), !plugin.fileManager.debug)) {
                itemToSell = player.getItemInHand().getType();
                plugin.operations.makeSale(player, player.getInventory(), plugin.operations.COMMAND);
            } else {
                plugin.messageUtils.send(player, plugin.respond.itemInvalid(plugin.messageUtils.capitalize(player.getItemInHand().getType().getKey().getKey())));
            }
        } else {
            if (plugin.marketData.contains(args[0], !plugin.fileManager.debug)) {
                itemToSell = Material.matchMaterial(args[0]);
                plugin.operations.makeSale(player, player.getInventory(), plugin.operations.COMMAND);
            } else {
                plugin.messageUtils.send(player, plugin.respond.itemInvalid(plugin.messageUtils.capitalize(args[0])));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.messageUtils.send(sender, plugin.respond.nonPlayer());
            return false;
        }

        if (!sender.hasPermission("market.command.sellall")) {
            plugin.messageUtils.send(sender, plugin.respond.noPerms());
            return false;
        }

        if (args.length == 0) {
            run((Player) sender,new String[]{"sellall", ((Player) sender).getItemInHand().getType().getKey().getKey()});
        } else {
            run((Player) sender, args);
        }

        return true;
    }

    public Material getItemToSell() {
        return this.itemToSell;
    }
}
