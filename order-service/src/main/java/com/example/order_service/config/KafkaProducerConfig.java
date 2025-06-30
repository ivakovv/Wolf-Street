package com.example.order_service.config;

import com.aws.protobuf.OrderCreateMessage;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;



@Configuration
public class KafkaProducerConfig {

    private String kafkaServer = "localhost:9092";
    private String schemaRegistryUrl = "http://localhost:8081";

    @Bean
    Map<String, Object> producerConfigs() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class);
        config.put(KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        config.put(KafkaProtobufSerializerConfig.AUTO_REGISTER_SCHEMAS, true);
        config.put(KafkaProtobufSerializerConfig.NORMALIZE_SCHEMAS, true);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        return config;
    }

    @Bean
    public ProducerFactory<String, OrderCreateMessage.OrderCreatedEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, OrderCreateMessage.OrderCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}