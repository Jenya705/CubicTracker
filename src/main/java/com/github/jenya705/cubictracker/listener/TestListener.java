package com.github.jenya705.cubictracker.listener;

import com.github.jenya705.cubictracker.CubicTracker;
import com.github.jenya705.cubictracker.database.query.ActionDataQuery;
import com.github.jenya705.cubictracker.database.query.ActionQuery;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class TestListener implements Listener {

    private final CubicTracker plugin;

    @EventHandler
    public void itemDrop(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        plugin.getQueryQueue()
                .addQuery(ActionQuery.builder()
                        .action("item-drop")
                        .location(event.getItemDrop().getLocation().clone())
                        .newData(ActionDataQuery.builder()
                                .material(itemStack.getType().name())
                                .data(Integer.toString(itemStack.getAmount()))
                                .build()
                        )
                        .source(event.getPlayer().getName())
                        .build()
                );
    }

}
