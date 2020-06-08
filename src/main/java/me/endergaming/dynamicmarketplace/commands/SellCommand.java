package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import me.endergaming.dynamicmarketplace.SaveData;
import me.endergaming.dynamicmarketplace.ShopOpperations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellCommand extends BaseCommand{
    private static DynamicMarketplace instance = DynamicMarketplace.getInstance();
    public SellCommand(String command) {
        super(command);
    }

    public static void run(Player player, String[] args, String cmd) {
        if (!player.hasPermission("market.sell")) {
            PlayerInteractions.noPermission((player));
            return;
        }

        if (args.length == 1) {
            PlayerInteractions.getHelp(player, cmd);
            return;
        }

        // Remove "sell" from args
        String[] str = args;
        str = String.join(" ", str).replace("sell ", "").split(" ");

        // Command actions
        if (SaveData.validItem(str[0], player)) {
            int amount = (str.length == 1) ? 1 : instance.messageUtils.checkAmount(str[1], player);
            if (amount > 0) {
                instance.operationsManager.sellItems(player, str[0], amount, false);
                SaveData.saveMarket();
            }
        } else {
            PlayerInteractions.itemInvalid(player, str[0]);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            PlayerInteractions.nonPlayer(sender);
            return false;
        }

        if (!sender.hasPermission("market.sell")) {
            PlayerInteractions.noPermission((Player) sender);
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(cmd.getUsage()); return false;
        }
        run((Player) sender, args, cmd.getName());

        return true;
    }
}
