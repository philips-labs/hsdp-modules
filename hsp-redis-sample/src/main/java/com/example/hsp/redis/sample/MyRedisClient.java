package com.example.hsp.redis.sample;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class MyRedisClient {
    private static final String MASTER_NAME = "redis-your-instance";
    private static final String PASSWORD = "your-password";
    private static final String HOSTNAME = "redis-your-instance.svc-2.na1.cluster.hsdp.io";
    private static final int PORT = 26379;

    public MyRedisClient() {
        initConnect();
    }

    private void initConnect() {
        try {
            log.info("Creating direct connect using lettuce...");
            RedisURI redisURI = RedisURI.builder()
                    .withSentinel(HOSTNAME, PORT, PASSWORD)
                    .withSentinelMasterId(MASTER_NAME)
                    .withStartTls(true)
                    .withPassword(PASSWORD.toCharArray())
                    .build();
            log.info("Creating redis client using uri - {}", redisURI.toURI().toString());
            RedisClient client = RedisClient.create(redisURI);
            log.info("Connecting using redis client");
            StatefulRedisConnection<String, String> connection = client.connect();
            log.info("Getting hold of sync command");
            RedisCommands<String, String> syncCommands = connection.sync();
            log.info("Adding sample data");
            syncCommands.set("mykey", "myvalue");
            log.info("Done. Shutting down redis client");
            client.shutdown();
        } catch (Exception ex) {
            log.error("Failed to init redis - {}", ex.getMessage());
        }
    }
}
