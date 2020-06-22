package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
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

    public void run(Player player, String[] args) {
        if (!player.hasPermission("market.command.worth")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        if (args.length > 0) {
//            PlayerInteractions.getHelp(player, args[0]);
            return;
        }

        // Command Actions
        if (plugin.marketData.contains(player.getItemInHand().getType(), true)) {
            plugin.operations.getWorth(player, player.getItemInHand());
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

        if (!sender.hasPermission("market.command.worth")) {
            plugin.messageUtils.send(sender, plugin.respond.noPerms());
            return false;
        }

        if (args.length > 0) {
//            PlayerInteractions.getHelp((Player) sender, cmd.getName());
            return false;
        }

        run((Player) sender, args);

        return true;
    }
}