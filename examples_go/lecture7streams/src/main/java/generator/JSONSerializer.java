package generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.kafka.common.header.Headers;


import java.util.Map;

public class JSONSerializer implements org.apache.kafka.common.serialization.Serializer{
    private ObjectWriter writer;

    public JSONSerializer() {
        this.writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Override
    public void configure(Map configs, boolean isKey) {
        org.apache.kafka.common.serialization.Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, Object o) {
        return this.serialize(s, null, o);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Object data) {
        try {
            String json = this.writer.writeValueAsString(data);
            return json.getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        org.apache.kafka.common.serialization.Serializer.super.close();
    }
}
