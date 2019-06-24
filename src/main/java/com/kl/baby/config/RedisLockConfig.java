package com.kl.baby.config;

import com.crossoverjie.distributed.constant.RedisToolsConstant;
import com.crossoverjie.distributed.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * @author Administrator
 */
@Configuration
@DependsOn(value = {"redisConfiguration"})
public class RedisLockConfig {


    private final JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    public RedisLockConfig(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    @Bean
    public RedisLock build() {
        return new RedisLock.Builder(jedisConnectionFactory, RedisToolsConstant.SINGLE)
                .lockPrefix("lock_")
                .sleepTime(10)
                .build();
    }
}