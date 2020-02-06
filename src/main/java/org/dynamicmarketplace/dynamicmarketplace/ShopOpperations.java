package org.dynamicmarketplace.dynamicmarketplace;

import com.sun.org.apache.bcel.internal.generic.SWITCH;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class ShopOpperations {

    private static Economy econ = DynamicMarketplace.economy;

    // Utils

    public static Material getItemMaterial (String name ){
        String converted = name.replaceAll("([A-Z])", "_$1").toUpperCase();
        return Material.getMaterial( converted );
    }

    public static void buy (Player player, String validItem, int validAmount ) {
        double price = SaveData.getFullPrice( validItem, validAmount, false );
        double bal = econ.getBalance( player );
        Material itemMaterial = getItemMaterial( validItem.toLowerCase() );
           // make sure this buy is valid
           if (bal < price) {
               PlayerInteractions.buyFailedCost(player, price, bal);
           } else if (price < 0) {
               PlayerInteractions.buyFailedQuantity(player, validItem, validAmount);
           }

           // do buy
           else {
               SaveData.updateQuantities(validItem, validAmount, false);
               econ.withdrawPlayer(player, price);

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

    public static void sellItems(Player player, String validItem, int validAmount, boolean sellAll) {
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
                        int checkReturn = ShopOpperations.itemChecks(player, player.getInventory().getItem(i));
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

        double bal = econ.getBalance(player);
        double price = SaveData.getFullPrice( validItem, sold, true );

        SaveData.updateQuantities( validItem, sold, true );

        if (sold < validAmount) {
            PlayerInteractions.sellFailedQuantity(player, validItem, sold, price);
        } else
            PlayerInteractions.sellSuccess( player, validItem, sold, price );

        econ.depositPlayer(player, price);
    }

    public static void sell(Player player, String validItem, int validAmount) {

        double bal = econ.getBalance( player );
        Material itemMat = getItemMaterial( validItem );


        int total = 0;

        for (ItemStack item : player.getInventory()) {
            // If the item is null/air continue
            System.out.println("------->item:"+item);
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

        econ.depositPlayer(player, price);

    }

    public static void itemInfo ( Player player, String validItem ){

        double amount = SaveData.marketQuantity(validItem);
        double priceFor1_buy = SaveData.getFullPrice( validItem, 1, false);
        double priceFor1_sell = SaveData.getFullPrice( validItem, 1, true);
        double priceFor64_buy = SaveData.getFullPrice( validItem, 64, false);
        double priceFor64_sell = SaveData.getFullPrice( validItem, 64, true);

        PlayerInteractions.itemInfo(player, validItem, amount, new double[]{ priceFor1_buy, priceFor1_sell, priceFor64_buy, priceFor64_sell});

    }

    public static int itemChecks(Player player, ItemStack item) {
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

    public static void sellHand ( Player player ){
        ItemStack item = player.getInventory().getItemInHand();

        // Preform an item check
        int returnValue = itemChecks(player, item);
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

    public static void infoHand ( Player player ){

        ItemStack item = player.getInventory().getItemInHand();
        String name = item.getType().toString().toLowerCase() ;
        if ( !SaveData.validItem(  name ) )
            PlayerInteractions.itemInvalid( player, name);
        else
            itemInfo ( player, name);
    }

    public static void cost (Player player, String validItem, int validAmount ) {
        double price = SaveData.getFullPrice( validItem, validAmount, false );
        PlayerInteractions.itemCost(player, validItem, validAmount, price );
    }
}
