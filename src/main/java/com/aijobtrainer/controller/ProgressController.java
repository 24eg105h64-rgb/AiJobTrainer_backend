package com.aijobtrainer.controller;

import com.aijobtrainer.model.Progress;
import com.aijobtrainer.security.UserPrincipal;
import com.aijobtrainer.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProgress(@AuthenticationPrincipal UserPrincipal principal) {
        Progress progress = progressService.getUserProgress(principal.getId());
        return ResponseEntity.ok(Map.of("progress", progress));
    }
}
