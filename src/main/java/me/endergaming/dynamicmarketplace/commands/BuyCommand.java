package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BuyCommand extends BaseCommand {
    private final DynamicMarketplace plugin;

    public BuyCommand(String command, @NotNull final DynamicMarketplace instance) {
        super(command);
        this.plugin = instance;
    }

    public void run(Player player, String[] args, String cmd) {
        if (!player.hasPermission("market.command.buy")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        if (args.length == 1) {
//            PlayerInteractions.getHelp(player, cmd);
            return;
        }

        // Remove "buy" from args
        String[] str = args;
        str = String.join(" ", str).replace("buy ", "").split(" ");

        // Command actions
        if (plugin.marketData.contains(str[0], true)) {
            int amount = (str.length == 1) ? 1 : plugin.messageUtils.checkAmount(str[1], player);
            if (amount > 64) amount = 64; // Limit purchases to 1 stack
            plugin.operations.makePurchase(player, str[0], amount);
        } else {
            plugin.messageUtils.send(player, plugin.respond.itemInvalid());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.messageUtils.send(sender, plugin.respond.nonPlayer());
            return true;
        }

        if (!sender.hasPermission("market.command.buy")) {
            plugin.messageUtils.send(sender, plugin.respond.noPerms());
            return false;
        }

        if (args.length == 0) {
//            PlayerInteractions.getHelp((Player) sender, cmd.getName());
            return false;
        }

        run((Player) sender, args, cmd.getName());

        return true;
    }
}
