package com.aijobtrainer.service;

import com.aijobtrainer.model.Progress;

public interface ProgressService {
    Progress getUserProgress(String userId);
    void recordInterviewCompletion(String userId, int score);
    void recordResumeAnalysis(String userId, int resumeScore);
    void recordCodingSolve(String userId);
}
