package me.endergaming.dynamicmarketplace.gui;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class InventoryListener implements Listener {
    private final DynamicMarketplace instance;
    public static HashMap<Integer, Boolean> slotHashMap = new HashMap<>();
    public static boolean realClose = false;
    public static final int[] Allowed = {10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 28, 29, 30, 31, 32};

    public InventoryListener(@NotNull final DynamicMarketplace instance) {
        this.instance = instance;
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    private static void init() {
        for (int s : Allowed) { // Fills HashMap
            slotHashMap.put(s, true);
        }
        realClose = false; // Reset realClose to false on player GUI open.
    }

    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getView().getTitle().equals(CollectorGUI.inventory_name)) { // Check GUI inventory name & if clicked outside
            if (!slotHashMap.containsKey(event.getSlot())) { // Click area outside allowed space
                if (event.getCurrentItem() == null) { /*System.out.println("getCurrentItem = null");*/
                    return;
                } // If clicked slot is null escape
                if (event.getClickedInventory().getType() != InventoryType.PLAYER) { // Handlers for GUI inventory clicks
                    event.setCancelled(true);
                    instance.collectorGUI.clicked((Player) event.getWhoClicked(), event.getSlot(), event.getCurrentItem(), event.getInventory());
                } else { // Handlers for player inventory clicks
                    if (!instance.operations.itemChecks((Player) event.getWhoClicked(), event.getCurrentItem())) { // If item passes all the checks continue
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInvOpen(InventoryOpenEvent event) {
        if (event.getView().getTitle().equals(CollectorGUI.inventory_name)) {
            init();
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (event.getPlayer().getOpenInventory() instanceof PlayerInventory)
            return;

        if (event.getPlayer().getOpenInventory().getTitle().equals(CollectorGUI.inventory_name)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        final Player player = (Player) event.getEntity();

        if (player.getOpenInventory() instanceof PlayerInventory)
            return;

        if (player.getOpenInventory().getTitle().equals(CollectorGUI.inventory_name)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(CollectorGUI.inventory_name)) {
            if (!realClose) {
                instance.collectorGUI.doCheck(event.getInventory(), (Player) event.getPlayer());
                instance.messageUtils.send((Player) event.getPlayer(), instance.respond.collectorFailed());
            }
        }
    }
}
