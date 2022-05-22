package io.slurm.kafka.lecture7.kafka.limit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.slurm.kafka.lecture7.kafka.UserLimitReach;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserLimitReachSerializer implements Serializer<UserLimitReach> {
    private static final Logger log = LoggerFactory.getLogger(UserLimitReachSerializer.class);

    private final ObjectMapper objectMapper;

    public UserLimitReachSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(String topic, UserLimitReach data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            return new byte[0];
        }
    }
}
