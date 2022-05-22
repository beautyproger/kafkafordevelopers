package io.slurm.kafka.lecture7.kafka.producer;

import io.slurm.kafka.utils.kafka.KafkaProperties;
import io.slurm.kafka.utils.kafka.UserAction;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserActionSendService {
    private static final Logger log = LoggerFactory.getLogger(UserActionSendService.class);

    private final KafkaProperties kafkaProperties;
    private final Producer<String, UserAction> producer;


    public UserActionSendService(
            KafkaProperties kafkaProperties,
            @Qualifier("UserActionProducers") Producer<String, UserAction> producer
    ) {
        this.kafkaProperties = kafkaProperties;
        this.producer = producer;
    }


    public CompletableFuture<RecordMetadata> sendUserAction(UserAction userAction) {
        ProducerRecord<String, UserAction> record =
                new ProducerRecord<>(
                        kafkaProperties.getTopic(),
                        userAction.getUserId(),
                        userAction
                );

        CallbackCompletableFuture completableFuture = new CallbackCompletableFuture();
        producer.send(record, completableFuture);
        return completableFuture;
    }

    private static class CallbackCompletableFuture extends CompletableFuture<RecordMetadata> implements Callback {
        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception == null) {
                complete(metadata);
            } else {
                completeExceptionally(exception);
            }
        }
    }
}
