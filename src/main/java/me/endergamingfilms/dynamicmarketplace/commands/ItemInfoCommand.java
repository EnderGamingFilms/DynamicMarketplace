package me.endergamingfilms.dynamicmarketplace.commands;

import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemInfoCommand extends BaseCommand {
    private final DynamicMarketplace plugin;

    public ItemInfoCommand(String command, @NotNull final DynamicMarketplace instance) {
        super(command);
        this.plugin = instance;
    }

    public void run(Player player, String[] args, String cmd) {
        if (!player.hasPermission("market.command.info")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        switch (args[0]) {
            case "info":
                args = String.join(" ", args).replaceFirst("(info ?)", "").split(" ");
                break;
            case "item":
                args = String.join(" ", args).replaceFirst("(item ?)", "").split(" ");
                break;
            case "iteminfo":
                args = String.join(" ", args).replaceFirst("(iteminfo ?)", "").split(" ");
                break;
        }

        // Command Actions
        if (!args[0].isEmpty()) {
            if (plugin.marketData.contains(args[0], !plugin.fileManager.debug)) {
                plugin.messageUtils.send(player, plugin.respond.itemInfo(args[0]));
            } else {
                plugin.messageUtils.send(player, plugin.respond.itemInvalid(plugin.messageUtils.capitalize(args[0])));
            }
        } else {
            if (plugin.marketData.contains(player.getItemInHand().getType(), !plugin.fileManager.debug)) {
                plugin.messageUtils.send(player, plugin.respond.itemInfo(player.getItemInHand().getType().getKey().getKey()));
            } else {
                plugin.messageUtils.send(player, plugin.respond.itemInvalid(plugin.messageUtils.capitalize(player.getItemInHand().getType().getKey().getKey())));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.messageUtils.send(sender, plugin.respond.nonPlayer());
            return false;
        }

        if (!sender.hasPermission("market.command.info")) {
            plugin.messageUtils.send(sender, plugin.respond.noPerms());
            return false;
        }

        if (args.length == 0) {
            run((Player) sender, new String[]{"info", ((Player) sender).getItemInHand().getType().getKey().getKey()}, label);
        } else {
            run((Player) sender, args, label);
        }

        return true;
    }
}
