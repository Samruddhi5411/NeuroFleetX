package com.example.NeuroFleetBackend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.NeuroFleetBackend.model.User;
import com.example.NeuroFleetBackend.repository.UserRepository;
import com.example.NeuroFleetBackend.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
                
                System.out.println("Token generated for user: " + user.getUsername() + " with role: " + user.getRole());
                
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("role", user.getRole().toUpperCase());
                response.put("username", user.getUsername());
                
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        Optional<User> existing = userRepository.findByUsername(user.getUsername());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Default role is CUSTOMER if not specified
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("CUSTOMER");
        }
        
        // Validate role
        String role = user.getRole().toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("FLEET_MANAGER") && 
            !role.equals("DRIVER") && !role.equals("CUSTOMER")) {
            return ResponseEntity.badRequest().body("Invalid role. Must be ADMIN, FLEET_MANAGER, DRIVER, or CUSTOMER");
        }
        
        user.setRole(role);
        userRepository.save(user);
        
        return ResponseEntity.ok("User registered successfully with role: " + role);
    }
}