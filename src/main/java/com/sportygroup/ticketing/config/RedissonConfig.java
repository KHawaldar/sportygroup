package com.sportygroup.ticketing.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.redis.host")
    public RedissonClient redissonClient(org.springframework.core.env.Environment env){
        String redisHost = env.getProperty("spring.redis.host");
        String redisPort = env.getProperty("spring.redis.port");
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" +redisHost + ":" +redisPort);
        return Redisson.create(config);


    }
}
