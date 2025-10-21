package com.example.jwtdemo.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/user/hello")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String userHello() {
        return "Hello USER or ADMIN!";
    }

    @GetMapping("/admin/hello")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminHello() {
        return "Hello ADMIN!";
    }
}
