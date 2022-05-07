package com.github.jenya705.cubictracker.database.query;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Jenya705
 */
public interface ActionQuery {

    static ActionQueryImpl.ActionQueryImplBuilder builder() {
        return ActionQueryImpl.builder();
    }

    long getEpoch();

    @NotNull
    String getAction();

    @NotNull
    String getSource();

    @NotNull
    Location getLocation();

    @Nullable
    String getTarget();

    @NotNull
    ActionDataQuery getNewData();

    @Nullable
    ActionDataQuery getOldData();

}
