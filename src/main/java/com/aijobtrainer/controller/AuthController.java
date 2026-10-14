package com.aijobtrainer.controller;

import com.aijobtrainer.dto.AuthResponse;
import com.aijobtrainer.dto.LoginRequest;
import com.aijobtrainer.dto.RegisterRequest;
import com.aijobtrainer.dto.UserResponse;
import com.aijobtrainer.security.UserPrincipal;
import com.aijobtrainer.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserResponse user = authService.getCurrentUser(principal.getId());
        return ResponseEntity.ok(Map.of("user", user));
    }
}
