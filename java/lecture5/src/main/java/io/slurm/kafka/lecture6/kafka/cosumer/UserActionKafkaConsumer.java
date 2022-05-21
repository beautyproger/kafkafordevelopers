package io.slurm.kafka.lecture6.kafka.cosumer;

import io.slurm.kafka.utils.GracefullyShutdownFinish;
import io.slurm.kafka.utils.kafka.KafkaProperties;
import io.slurm.kafka.utils.kafka.UserAction;
import io.slurm.kafka.utils.GracefullyShutdownStartEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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
            KafkaProperties properties,
            WorkerProperties workerProperties
    ) {
        this.consumer = consumer;
        this.processor = processor;
        this.properties = properties;
        kafkaWorkerExecutor = Executors.newFixedThreadPool(
                workerProperties.getTreadCount(),
                runnable -> new Thread(runnable, "KafkaWorker-" + threadId.incrementAndGet())
        );
    }

    private static final AtomicLong threadId = new AtomicLong(0);
    private final ExecutorService kafkaWorkerExecutor;
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

    @EventListener
    public void gracefullyShutdownFinish(GracefullyShutdownFinish GracefullyShutdownFinishEvent){
        kafkaWorkerExecutor.shutdown();
        try {
            kafkaWorkerExecutor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            log.warn("Await interrupted", ex);
            Thread.currentThread().interrupt();
        }
    }

    void runConsumer() {
        try {
            consumer.subscribe(List.of(properties.getTopic()));
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
                consumer.commitAsync();
            }
        } catch (InterruptedException ex) {
            log.error("{} thread execution interrupted", getClass().getSimpleName(), ex);
            exitFlag = false;
        } catch (WakeupException ex) {
            log.info("{} thread finish execution", getClass().getSimpleName(), ex);
        } catch (Exception ex) {
            log.error("kafka internal error when fetching records", ex);
            System.exit(13);
        } finally {
            consumer.unsubscribe();
        }
    }

    private void processMessages(ConsumerRecords<String, UserAction> consumerRecords) throws InterruptedException {
        // FIXME
    }
}
