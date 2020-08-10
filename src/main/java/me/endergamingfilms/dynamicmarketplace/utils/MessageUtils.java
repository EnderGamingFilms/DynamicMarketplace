package me.endergamingfilms.dynamicmarketplace.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Logger;

public class MessageUtils {
    private final DynamicMarketplace plugin;
    public String prefix = "&6[Market] &f";
    public final static String NL = "\n";
    public final static String SPACE = " ";

    public MessageUtils(@NotNull final DynamicMarketplace instance) {
        plugin = instance;
    }

    /**
     * @param path String
     * @param type BOOLEAN, INT, DOUBLE, STRING[], STRING
     */
    public boolean grabConfig(final String path, boolean type) {
        return plugin.fileManager.getConfig().getBoolean(path);
    }

    /**
     * @param path String
     * @param type BOOLEAN, INT, DOUBLE, STRING[], STRING
     */
    public int grabConfig(final String path, int type) {
        return plugin.fileManager.getConfig().getInt(path);
    }

    /**
     * @param path String[]
     * @param type BOOLEAN, INT, DOUBLE, STRING[], STRING
     */
    public List<?> grabConfig(final String path, String[] type) {
        return plugin.fileManager.getConfig().getList(path);
    }

    /**
     * @param path String
     * @param type BOOLEAN, INT, DOUBLE, STRING[], STRING
     */
    public String grabConfig(final String path, String type) {
        return plugin.fileManager.getConfig().getString(path);
    }

    /**
     * @param path String
     * @param type BOOLEAN, INT, DOUBLE, STRING[], STRING
     */
    public double grabConfig(final String path, double type) {
        return plugin.fileManager.getConfig().getDouble(path);
    }

    /**
     * @param path String
     * @return raw message string
     */
    public String grabRaw(final String path) {
        String msg = plugin.fileManager.getMessages().getString(path);
        if (msg != null) {
            return msg;
        }
        return "&cnull";
    }

    public String getFormattedMessage(final String path) {
        return format(grabRaw(path));
    }

    public String getFormattedMessage(final String path, final boolean hasPrefix) {
        return format(grabRaw(path), hasPrefix);
    }

    public String getFormattedMessage(final String path, final double tax, final boolean hasPrefix) {
        return format(replace(grabRaw(path), tax), hasPrefix);
    }

    public String getFormattedMessage(String path, String cost, String balance) {
        return format(replace(grabRaw(path), cost, balance));
    }

    public String getFormattedMessage(final String path, final String item, final int amount) {
        return format(replace(grabRaw(path), item, amount));
    }

    public String getFormattedMessage(final String path, final String item, final int amount, final String sale) {
        return format(replace(grabRaw(path), item, amount, sale));
    }

    public String getFormattedMessage(String path, String path2, boolean hasPrefix) {
        return format(replace(grabRaw(path), grabRaw(path2), hasPrefix), hasPrefix);
    }

    public String getFormattedMessage(String path, String value, boolean hasPrefix, boolean x) {
        return format(replace(grabRaw(path), value), hasPrefix);
    }

    public String format(final String msg) {
        return prefix(msg);
    }

    public String format(final String msg, final boolean hasPrefix) {
        if (hasPrefix) {
            return prefix(msg);
        } else {
            return colorize(msg);
        }
    }

    public String replace(String msg, String item, int amount) {
        msg = msg.replace("%item%", item);
        msg = msg.replace("%amount%", String.valueOf(amount));
        return msg;
    }

    private String replace(String msg, String cost, String balance) {
        msg = msg.replace("%cost%", String.valueOf(cost));
        msg = msg.replace("%balance%", String.valueOf(balance));
        return msg;
    }

    public String replace(String msg, String item, int amount, String cost) {
        msg = msg.replace("%item%", item);
        msg = msg.replace("%amount%", String.valueOf(amount));
        msg = msg.replace("%cost%", String.valueOf(cost));
        return msg;
    }

    public String replace(String msg, final int value) {
        msg = msg.replace("%cost%", String.valueOf(value));
        msg = msg.replace("%tax%", String.valueOf(value));
        return msg;
    }

    public String replace(String msg, final double value) {
        msg = msg.replace("%cost%", String.valueOf(value));
        msg = msg.replace("%tax%", String.valueOf(value));
        return msg;
    }

    public String replace(String msg, final String name, final boolean hasPrefix) {
        msg = msg.replace("%collector_name%", name);
        return msg;
    }

    public  String replace(String msg, String profit) {
        msg = msg.replace("%profit%", profit);
        return msg;
    }

    public int checkAmount(String amount, Player player) {
        if (amount.matches("[0-9]+")) {
            int number = Integer.parseInt(amount);
            if ( number < 1000 )
                return  number;
        }
        send(player, plugin.respond.invalidInput());
        return 1;
    }

    public void send(Player player, String message) {
        player.sendMessage(message);
    }

    public void send(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    public void send(Player player, BaseComponent message) {
        player.spigot().sendMessage(message);
    }

    /**
     * Credit:
     * @YourCoal
     * https://www.spigotmc.org/threads/how-to-get-a-user-friendly-item-name.373484/
     */
    public String capitalize(String input) { //
        StringBuilder output = new StringBuilder();
        for (String s : input.split("_")) {
            output.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
        }
        return output.substring(0, output.length() - 1);
    }

    /**
     * Credit:
     * @lokka30
     * https://github.com/lokka30/PhantomLib
     *
     * This method will join a prefix and message together and return a chat color translated string.
     *
     * @param msg    the message string
     * @return the colorized string of the prefix and msg joined together
     */
    private String prefix(final String msg) {
        return colorize(prefix + msg);
    }

    /**
     * This method will translate colour codes (e.g. &a) in the specified message.
     *
     * @param msg the message which should have colour codes translated
     * @return the translated string
     */
    public String colorize(final String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * INFO = Logger.info
     * WARNING = Logger.warning
     * SEVERE = Logger.severe
     */
    public enum LogLevel {
        INFO, WARNING, SEVERE
    }

    /**
     * This method will log a message to the console.
     *
     * @param logLevel the LogLevel enum determining the severity of the logged message
     * @param msg      the message which should be sent in the log
     */
    public void log(final LogLevel logLevel, String msg) {
        msg = colorize(prefix + msg);
        Logger logger = Bukkit.getLogger();
        switch (logLevel) {
            case INFO:
                logger.info(msg);
                break;
            case WARNING:
                logger.warning(msg);
                break;
            case SEVERE:
                logger.severe(msg);
                break;
            default:
                throw new IllegalStateException("Undefined LogLevel: " + logLevel.toString());
        }
    }
}
