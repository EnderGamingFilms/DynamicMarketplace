package org.dynamicmarketplace.dynamicmarketplace;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PlaceholderHook extends PlaceholderExpansion {
    private DynamicMarketplace plugin;

    public PlaceholderHook() {
    }

    public PlaceholderHook(DynamicMarketplace plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "EnderGamingFilms";
    }

    @Override
    public String getIdentifier() {
        return "market";
    }

    @Override
    public String getVersion(){
        return "1.1.5";
    }

    public class ItemWrapper {

        private boolean selling;
        private String mat;
        private int amount;
        private double price;
        private String name;

        public ItemWrapper(String material, short data, int amt) {
            this.mat = material.toUpperCase();
            this.amount = amt;
            this.price = 0.00;
            this.name = "";
            this.selling = false;
        }

        public ItemWrapper() {
        }

        public String getName() {
            return this.name;
        }

        public void setName(String newName) {
            this.name = newName;
        }

        public boolean getSelling() {
            return this.selling;
        }

        public void setSelling(boolean isSelling) {
            this.selling = isSelling;
        }

        public double getPrice() {
            return this.price;
        }

        public void setPrice(double newPrice) {
            this.price = newPrice;
        }

        public String getType() {
            return this.mat;
        }

        public void setType(Material material) {
            this.mat = material.toString();
            this.mat = this.mat.toUpperCase();
        }

        public void setType(String material) {
            this.mat = material.toUpperCase();
        }

        public int getAmount() {
            return this.amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
    private int getInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static DecimalFormat decF = new DecimalFormat("0.00");

    private ItemWrapper getItem(String input) {
        ItemWrapper wrapper = new ItemWrapper();
        String[] arrayOfString;
        int j = (arrayOfString = input.split(",")).length;
        for (int i = 0; i < j; i++) {
            String part = arrayOfString[i];

            if (part.startsWith("buyPrice_")) {
                wrapper.setSelling(false);
                part = part.replace("buyPrice_", "");
            }else if (part.startsWith("sellPrice_")) {
                wrapper.setSelling(true);
                part = part.replace("sellPrice_", "");
            } else if (part.startsWith("friendly_")) {
                part = part.replace("friendly_", "");
            } else if (part.startsWith("amount_")) {
                part = part.replace("amount_", "");
            }

            if (part.startsWith("mat:")) {
                part = part.replace("mat:", "");
                try {
                    if (getInt(part) > 0) {
                        wrapper.setType(Material.getMaterial(part));
                        continue;
                    }
                    wrapper.setType(part);
                    continue;
                } catch (Exception ex) {
                    //error check: if passed in mat is null
                    Bukkit.getServer().broadcastMessage(ChatColor.RED+"[Market-Err] Mat parser returned null, please check spelling");
                    return wrapper;
                }
            }

            if (part.startsWith("amt:")) {
                part = part.replace("amt:", "");
                wrapper.setAmount(getInt(part));
                continue;
            }
        }

        // Check to see if the item that was passed in is an actual item in-game.
        if(!SaveData.validItem(wrapper.getType())) {
            return wrapper;
        }


        // Set the price of the called item
        wrapper.setPrice(SaveData.getFullPrice(wrapper.getType(), wrapper.getAmount(), wrapper.getSelling()));

        // Format and round price.
        wrapper.setPrice(round(wrapper.getPrice(), 2));
//        System.out.println("-------->Price: " + wrapper.getPrice());

        // If Price is negative it is 0
        if (wrapper.getPrice()<0.0) {
            wrapper.setPrice(Math.abs(wrapper.getPrice()));
        }

        // Set friendly name for item
        wrapper.setName(getName(wrapper));

        return wrapper;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String capitalize(String input, String split) { // https://www.spigotmc.org/threads/how-to-get-a-user-friendly-item-name.373484/
        String output = "";
        for (String s : input.split(split)) {
            output += s.substring(0, 1).toUpperCase()+s.substring(1).toLowerCase()+" ";
        }
        return output.substring(0, output.length()-1);
    }

    public static String getName(ItemWrapper wrapper) {
        String materialName = Material.getMaterial(wrapper.getType()).getKey().getKey();
        return capitalize(materialName, "_");
    }

    public static int getMarketAmount(String validItem) {
        return (int) SaveData.marketQuantity(validItem);
    }

    @Override
    public String onPlaceholderRequest(Player player, String args){

        if (args.startsWith("buyPrice_")) {
            ItemWrapper wrapper;
            wrapper = getItem(args);
            return String.valueOf(wrapper.getPrice());
        }

        if (args.startsWith("sellPrice_")) {
            ItemWrapper wrapper;
            wrapper = getItem(args);
            return String.valueOf(wrapper.getPrice());
        }

        if (args.startsWith("friendly_")) {
            ItemWrapper wrapper;
            wrapper = getItem(args);
            return String.valueOf(wrapper.getName());
        }

        if (args.startsWith("amount_")) {
            ItemWrapper wrapper;
            wrapper = getItem(args);
            int amount = getMarketAmount(wrapper.getType());

            if (amount == -1)
                return "unknown";
            else
                return String.valueOf(amount);
        }

        if (args.equals("hand")) {
            if ((ShopOpperations.itemChecks(player, player.getItemInHand()) > 5)) {
                double price = SaveData.getFullPrice(player.getItemInHand().getType().toString(), player.getItemInHand().getAmount(), true);
                price = round(price, 2);
                return String.valueOf(price);
            }
            return "0.00";
        }

        // Return null if an invalid placeholder
        return "";
    }
}
