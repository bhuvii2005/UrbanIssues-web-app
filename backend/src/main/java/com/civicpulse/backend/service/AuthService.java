package com.civicpulse.backend.service;

import com.civicpulse.backend.dto.AuthRequest;
import com.civicpulse.backend.model.Role;
import com.civicpulse.backend.model.User;
import com.civicpulse.backend.repository.UserRepository;
import com.civicpulse.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.RESIDENT); // Default role is Resident
        
        return userRepository.save(user);
    }

    public Map<String, String> login(AuthRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty() || !passwordEncoder.matches(request.getPassword(), optionalUser.get().getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = optionalUser.get();

        if (user.isBanned()) {
            throw new RuntimeException("Account is banned");
        }

        String token = jwtUtil.generateToken(user);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}
