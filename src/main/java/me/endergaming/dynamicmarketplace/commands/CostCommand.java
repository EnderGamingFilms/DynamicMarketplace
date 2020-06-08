package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import me.endergaming.dynamicmarketplace.SaveData;
import me.endergaming.dynamicmarketplace.ShopOpperations;
import org.bukkit.entity.Player;

public class CostCommand {
    private static DynamicMarketplace instance = DynamicMarketplace.getInstance();
    public static void run(Player player, String[] args) {
        if (!player.hasPermission("market.cost")) {
            PlayerInteractions.noPermission(player);
            return;
        }

        if (args.length == 1) {
            PlayerInteractions.getHelp(player, args[0]);
            return;
        }
        // Remove "cost" from args
        String[] str = args;
        str = String.join(" ", str).replace("cost ", "").split(" ");

        // Command actions
        if (SaveData.validItem(str[0], player)) {
            int amount = (str.length == 1) ? 1 : instance.messageUtils.checkAmount(str[1], player);
            if (amount > 0) {
                instance.operationsManager.cost(player, str[0], amount);
                SaveData.saveMarket();
            }
        } else {
            PlayerInteractions.itemInvalid(player, str[0]);
        }
    }
}
