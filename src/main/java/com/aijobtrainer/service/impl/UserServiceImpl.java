package com.aijobtrainer.service.impl;

import com.aijobtrainer.dto.PasswordChangeRequest;
import com.aijobtrainer.dto.ProfileUpdateRequest;
import com.aijobtrainer.dto.UserResponse;
import com.aijobtrainer.exception.BadRequestException;
import com.aijobtrainer.exception.ResourceNotFoundException;
import com.aijobtrainer.model.User;
import com.aijobtrainer.repository.*;
import com.aijobtrainer.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final CodingSubmissionRepository codingSubmissionRepository;
    private final ProgressRepository progressRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           InterviewRepository interviewRepository,
                           ResumeAnalysisRepository resumeAnalysisRepository,
                           CodingSubmissionRepository codingSubmissionRepository,
                           ProgressRepository progressRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.codingSubmissionRepository = codingSubmissionRepository;
        this.progressRepository = progressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.fromUser(user);
    }

    @Override
    public UserResponse updateProfile(String userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }
        if (request.getCollege() != null) {
            user.setCollege(request.getCollege().trim());
        }
        if (request.getDegree() != null) {
            user.setDegree(request.getDegree().trim());
        }
        if (request.getBranch() != null) {
            user.setBranch(request.getBranch().trim());
        }
        if (request.getGraduationYear() != null) {
            user.setGraduationYear(request.getGraduationYear());
        }
        if (request.getSkills() != null) {
            user.setSkills(request.getSkills());
        }
        user.setUpdatedAt(Instant.now());

        User saved = userRepository.save(user);
        return UserResponse.fromUser(saved);
    }

    @Override
    public void updatePassword(String userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    @Override
    public void deleteAccount(String userId) {
        userRepository.deleteById(userId);
        interviewRepository.deleteByUserId(userId);
        resumeAnalysisRepository.deleteByUserId(userId);
        codingSubmissionRepository.deleteByUserId(userId);
        progressRepository.deleteByUserId(userId);
    }
}
