package io.slurm.kafka.lecture6.kafka;

import io.slurm.kafka.lecture6.kafka.cosumer.UserActionDeserializer;
import io.slurm.kafka.lecture6.kafka.producer.UserActionSerializer;
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
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getConsumerGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new KafkaConsumer<>(props, null, deserializer);
    }

    @Bean(destroyMethod = "close")
    public KafkaProducer<String, UserAction> createProducer(KafkaProperties properties, UserActionSerializer serializer) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, properties.getProducerName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props, null, serializer);
    }
}

