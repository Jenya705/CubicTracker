package com.github.jenya705.cubictracker.database.query;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;

/**
 * @author Jenya705
 */
@Data
@Builder
class ActionQueryImpl implements ActionQuery {

    private final long epoch;
    @NonNull
    private final String action;
    @NonNull
    private final String source;
    @NonNull
    private final Location location;
    private final String target;
    @NonNull
    private final ActionDataQuery newData;
    private final ActionDataQuery oldData;
}
