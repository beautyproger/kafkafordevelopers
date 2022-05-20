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
        // FIXME
    }
}
