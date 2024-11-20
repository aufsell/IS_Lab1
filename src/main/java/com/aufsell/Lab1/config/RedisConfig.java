package com.aufsell.Lab1.config;

import com.aufsell.Lab1.dto.PageContent;
import com.aufsell.Lab1.dto.VehicleDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    // RedisTemplate для работы с VehicleDTO
    @Bean
    public RedisTemplate<String, VehicleDTO> vehicleRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, VehicleDTO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, PageContent<VehicleDTO>> pageContentRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, PageContent<VehicleDTO>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}
