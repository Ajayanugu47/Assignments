    package com.example.demo44.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

    @Bean(name = "ioExecutor")
    public ExecutorService ioExecutor() {
        // Use a bounded pool; tune for your machine
        return Executors.newFixedThreadPool(4);
    }
}


