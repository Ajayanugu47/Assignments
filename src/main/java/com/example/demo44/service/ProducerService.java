package com.example.demo44.service;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {
    private static final Logger log = LoggerFactory.getLogger(ProducerService.class);
    private final BlockingQueue<Integer> queue;

    public ProducerService(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    public void produce(int item) {
        try {
            queue.put(item); // blocks if full
            log.info("Produced {}", item);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Producer interrupted while putting {}", item);
        }
    }

    /** Optional: send a poison pill to request consumer shutdown */
    public void stopConsumer() {
        try {
            queue.put(Integer.MIN_VALUE);
            log.info("Sent poison pill");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
