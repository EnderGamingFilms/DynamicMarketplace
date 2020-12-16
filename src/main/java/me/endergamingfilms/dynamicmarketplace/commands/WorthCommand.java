package me.endergamingfilms.dynamicmarketplace.commands;

import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorthCommand extends BaseCommand {
    private final DynamicMarketplace plugin;

    public WorthCommand(String command, @NotNull final DynamicMarketplace instance) {
        super(command);
        this.plugin = instance;
    }

    public void run(Player player) {
        if (!player.hasPermission("market.command.worth")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        // Command Actions
        if (plugin.marketData.contains(player.getItemInHand().getType(), !plugin.fileManager.debug)) {
            plugin.operations.getWorth(player, player.getItemInHand());
        } else {
            plugin.messageUtils.send(player, plugin.respond.itemInvalid(player.getItemInHand().getType().getKey().getKey()));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.messageUtils.send(sender, plugin.respond.nonPlayer());
            return false;
        }

        if (!sender.hasPermission("market.command.worth")) {
            plugin.messageUtils.send(sender, plugin.respond.noPerms());
            return false;
        }

        run((Player) sender);

        return true;
    }
}
