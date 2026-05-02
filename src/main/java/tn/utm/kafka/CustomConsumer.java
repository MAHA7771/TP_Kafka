package tn.utm.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class CustomConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "groupe-custom-1");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                CommandeDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        try (Consumer<String, Commande> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList("commandes"));
            System.out.println("⏳ En attente de commandes... (Ctrl+C pour arrêter)");
            while (true) {
                ConsumerRecords<String, Commande> records =
                        consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, Commande> record : records) {
                    System.out.printf(
                            "▶ partition=%d, offset=%d, key=%s, commande=%s%n",
                            record.partition(), record.offset(), record.key(), record.value()
                    );
                }
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        }
    }
}