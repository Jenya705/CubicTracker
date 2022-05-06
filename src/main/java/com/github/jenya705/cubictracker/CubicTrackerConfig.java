package com.github.jenya705.cubictracker;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;

/**
 * @author Jenya705
 */
public class CubicTrackerConfig implements SettingsHolder {

    public static final Property<String> SQL_HOST = PropertyInitializer
            .newProperty("database.host", "localhost:3306");

    public static final Property<String> SQL_USER = PropertyInitializer
            .newProperty("database.user", "root");

    public static final Property<String> SQL_PASSWORD = PropertyInitializer
            .newProperty("database.password", "1");

    public static final Property<String> SQL_DATABASE = PropertyInitializer
            .newProperty("database.database", "sys");

}
