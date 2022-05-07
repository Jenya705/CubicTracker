package com.github.jenya705.cubictracker.database.query;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Jenya705
 */
@Data
@Builder
class ActionDataQueryImpl implements ActionDataQuery {

    @NonNull
    private final String material;
    @NonNull
    private final String data;
    private final String repeatData;
    private final List<ActionCycleDataQuery> cycle;

    public static class ActionDataQueryImplBuilder {
        public ActionDataQueryImplBuilder cycle(ActionCycleDataQuery query) {
            cycle = Objects.requireNonNullElseGet(cycle, ArrayList::new);
            cycle.add(query);
            return this;
        }
    }

}
