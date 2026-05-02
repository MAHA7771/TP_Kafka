package tn.utm.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class SimpleProducer {
    public static void main(String[] args) {
        // Configuration du producteur
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        // Garanties de durabilité
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // attend l'ack de tous les replicas
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // 3 tentatives en cas d'échec
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // évite les doublons

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            for (int i = 1; i <= 10; i++) {
                String key = "client-" + (i % 3);
                String value = "Achat numéro " + i + " (" + (50 + i * 17) + " DT)";
                ProducerRecord<String, String> record =
                        new ProducerRecord<>("ventes", key, value);
                // Envoi asynchrone avec callback
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        System.err.println("Échec d'envoi : " + exception.getMessage());
                    } else {
                        System.out.printf(
                                "✓ Envoyé — partition=%d, offset=%d, key=%s%n",
                                metadata.partition(), metadata.offset(), key
                        );
                    }
                });
            }
            // flush() force l'envoi des messages bufferisés avant la fermeture
            producer.flush();
        } catch (Exception e) {
            System.err.println("Erreur dans le producteur: " + e.getMessage());
        }
    }
}