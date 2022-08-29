package io.slurm.kafka.lecture7.kafka.cosumer;

import io.slurm.kafka.lecture7.actor.ActorProperties;
import io.slurm.kafka.lecture7.actor.RootActor;
import io.slurm.kafka.lecture7.kafka.limit.UserLimitReachSendService;
import io.slurm.kafka.utils.GracefullyShutdownStartEvent;
import io.slurm.kafka.utils.kafka.KafkaProperties;
import io.slurm.kafka.utils.kafka.UserAction;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
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
    private final RootActor rootActor;
    private final UserLimitReachSendService userLimitReachSendService;
    private final KafkaProperties properties;
    private final ActorProperties actorProperties;

    public UserActionKafkaConsumer(
            Consumer<String, UserAction> consumer,
            RootActor rootActor,
            UserLimitReachSendService userLimitReachSendService,
            KafkaProperties properties,
            ActorProperties actorProperties
    ) {
        this.consumer = consumer;
        this.rootActor = rootActor;
        this.userLimitReachSendService = userLimitReachSendService;
        this.properties = properties;
        this.actorProperties = actorProperties;
    }

    private static final AtomicLong threadId = new AtomicLong(0);
    private final Thread consumerThread = new Thread(
            this::runConsumer,
            this.getClass().getSimpleName()
    );
    private volatile boolean exitFlag = true;

    @EventListener
    public void startEventCycle(ContextRefreshedEvent event){
        consumerThread.start();
    }

    @EventListener
    public void gracefullyShutdownStart(GracefullyShutdownStartEvent gracefullyShutdownStartEvent){
        exitFlag = false;
        consumer.wakeup();
    }

    private class Listener implements ConsumerRebalanceListener {
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            partitions.forEach(rootActor::onPartitionsRevoked);
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            Instant instant = Instant.now().minus(actorProperties.getWindowPeriod().multipliedBy(2));
            Map<TopicPartition, Long> partitionTimeMap = partitions.stream().collect(Collectors.toMap(
                    key -> key,
                    val -> instant.toEpochMilli()
            ));
            // FIXME
        }
    }

    void runConsumer() {
        try {
            consumer.subscribe(List.of(properties.getTopic()), new Listener());
            while (exitFlag) {
                final ConsumerRecords<String, UserAction> consumerRecords = consumer.poll(Duration.ofMillis(5000));
                boolean messageProcessingNotFinished;
                int failCount = 0;
                do {
                    try {
                        processMessages(consumerRecords);
                        messageProcessingNotFinished = false;
                    } catch (Exception ex) {
                        messageProcessingNotFinished = true;
                        failCount++;
                        if (failCount > MAX_MESSAGE_PROCESS_RETRY) {
                            log.error("Unable to process any message after {} retry", MAX_MESSAGE_PROCESS_RETRY, ex);
                            System.exit(13);
                        } else {
                            log.warn("Unable to process messages", ex);
                            Thread.sleep(1000);
                        }
                    }
                } while (messageProcessingNotFinished);
            }
        } catch (InterruptedException ex) {
            log.error("{} thread execution interrupted", getClass().getSimpleName(), ex);
            exitFlag = false;
        } catch (WakeupException ex) {
            log.info("{} thread finish execution", getClass().getSimpleName(), ex);
        } catch (Exception ex) {
            log.error("kafka internal error when fetching records", ex);
            System.exit(13);

        }
    }

    private void processMessages(ConsumerRecords<String, UserAction> consumerRecords) throws InterruptedException {
        if (consumerRecords.count() > 0) {
            log.info("Records fetched {}", consumerRecords.count());
        } else {
            log.debug("Records fetched {}", consumerRecords.count());
        }
        // FIXME
    }
}
