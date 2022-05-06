package com.github.jenya705.cubictracker.listener;

import com.github.jenya705.cubictracker.CubicTracker;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jenya705
 */
public class CubicTrackerListenerContainer {

    private final Map<String, Listener> listeners = new HashMap<>();

    public CubicTrackerListenerContainer(CubicTracker plugin) {
        listeners.put("drop-item", new DropItemListener());
        listeners.forEach((s, listener) ->
                plugin.getServer().getPluginManager().registerEvents(listener, plugin)
        );
    }

}
