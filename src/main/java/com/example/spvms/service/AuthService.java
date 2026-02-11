package com.example.spvms.service;

import com.example.spvms.dto.RegisterRequest;
import com.example.spvms.dto.RegisterResponse;
import com.example.spvms.model.Role;
import com.example.spvms.model.User;
import com.example.spvms.repository.RoleRepository;
import com.example.spvms.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /* ================= REGISTER USER ================= */

    public RegisterResponse register(RegisterRequest request) {

        // Duplicate email check
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create User
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        // Assign roles
        Set<Role> roles = new HashSet<>();

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() ->
                                new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        } else {
            // Default role - try ADMIN first, fallback to VENDOR
            Role defaultRole = roleRepository.findByName("ADMIN")
                    .or(() -> roleRepository.findByName("VENDOR"))
                    .orElseThrow(() ->
                            new RuntimeException("No default role found"));
            roles.add(defaultRole);
        }

        user.setRoles(roles);

        // Save user
        User savedUser = userRepository.save(user);

        // Response
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }
}
