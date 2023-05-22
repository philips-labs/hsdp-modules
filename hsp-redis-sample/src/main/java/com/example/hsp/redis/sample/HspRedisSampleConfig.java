package com.example.hsp.redis.sample;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class HspRedisSampleConfig {

    @Bean
    public MyRedisClient redisClient() {
        return new MyRedisClient();
    }

}
