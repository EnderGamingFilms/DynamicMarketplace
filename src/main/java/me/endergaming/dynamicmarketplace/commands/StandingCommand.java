package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.endergaming.dynamicmarketplace.database.SQLGetter.defaultStanding;

public class StandingCommand {
    private final DynamicMarketplace plugin;

    public StandingCommand(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    public void runFromConsole(CommandSender sender, String[] args) { // When the command is run from console
        // Check if Standing is enabled
        if (!plugin.fileManager.collectorHasStanding) {
            plugin.messageUtils.send(sender, plugin.messageUtils.format("&cPlease enable standing in config.yml to use this command."));
            return;
        }

        // Remove "standing" from args
        String[] str = args;
        str = String.join(" ", str).replaceFirst("(standing ?)", "").split(" ");

        // Must pass in 3 arguments
        if (str.length != 3) {
            if (str[0].equalsIgnoreCase("get")) {
                // Specified player must be online
                if (str.length < 2 || plugin.getServer().getPlayer(str[1]) == null) {
                    plugin.messageUtils.send(sender, plugin.respond.collectorInvalidPlayer());
                    return;
                }
                plugin.messageUtils.send(sender, plugin.messageUtils.format("&aCurrent Standing&f " +
                        plugin.standing.getStanding(plugin.getServer().getPlayer(str[1]).getUniqueId()) + " &7(&b" + str[1] + "&7)"));
            } else {
                plugin.messageUtils.send(sender, plugin.respond.getHelp(args[0]));
            }
            return;
        }

        // Specified player must be online
        if (plugin.getServer().getPlayer(str[1]) == null) {
            plugin.messageUtils.send(sender, plugin.respond.collectorInvalidPlayer());
            return;
        }

        // Set player uuid
        UUID uuid = plugin.getServer().getPlayer(str[1]).getUniqueId();
        // Check if passed in amount is an integer
        int amount = str[2].matches("[0-9]+") ? Integer.parseInt(str[2]) : defaultStanding;
        // Decide what to do
        if (str[0].equalsIgnoreCase("add")) {
            plugin.standing.addStanding(uuid, amount);
        } else if (str[0].equalsIgnoreCase("remove")) {
            if ((plugin.standing.getStanding(uuid) - amount) < defaultStanding)
                plugin.standing.setStanding(uuid, defaultStanding);
            else
                plugin.standing.removeStanding(uuid, amount);
        } else if (str[0].equalsIgnoreCase("set")) {
            plugin.standing.setStanding(uuid, Math.max(amount, defaultStanding));
        }
    }

    public void runFromPlayer(Player player, String[] args, String cmd) {
        // Check if Standing is enabled
        if (!plugin.fileManager.collectorHasStanding) {
            plugin.messageUtils.send(player, plugin.messageUtils.format("&cPlease enable standing in config.yml to use this command."));
            return;
        }

        if (!player.hasPermission("market.command.standing")) {
            player.sendMessage(plugin.respond.noPerms());
            return;
        }

        // Remove "standing" from args
        String[] str = args;
        str = String.join(" ", str).replaceFirst("(standing ?)", "").split(" ");

        // Must pass in 3 arguments
        if (str.length != 3) {
            if (str[0].equalsIgnoreCase("get")) {
                // Specified player must be online
                if (str.length < 2 || plugin.getServer().getPlayer(str[1]) == null) {
                    plugin.messageUtils.send(player, plugin.respond.collectorInvalidPlayer());
                    return;
                }
                plugin.messageUtils.send(player, plugin.messageUtils.format("&aCurrent Standing&f " +
                        plugin.standing.getStanding(plugin.getServer().getPlayer(str[1]).getUniqueId()) + " &7(&b" + plugin.getServer().getPlayer(str[1]).getName() + "&7)"));
            } else {
                plugin.messageUtils.send(player, plugin.respond.getHelp(cmd));
            }
            return;
        }

        // Specified player must be online
        if (plugin.getServer().getPlayer(str[1]) == null) {
            plugin.messageUtils.send(player, plugin.respond.collectorInvalidPlayer());
            return;
        }

        // Set player uuid
        UUID uuid = plugin.getServer().getPlayer(str[1]).getUniqueId();
        // Check if passed in amount is an integer
        int amount = str[2].matches("[0-9]+") ? Integer.parseInt(str[2]) : defaultStanding;
        // Decide what to do
        if (str[0].equalsIgnoreCase("add")) {
            plugin.standing.addStanding(uuid, amount);
            plugin.messageUtils.send(player,plugin.respond.standingModified(amount, uuid));
        } else if (str[0].equalsIgnoreCase("remove")) {
            if ((plugin.standing.getStanding(uuid) - amount) < defaultStanding)
                plugin.standing.setStanding(uuid, defaultStanding);
            else
                plugin.standing.removeStanding(uuid, amount);
            plugin.messageUtils.send(player,plugin.respond.standingModified(amount, uuid));
        } else if (str[0].equalsIgnoreCase("set")) {
            plugin.standing.setStanding(uuid, Math.max(amount, defaultStanding));
            plugin.messageUtils.send(player,plugin.respond.standingModified(amount, uuid));
        }
    }
}
