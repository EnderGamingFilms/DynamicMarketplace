package me.endergaming.dynamicmarketplace.utils;

import org.jetbrains.annotations.NotNull;
import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class Responses {
    public static final boolean BOOLEAN = false;
    public static final int INT = 0;
    public static final double DOUBLE = 0.0;
    public static final String[] LIST = new String[0];
    private final DynamicMarketplace plugin;

    public Responses(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    /** |-------------- Basic Responses --------------| */
    public String noPerms() {
        return plugin.messageUtils.getFormattedMessage("no-permission.message", plugin.fileManager
                .getMessages().getBoolean("no-permission.prefix"));
    }

    public String pluginReload(final long time) {
        return plugin.messageUtils.format("&dAll config files have been reloaded. &7(" + time + "ms)");
    }

    public String nonPlayer() {
        return plugin.messageUtils.getFormattedMessage("non-player");
    }

    public String itemIsNamed() {
        return plugin.messageUtils.getFormattedMessage("item-errors.named");
    }

    public String itemIsEnchanted() {
        return plugin.messageUtils.getFormattedMessage("item-errors.enchanted");
    }

    public String itemIsDamaged() {
        return plugin.messageUtils.getFormattedMessage("item-errors.damaged");
    }

    public String itemInvalid() {
        return plugin.messageUtils.getFormattedMessage("item-errors.invalid");
    }

    public String holdingNothing() {
        return plugin.messageUtils.getFormattedMessage("item-errors.holding-air");
    }

    public String invalidInput() {
        return plugin.messageUtils.getFormattedMessage("invalid-input");
    }

    public String itemCost(String item, final int amount, final String price) {
        return plugin.messageUtils.getFormattedMessage("item-cost", item, amount, price);
    }
    //------------------------------------------

    /** |-------------- Buy Responses --------------| */
    public String buyFailedCost(final String cost, final String balance) {
        return plugin.messageUtils.getFormattedMessage("buy.fail-cost", cost, balance);
    }

    public String buyFailedAmount(String item, final int amount) {
            return plugin.messageUtils.getFormattedMessage("buy.fail-amount", item, amount);
    }

    public String buyFailedSpace(String item, final int amount, final String sale) {
        return plugin.messageUtils.getFormattedMessage("buy.fail-space", item, amount, sale);
    }

    public String buySuccess(String item, final int amount, final String sale) {
        return plugin.messageUtils.getFormattedMessage("buy.success", item, amount, sale);
    }
    //------------------------------------------

    /** |-------------- Sell Responses --------------| */
    public String sellFailedAmount(String item, final int amount) {
        return plugin.messageUtils.getFormattedMessage("sell.fail-amount", item, amount);
    }

    public String sellSuccess(String item, final int amount, final String sale) {
        return plugin.messageUtils.getFormattedMessage("sell.success", item, amount, sale);
    }
    //------------------------------------------

    /** |-------------- The Collector Responses --------------| */
    public TextComponent collectorSuccess(final int profits, String total, String adjusted) {
        TextComponent message = new TextComponent(plugin.messageUtils
                .getFormattedMessage("collector.success", "collector.name", true)
                + MessageUtils.SPACE + collectorTax(profits));

        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.messageUtils
                .colorize(collectorTotal(total) + MessageUtils.NL + collectorAdjTotal(adjusted))).create()));

        return message;
    }

    public String collectorFailed() {
        return plugin.messageUtils.getFormattedMessage("collector.failed");
    }

    public String collectorInvalidPlayer() {
        return plugin.messageUtils.getFormattedMessage("collector.invalid-player");
    }

    public String collectorTotal(String total) {
        return  plugin.messageUtils.getFormattedMessage("collector.total", total, false, true);
    }

    public String collectorAdjTotal(String adjusted) {
        return  plugin.messageUtils.getFormattedMessage("collector.adjusted", adjusted, false, true);
    }

    public String collectorTax(final int tax) {
        return plugin.messageUtils.getFormattedMessage("collector.profit", tax, false);
    }

    public void test(Player player) {
        Player p = player;
        if (plugin.messageUtils.grabConfig("Debug", BOOLEAN)) {
            p.sendMessage(collectorInvalidPlayer());
            p.spigot().sendMessage(collectorSuccess(77, "$300.0", "$290.0"));
            p.sendMessage(collectorFailed());
            p.sendMessage(sellSuccess("Diamond", 13, "$512.23"));
            p.sendMessage(sellFailedAmount("Melon", 17));
            p.sendMessage(buySuccess("Stick", 7, "$14.13"));
            p.sendMessage(buyFailedAmount("White Wool", 4));
            p.sendMessage(buyFailedCost("$42.13", "$4.12"));
            p.sendMessage(buyFailedSpace("Iron Ingot", 64, "$701.76"));
            p.sendMessage(itemCost("Apple", 4, "$12.10"));
            p.sendMessage(holdingNothing());
            p.sendMessage(itemInvalid());
            p.sendMessage(itemIsDamaged());
            p.sendMessage(itemIsEnchanted());
            p.sendMessage(itemIsNamed());
            p.sendMessage(nonPlayer());
            p.sendMessage(noPerms());
            p.sendMessage(pluginReload(217));
        }
    }
    //------------------------------------------
}
