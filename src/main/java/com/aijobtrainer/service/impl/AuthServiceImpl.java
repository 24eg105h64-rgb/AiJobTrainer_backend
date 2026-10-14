package com.aijobtrainer.service.impl;

import com.aijobtrainer.dto.AuthResponse;
import com.aijobtrainer.dto.LoginRequest;
import com.aijobtrainer.dto.RegisterRequest;
import com.aijobtrainer.dto.UserResponse;
import com.aijobtrainer.exception.BadRequestException;
import com.aijobtrainer.model.Progress;
import com.aijobtrainer.model.User;
import com.aijobtrainer.repository.ProgressRepository;
import com.aijobtrainer.repository.UserRepository;
import com.aijobtrainer.security.JwtService;
import com.aijobtrainer.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProgressRepository progressRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           ProgressRepository progressRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.progressRepository = progressRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (request.getConfirmPassword() != null && !request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("This email is already registered");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCollege(request.getCollege());
        user.setDegree(request.getDegree());
        user.setBranch(request.getBranch());
        user.setGraduationYear(request.getGraduationYear());
        user.setRole("USER");
        user.setSkills(new ArrayList<>());
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        // Initialize user progress
        Progress progress = new Progress(savedUser.getId());
        progressRepository.save(progress);

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
        return new AuthResponse("Registration successful", token, UserResponse.fromUser(savedUser));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse("Login successful", token, UserResponse.fromUser(user));
    }

    @Override
    public UserResponse getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return UserResponse.fromUser(user);
    }
}
