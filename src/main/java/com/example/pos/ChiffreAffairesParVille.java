package com.example.pos;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.google.gson.Gson;

public class ChiffreAffairesParVille {
    private static final Gson gson = new Gson();
    private static final Map<String, Double> caParVille = new HashMap<>();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ca-1");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("pos-events"));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (caParVille) {
                    System.out.println("Chiffre d'affaires par ville:");
                    caParVille.forEach((ville, ca) -> System.out.println(ville + ": " + ca));
                }
            }
        }, 0, 5000);

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    PosEvent event = gson.fromJson(record.value(), PosEvent.class);
                    synchronized (caParVille) {
                        Double m = event.getMontant();
                        double montant = m != null ? m : 0.0;
                        if ("VENTE".equals(event.getType())) {
                            caParVille.put(event.getVille(), caParVille.getOrDefault(event.getVille(), 0.0) + montant);
                        } else if ("RETOUR".equals(event.getType())) {
                            caParVille.put(event.getVille(), caParVille.getOrDefault(event.getVille(), 0.0) - montant);
                        }
                    }
                }
                consumer.commitSync();
            }
        } finally {
            consumer.close();
            timer.cancel();
        }
    }
}