package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SellAllCommand extends BaseCommand {
    private final DynamicMarketplace plugin;

    public SellAllCommand(String command, @NotNull final DynamicMarketplace instance) {
        super(command);
        this.plugin = instance;
    }

    public void run(Player player) {
        if (!player.hasPermission("market.command.sellall")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        // Command Actions
        if (plugin.marketData.contains(player.getItemInHand().getType(), true)) {
            plugin.operations.makeSale(player, player.getInventory(), plugin.operations.COMMAND);
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

        if (!sender.hasPermission("market.command.sellall")) {
            plugin.messageUtils.send(sender, plugin.respond.noPerms());
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(cmd.getUsage());
            return false;
        }
        run((Player) sender);

        return true;
    }
}
