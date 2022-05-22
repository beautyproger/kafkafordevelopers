package io.slurm.kafka.lecture7.actor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartitionActor {
    private static final Logger log = LoggerFactory.getLogger(PartitionActor.class);

    private final ActorProperties properties;

    private final HashMap<String, UserActor> subActorMap = new HashMap<>();
    private Instant nextCleanup = Instant.MIN;

    public PartitionActor(ActorProperties properties) {
        this.properties = properties;
    }

    public EventAddResult addEvent(UserEvent event) {
        if (nextCleanup.isBefore(event.getEventTime())) {
            cleanUp(event.getEventTime());
        }
        // FIXME
        return null;
    }

    public void cleanUp(Instant time) {
        Instant lowThreshold = time.minus(properties.getWindowPeriod());
        subActorMap.values().removeIf(actor -> actor.cleanUp(lowThreshold) == UserActor.CleanUpResult.CLEAN);
        nextCleanup = time.plus(properties.getCleanupPeriod());
    }

    public Map<String, Long> queryState() {
        return subActorMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue().queryState()
                ));
    }
}
