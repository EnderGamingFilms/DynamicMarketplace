package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CollectorCommand {
    private static DynamicMarketplace instance = DynamicMarketplace.getInstance();

    public static void runFromConsole(CommandSender sender, String[] args) { // When the command is run from console
        Player playerNew = Bukkit.getPlayerExact(args[1]);
        if (playerNew != null && playerNew.isValid()) {
            playerNew.openInventory(instance.collectorGUI.GUI(playerNew));
        } else {
            DynamicMarketplace.getInstance().interactionManager.collectorInvalidPlayer(sender);
        }
    }

    public static void runFromPlayer(Player player, String[] args) {
        if (!player.hasPermission("market.collector")) {
            player.sendMessage(instance.respond.noPerms());
            return;
        }

        if (args.length >= 2) { // If sender runs /market collector <somePlayer>
            Player playerNew = Bukkit.getPlayerExact(args[1]);
            if (playerNew != null && playerNew.isValid()) {
                playerNew.openInventory(instance.collectorGUI.GUI(playerNew));
            } else {
                instance.respond.collectorInvalidPlayer();
            }
        } else { // If sender runs /market collector
            player.openInventory(instance.collectorGUI.GUI(player));
        }
    }
}
