package com.example.analytic_service.config;

import java.util.HashMap;
import java.util.Map;

import com.aws.protobuf.DealMessages;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;


import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.schema-registry-url}")
    private String schemaRegistryUrl;
    @Bean
    Map<String, Object> consumerConfig(){
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "deals-group");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        config.put(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        config.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, DealMessages.DealEvent.class);
        return config;
    }

    @Bean
    public ConsumerFactory<String, DealMessages.DealEvent> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DealMessages.DealEvent> kafkaListenerContainerFactory
            (final ConsumerFactory<String, DealMessages.DealEvent> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, DealMessages.DealEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
