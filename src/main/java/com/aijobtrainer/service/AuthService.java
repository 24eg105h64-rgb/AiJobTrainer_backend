package com.aijobtrainer.service;

import com.aijobtrainer.dto.AuthResponse;
import com.aijobtrainer.dto.LoginRequest;
import com.aijobtrainer.dto.RegisterRequest;
import com.aijobtrainer.dto.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser(String userId);
}
