package tn.utm.kafka;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonProducer {
    public static void main(String[] args) {
        // Configuration du producteur
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        // Garanties de durabilité
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            for (int i = 1; i <= 5; i++) {
                Commande commande = new Commande(
                        "cmd-" + i,
                        new Date(),
                        Arrays.asList("Article " + i, "Article " + (i+1)),
                        100.0 + i * 50
                );
                String json = mapper.writeValueAsString(commande);
                ProducerRecord<String, String> record =
                        new ProducerRecord<>("commandes", commande.getId(), json);
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
        } catch (Exception e) {
        }
    }
}