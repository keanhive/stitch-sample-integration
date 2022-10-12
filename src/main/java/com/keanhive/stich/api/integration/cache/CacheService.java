package com.keanhive.stich.api.integration.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author KeanHive
 * @Date 06/10/2022
 */

@Slf4j
@Service
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class CacheService extends CachingConfigurerSupport {

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void setItem(final String key, String value, int timeOut) {
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, timeOut, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Exception trying to save {}, value {}", key, value, e);
        }
    }

    public String getItem(final String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Exception trying to get {} from redis", key, e);
        }

        return null;
    }
}
