package com.aijobtrainer.service;

import com.aijobtrainer.dto.StatisticsResponse;
import com.aijobtrainer.dto.UserResponse;
import com.aijobtrainer.model.Interview;

import java.util.List;

public interface AdminService {
    StatisticsResponse getStatistics();
    List<UserResponse> getAllUsers();
    UserResponse updateUserRole(String userId, String role);
    void deleteUser(String userId);
    List<Interview> getAllInterviews();
}
