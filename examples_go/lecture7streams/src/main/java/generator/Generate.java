package generator;

import consumer.Measure;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

public class Generate {
    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        config.put("client.id", "generator");
        config.put("bootstrap.servers", "localhost:29092");
        config.put("acks", "all");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "generator.JSONSerializer");

        Reader reader = new Reader();
        List<Measure> data = reader.readCSV();

        KafkaProducer producer = new KafkaProducer<String, Measure>(config);

        for(Measure item : data) {
            Future<RecordMetadata> future = producer.send(new ProducerRecord<String, Measure>("metrics",item));
            System.out.println(future.get().timestamp());
        }

    }
}
