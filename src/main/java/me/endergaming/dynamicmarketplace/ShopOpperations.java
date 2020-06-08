package me.endergaming.dynamicmarketplace;

import me.endergaming.dynamicmarketplace.gui.InventoryListener;
import me.endergaming.dynamicmarketplace.utils.PlayerInteractions;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ShopOpperations {
    private static double tax = 0.60;
    private DynamicMarketplace instance = DynamicMarketplace.getInstance();


    // Utils
    public Material getItemMaterial (String name ){
        String converted = name.replaceAll("([A-Z])", "_$1").toUpperCase();
        return Material.getMaterial( converted );
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Buy & Sell Operations
    public double collectorCalc(Inventory inv) {
        double price = 0.0;
        for (int slot : InventoryListener.Allowed) {
            if (inv.getItem(slot) != null) { // If Slot is not empty getFullPrice
                price += SaveData.getFullPrice(inv.getItem(slot).getType().toString(), inv.getItem(slot).getAmount(), true); // Get price for item stack
            }
        }

        return price;
    }

    public void setTax(double amount) {
        tax = 0.01 * amount;
    }

    public double getTax() {
        return tax;
    }

    public void collectorSell(Player player, Inventory inv){
        // Sell all items in CollectorGUI
        double price = 0.0, total = 0.0;
        for (int slot : InventoryListener.Allowed) {
            if (inv.getItem(slot) != null) { // If Slot is not empty getFullPrice
                price += SaveData.getFullPrice(inv.getItem(slot).getType().toString(), inv.getItem(slot).getAmount(), true); // Get price for item stack

                // Update market quantities
                SaveData.updateQuantities(inv.getItem(slot).getType().toString(), inv.getItem(slot).getAmount(), true);
            }
        }

        // Calculate taxed price for the player
        total = price;
//        if (player.hasPermission(CommandManager.taxPerm + "premium")) {
//            price *= 0.60;
//        } else {
//            price *= 0.60;
//        }
        price *= tax;

        price = round(price, 2); // Round price to 2 decimal places

        // Update player balance
        instance.economy.depositPlayer(player, price);

        // Send player Balance & Price messages
        player.sendMessage("\n");
        player.sendMessage(ChatColor.YELLOW + "Total Worth: " + ChatColor.GOLD + instance.economy.format(round(total, 2)));
        player.sendMessage(ChatColor.YELLOW + "You Received: " + ChatColor.GREEN + instance.economy.format(price));
        //player.sendMessage(ChatColor.YELLOW + "New Balance: " + ChatColor.GOLD + econ.format(econ.getBalance(player)));
    }

    public void buy(Player player, String validItem, int validAmount) {
        double price = SaveData.getFullPrice(validItem, validAmount, false);
        double bal = instance.economy.getBalance(player);
        Material itemMaterial = getItemMaterial(validItem.toLowerCase());
           // make sure this buy is valid
           if (bal < price) {
               PlayerInteractions.buyFailedCost(player, price, bal);
           } else if (price < 0) {
               PlayerInteractions.buyFailedQuantity(player, validItem, validAmount);
           }

           // do buy
           else {
               SaveData.updateQuantities(validItem, validAmount, false);
               instance.economy.withdrawPlayer(player, price);

               int originalAmount = validAmount;

               int stackSize = itemMaterial.getMaxStackSize();

               while (validAmount > 0) {

                   if (player.getInventory().firstEmpty() == -1) {
                       int total = originalAmount - validAmount;
                       price = SaveData.getFullPrice(validItem, total, false);
                       PlayerInteractions.buyFailedSpace(player, validItem, total, price);

                       return;
                   }
                   int stack = validAmount > stackSize ? stackSize : validAmount;
                   validAmount -= stack;
                   ItemStack items = new ItemStack(itemMaterial);
                   items.setAmount(stack);
                   player.getInventory().addItem(items);
               }
               PlayerInteractions.buySuccess(player, validItem, originalAmount, price);
           }

    }

    public void sellItems(Player player, String validItem, int validAmount, boolean sellAll) {
        Material itemMat = getItemMaterial(validItem);
        boolean removed = false;
        int sold = 0;
        int amount = validAmount;

        if (sellAll) {
            amount = 2304;
        }

        for (int total = 0; total < amount; ++total) {
            if (player.getInventory().contains(itemMat)) {
                for (int i = 0; i < (player.getInventory().getSize()); ++i) {
                    if (player.getInventory().getItem(i) == null) continue;
                    if (removed == false && player.getInventory().getItem(i).getType().toString() == itemMat.toString()) {
                        //player.sendMessage("Item was found in slot " + (i + 1));
                        int checkReturn = instance.operationsManager.itemChecks(player.getInventory().getItem(i));
                        if (checkReturn == 6) {
                            if (player.getInventory().getItem(i).getAmount() > 1) {
                                player.getInventory().getItem(i).setAmount(player.getInventory().getItem(i).getAmount() - 1);
                            } else {
                                player.getInventory().clear(i);
                            }
                            removed = true;
                            sold += 1;
                        } else continue;
                    }
                }
                removed = false;
            }
        }

        double bal = instance.economy.getBalance(player);
        double price = round(SaveData.getFullPrice( validItem, sold, true ), 2);

        SaveData.updateQuantities( validItem, sold, true );

        if (sold < validAmount) {
            PlayerInteractions.sellFailedQuantity(player, validItem, sold, price);
        } else
            PlayerInteractions.sellSuccess( player, validItem, sold, price );

        instance.economy.depositPlayer(player, price);
    }

    public void sell(Player player, String validItem, int validAmount) {

        double bal = instance.economy.getBalance( player );
        Material itemMat = getItemMaterial( validItem );


        int total = 0;

        for (ItemStack item : player.getInventory()) {
            // If the item is null/air continue
            if ( item == null ) continue;

            if ( item.getType() == itemMat && total < validAmount ) {

                if ( total + item.getAmount() > validAmount ) {
                    item.setAmount( item.getAmount() - ( validAmount - total ) );
                    total = validAmount;
                }
                else {
                    ItemStack mat = new ItemStack(Material.AIR);
                    total += item.getAmount();
                    // Removes the item in the players hand
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), mat);
                }

                if ( total >= validAmount )
                    break;

            }
        }

        double price = SaveData.getFullPrice( validItem, total, true );
        SaveData.updateQuantities( validItem, total, true );

        if ( total < validAmount )
            PlayerInteractions.sellFailedQuantity( player, validItem, total, price );
        else
            PlayerInteractions.sellSuccess( player, validItem, total, price );

        instance.economy.depositPlayer(player, price);

    }

    public void itemInfo ( Player player, String validItem ){

        double amount = SaveData.marketQuantity(validItem);
        double priceFor1_buy = SaveData.getFullPrice( validItem, 1, false);
        double priceFor1_sell = SaveData.getFullPrice( validItem, 1, true);
        double priceFor64_buy = SaveData.getFullPrice( validItem, 64, false);
        double priceFor64_sell = SaveData.getFullPrice( validItem, 64, true);

        PlayerInteractions.itemInfo(player, validItem, amount, new double[]{ priceFor1_buy, priceFor1_sell, priceFor64_buy, priceFor64_sell});

    }

    public int itemChecks(ItemStack item) {
            // Check if player is holding an item - Case 1
        if (item.getAmount() < 1) return 1;
            // Check if an item has a custom name - Case 2
        else if (item.getItemMeta().hasDisplayName()) return 2;
            // Check if an item has enchants - Case 3
        else if (!item.getEnchantments().isEmpty()) return 3;
            // Check if an item is damaged - Case 4
        else if (((Damageable) item.getItemMeta()).getDamage() != 0 ) return 4;
            // Check if an item is valid - Case 5
        else if (!SaveData.validItem(item.getType().toString().toLowerCase())) return 5;
            // If all checks were passed successfully - Case 6
        else return 6;
    }

    public void sellHand ( Player player ){
        ItemStack item = player.getInventory().getItemInHand();

        // Preform an item check
        int returnValue = itemChecks(item);
        switch(returnValue) {
            case 1:
                PlayerInteractions.noItemInHand(player);
                break;
            case 2:
                PlayerInteractions.hasName(player);;
                break;
            case 3:
                PlayerInteractions.invalidSellEnchant(player);
                break;
            case 4:
                PlayerInteractions.invalidSellDamaged(player);
                break;
            case 5:
                PlayerInteractions.itemInvalid(player, item.getType().toString().toLowerCase());
                break;
            case 6:
                sell(player, item.getType().toString().toLowerCase(), item.getAmount());
                break;
            default:
                player.sendMessage(ChatColor.RED+"[Market-Err] Item check defaulted, this shouldn't happen");
        }
    }

    public void infoHand ( Player player ){

        ItemStack item = player.getInventory().getItemInHand();
        String name = item.getType().toString().toLowerCase() ;
        if ( !SaveData.validItem(  name ) )
            PlayerInteractions.itemInvalid( player, name);
        else
            itemInfo ( player, name);
    }

    public void cost (Player player, String validItem, int validAmount ) {
        double price = SaveData.getFullPrice( validItem, validAmount, false );
        PlayerInteractions.itemCost(player, validItem, validAmount, instance.economy.format(price));
    }
}
