package com.aijobtrainer.service.impl;

import com.aijobtrainer.model.Interview;
import com.aijobtrainer.model.Progress;
import com.aijobtrainer.model.ResumeAnalysis;
import com.aijobtrainer.repository.CodingSubmissionRepository;
import com.aijobtrainer.repository.InterviewRepository;
import com.aijobtrainer.repository.ProgressRepository;
import com.aijobtrainer.repository.ResumeAnalysisRepository;
import com.aijobtrainer.service.ProgressService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;
    private final InterviewRepository interviewRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final CodingSubmissionRepository codingSubmissionRepository;

    public ProgressServiceImpl(ProgressRepository progressRepository,
                               InterviewRepository interviewRepository,
                               ResumeAnalysisRepository resumeAnalysisRepository,
                               CodingSubmissionRepository codingSubmissionRepository) {
        this.progressRepository = progressRepository;
        this.interviewRepository = interviewRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.codingSubmissionRepository = codingSubmissionRepository;
    }

    @Override
    public Progress getUserProgress(String userId) {
        return progressRepository.findByUserId(userId).orElseGet(() -> {
            Progress fresh = recalculateProgress(userId);
            return progressRepository.save(fresh);
        });
    }

    @Override
    public void recordInterviewCompletion(String userId, int score) {
        Progress p = recalculateProgress(userId);
        progressRepository.save(p);
    }

    @Override
    public void recordResumeAnalysis(String userId, int resumeScore) {
        Progress p = recalculateProgress(userId);
        progressRepository.save(p);
    }

    @Override
    public void recordCodingSolve(String userId) {
        Progress p = recalculateProgress(userId);
        progressRepository.save(p);
    }

    private Progress recalculateProgress(String userId) {
        List<Interview> interviews = interviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<ResumeAnalysis> analyses = resumeAnalysisRepository.findByUserIdOrderByCreatedAtDesc(userId);
        long solvedCount = codingSubmissionRepository.findByUserIdAndStatus(userId, "ACCEPTED").size();

        int avgScore = 0;
        if (!interviews.isEmpty()) {
            int total = 0;
            for (Interview inv : interviews) {
                total += inv.getScore();
            }
            avgScore = (int) Math.round((double) total / interviews.size());
        }

        int latestResume = analyses.isEmpty() ? 0 : analyses.get(0).getScore();

        Progress p = progressRepository.findByUserId(userId).orElse(new Progress(userId));
        p.setInterviewsCompleted(interviews.size());
        p.setAverageInterviewScore(avgScore);
        p.setResumeScore(latestResume);
        p.setCodingProblemsSolved((int) solvedCount);
        p.setCurrentStreak(Math.max(1, Math.min(14, interviews.size() + (int) solvedCount)));

        Map<String, Integer> skills = new HashMap<>();
        skills.put("Communication", Math.max(65, Math.min(98, avgScore > 0 ? avgScore : 75)));
        skills.put("Technical", Math.max(60, Math.min(99, avgScore > 0 ? avgScore + 5 : 80)));
        skills.put("Resume", Math.max(60, Math.min(98, latestResume > 0 ? latestResume : 70)));
        skills.put("Coding", Math.min(98, (int) Math.max(60, 60 + solvedCount * 8)));
        skills.put("Problem Solving", Math.max(62, Math.min(96, avgScore > 0 ? avgScore + 2 : 78)));
        p.setSkills(skills);

        p.setWeeklyActivity(List.of(2, 4, 5, 3, 6, 2, 1));
        p.setUpdatedAt(Instant.now());
        return p;
    }
}
