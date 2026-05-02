package tn.utm.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class CustomProducer {
    public static void main(String[] args) {
        // Configuration du producteur
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                CommandeSerializer.class.getName());
        // Garanties de durabilité
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        try (Producer<String, Commande> producer = new KafkaProducer<>(props)) {
            for (int i = 1; i <= 5; i++) {
                Commande commande = new Commande(
                        "cmd-" + i,
                        new Date(),
                        Arrays.asList("Article " + i, "Article " + (i+1)),
                        100.0 + i * 50
                );
                ProducerRecord<String, Commande> record =
                        new ProducerRecord<>("commandes", commande.getId(), commande);
                // Envoi asynchrone avec callback
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        System.err.println("Échec d'envoi : " + exception.getMessage());
                    } else {
                        System.out.printf(
                                "✓ Envoyé — partition=%d, offset=%d, key=%s%n",
                                metadata.partition(), metadata.offset(), commande.getId()
                        );
                    }
                });
            }
            producer.flush();
        }
    }
}