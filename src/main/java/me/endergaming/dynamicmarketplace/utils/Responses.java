package me.endergaming.dynamicmarketplace.utils;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.xml.soap.Text;

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

    public String itemWorth(String item, final int amount, final String price) {
        return plugin.messageUtils.getFormattedMessage("item-worth", item, amount, price);
    }

    public String genMissingFile(boolean passed) {
        if (passed) {
            return plugin.messageUtils.format("&eMissing materials file &7(unresolved.yml)&e was successfully created!");
        }
        return plugin.messageUtils.format("&cThere was an error creating missing materials file &7(unresolved.yml)");
    }

    public TextComponent itemInfo(String item) {
        TextComponent message = new TextComponent();
        message.addExtra(plugin.messageUtils.colorize("&e---------- &6Market &e----------" + MessageUtils.NL));
        message.addExtra(plugin.messageUtils.colorize("&fItem: &b" + plugin.marketData.getItem(item).getFriendly() + MessageUtils.NL));
        if (plugin.marketData.getItem(item).hasRecipe()) {
            message.addExtra(plugin.messageUtils.colorize("&f*This is a crafted item." + MessageUtils.NL));
        } else {
            message.addExtra(plugin.messageUtils.colorize("&fQuantity: &b~" +
                    plugin.marketData.getItem(item).getAmount()) + MessageUtils.NL);
        }
        message.addExtra(plugin.messageUtils.colorize("&fBuy  : &a" +
                plugin.economy.format(plugin.marketData.getItem(item).getBuyPrice(1)) + "&f each, &a" +
                plugin.economy.format(plugin.marketData.getItem(item).getBuyPrice(64)) + "&f for 64" + MessageUtils.NL));
        message.addExtra(plugin.messageUtils.colorize("&fSell  : &a" +
                plugin.economy.format(plugin.marketData.getItem(item).getSellPrice(1)) + "&f each, &a" +
                plugin.economy.format(plugin.marketData.getItem(item).getSellPrice(64)) + "&f for 64" + MessageUtils.NL));
        message.addExtra(plugin.messageUtils.colorize("&e---------------------------"));

        return message;
    }

    //------------------------------------------

    /** |-------------- Buy Responses --------------| */
    public String buyFailedCost(final String cost, final String balance) {
        return plugin.messageUtils.getFormattedMessage("buy.fail-cost", cost, balance);
    }

    public String buyFailedAmount(String item, final int amount) {
            return plugin.messageUtils.getFormattedMessage("buy.fail-amount", item, amount);
    }

    public String buyFailedSpace(String item, final int amount) {
        return plugin.messageUtils.getFormattedMessage("buy.fail-space", item, amount);
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
    //------------------------------------------

    /** |-------------- Help/Usage Responses --------------| */
    public String getHelp(final String cmd) {
        return plugin.messageUtils.getFormattedMessage("help." + cmd, false);
    }

    public TextComponent getHelp(Player player) {
        TextComponent message = new TextComponent();
        message.addExtra(plugin.messageUtils.getFormattedMessage("help.header", false) + MessageUtils.NL);
        if (player.hasPermission("market.reload"))
            message.addExtra(getHelp("reload") + MessageUtils.NL);
        if (player.hasPermission("market.command.collector"))
            message.addExtra(getHelp("collector") + MessageUtils.NL);
        if (player.hasPermission("market.command.buy"))
            message.addExtra(getHelp("buy") + MessageUtils.NL);
        if (player.hasPermission("market.command.sell"))
            message.addExtra(getHelp("sell") + MessageUtils.NL);
        if (player.hasPermission("market.command.sellall"))
            message.addExtra(getHelp("sellall") + MessageUtils.NL);
        if (player.hasPermission("market.command.info"))
            message.addExtra(getHelp("iteminfo") + MessageUtils.NL);
        if (player.hasPermission("market.command.worth"))
            message.addExtra(getHelp("worth") + MessageUtils.NL);
        if (player.hasPermission("market.command.sellhand"))
            message.addExtra(getHelp("sellhand") + MessageUtils.NL);
        message.addExtra(plugin.messageUtils.colorize("       &7Author: " + plugin.getDescription().getAuthors().get(0) +
                "&7       |       Version: " + plugin.getDescription().getVersion()));
        return message;
    }
    //------------------------------------------
}
