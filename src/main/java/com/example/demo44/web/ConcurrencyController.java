package com.example.demo44.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo44.service.ExecutorDemoService;

@RestController
@RequestMapping("/concurrency")
public class ConcurrencyController {

    private final ExecutorDemoService svc;

    public ConcurrencyController(ExecutorDemoService svc) {
        this.svc = svc;
    }

    // Runnable vs Callable demo
    @GetMapping("/executor")
    public String executorDemo() {
        return svc.demoExecutorService();
    }

    // CompletableFuture: 3 APIs in parallel and combine
    @GetMapping("/cf")
    public String completableFutureDemo() {
        return svc.demoCompletableFuture();
    }
}
