package com.civicpulse.backend.controller;

import com.civicpulse.backend.model.User;
import com.civicpulse.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*") // For local dev
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PatchMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleBanStatus(@PathVariable UUID id, @RequestBody Map<String, Boolean> payload) {
        if (!payload.containsKey("banned")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing 'banned' boolean field"));
        }

        return userRepository.findById(id).map(user -> {
            user.setBanned(payload.get("banned"));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Ban status updated successfully"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
