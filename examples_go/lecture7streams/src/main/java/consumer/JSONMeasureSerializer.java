package consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JSONMeasureSerializer implements Serializer<Measure> {
    private ObjectWriter writer;

    public JSONMeasureSerializer() {
        this.writer = new ObjectMapper().writer();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, Measure measure) {
        return this.serialize(s, null, measure);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Measure data) {
        try {
            String json = this.writer.writeValueAsString(data);
            return json.getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
