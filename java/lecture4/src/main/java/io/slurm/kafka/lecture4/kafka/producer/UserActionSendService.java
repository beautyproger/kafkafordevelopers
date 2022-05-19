package io.slurm.kafka.lecture4.kafka.producer;

import io.slurm.kafka.utils.kafka.UserAction;
import io.slurm.kafka.utils.kafka.KafkaProperties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserActionSendService {
    private static final Logger log = LoggerFactory.getLogger(UserActionSendService.class);

    private final KafkaProperties kafkaProperties;
    private final Producer<String, UserAction> producer;


    public UserActionSendService(
            KafkaProperties kafkaProperties,
            Producer<String, UserAction> producer
    ) {
        this.kafkaProperties = kafkaProperties;
        this.producer = producer;
    }


    public void sendUserAction(UserAction userAction) {
        ProducerRecord<String, UserAction> record =
                new ProducerRecord<>(
                        kafkaProperties.getTopic(),
                        userAction.getUserId(),
                        userAction
                );

        try {
            producer.send(record).get();
        } catch (InterruptedException ex) {
            log.warn("Await interrupted", ex);
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException)ex.getCause();
            } else {
                throw new RuntimeException("Unable to send message in kafka", ex.getCause());
            }
        }
    }
}
