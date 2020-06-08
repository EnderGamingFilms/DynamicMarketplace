package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import me.endergaming.dynamicmarketplace.SaveData;
import org.bukkit.entity.Player;

public class ReloadCommand {
    private static DynamicMarketplace instance = DynamicMarketplace.getInstance();

    public static void run(Player player) {
        long start = System.currentTimeMillis();
        if (!player.hasPermission("market.reload")) {
            PlayerInteractions.noPermission((player));
            return;
        }
        SaveData.reloadALL();
        instance.fileManager.reloadAll();
        long end = System.currentTimeMillis();
        player.sendMessage(instance.respond.pluginReload(end-start));
    }
}
