package tn.utm.kafka;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class CommandeDeserializer implements Deserializer<Commande> {
    private final ObjectMapper mapper = new ObjectMapper();

    public CommandeDeserializer() {
        mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // configuration if needed
    }

    @Override
    public Commande deserialize(String topic, byte[] data) {
        try {
            return mapper.readValue(data, Commande.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing Commande", e);
        }
    }

    @Override
    public void close() {
        // cleanup if needed
    }
}