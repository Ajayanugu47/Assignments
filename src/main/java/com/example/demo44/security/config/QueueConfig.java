package com.example.demo44.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Bean
    public BlockingQueue<Integer> workQueue() {
        // Bounded queue; adjust capacity as needed
        return new ArrayBlockingQueue<>(100);
    }
}
