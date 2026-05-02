package com.example.pos;

import java.time.Instant;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.google.gson.Gson;

public class SimulateurCaisse {
    private static final String[] villes = {"Tunis", "Sousse", "Sfax", "Bizerte", "Gabès"};
    private static final String[] types = {"VENTE", "RETOUR", "OUVERTURE"};
    private static final int[] typeWeights = {70, 10, 20};
    private static final Random random = new Random();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable sendEvent = new Runnable() {
            @Override
            public void run() {
                String ville = villes[random.nextInt(villes.length)];
                String type = getRandomType();
                PosEvent event = new PosEvent();
                event.setType(type);
                event.setVille(ville);
                event.setIdCaisse("CAISSE-" + ville.toUpperCase() + "-" + random.nextInt(10));
                event.setTimestamp(Instant.now().toString());
                if ("VENTE".equals(type) || "RETOUR".equals(type)) {
                    event.setMontant(5 + random.nextDouble() * 495);
                    event.setProduits(Arrays.asList("pain", "lait", "fromage"));
                }
                String json = gson.toJson(event);
                ProducerRecord<String, String> record = new ProducerRecord<>("pos-events", ville, json);
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                    }
                });
                // Schedule next
                long delay = 100 + random.nextInt(401);
                executor.schedule(this, delay, TimeUnit.MILLISECONDS);
            }
        };

        // Start first
        executor.schedule(sendEvent, 0, TimeUnit.MILLISECONDS);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            producer.close();
        }));

        // Keep main thread alive
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String getRandomType() {
        int total = 0;
        for (int w : typeWeights) total += w;
        int rand = random.nextInt(total);
        int sum = 0;
        for (int i = 0; i < types.length; i++) {
            sum += typeWeights[i];
            if (rand < sum) return types[i];
        }
        return types[0];
    }
}