package consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class JSONMeasureDeserializer implements Deserializer<Measure> {
    private ObjectReader objectReader;

    public JSONMeasureDeserializer() {
        this.objectReader = new ObjectMapper().reader();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public Measure deserialize(String s, byte[] bytes) {
        try {
            System.out.println("deserialize");
            Measure test = this.objectReader.readValue(bytes, Measure.class);
            System.out.println(test.timestamp);
            return test;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Measure deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
