package com.aijobtrainer.controller;

import com.aijobtrainer.dto.CodingSubmitRequest;
import com.aijobtrainer.model.CodingProblem;
import com.aijobtrainer.model.CodingSubmission;
import com.aijobtrainer.security.UserPrincipal;
import com.aijobtrainer.service.CodingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coding")
public class CodingController {

    private final CodingService codingService;

    public CodingController(CodingService codingService) {
        this.codingService = codingService;
    }

    @GetMapping("/problems")
    public ResponseEntity<Map<String, Object>> getProblems() {
        List<CodingProblem> problems = codingService.getAllProblems();
        return ResponseEntity.ok(Map.of("problems", problems));
    }

    @GetMapping("/problems/{id}")
    public ResponseEntity<CodingProblem> getProblemById(@PathVariable String id) {
        CodingProblem problem = codingService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitCode(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CodingSubmitRequest request
    ) {
        CodingSubmission submission = codingService.submitCode(principal.getId(), request);
        return new ResponseEntity<>(Map.of(
                "message", "Coding submission recorded.",
                "submission", submission,
                "result", Map.of(
                        "score", submission.getScore(),
                        "solved", "ACCEPTED".equals(submission.getStatus()),
                        "status", submission.getStatus(),
                        "executionTime", submission.getExecutionTime()
                )
        ), HttpStatus.CREATED);
    }
}
