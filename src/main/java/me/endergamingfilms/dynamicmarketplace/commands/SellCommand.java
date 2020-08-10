package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SellCommand extends BaseCommand {
    private final DynamicMarketplace plugin;

    public SellCommand(String command, @NotNull final DynamicMarketplace instance) {
        super(command);
        this.plugin = instance;
    }

    public void run(Player player, String[] args, String cmd) {
        if (!player.hasPermission("market.command.sell")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        if (args.length == 1) {
            plugin.messageUtils.send(player, plugin.respond.getHelp(cmd));
            return;
        }

        // Remove "sell" from args
        String[] str = args;
        str = String.join(" ", str).replace("sell ", "").split(" ");

        // Command actions
        if (plugin.marketData.contains(str[0], !plugin.fileManager.debug)) {
            int amount = (str.length == 1) ? 1 : plugin.messageUtils.checkAmount(str[1], player);
            if (amount > 128) amount = 128; // Limit purchases to 2 stack
            plugin.operations.makeSale(player, str[0], amount);
        } else {
            plugin.messageUtils.send(player, plugin.respond.itemInvalid(plugin.messageUtils.capitalize(str[0])));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.messageUtils.send(sender, plugin.respond.nonPlayer());
            return false;
        }

        if (!sender.hasPermission("market.command.sell")) {
            plugin.messageUtils.send(sender, plugin.respond.noPerms());
            return false;
        }

        if (args.length == 0) {
            plugin.messageUtils.send(sender, plugin.respond.getHelp(label));
            return false;
        }

        run((Player) sender, args, label);

        return true;
    }
}
