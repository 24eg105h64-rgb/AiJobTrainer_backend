package com.aijobtrainer.service.impl;

import com.aijobtrainer.dto.StatisticsResponse;
import com.aijobtrainer.dto.UserResponse;
import com.aijobtrainer.exception.BadRequestException;
import com.aijobtrainer.exception.ResourceNotFoundException;
import com.aijobtrainer.model.Interview;
import com.aijobtrainer.model.User;
import com.aijobtrainer.repository.*;
import com.aijobtrainer.service.AdminService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final CodingSubmissionRepository codingSubmissionRepository;
    private final ProgressRepository progressRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            InterviewRepository interviewRepository,
                            QuestionRepository questionRepository,
                            ResumeAnalysisRepository resumeAnalysisRepository,
                            CodingSubmissionRepository codingSubmissionRepository,
                            ProgressRepository progressRepository) {
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.codingSubmissionRepository = codingSubmissionRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public StatisticsResponse getStatistics() {
        long totalUsers = userRepository.count();
        long totalQuestions = questionRepository.count();
        long totalInterviews = interviewRepository.count();

        List<Interview> interviews = interviewRepository.findAll();
        int avgScore = 0;
        if (!interviews.isEmpty()) {
            int total = 0;
            for (Interview inv : interviews) {
                total += inv.getScore();
            }
            avgScore = (int) Math.round((double) total / interviews.size());
        }

        return new StatisticsResponse(totalUsers, totalInterviews, avgScore, totalQuestions, totalUsers);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    @Override
    public UserResponse updateUserRole(String userId, String role) {
        String normalized = role != null ? role.toUpperCase().trim() : "USER";
        if (!normalized.equals("USER") && !normalized.equals("ADMIN")) {
            throw new BadRequestException("Role must be USER or ADMIN");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(normalized);
        user.setUpdatedAt(Instant.now());
        User saved = userRepository.save(user);

        return UserResponse.fromUser(saved);
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.deleteById(userId);
        interviewRepository.deleteByUserId(userId);
        resumeAnalysisRepository.deleteByUserId(userId);
        codingSubmissionRepository.deleteByUserId(userId);
        progressRepository.deleteByUserId(userId);
    }

    @Override
    public List<Interview> getAllInterviews() {
        return interviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
