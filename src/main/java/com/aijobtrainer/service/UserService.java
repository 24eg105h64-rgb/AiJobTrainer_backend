package com.aijobtrainer.service;

import com.aijobtrainer.dto.PasswordChangeRequest;
import com.aijobtrainer.dto.ProfileUpdateRequest;
import com.aijobtrainer.dto.UserResponse;

public interface UserService {
    UserResponse getProfile(String userId);
    UserResponse updateProfile(String userId, ProfileUpdateRequest request);
    void updatePassword(String userId, PasswordChangeRequest request);
    void deleteAccount(String userId);
}
