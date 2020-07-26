package me.endergaming.dynamicmarketplace.database;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.MessageUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static me.endergaming.dynamicmarketplace.utils.FileManager.BOOLEAN;
import static me.endergaming.dynamicmarketplace.utils.FileManager.STRING;

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
        isEnabled = plugin.messageUtils.grabConfig("MySQL.Enable", BOOLEAN);
        host = plugin.messageUtils.grabConfig("MySQL.Host", STRING);
        port = plugin.messageUtils.grabConfig("MySQL.Port", STRING);
        database = plugin.messageUtils.grabConfig("MySQL.Database", STRING);
        properties.setProperty("user", plugin.messageUtils.grabConfig("MySQL.Username", STRING));
        properties.setProperty("password", plugin.messageUtils.grabConfig("MySQL.Password", STRING));
        properties.setProperty("useSSL", plugin.messageUtils.grabConfig("MySQL.useSSL", STRING));
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
