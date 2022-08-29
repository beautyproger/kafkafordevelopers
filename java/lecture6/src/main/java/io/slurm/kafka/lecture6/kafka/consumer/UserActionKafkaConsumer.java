package io.slurm.kafka.lecture6.kafka.cosumer;

import io.slurm.kafka.lecture6.database.EmulatedRepository;
import io.slurm.kafka.utils.GracefullyShutdownStartEvent;
import io.slurm.kafka.utils.kafka.KafkaProperties;
import io.slurm.kafka.utils.kafka.UserAction;
import java.time.Duration;
import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
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
    private final EmulatedRepository repository;
    private final KafkaProperties properties;

    public UserActionKafkaConsumer(
            Consumer<String, UserAction> consumer,
            EmulatedRepository repository,
            KafkaProperties properties
    ) {
        this.consumer = consumer;
        this.repository = repository;
        this.properties = properties;
    }

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
    public void gracefullyShutdownStart(GracefullyShutdownStartEvent gracefullyShutdownStartEvent) {
        exitFlag = false;
        consumer.wakeup();
    }

    void runConsumer() {
        try {
            consumer.subscribe(List.of(properties.getTopic()),
                    null // FIXME
            );
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

    private void processMessages(ConsumerRecords<String, UserAction> consumerRecords) {
        if (consumerRecords.count() > 0) {
            log.info("Records fetched {}", consumerRecords.count());
        } else {
            log.debug("Records fetched {}", consumerRecords.count());
        }
        // FIXME
    }
}
