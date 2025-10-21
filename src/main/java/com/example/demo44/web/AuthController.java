package com.example.jwtdemo.web;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwtdemo.security.jwt.JwtUtil;

import jakarta.validation.constraints.NotBlank;

record LoginRequest(@NotBlank String username, @NotBlank String password) {}
record TokenResponse(String token) {}

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return new TokenResponse(jwtUtil.generateToken(username, role));
    }
}
