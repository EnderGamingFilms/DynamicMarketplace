package me.endergamingfilms.dynamicmarketplace.commands;

import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand {
    private final DynamicMarketplace plugin;

    public ReloadCommand(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    public void run(Player player) {
        if (!player.hasPermission("market.reload")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        // Reload Actions
        long start = System.currentTimeMillis();
        plugin.fileManager.saveMaterialData(); // Save materials.yml before load
        plugin.fileManager.loadAll();
        long end = System.currentTimeMillis();
        // Send Response
        plugin.messageUtils.send(player, plugin.respond.pluginReload(end - start));
    }

    public void load(Player player) {
        if (!player.hasPermission("market.reload")) {
            plugin.messageUtils.send(player, plugin.respond.noPerms());
            return;
        }

        long start = System.currentTimeMillis();
        plugin.fileManager.loadAll();
        long end = System.currentTimeMillis();
        // Send Response
        plugin.messageUtils.send(player, plugin.respond.pluginReload(end - start));
    }
}
