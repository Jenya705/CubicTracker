package com.github.jenya705.cubictracker.database;

import com.github.jenya705.cubictracker.CubicTracker;
import com.github.jenya705.cubictracker.CubicTrackerConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Jenya705
 */
@Getter
public class DBConnection {

    private final HikariDataSource dataSource;

    public DBConnection(CubicTracker plugin) {
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
        try (InputStream setupSQLInputStream = plugin.getResource("setup.sql");
             Statement statement = getConnection().createStatement()){
            if (setupSQLInputStream == null) {
                throw new IOException("setup.sql is not found in resources folder");
            }
            String setupSQL = new String(setupSQLInputStream.readAllBytes());
            for (String operation: setupSQL.split(";")) {
                if (operation.trim().isEmpty()) break;
                statement.addBatch(operation + ";");
            }
            statement.executeLargeBatch();
        } catch (SQLException | IOException e) {
            plugin.logger().error("Failed to setup:", e);
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


}
