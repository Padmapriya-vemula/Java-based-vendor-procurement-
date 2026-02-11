package com.example.spvms.controllers;

import com.example.spvms.dto.LoginRequest;
import com.example.spvms.dto.RegisterRequest;
import com.example.spvms.dto.RegisterResponse;
import com.example.spvms.model.User;
import com.example.spvms.repository.UserRepository;
import com.example.spvms.service.AuthService;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.spvms.config.JwtService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthController(AuthService authService, JwtService jwtService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;

    }

    /* ================= REGISTER ================= */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    /* ================= SAMPLE GET ================= */
    @GetMapping("/test")
    public String test() {
        return "Auth API working";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User has no roles assigned");
            }

            String role = user.getRoles().iterator().next().getName();

            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role.toUpperCase();
            }

            String token = jwtService.generateToken(user.getEmail(), role);

            return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", role
                )
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

}