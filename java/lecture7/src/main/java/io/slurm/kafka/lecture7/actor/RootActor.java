package io.slurm.kafka.lecture7.actor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RootActor {
    private static final Logger log = LoggerFactory.getLogger(RootActor.class);

    private final ActorProperties properties;

    private final HashMap<TopicPartition, PartitionActor> partitionMap = new HashMap<>();

    public RootActor(ActorProperties properties) {
        this.properties = properties;
    }

    public void onPartitionsAssigned(TopicPartition partition) {
        // FIXME
    }

    public void onPartitionsRevoked(TopicPartition partition) {
        // FIXME
    }

    public EventAddResult addEvent(TopicPartition partition, UserEvent event) {
        PartitionActor actor = partitionMap.get(partition);
        if (actor == null) {
            log.error("Event on partition {}, but it not assigned", partition);
            return EventAddResult.NONE;
        } else {
            return actor.addEvent(event);
        }
    }

    public Map<String, Long> queryState() {
        return partitionMap.values().stream()
                .flatMap(partitionActor -> partitionActor.queryState().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
