package com.example.market_data_service.config;

import com.aws.protobuf.DealMessages;
import com.aws.protobuf.OrderMessages;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Bean
    Map<String, Object> orderConsumerConfig() {
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "market-data-order-group");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        config.put(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        config.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, OrderMessages.OrderEvent.class);
        return config;
    }

    @Bean
    public ConsumerFactory<String, OrderMessages.OrderEvent> orderConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(orderConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderMessages.OrderEvent> orderKafkaListenerContainerFactory
            (final ConsumerFactory<String, OrderMessages.OrderEvent> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, OrderMessages.OrderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    Map<String, Object> dealConsumerConfig() {
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "market-data-executed-deal-group");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        config.put(KafkaProtobufDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        config.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, DealMessages.DealEvent.class);
        return config;
    }

    @Bean
    public ConsumerFactory<String, DealMessages.DealEvent> dealConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(dealConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DealMessages.DealEvent> dealKafkaListenerContainerFactory
            (final ConsumerFactory<String, DealMessages.DealEvent> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, DealMessages.DealEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
