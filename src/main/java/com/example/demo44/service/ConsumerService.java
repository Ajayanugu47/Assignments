package com.example.demo44.service;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class ConsumerService implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(ConsumerService.class);

    private final BlockingQueue<Integer> queue;
    private volatile boolean running = false;
    private Thread worker;

    public ConsumerService(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void start() {
        if (running) return;
        running = true;
        worker = new Thread(() -> {
            while (running) {
                try {
                    Integer item = queue.take(); // blocks if empty
                    if (item == Integer.MIN_VALUE) {
                        log.info("Received poison pill -> stopping consumer loop");
                        break;
                    }
                    // process the item
                    log.info("Consumed {}", item);
                    // simulate work
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (running) log.warn("Consumer interrupted");
                }
            }
            running = false;
        }, "demo44-consumer");
        worker.setDaemon(true);
        worker.start();
        log.info("Consumer started");
    }

    @Override public void stop() {
        running = false;
        if (worker != null) worker.interrupt();
        log.info("Consumer stopping");
    }

    @Override public boolean isRunning() { return running; }
    @Override public int getPhase() { return 0; }             // default
    @Override public boolean isAutoStartup() { return true; } // start with context
    @Override public void stop(Runnable callback) { stop(); callback.run(); }
}
