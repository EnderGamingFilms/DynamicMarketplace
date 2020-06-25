package me.endergaming.dynamicmarketplace.Database;

import me.endergaming.dynamicmarketplace.DynamicMarketplace;
import me.endergaming.dynamicmarketplace.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLGetter {
    private final DynamicMarketplace plugin;
    public static final int defaultStanding = 500;

    public SQLGetter(@NotNull final DynamicMarketplace instance) {
        this.plugin = instance;
    }

    public void createTable() {
        PreparedStatement pStatement;
        try {
            pStatement = plugin.database.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS market_standing"
                    + "(uuid VARCHAR(36), standing SMALLINT(3), PRIMARY KEY (uuid))");
            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(Player player) {
        try {
            UUID uuid = player.getUniqueId();
            PreparedStatement pStatement = plugin.database.getConnection().prepareStatement("SELECT * FROM market_standing WHERE uuid=?");
            pStatement.setString(1, uuid.toString());
            ResultSet results = pStatement.executeQuery();
            results.next();

            if (!playerExists(uuid)) {
                PreparedStatement pStatement2 = plugin.database.getConnection()
                        .prepareStatement("INSERT IGNORE INTO market_standing" + " (uuid,standing) VALUES (?,?)");
                pStatement2.setString(1, uuid.toString());
                // Default Standing all players should start with
                pStatement2.setString(2, String.valueOf(defaultStanding));
                pStatement2.executeUpdate();

                if (plugin.fileManager.debug) {
                    plugin.messageUtils.log(MessageUtils.LogLevel.INFO, "&e" + player.getDisplayName() + " &ehas been added to the database.");
                }

                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID uuid) {
        try {
            PreparedStatement pStatement = plugin.database.getConnection().prepareStatement("SELECT * FROM market_standing WHERE uuid=?");
            pStatement.setString(1, uuid.toString());

            ResultSet results = pStatement.executeQuery();
            // Return true or false if player exists in table
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addStanding(UUID uuid, int standing) {
        try {
            PreparedStatement pStatement = plugin.database.getConnection().prepareStatement("UPDATE market_standing SET standing=? WHERE uuid=?");
            pStatement.setInt(1, (getStanding(uuid) + standing));
            pStatement.setString(2, uuid.toString());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeStanding(UUID uuid, int standing) {
        try {
            PreparedStatement pStatement = plugin.database.getConnection().prepareStatement("UPDATE market_standing SET standing=? WHERE uuid=?");
            pStatement.setInt(1, (getStanding(uuid) - standing));
            pStatement.setString(2, uuid.toString());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setStanding(UUID uuid, int standing) {
        try {
            PreparedStatement pStatement = plugin.database.getConnection().prepareStatement("UPDATE market_standing SET standing=? WHERE uuid=?");
            pStatement.setInt(1, standing);
            pStatement.setString(2, uuid.toString());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStanding(UUID uuid) {
        try {
            PreparedStatement pStatement = plugin.database.getConnection().prepareStatement("SELECT standing FROM market_standing WHERE uuid=?");
            pStatement.setString(1, uuid.toString());
            ResultSet result = pStatement.executeQuery();
            int tempStanding = defaultStanding;

            if (result.next()) {
                tempStanding = result.getInt("standing");
                return tempStanding;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaultStanding;
    }

    public void clearTable() {
        try {
            PreparedStatement pStatement = plugin.database.getConnection().prepareStatement("TRUNCATE market_standing");
            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
