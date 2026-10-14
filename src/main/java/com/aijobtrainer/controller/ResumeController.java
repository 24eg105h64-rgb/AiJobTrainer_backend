package com.aijobtrainer.controller;

import com.aijobtrainer.dto.ResumeAnalyzeRequest;
import com.aijobtrainer.model.ResumeAnalysis;
import com.aijobtrainer.security.UserPrincipal;
import com.aijobtrainer.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> analyzeJson(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ResumeAnalyzeRequest request
    ) {
        ResumeAnalysis analysis = resumeService.analyzeResume(principal.getId(), null, request);
        return new ResponseEntity<>(Map.of(
                "message", "Resume analyzed successfully.",
                "analysis", analysis
        ), HttpStatus.CREATED);
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> analyzeMultipart(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "jobTitle", required = false, defaultValue = "Full Stack Developer") String jobTitle,
            @RequestParam(value = "summary", required = false, defaultValue = "") String summary,
            @RequestParam(value = "skills", required = false) String skills
    ) {
        ResumeAnalyzeRequest req = new ResumeAnalyzeRequest();
        req.setJobTitle(jobTitle);
        req.setSummary(summary);
        if (skills != null && !skills.isBlank()) {
            List<String> list = Arrays.stream(skills.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            req.setSkills(list);
        }

        ResumeAnalysis analysis = resumeService.analyzeResume(principal.getId(), file, req);
        return new ResponseEntity<>(Map.of(
                "message", "Resume analyzed successfully.",
                "analysis", analysis
        ), HttpStatus.CREATED);
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory(@AuthenticationPrincipal UserPrincipal principal) {
        List<ResumeAnalysis> analyses = resumeService.getResumeHistory(principal.getId());
        return ResponseEntity.ok(Map.of("analyses", analyses));
    }
}
