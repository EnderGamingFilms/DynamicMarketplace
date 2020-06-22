package me.endergaming.dynamicmarketplace.gui;

import org.jetbrains.annotations.NotNull;
import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CollectorGUI {
    private final DynamicMarketplace instance;
    public static final int fillThese[] = {0,1,2,3,4,5,6,7,8,9,15,17,18,24,25,26,27,33,35,36,37,38,39,40,41,42,43,44,45};
    public static final String borderItem = " ", cancelItem = ChatColor.RED + "Cancel", sellItem = ChatColor.GREEN + "Sell These Items";
    public static Inventory inv;
    public static String inventory_name;
    public static int rows = 9 * 5;

    public CollectorGUI(@NotNull final DynamicMarketplace instance) {
        this.instance = instance;
        initialize();
    }

    public void initialize() {
        inventory_name = ChatColor.DARK_GRAY + "The Collector";

        inv = Bukkit.createInventory(null, rows);
    }

    public Inventory GUI (Player player) {
        Inventory toReturn = Bukkit.createInventory(null, rows, inventory_name);

        placeItem(inv, 16, createItem(Material.RED_STAINED_GLASS_PANE, 1, cancelItem));
        placeItem(inv, 34, createItem(Material.GREEN_STAINED_GLASS_PANE, 1, sellItem));

        setFill(createItem(Material.GRAY_STAINED_GLASS_PANE, 1, borderItem));

        toReturn.setContents((inv.getContents()));
        return toReturn;
    }

    public void placeItem(Inventory inv, int invSlot, ItemStack item) {
        inv.setItem(invSlot, item);
    }

    public ItemStack createItem(Material material, int amount, String displayName, String... loreString) { // With Lore
        ItemStack item;
        List<String> lore = new ArrayList();

        item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        for (String s : loreString) {
                lore.add(s);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createItem(Material material, int amount, String displayName) { // With No Lore
        ItemStack item;

        item = new ItemStack(material, amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        return item;
    }

    public void setFill(ItemStack item) {
        int slot;
        for (int i = 0; i < rows; ++i) {
            for (int x = 0; x < fillThese.length; ++x) {
                slot = fillThese[x];
                if (i == slot) {
                    inv.setItem(i, item);
                }
            }
        }
    }

    public boolean getSlot(final int[] array, final int key) {
        return ArrayUtils.contains(array, key);
    }

    public void doCheck(final Inventory inv , final Player player) {
        final Collection<ItemStack> drops = new ArrayList<>(Collections.emptyList());

        for (int i=0; i<=inv.getSize(); i++) {
            if (getSlot(fillThese , i) || i == 16 || i == 34) {
                continue;
            }

            if (inv.getItem(i) != null)
                drops.add(inv.getItem(i));
        }

        if (!drops.isEmpty()) {
            final Map<Integer, ItemStack> map = player.getInventory().addItem(drops.toArray(new ItemStack[0]));

            for (final ItemStack item : map.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    // This is where selling items happens
    public void clicked(Player player, int slot, ItemStack clicked, Inventory inv) {
        String cItem = clicked.getItemMeta().getDisplayName();
        if (inv == null) { // Check if the given inventor is null - It shouldn't be
            System.out.println("clicked() - Inventory NUll");
            return;
        }
        switch (clicked.getType()) {
            case RED_STAINED_GLASS_PANE: // Cancel
                player.closeInventory();
                break;
            case GREEN_STAINED_GLASS_PANE: // Accept
                boolean empty = true; InventoryListener.realClose = true;

                // Check if the player put anything in the CollectorGUI
                for (int s : InventoryListener.Allowed) { if (inv.getItem(s) != null) { empty = false; break; } }

                if (!empty) { // There is stuff
                    instance.operations.makeSale(player, inv, instance.operations.COLLECTOR);
                } else { // There was nothing
                    instance.messageUtils.send(player, instance.respond.collectorFailed());
                }

                player.closeInventory();
                break;
            case GRAY_STAINED_GLASS_PANE: // Border
                // Do nothing here (Just an item border)
                break;
            default:
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "Item has no click case");
        }
    }
}
