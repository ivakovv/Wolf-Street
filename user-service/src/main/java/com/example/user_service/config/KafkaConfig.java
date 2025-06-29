package com.example.user_service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.example.user_service.dto.event.UserRegistredEvent;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Bean
    Map<String, Object> producerConfigs(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 30000);
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 2000);
        return config;
    }
    @Bean
    public ProducerFactory<String, UserRegistredEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }
    @Bean
    public KafkaTemplate<String, UserRegistredEvent> kafkaTemplate(ProducerFactory<String, UserRegistredEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
