package com.github.jenya705.cubictracker.database;

import com.github.jenya705.cubictracker.CubicTracker;
import com.github.jenya705.cubictracker.CubicTrackerConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Cleanup;
import lombok.Getter;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Jenya705
 */
@Getter
public class CubicTrackerDatabase {

    private final HikariDataSource dataSource;

    public CubicTrackerDatabase(CubicTracker plugin) {
        HikariConfig hikariConfig = new HikariConfig();
        try (InputStream propertiesStream = plugin.getResource("hikari.properties")) {
            Properties properties = new Properties();
            properties.load(propertiesStream);
            hikariConfig.setDataSourceProperties(properties);
        } catch (Exception e) {
            plugin.logger().warn("Failed to load properties:", e);
        }
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            plugin.logger().error("Failed to load driver:", e);
            throw new RuntimeException(e);
        }
        hikariConfig.setJdbcUrl("jdbc:mysql://%s/%s".formatted(
                plugin.config().getProperty(CubicTrackerConfig.SQL_HOST),
                plugin.config().getProperty(CubicTrackerConfig.SQL_DATABASE)
        ));
        hikariConfig.setUsername(
                plugin.config().getProperty(CubicTrackerConfig.SQL_USER)
        );
        hikariConfig.setPassword(
                plugin.config().getProperty(CubicTrackerConfig.SQL_PASSWORD)
        );
        dataSource = new HikariDataSource(hikariConfig);
        try {
            createMapTable("entities_map");
            createMapTable("worlds_map");
            createMapTable("materials_map");
            createMapTable("actions_map");
        } catch (SQLException e) {
            plugin.logger().error("Failed to setup:", e);
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void fastUpdate(String sql) throws SQLException {
        @Cleanup Statement statement = getConnection().createStatement();
        statement.execute(sql);
    }

    public ResultSet fastQuery(String sql) throws SQLException {
        @Cleanup Statement statement = getConnection().createStatement();
        return statement.executeQuery(sql);
    }

    private void createMapTable(String tableName) throws SQLException {
        fastUpdate("""
                CREATE TABLE IF NOT EXISTS %s (
                    `id` INT NOT NULL AUTO_INCREMENT,
                    `name` VARCHAR(255) NOT NULL,
                    PRIMARY KEY(`id`),
                    UNIQUE(`name`)
                );
                """.formatted(tableName));
    }

}
