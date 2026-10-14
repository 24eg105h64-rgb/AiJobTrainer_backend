package com.aijobtrainer.service;

import com.aijobtrainer.dto.ResumeAnalyzeRequest;
import com.aijobtrainer.model.ResumeAnalysis;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {
    ResumeAnalysis analyzeResume(String userId, MultipartFile file, ResumeAnalyzeRequest request);
    List<ResumeAnalysis> getResumeHistory(String userId);
}
