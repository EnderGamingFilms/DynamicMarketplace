package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import me.endergaming.dynamicmarketplace.SaveData;
import me.endergaming.dynamicmarketplace.ShopOpperations;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellHandCommand extends BaseCommand {
    private static DynamicMarketplace instance = DynamicMarketplace.getInstance();

    public SellHandCommand(String command) {
        super(command);
    }

    public static void run(Player player) {
        if (!player.hasPermission("market.sellhand")) {
            PlayerInteractions.noPermission((player));
            return;
        }

        instance.operationsManager.sellHand(player);
        SaveData.saveMarket();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            PlayerInteractions.nonPlayer(sender);
            return false;
        }
        run((Player) sender);

        return true;
    }
}
