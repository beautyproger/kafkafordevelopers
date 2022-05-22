package io.slurm.kafka.lecture7.kafka.limit;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(LimitReachProperties.PREFIX)
@Validated
public class LimitReachProperties {
    public static final String PREFIX = "io.slurm.kafka.limit";

    private List<String> bootstrapServers = new ArrayList<>();
    private String producerName;
    private String consumerGroupId;
    private String topic;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }


}
