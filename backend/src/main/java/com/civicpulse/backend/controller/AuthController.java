package com.civicpulse.backend.controller;

import com.civicpulse.backend.dto.AuthRequest;
import com.civicpulse.backend.model.User;
import com.civicpulse.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // For local development only, adjust as needed in production
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid AuthRequest request) {
        try {
            User newUser = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User created successfully", "userId", newUser.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {
            Map<String, String> tokenResponse = authService.login(request);
            return ResponseEntity.ok(tokenResponse);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Account is banned")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
}
