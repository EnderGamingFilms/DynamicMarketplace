package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand extends BaseCommand {
    private static DynamicMarketplace instance = DynamicMarketplace.getInstance();

    public BuyCommand(String command) {
        super(command);
    }

    public static void run(Player player, String[] args, String cmd) {
        if (!player.hasPermission("market.buy")) {
            PlayerInteractions.noPermission((player));
            return;
        }

        if (args.length == 1) {
            PlayerInteractions.getHelp(player, cmd);
            return;
        }

        // Remove "buy" from args
        String[] str = args;
        str = String.join(" ", str).replace("buy ", "").split(" ");

        // Command actions
        if (instance.marketData.contains(str[0], true)) {
            int amount = (str.length == 1) ? 1 : instance.messageUtils.checkAmount(str[1], player);
            if (amount > 64) amount = 64; // Limit purchases to 1 stack
            instance.operations.makePurchase(player, str[0], amount);
        } else {
            instance.messageUtils.send(player, instance.respond.itemInvalid());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            PlayerInteractions.nonPlayer(sender);
            return true;
        }

        if (!sender.hasPermission("market.buy")) {
            PlayerInteractions.noPermission((Player) sender);
            return false;
        }

        if (args.length == 0) {
            PlayerInteractions.getHelp((Player) sender, cmd.getName());
            return false;
        }

        run((Player) sender, args, cmd.getName());

        return true;
    }
}
