package com.github.jenya705.cubictracker.database.query;

import com.github.jenya705.cubictracker.CubicTracker;
import com.github.jenya705.cubictracker.CubicTrackerConfig;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class ActionQueryQueue implements Runnable {

    private final CubicTracker plugin;
    private final Deque<ActionQuery> queries = new ConcurrentLinkedDeque<>();

    public void addQuery(ActionQuery query) {
        queries.addLast(query);
    }

    public void start() {
        runWithDelay(plugin.config().getProperty(CubicTrackerConfig.EMPTY_QUERY_DELAY));
    }

    @Override
    public void run() {
        if (queries.isEmpty()) {
            runWithDelay(plugin.config().getProperty(CubicTrackerConfig.EMPTY_QUERY_DELAY));
            return;
        }
        int batchSize = plugin.config().getProperty(CubicTrackerConfig.QUERY_BATCH_SIZE);
        int added = 0;
        ActionQuery[] polledQueries = new ActionQuery[batchSize];
        Set<String> worlds = new HashSet<>();
        Set<String> entities = new HashSet<>();
        Set<String> actions = new HashSet<>();
        while (added < batchSize){
            if (queries.isEmpty()) break;
            ActionQuery query = queries.poll();
            worlds.add(query.getLocation().getWorld().getName());
            entities.add(query.getSource());
            entities.add(stringToNotNull(query.getTarget()));
            actions.add(query.getAction());
            polledQueries[added++] = query;
        }
        try (
                Connection connection = plugin.getDbConnection().getConnection();
                PreparedStatement actionsIdStatement = connection.prepareStatement(
                        "INSERT IGNORE INTO `actions_id_map` (`name`) VALUES (?);");
                PreparedStatement worldsIdStatement = connection.prepareStatement(
                        "INSERT IGNORE INTO `worlds_id_map` (`name`) VALUES (?);");
                PreparedStatement entitiesIdStatement = connection.prepareStatement(
                        "INSERT IGNORE INTO `entities_id_map` (`name`) VALUES (?);");
                PreparedStatement materialsIdStatement = connection.prepareStatement(
                        "INSERT IGNORE INTO `materials_id_map` (`name`) VALUES (?);");
                PreparedStatement actionsRepeatStatement = connection.prepareStatement(
                        "INSERT IGNORE INTO `actions_repeat_data` (`data`) VALUES (?);");
                PreparedStatement actionsDataStatement = connection.prepareStatement("""
                       INSERT INTO actions_data (`material`, `data`, `repeat_data`)
                       SELECT `material`.`id`, ?, `repeat_data`.`id`
                       FROM `materials_id_map` `material`, `actions_repeat_data` `repeat_data`
                       WHERE `material`.`name` = ? AND `repeat_data`.`md5` = UNHEX(MD5(?));
                       """, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement actionsCycleStatement = connection.prepareStatement(
                        "INSERT INTO actions_cycle_data (`linked`, `data`) VALUES (?, ?);");
                PreparedStatement actionsStatement = connection.prepareStatement("""
                        INSERT INTO `actions`
                        (`epoch`, `action`, `source`, `world`, `x`, `y`, `z`, `target`, `new_data`, `old_data`)
                        SELECT ?, `action`.`id`, `source`.`id`, `world`.`id`, ?, ?, ?, `target`.`id`, ?, ?
                        FROM `actions_id_map` `action`, `entities_id_map` `source` , `worlds_id_map` `world`, `entities_id_map` `target`
                        WHERE `action`.`name` = ? AND `source`.`name` = ? AND `world`.`name` = ? AND `target`.`name` = ?;
                        """)
                )
        {
            for (String action: actions) {
                actionsIdStatement.setString(1, action);
                actionsIdStatement.addBatch();
            }
            actionsIdStatement.executeLargeBatch();
            for (String world: worlds) {
                worldsIdStatement.setString(1, world);
                worldsIdStatement.addBatch();
            }
            worldsIdStatement.executeBatch();
            for (String entity: entities) {
                entitiesIdStatement.setString(1, entity);
                entitiesIdStatement.addBatch();
            }
            entitiesIdStatement.executeBatch();
            for (int i = 0; i < added; ++i) {
                ActionQuery query = polledQueries[i];
                fillDataTables(query.getNewData(), materialsIdStatement, actionsRepeatStatement, actionsDataStatement);
                if (query.getOldData() != null) {
                    fillDataTables(query.getOldData(), materialsIdStatement, actionsRepeatStatement, actionsDataStatement);
                }
            }
            materialsIdStatement.executeLargeBatch();
            actionsRepeatStatement.executeLargeBatch();
            actionsDataStatement.executeLargeBatch();
            ResultSet keys = actionsDataStatement.getGeneratedKeys();
            for (int i = 0; i < added; ++i) {
                ActionQuery query = polledQueries[i];
                actionsStatement.setLong(1, query.getEpoch());
                actionsStatement.setInt(2, query.getLocation().getBlockX());
                actionsStatement.setInt(3, query.getLocation().getBlockY());
                actionsStatement.setInt(4, query.getLocation().getBlockZ());
                actionsStatement.setLong(5, fillCycleActionTable(query.getNewData(), keys, actionsCycleStatement));
                actionsStatement.setObject(6, query.getOldData() == null ?
                        null : fillCycleActionTable(query.getOldData(), keys, actionsCycleStatement)
                );
                actionsStatement.setString(7, query.getAction());
                actionsStatement.setString(8, query.getSource());
                actionsStatement.setString(9, query.getLocation().getWorld().getName());
                actionsStatement.setString(10, stringToNotNull(query.getTarget()));
                actionsStatement.addBatch();
            }
            actionsCycleStatement.executeLargeBatch();
            actionsStatement.executeLargeBatch();
        } catch (SQLException e) {
            plugin.logger().error("Failed to insert actions:", e);
            for (int i = added - 1; i > 0; i--) {
                queries.addFirst(polledQueries[i]);
            }
        }
        runWithDelay(plugin.config().getProperty(CubicTrackerConfig.QUERY_DELAY));
    }

    private static long fillCycleActionTable(ActionDataQuery data,
                                             ResultSet keys,
                                             PreparedStatement actionsCycle) throws SQLException {
        keys.next();
        long newDataId = keys.getLong(1);
        if (data.getCycle() != null) {
            for (ActionCycleDataQuery cycleData: data.getCycle()) {
                if (cycleData == null) continue;
                keys.next();
                long cycleDataId = keys.getLong(1);
                actionsCycle.setLong(1, newDataId);
                actionsCycle.setLong(2, cycleDataId);
                actionsCycle.addBatch();
            }
        }
        return newDataId;
    }

    private static void fillDataTables(ActionDataQuery data,
                                       PreparedStatement materials,
                                       PreparedStatement actionsRepeat,
                                       PreparedStatement actionsData) throws SQLException {
        fillCycleDataTables(data, materials, actionsRepeat, actionsData);
        if (data.getCycle() != null) {
            for (ActionCycleDataQuery cycleData: data.getCycle()) {
                if (cycleData == null) continue;
                fillCycleDataTables(cycleData, materials, actionsRepeat, actionsData);
            }
        }
    }

    private static void fillCycleDataTables(ActionCycleDataQuery data,
                                            PreparedStatement materials,
                                            PreparedStatement actionsRepeat,
                                            PreparedStatement actionsData) throws SQLException {
        String repeatData = stringToNotNull(data.getRepeatData());
        materials.setString(1, data.getMaterial());
        materials.addBatch();
        actionsRepeat.setString(1, repeatData);
        actionsRepeat.addBatch();
        actionsData.setString(1, data.getData());
        actionsData.setString(2, data.getMaterial());
        actionsData.setString(3, repeatData);
        actionsData.addBatch();
    }

    private void runWithDelay(int delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(
                plugin, this, delay
        );
    }

    private static String stringToNotNull(String target) {
        return Objects.requireNonNullElse(target, "");
    }

}
