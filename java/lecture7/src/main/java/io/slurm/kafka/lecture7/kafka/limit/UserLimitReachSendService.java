package io.slurm.kafka.lecture7.kafka.limit;

import io.slurm.kafka.lecture7.kafka.UserLimitReach;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserLimitReachSendService {
    private static final Logger log = LoggerFactory.getLogger(UserLimitReachSendService.class);

    private final LimitReachProperties limitReachProperties;
    private final Producer<String, UserLimitReach> producer;


    public UserLimitReachSendService(
            LimitReachProperties limitReachProperties,
            @Qualifier("UserLimitReachProducers") Producer<String, UserLimitReach> producer
    ) {
        this.limitReachProperties = limitReachProperties;
        this.producer = producer;
    }


    public void sendUserAction(UserLimitReach limitReach) {
        log.info("Limit reach for user {} at {}", limitReach.getUserId(), limitReach.getReachTime());
        ProducerRecord<String, UserLimitReach> record =
                new ProducerRecord<>(
                        limitReachProperties.getTopic(),
                        limitReach.getUserId(),
                        limitReach
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
