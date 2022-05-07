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

    public static final Property<Integer> QUERY_BATCH_SIZE = PropertyInitializer
            .newProperty("query.batch-size", 1024);

    public static final Property<Integer> QUERY_DELAY = PropertyInitializer
            .newProperty("query.delay", 1);

    public static final Property<Integer> EMPTY_QUERY_DELAY = PropertyInitializer
            .newProperty("query.empty-delay", 5);

}
