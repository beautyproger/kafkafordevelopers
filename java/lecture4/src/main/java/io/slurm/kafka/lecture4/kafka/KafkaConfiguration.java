package io.slurm.kafka.lecture4.kafka;

import io.slurm.kafka.lecture4.kafka.cosumer.UserActionDeserializer;
import io.slurm.kafka.lecture4.kafka.producer.UserActionSerializer;
import io.slurm.kafka.utils.kafka.KafkaProperties;
import io.slurm.kafka.utils.kafka.UserAction;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KafkaConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KafkaConfiguration.class);

    @Bean(destroyMethod = "close")
    public Consumer<String, UserAction> createConsumer(KafkaProperties properties, UserActionDeserializer deserializer) {
        // FIXME
        return null;
    }

    @Bean(destroyMethod = "close")
    public KafkaProducer<String, UserAction> createProducer(KafkaProperties properties, UserActionSerializer serializer) {
        // FIXME
        return null;

    }
}

