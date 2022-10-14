package com.keanhive.stich.api.integration.configuration;

import com.keanhive.stich.api.integration.utils.AppProperties;
import com.keanhive.stich.api.integration.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;

/**
 * @author KeanHive
 * @Date 06/10/2022
 */

@Slf4j
@Configuration
@EnableCaching
public class RedisConfiguration {

    @Autowired
    private AppProperties appProperties;

    @Bean
    RedisTemplate<String, String> redisTemplate() {

        if (StringUtils.equalsIgnoreCase(appProperties.getRedisMode(), Constants.REDIS_MODE_STANDALONE)) {
            return getRedisStandaloneTemplate();
        }

        log.debug("redis mode is cluster");
        return getRedisClusterTemplate();
    }

    private RedisTemplate<String, String> getRedisClusterTemplate() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(Arrays.asList(appProperties.getRedisClusterUrl().split(",")));
        redisClusterConfiguration.setPassword(RedisPassword.of(appProperties.getRedisClusterPassword()));
        redisClusterConfiguration.setUsername(appProperties.getRedisClusterUsername());
        JedisConnectionFactory factory = new JedisConnectionFactory(redisClusterConfiguration, jedisPoolConfig);

        RedisTemplate<String, String> redisTemplate = getRedisTemplate();

        factory.afterPropertiesSet();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    private RedisTemplate<String, String> getRedisStandaloneTemplate() {
        RedisTemplate<String, String> redisTemplate = getRedisTemplate();

        RedisStandaloneConfiguration configuration = getRedisConfiguration();

        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().build();
        JedisConnectionFactory factory = new JedisConnectionFactory(configuration, jedisClientConfiguration);
        factory.afterPropertiesSet();
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    private RedisStandaloneConfiguration getRedisConfiguration() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(appProperties.getRedisHost());
        configuration.setPort(appProperties.getRedisPort());
        configuration.setUsername(appProperties.getRedisUsername());
        configuration.setPassword(appProperties.getRedisPassword());
        return configuration;
    }

    private RedisTemplate<String, String> getRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(String.class));
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        return redisTemplate;
    }
}
