package com.example.portfolio_service.config;

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

import com.aws.protobuf.UserMessages;

import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.schema-registry-url}")
    private String schemaRegistryUrl;
    @Bean
    Map<String, Object> userConsumerConfig(){
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "user-created-group");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        config.put(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        config.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, UserMessages.UserCreatedEvent.class);
        return config;
    }

    @Bean
    public ConsumerFactory<String, UserMessages.UserCreatedEvent> userConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(userConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserMessages.UserCreatedEvent> userKafkaListenerContainerFactory
            (final ConsumerFactory<String, UserMessages.UserCreatedEvent> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, UserMessages.UserCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    Map<String, Object> tradeConsumerConfig(){
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "executed-deal-group");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        config.put(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        config.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, DealMessages.DealEvent.class);
        return config;
    }

    @Bean
    public ConsumerFactory<String, DealMessages.DealEvent> tradeConsumerFactory(){
        return new DefaultKafkaConsumerFactory<>(tradeConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DealMessages.DealEvent> dealKafkaListenerContainerFactory
            (final ConsumerFactory<String, DealMessages.DealEvent> consumerFactory){
        final ConcurrentKafkaListenerContainerFactory<String, DealMessages.DealEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
