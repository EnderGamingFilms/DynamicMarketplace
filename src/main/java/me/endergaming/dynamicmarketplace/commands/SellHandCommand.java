package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SellHandCommand extends BaseCommand {
    private final DynamicMarketplace plugin;

    public SellHandCommand(String command, @NotNull final DynamicMarketplace instance) {
        super(command);
        this.plugin = instance;
    }

    public void run(Player player) {
        if (!player.hasPermission("market.command.sellhand")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        // Command Actions
        if (plugin.marketData.contains(player.getItemInHand().getType(), !plugin.fileManager.debug)) {
            plugin.operations.makeSale(player, player.getItemInHand().getType().toString(), player.getItemInHand().getAmount());
        } else {
            plugin.messageUtils.send(player, plugin.respond.itemInvalid());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.messageUtils.send(sender, plugin.respond.nonPlayer());
            return false;
        }

        if (!sender.hasPermission("market.command.sellhand")) {
            plugin.messageUtils.send(sender, plugin.respond.noPerms());
            return false;
        }

        run((Player) sender);

        return true;
    }
}
