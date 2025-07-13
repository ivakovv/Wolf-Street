package com.example.market_data_service.config;

import com.example.market_data_service.dto.ohlc.Ohlc;
import com.example.market_data_service.dto.orderbook.OrderBookEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port) {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean
    public ObjectMapper redisObjectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    @Bean
    public RedisTemplate<String, OrderBookEntry> orderBookRedisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper redisObjectMapper) {
        RedisTemplate<String, OrderBookEntry> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(redisObjectMapper, OrderBookEntry.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(redisObjectMapper, OrderBookEntry.class));
        return template;
    }
    @Bean
    public RedisTemplate<String, Ohlc> ohlcRedisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper redisObjectMapper) {
        RedisTemplate<String, Ohlc> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(redisObjectMapper, Ohlc.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(redisObjectMapper, Ohlc.class));
        return template;
    }
}
