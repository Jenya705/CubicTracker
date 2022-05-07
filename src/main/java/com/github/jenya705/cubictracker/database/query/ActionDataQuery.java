package com.github.jenya705.cubictracker.database.query;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Jenya705
 */
public interface ActionDataQuery extends ActionCycleDataQuery {

    static ActionDataQueryImpl.ActionDataQueryImplBuilder builder() {
        return ActionDataQueryImpl.builder();
    }

    @Nullable
    List<ActionCycleDataQuery> getCycle();

}
