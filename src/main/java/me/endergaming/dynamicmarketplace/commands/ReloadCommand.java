package me.endergaming.dynamicmarketplace.commands;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import me.endergaming.dynamicmarketplace.SaveData;
import org.bukkit.entity.Player;

public class ReloadCommand {
    private static DynamicMarketplace plugin = DynamicMarketplace.getInstance();

    public static void run(Player player) {
        if (!player.hasPermission("market.reload")) {
            PlayerInteractions.noPermission((player));
            return;
        }

        // Reload Actions
        long start = System.currentTimeMillis();
        plugin.fileManager.saveMaterialData();
//        SaveData.reloadALL();
        plugin.fileManager.loadAll();
        long end = System.currentTimeMillis();
        player.sendMessage(plugin.respond.pluginReload(end-start));
    }
}
