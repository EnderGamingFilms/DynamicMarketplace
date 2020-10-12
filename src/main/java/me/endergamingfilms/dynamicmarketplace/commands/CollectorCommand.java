package me.endergamingfilms.dynamicmarketplace.commands;

import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CollectorCommand {
    private final DynamicMarketplace plugin;

    public CollectorCommand(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    public void runFromConsole(CommandSender sender, String[] args) { // When the command is run from console
        if (!plugin.fileManager.collectorIsEnabled) { // Collector is disabled (config.yml)
            plugin.messageUtils.send(sender, plugin.messageUtils.format("&cThe Collector is not enabled..."));
            return;
        }

        if (args.length >= 2) {
            Player playerNew = Bukkit.getPlayerExact(args[1]);
            if (playerNew != null && playerNew.isValid()) {
                playerNew.openInventory(plugin.collectorGUI.GUI(playerNew));
            } else {
                plugin.messageUtils.send(sender, plugin.respond.collectorInvalidPlayer());
            }
        } else {
            plugin.messageUtils.send(sender, plugin.respond.getHelp(args[0]));
        }
    }

    public void runFromPlayer(Player player, String[] args) {
        if (!player.hasPermission("market.command.collector")) {
            player.sendMessage(plugin.respond.noPerms());
            return;
        }

        if (!plugin.fileManager.collectorIsEnabled) { // Collector is disabled (config.yml)
            plugin.messageUtils.send(player, plugin.messageUtils.format("&cThe Collector is not enabled..."));
            return;
        }

        if (args.length >= 2) { // If sender runs /market collector <somePlayer>
            Player playerNew = Bukkit.getPlayerExact(args[1]);
            if (playerNew != null && playerNew.isValid()) {
                playerNew.openInventory(plugin.collectorGUI.GUI(playerNew));
            } else {
                plugin.messageUtils.send(player, plugin.respond.collectorInvalidPlayer());
            }
        } else { // If sender runs /market collector
            player.openInventory(plugin.collectorGUI.GUI(player));
        }
    }
}
