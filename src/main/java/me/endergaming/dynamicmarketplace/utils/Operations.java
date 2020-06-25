package me.endergaming.dynamicmarketplace.utils;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Operations {
    private final DynamicMarketplace plugin;
    public final String COLLECTOR = "collector";
    public final String COMMAND = "command";

    public Operations(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    public boolean makePurchase(Player player, String item, int amount) {
        MarketData.MarketItem marketItem = plugin.marketData.getItem(item, !plugin.fileManager.debug);
        int purchased = 0;
        double balance = plugin.economy.getBalance(player);

        // Check if player has enough money to make this purchase
        if (balance < marketItem.getBuyPrice(amount)) {
            // The player has insufficient funds
            plugin.messageUtils.send(player, plugin.respond.buyFailedCost(getFormatPrice(marketItem.getBuyPrice(amount)), String.valueOf(balance)));
        } else if (marketItem.getAmount() != -1 && marketItem.getAmount() < amount) {
            // There are not enough items in the market to sell to the player
            plugin.messageUtils.send(player, plugin.respond.buyFailedAmount(marketItem.getFriendly(), amount));
        } else {
            int oldAmount = amount;
            // Everything is valid at this point so make marketItem
            ItemStack itemStack = new ItemStack(marketItem.getMaterial());
            // If the purchase is valid, continue with purchase
            while (amount > 0) {
                if (player.getInventory().firstEmpty() == -1) {
                    // TODO: Implement way to buy item when inventory is full
                    break;
                } else { // If inventory is not full
                    // set size to the purchase amount or to the max item stackSize
                    int size = Math.min(amount, itemStack.getType().getMaxStackSize());

                    itemStack.setAmount(size);

                    // Update tacking values
                    amount -= size;
                    purchased += size;

                    // Add items to inventory
                    player.getInventory().addItem(itemStack);
                }
            }
            // Calculate price
            double price = marketItem.getBuyPrice(purchased);
            // Update player economy balance
            plugin.economy.withdrawPlayer(player, price);
            // Send success message or fail message
            if (purchased == 0) {
                plugin.messageUtils.send(player, plugin.respond.buyFailedSpace(marketItem.getFriendly(), oldAmount));
            } else {
                plugin.messageUtils.send(player, plugin.respond.buySuccess(marketItem.getFriendly(), purchased, getFormatPrice(price)));
            }
            // Update Market Items
            if (marketItem.getAmount() != -1) { // Update basic items
                marketItem.setAmount(marketItem.getAmount() - purchased);
            } else { // Update crafted items
                // Update base item amounts from items with recipes
                if (marketItem.hasRecipe()) {
                    updateFromRecipe(marketItem.getRecipe(), Math.min(purchased, oldAmount), false);
                }
            }
            plugin.fileManager.calcItemData();
            return true;
        }
        return false;
    }

    public boolean makeSale(Player player, String item, int amount) {
        MarketData.MarketItem marketItem = plugin.marketData.getItem(item, !plugin.fileManager.debug);
        // Find total amount of item in the inventory
        int found = 0;
        for (ItemStack stack : player.getInventory()) {
            if (stack == null) continue;
            if (stack.getType() == marketItem.getMaterial()) {
                if (itemChecks(stack)) {
                    found += stack.getAmount();
                }
            }
        }
        // Calculate price
        double price = marketItem.getSellPrice(Math.min(found, amount));
        // Update player economy balance
        plugin.economy.depositPlayer(player, price);
        // Remove items from the inventory
        removeFromInventory(player.getInventory(), player.getInventory().getContents(), marketItem.getMaterial(), Math.min(found, amount));
        // Update Market Items
        if (marketItem.getAmount() != -1) { // Update basic items
            marketItem.setAmount(marketItem.getAmount() + Math.min(found, amount));
        } else { // Update crafted items
            // Update base item amounts from items with recipes
            if (marketItem.hasRecipe()) {
                updateFromRecipe(marketItem.getRecipe(), Math.min(found, amount), true);
            }
        }
        plugin.fileManager.calcItemData();
        // Send success message or fail message
        if (found < amount) {
            plugin.messageUtils.send(player, plugin.respond.sellFailedAmount(marketItem.getFriendly(), found));
        } else {
            plugin.messageUtils.send(player, plugin.respond.sellSuccess(marketItem.getFriendly(), amount, getFormatPrice(price)));
        }
        return true;
    }

    // This is used to sell the entire passed in inventory
    public void makeSale(Player player, Inventory inventory, String actionSet) {
        if (actionSet.equals(COLLECTOR)) {
            List<ItemStack> itemStackList = new ArrayList<>();
            double totalPrice = 0.0;
            int found = 0;
            for (ItemStack stack : player.getOpenInventory().getTopInventory().getContents()) {
                if (stack == null) continue;

                if (itemChecks(stack)) {
                    totalPrice += plugin.marketData.getItem(stack.getType(), !plugin.fileManager.debug).getSellPrice(stack.getAmount());
                    // Remove items from the inventory
                    removeFromInventory(inventory, player.getOpenInventory().getTopInventory().getContents(), stack.getType(), stack.getAmount());
                    // Track Items Being Found
                    found += 1;
                    itemStackList.add(stack);
                }
            }
            if (plugin.fileManager.debug) { // Debug for selling items in custom GUI (collector)
                System.out.println("Items Found: " + found);
                System.out.println("Total Price: " + getFormatPrice(totalPrice));
                System.out.println("Items Sold: " + Arrays.toString(itemStackList.toArray()));
            }
            // Update Market Items
            MarketData.MarketItem marketItem;
            for (ItemStack stack : itemStackList) {
                marketItem = plugin.marketData.getItem(stack);
                if (marketItem.getAmount() != -1) { // Update basic items
                    marketItem.setAmount(marketItem.getAmount() + stack.getAmount());
                } else { // Update crafted items
                    // Update base item amounts from items with recipes
                    if (marketItem.hasRecipe()) {
                        updateFromRecipe(marketItem.getRecipe(), stack.getAmount(), true);
                    }
                }
            }
            plugin.fileManager.calcItemData();
            // Profits calculations
            double profits;
            if (plugin.fileManager.collectorHasStanding) { // Check if "The Collector" standing system is enabled
                profits = (plugin.standing.getStanding(player.getUniqueId()) / 100.0) + plugin.fileManager.collectorDefTax;
            } else {
                profits = plugin.fileManager.collectorDefTax;
            }
            // Economy Actions
            profits = MarketData.round(profits, 2);
            plugin.messageUtils.send(player, plugin.respond.collectorSuccess(profits, getFormatPrice(totalPrice), getFormatPrice(totalPrice * (profits / 100))));
            totalPrice = MarketData.round(totalPrice * (profits / 100), 2);
            plugin.economy.depositPlayer(player, totalPrice);
        } else if (actionSet.equals(COMMAND)) {
            double totalPrice = 0.0;
            int found = 0;
            Material item = plugin.cmdManager.sellAllCmd.getItemToSell();
            MarketData.MarketItem marketItem = plugin.marketData.getItem(item);
            for (ItemStack stack : player.getInventory()) {
                if (stack == null) continue;

                if (stack.getType() == item) {
                    if (itemChecks(stack)) {
                        totalPrice += plugin.marketData.getItem(stack.getType(), !plugin.fileManager.debug).getSellPrice(stack.getAmount());
                        // Remove items from the inventory
                        removeFromInventory(inventory, inventory.getContents(), stack.getType(), stack.getAmount());
                        // Track Items Being Found
                        found += stack.getAmount();
                    }
                }
            }
            if (found == 0) {
                plugin.messageUtils.send(player, plugin.respond.holdingNothing());
            } else {
                // Update Market Items
                if (marketItem.getAmount() != -1) { // Update basic items
                    marketItem.setAmount(marketItem.getAmount() + found);
                } else { // Update crafted items
                    // Update base item amounts from items with recipes
                    if (marketItem.hasRecipe()) {
                        updateFromRecipe(marketItem.getRecipe(), found, true);
                    }
                }
                plugin.fileManager.calcItemData();
                // Deposit Profits & Send Messages
                plugin.economy.depositPlayer(player, totalPrice);
                plugin.messageUtils.send(player, plugin.respond.sellSuccess(plugin.marketData.getItem(item, true).getFriendly(), found, getFormatPrice(totalPrice)));
            }
        }
    }

    private void updateFromRecipe(String recipe, int purchaseAmount, boolean selling) {
        String returned = parseRecipe(recipe);
        if (!returned.equals("air")) {
            Material material = plugin.marketData.getItem(returned).getMaterial();
            if (selling) {
                plugin.marketData.getItem(material, false).setAmount(plugin.marketData.getItem(material, false).getAmount() + purchaseAmount);
            } else {
                plugin.marketData.getItem(material, false).setAmount(plugin.marketData.getItem(material, false).getAmount() - purchaseAmount);
            }
        }
    }

    private String parseRecipe(String recipe) {
        String[] splitString = new String[0];
        if (recipe.contains(",")) { // If recipe has multiple parts and needs to be parsed
            String[] parts = recipe.split(",");
            for (int i = 0; i < parts.length; ++i) {//String s : parts) {
                splitString = recipe.trim().split(" ");
                if (!plugin.marketData.getItem(splitString[0]).hasRecipe()) {
                    return splitString[0];
                } else {
                    splitString[0] = parseRecipe(plugin.marketData.getItem(splitString[0]).getRecipe());
                }
            }
        } else { // If the recipe consists of only a single item
            splitString = recipe.trim().split(" ");
            if (!plugin.marketData.getItem(splitString[0]).hasRecipe()) {
                return splitString[0];
            } else {
                splitString[0] = parseRecipe(plugin.marketData.getItem(splitString[0]).getRecipe());
            }
        }
        return splitString[0];
    }

    public String getFormatPrice(double price) {
        return plugin.economy.format(price);
    }

    public void removeFromInventory(Inventory inventory, ItemStack[] contents, Material item, int amount) {
        int itemsCounted = 0;
        // Go through the entire inventory looking for item
        for (ItemStack stack : contents) {
            if (stack == null) continue; // Skip everything if itemStack == null
            // Check if this is the item we are looking for and if it is damaged/named/etc..
            if (stack.getType() == item && itemChecks(stack)) {
                if (itemsCounted + stack.getAmount() > amount) {
                    stack.setAmount(stack.getAmount() - (amount - itemsCounted));
                    itemsCounted = amount;
                } else {
                    itemsCounted += stack.getAmount();
                    inventory.removeItem(stack);
                }
            }
            if (itemsCounted >= amount) break; // leave once amount it reached/exceeded
        }
    }

    public boolean itemChecks(ItemStack item) {
        // Check if player is holding an item - Case 1
        if (item.getAmount() < 1) {
            return false;
        }
        // Check if an item has a custom name/lore - Case 2
        else if (item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore()) {
            return false;
        }
        // Check if an item has enchants - Case 3
        else if (!item.getEnchantments().isEmpty()) {
            return false;
        }
        // Check if an item is damaged - Case 4
        else if (((Damageable) item.getItemMeta()).getDamage() != 0) {
            return false;
        }
        // Check if an item is valid - Case 5
        else if (!plugin.marketData.contains(item.getType(), !plugin.fileManager.debug)) {
            return false;
        }
        // If all checks were passed successfully - Case 6
        else return true;
    }

    public boolean itemChecks(Player player, ItemStack item) {
        // Check if player is holding an item - Case 1
        if (item.getAmount() < 1) {
            plugin.messageUtils.send(player, plugin.respond.holdingNothing());
            return false;
        }
        // Check if an item has a custom name/lore - Case 2
        else if (item.getItemMeta().hasDisplayName() || item.getItemMeta().hasLore()) {
            plugin.messageUtils.send(player, plugin.respond.itemIsCustom());
            return false;
        }
        // Check if an item has enchants - Case 3
        else if (!item.getEnchantments().isEmpty()) {
            plugin.messageUtils.send(player, plugin.respond.itemIsEnchanted());
            return false;
        }
        // Check if an item is damaged - Case 4
        else if (((Damageable) item.getItemMeta()).getDamage() != 0) {
            plugin.messageUtils.send(player, plugin.respond.itemIsDamaged());
            return false;
        }
        // Check if an item is valid - Case 5
        else if (!plugin.marketData.contains(item.getType(), !plugin.fileManager.debug)) {
            plugin.messageUtils.send(player, plugin.respond.itemInvalid(plugin.messageUtils.capitalize(item.getType().getKey().getKey())));
            return false;
        }
        // If all checks were passed successfully - Case 6
        else return true;
    }

    public void getWorth(Player player, ItemStack item) {
        double price = plugin.marketData.getItem(item).getSellPrice(item.getAmount());
        final String itemName = plugin.marketData.getItem(item).getFriendly();
        final String message = plugin.respond.itemWorth(itemName, item.getAmount(), getFormatPrice(price));
        plugin.messageUtils.send(player, message);
    }
}
