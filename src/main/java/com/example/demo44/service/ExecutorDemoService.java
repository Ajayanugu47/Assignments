package com.example.demo44.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class ExecutorDemoService {
    private final ExecutorService ioExecutor;
    private static final Random RND = new Random();

    public ExecutorDemoService(ExecutorService ioExecutor) {
        this.ioExecutor = ioExecutor;
    }

    /* 1) Runnable vs Callable */
    public String demoExecutorService() {
        StringBuilder sb = new StringBuilder();
        Future<?> f1 = ioExecutor.submit(() -> log(sb, "Runnable: start"));
        Future<Integer> f2 = ioExecutor.submit(() -> {
            log(sb, "Callable: start");
            sleep(300);
            return 42;
        });
        Future<String> f3 = ioExecutor.submit(() -> {
            log(sb, "Callable-slow: start");
            sleep(1_500);
            return "slow-result";
        });

        try {
            f1.get(); // Runnable returns null
            Integer ans = f2.get();
            log(sb, "Callable result = " + ans);

            try {
                String slow = f3.get(500, TimeUnit.MILLISECONDS);
                log(sb, "Slow result = " + slow);
            } catch (TimeoutException te) {
                log(sb, "Timed out… cancelling slow task");
                f3.cancel(true);
            }
        } catch (Exception e) {
            log(sb, "Error: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }
        return sb.toString();
    }

    /* 2) CompletableFuture: 3 APIs in parallel and combine */
    public String demoCompletableFuture() {
        Instant t0 = Instant.now();

        CompletableFuture<String> apiA =
                CompletableFuture.supplyAsync(() -> callApi("A", 400), ioExecutor)
                        .orTimeout(1, TimeUnit.SECONDS)
                        .exceptionally(ex -> "A:ERROR(" + ex.getClass().getSimpleName() + ")");

        CompletableFuture<String> apiB =
                CompletableFuture.supplyAsync(() -> callApi("B", 700), ioExecutor)
                        .completeOnTimeout("B:DEFAULT", 1, TimeUnit.SECONDS);

        CompletableFuture<String> apiC =
                CompletableFuture.supplyAsync(() -> callApi("C", 300), ioExecutor);

        String combined = CompletableFuture.allOf(apiA, apiB, apiC)
                .thenApply(v -> {
                    List<String> parts = Arrays.asList(apiA.join(), apiB.join(), apiC.join());
                    return parts.stream().collect(Collectors.joining(" | "));
                })
                .join();

        long ms = Duration.between(t0, Instant.now()).toMillis();
        return "Combined = " + combined + "  (elapsed ~" + ms + " ms)";
    }

    private String callApi(String name, int baseMillis) {
        int delay = baseMillis + RND.nextInt(250);
        for (int spent = 0; spent < delay; spent += 50) {
            sleep(50);
            if (Thread.currentThread().isInterrupted()) {
                throw new CancellationException("API-" + name + " interrupted");
            }
        }
        return name + ":OK(" + delay + "ms)";
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static void log(StringBuilder sb, String msg) {
        sb.append("[").append(java.time.LocalTime.now()).append("] ")
          .append(Thread.currentThread().getName()).append(" :: ")
          .append(msg).append("\n");
    }

    /** Graceful shutdown; escalates to shutdownNow() if needed */
    @PreDestroy
    public void shutdownPool() {
        ioExecutor.shutdown();
        try {
            if (!ioExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                List<Runnable> dropped = ioExecutor.shutdownNow();
                // Optionally log dropped.size()
                ioExecutor.awaitTermination(300, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ioExecutor.shutdownNow();
        }
    }
}
