package tn.utm.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public class CommandeSerializer implements Serializer<Commande> {
    private final ObjectMapper mapper = new ObjectMapper();

    public CommandeSerializer() {
        mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // configuration if needed
    }

    @Override
    public byte[] serialize(String topic, Commande data) {
        try {
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing Commande", e);
        }
    }

    @Override
    public void close() {
        // cleanup if needed
    }
}