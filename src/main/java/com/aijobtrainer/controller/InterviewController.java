package com.aijobtrainer.controller;

import com.aijobtrainer.dto.InterviewSubmitRequest;
import com.aijobtrainer.model.Interview;
import com.aijobtrainer.model.InterviewQuestionItem;
import com.aijobtrainer.security.UserPrincipal;
import com.aijobtrainer.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitInterview(@AuthenticationPrincipal UserPrincipal principal,
                                                               @RequestBody InterviewSubmitRequest request) {
        Interview interview = interviewService.submitInterview(principal.getId(), request);
        return new ResponseEntity<>(Map.of(
                "message", "Interview submitted successfully.",
                "interview", interview
        ), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserInterviews(@AuthenticationPrincipal UserPrincipal principal) {
        List<Interview> interviews = interviewService.getUserInterviews(principal.getId());
        return ResponseEntity.ok(Map.of("interviews", interviews));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getInterviewById(@AuthenticationPrincipal UserPrincipal principal,
                                                                @PathVariable String id) {
        Interview interview = interviewService.getInterviewById(id, principal.getId());
        return ResponseEntity.ok(Map.of("interview", interview));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteInterview(@AuthenticationPrincipal UserPrincipal principal,
                                                               @PathVariable String id) {
        interviewService.deleteInterview(id, principal.getId());
        return ResponseEntity.ok(Map.of("message", "Interview deleted."));
    }

    @GetMapping("/questions")
    public ResponseEntity<Map<String, Object>> getInterviewQuestions(
            @RequestParam(defaultValue = "Technical") String type,
            @RequestParam(defaultValue = "Medium") String difficulty,
            @RequestParam(defaultValue = "5") int count
    ) {
        List<InterviewQuestionItem> questions = interviewService.generateQuestions(type, difficulty, count);
        return ResponseEntity.ok(Map.of("questions", questions));
    }
}
