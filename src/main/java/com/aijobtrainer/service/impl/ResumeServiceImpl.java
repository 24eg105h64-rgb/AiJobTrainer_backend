package com.aijobtrainer.service.impl;

import com.aijobtrainer.dto.ResumeAnalyzeRequest;
import com.aijobtrainer.model.ResumeAnalysis;
import com.aijobtrainer.repository.ResumeAnalysisRepository;
import com.aijobtrainer.service.AIService;
import com.aijobtrainer.service.ProgressService;
import com.aijobtrainer.service.ResumeService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final AIService aiService;
    private final ProgressService progressService;

    public ResumeServiceImpl(ResumeAnalysisRepository resumeAnalysisRepository,
                             AIService aiService,
                             ProgressService progressService) {
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.aiService = aiService;
        this.progressService = progressService;
    }

    @Override
    public ResumeAnalysis analyzeResume(String userId, MultipartFile file, ResumeAnalyzeRequest request) {
        String fileName = "";
        String extractedText = "";

        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();
            try {
                extractedText = aiService.extractText(file.getBytes(), fileName);
            } catch (Exception ignored) {
            }
        }

        String jobTitle = (request != null && request.getJobTitle() != null && !request.getJobTitle().isBlank())
                ? request.getJobTitle()
                : "Full Stack Developer";

        String summary = (request != null && request.getSummary() != null) ? request.getSummary() : "";
        List<String> skills = (request != null && request.getSkills() != null) ? request.getSkills() : new ArrayList<>();

        String fullContent = extractedText + " " + summary;

        AIService.ResumeEvaluation eval = aiService.evaluateResume(fullContent, jobTitle, skills);

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setUserId(userId);
        analysis.setFileName(fileName);
        analysis.setJobTitle(jobTitle);
        analysis.setSummary(summary.length() > 300 ? summary.substring(0, 300) + "..." : summary);
        analysis.setScore(eval.score);
        analysis.setSkillsScore(eval.skillsScore);
        analysis.setEducationScore(eval.educationScore);
        analysis.setProjectsScore(eval.projectsScore);
        analysis.setExperienceScore(eval.experienceScore);
        analysis.setFormattingScore(eval.formattingScore);
        analysis.setKeywordScore(eval.keywordScore);
        analysis.setStrengths(eval.strengths);
        analysis.setMissingSkills(eval.missingSkills);
        analysis.setSuggestions(eval.suggestions);
        analysis.setCreatedAt(Instant.now());

        ResumeAnalysis saved = resumeAnalysisRepository.save(analysis);
        progressService.recordResumeAnalysis(userId, eval.score);

        return saved;
    }

    @Override
    public List<ResumeAnalysis> getResumeHistory(String userId) {
        return resumeAnalysisRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
