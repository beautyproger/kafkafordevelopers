package io.slurm.kafka.lecture7.actor;

import java.time.Instant;
import java.util.LinkedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserActor {
    private final ActorProperties properties;

    @NotNull
    private final LinkedList<UserEvent> events = new LinkedList<>();
    @Nullable
    private Instant activationTime = Instant.MIN;

    public UserActor(ActorProperties properties) {
        this.properties = properties;
    }

    public EventAddResult addEvent(UserEvent event) {
        // FIXME
        // Вы должны вызвать cleanUp(eventTime.minus(properties.getWindowPeriod())); если подозреваете что придел достигну
        return EventAddResult.NONE;
    }

    public CleanUpResult cleanUp(Instant time) {
        events.removeIf(event -> event.getEventTime().isBefore(time));
        if (activationTime != null && activationTime.isBefore(time)) {
            activationTime = null;
        }
        if (events.isEmpty() && activationTime == null) {
            return CleanUpResult.CLEAN;
        } else {
            return CleanUpResult.DIRTY;
        }
    }

    public long queryState() {
        return events.size();
    }

    public enum CleanUpResult{
        CLEAN,
        DIRTY,
    }
}
