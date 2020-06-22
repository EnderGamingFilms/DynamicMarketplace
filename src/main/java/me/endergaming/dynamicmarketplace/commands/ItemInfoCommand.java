package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemInfoCommand extends BaseCommand {
    private final DynamicMarketplace plugin;
    public ItemInfoCommand(String command, @NotNull final DynamicMarketplace instance) {
        super(command);
        this.plugin = instance;
    }

    public void run(Player player, String[] args, String cmd) {
//        if (!player.hasPermission("market.info")) {
//            PlayerInteractions.noPermission((player));
//            return;
//        }
//
//        if (args.length == 1) {
//            instance.operationsManager.infoHand(player);
//            return;
//        }
//
//        // Remove "info" from args
//        String[] str = args;
//        switch (str[0]) {
//            case "info":
//                str = String.join(" ", str).replace("info ", "").split(" ");
//                break;
//            case "item":
//                str = String.join(" ", str).replace("item ", "").split(" ");
//                break;
//            case "iteminfo":
//                str = String.join(" ", str).replace("iteminfo ", "").split(" ");
//                break;
//        }
//
//
//
//        // Command actions
//        if (SaveData.validItem(str[0], player)) {
//            instance.operationsManager.itemInfo(player, str[0]);
//            SaveData.saveMarket();
//        } else {
//            PlayerInteractions.itemInvalid(player, str[0]);
//        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//        if (!(sender instanceof Player)) {
//            PlayerInteractions.nonPlayer(sender);
//            return false;
//        }
//
//        if (!sender.hasPermission("market.info")) {
//            PlayerInteractions.noPermission((Player) sender);
//            return false;
//        }
//
//        if (args.length == 0) {
//            instance.operationsManager.infoHand((Player) sender);
//        } else {
//            // Command actions
//            if (SaveData.validItem(args[0], (Player) sender)) {
//                instance.operationsManager.itemInfo((Player) sender, args[0]);
//                SaveData.saveMarket();
//            } else {
//                PlayerInteractions.itemInvalid((Player) sender, args[0]);
//            }
//        }

        return true;
    }
}
