package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import me.endergaming.dynamicmarketplace.SaveData;
import me.endergaming.dynamicmarketplace.ShopOpperations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellAllCommand extends BaseCommand{
    private static DynamicMarketplace instance = DynamicMarketplace.getInstance();
    public SellAllCommand(String command) {
        super(command);
    }

    public static void run(Player player, String[] args, String cmd) {
        if (!player.hasPermission("market.sellall")) {
            PlayerInteractions.noPermission((player));
            return;
        }

        if (args.length == 1) {
            PlayerInteractions.getHelp(player, cmd);
            return;
        }

        // Remove "sell" from args
        String[] str = args;
        str = String.join(" ", str).replace("sellall ", "").split(" ");

        // Command actions
        if (SaveData.validItem(str[0], player)) {
            instance.operationsManager.sellItems(player, str[0], 1, true);
            SaveData.saveMarket();
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

        if (!sender.hasPermission("market.sellall")) {
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
