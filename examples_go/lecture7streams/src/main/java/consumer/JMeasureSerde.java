package consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JMeasureSerde implements Serde<Measure> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serde.super.configure(configs, isKey);
    }

    @Override
    public void close() {
        Serde.super.close();
    }

    @Override
    public Serializer<Measure> serializer() {
        return new JSONMeasureSerializer();
    }

    @Override
    public Deserializer<Measure> deserializer() {
        return new JSONMeasureDeserializer();
    }
}
