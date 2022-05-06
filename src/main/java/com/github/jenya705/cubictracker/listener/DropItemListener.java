package com.github.jenya705.cubictracker.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author Jenya705
 */
public class DropItemListener implements Listener {

    @EventHandler
    public void drop(PlayerDropItemEvent event) throws Exception {
        ItemStack item = event.getItemDrop().getItemStack();

    }

}
