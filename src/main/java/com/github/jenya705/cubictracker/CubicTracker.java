package com.github.jenya705.cubictracker;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import com.github.jenya705.cubictracker.database.DBConnection;
import com.github.jenya705.cubictracker.database.query.ActionQueryQueue;
import com.github.jenya705.cubictracker.listener.TestListener;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;

@Getter
public final class CubicTracker extends JavaPlugin {

    @Getter(AccessLevel.PRIVATE) private final Logger pluginLogger = getSLF4JLogger();
    @Getter(AccessLevel.PRIVATE) private SettingsManager pluginConfig;

    private final ActionQueryQueue queryQueue = new ActionQueryQueue(this);

    private DBConnection dbConnection;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs(); // IGNORED
        pluginConfig = SettingsManagerBuilder
                .withYamlFile(new File(getDataFolder(), "config.yml"))
                .configurationData(CubicTrackerConfig.class)
                .useDefaultMigrationService()
                .create();
        pluginConfig.save();
        dbConnection = new DBConnection(this);
        queryQueue.start();
        getServer().getPluginManager().registerEvents(new TestListener(this), this);
    }

    @Override
    public void onDisable() {

    }

    public SettingsManager config() {
        return pluginConfig;
    }

    public Logger logger() {
        return pluginLogger;
    }

    @NotNull
    public FileConfiguration getConfig() {
        logger().warn("Getting not valid config use config() method instead");
        return super.getConfig();
    }

}
