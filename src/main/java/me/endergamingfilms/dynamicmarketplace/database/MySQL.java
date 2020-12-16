package me.endergamingfilms.dynamicmarketplace.database;

import me.endergamingfilms.dynamicmarketplace.DynamicMarketplace;
import me.endergamingfilms.dynamicmarketplace.utils.MessageUtils;
import me.endergamingfilms.dynamicmarketplace.utils.FileManager;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQL {
    private final DynamicMarketplace plugin;
    private Connection connection;
    private boolean isEnabled;
    // Connection Data
    private final Properties properties = new Properties();
    private String host;
    private String port;
    private String database;

    public MySQL(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    public void init() {
        isEnabled = plugin.messageUtils.grabConfig("MySQL.Enable", FileManager.BOOLEAN);
        host = plugin.messageUtils.grabConfig("MySQL.Host", FileManager.STRING);
        port = plugin.messageUtils.grabConfig("MySQL.Port", FileManager.STRING);
        database = plugin.messageUtils.grabConfig("MySQL.Database", FileManager.STRING);
        properties.setProperty("user", plugin.messageUtils.grabConfig("MySQL.Username", FileManager.STRING));
        properties.setProperty("password", plugin.messageUtils.grabConfig("MySQL.Password", FileManager.STRING));
        properties.setProperty("useSSL", plugin.messageUtils.grabConfig("MySQL.useSSL", FileManager.STRING));
        properties.setProperty("autoReconnect", "true");
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                    host + ":" + port + "/" + database, properties); // + "?useSSL=" + useSSL, username, password);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
                plugin.messageUtils.log(MessageUtils.LogLevel.INFO, "&cClosed database connections.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
