package com.github.jenya705.cubictracker.database.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Jenya705
 */
public interface ActionCycleDataQuery {

    static ActionDataQueryImpl.ActionDataQueryImplBuilder builder() {
        return ActionDataQueryImpl.builder();
    }

    @NotNull
    String getMaterial();

    @NotNull
    String getData();

    @Nullable
    String getRepeatData();

}
