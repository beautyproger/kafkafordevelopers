package io.slurm.kafka.lecture4.kafka.cosumer;

import io.slurm.kafka.utils.kafka.KafkaProperties;
import io.slurm.kafka.utils.kafka.UserAction;
import io.slurm.kafka.utils.GracefullyShutdownStartEvent;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class UserActionKafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(UserActionKafkaConsumer.class);
    public static final int MAX_MESSAGE_PROCESS_RETRY = 600;

    private final Consumer<String, UserAction> consumer;
    private final UserActionProcessor processor;
    private final KafkaProperties properties;

    public UserActionKafkaConsumer(
            Consumer<String, UserAction> consumer,
            UserActionProcessor processor,
            KafkaProperties properties
    ) {
        this.consumer = consumer;
        this.processor = processor;
        this.properties = properties;
    }

    private final Thread consumerThread = new Thread(
            this::runConsumer,
            this.getClass().getSimpleName()
    );
    private volatile boolean exitFlag = true;

    @EventListener
    public void startEventCycle(ContextRefreshedEvent event){
        // FIXME
    }

    @EventListener
    public void stopEventCycle(GracefullyShutdownStartEvent gracefullyShutdownStartEvent){
        // FIXME
    }

    void runConsumer() {
        // FIXME
    }


}
