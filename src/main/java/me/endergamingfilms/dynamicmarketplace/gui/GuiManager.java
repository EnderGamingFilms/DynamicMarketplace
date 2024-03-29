package me.endergamingfilms.dynamicmarketplace.gui;

import org.jetbrains.annotations.NotNull;
import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;

public class GuiManager {
    private CollectorGUI collectorGUI;
    private InventoryListener inventoryListener;

    public GuiManager(@NotNull final DynamicMarketplace instance) {
//        collectorGUI = new CollectorGUI(instance);
//        inventoryListener = new InventoryListener(instance);
    }

    public CollectorGUI getCollectorGUI() {
        return collectorGUI;
    }

    public InventoryListener getInventoryListener() {
        return inventoryListener;
    }
}
