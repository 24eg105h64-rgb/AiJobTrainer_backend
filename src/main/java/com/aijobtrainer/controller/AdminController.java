package com.aijobtrainer.controller;

import com.aijobtrainer.dto.AdminUserRoleRequest;
import com.aijobtrainer.dto.QuestionRequest;
import com.aijobtrainer.dto.StatisticsResponse;
import com.aijobtrainer.dto.UserResponse;
import com.aijobtrainer.model.Interview;
import com.aijobtrainer.model.Question;
import com.aijobtrainer.service.AdminService;
import com.aijobtrainer.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final QuestionService questionService;

    public AdminController(AdminService adminService, QuestionService questionService) {
        this.adminService = adminService;
        this.questionService = questionService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        StatisticsResponse stats = adminService.getStatistics();
        return ResponseEntity.ok(Map.of("stats", stats));
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(Map.of("users", users));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, Object>> updateUserRole(@PathVariable String id,
                                                              @Valid @RequestBody AdminUserRoleRequest request) {
        UserResponse user = adminService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok(Map.of(
                "message", "Role updated.",
                "user", user
        ));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted."));
    }

    @GetMapping("/interviews")
    public ResponseEntity<Map<String, Object>> getAllInterviews() {
        List<Interview> interviews = adminService.getAllInterviews();
        return ResponseEntity.ok(Map.of("interviews", interviews));
    }

    @PostMapping("/questions")
    public ResponseEntity<Map<String, Object>> createQuestion(@RequestBody QuestionRequest request) {
        Question question = questionService.createQuestion(request);
        return new ResponseEntity<>(Map.of(
                "message", "Question created.",
                "question", question
        ), HttpStatus.CREATED);
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<Map<String, Object>> updateQuestion(@PathVariable String id, @RequestBody QuestionRequest request) {
        Question question = questionService.updateQuestion(id, request);
        return ResponseEntity.ok(Map.of(
                "message", "Question updated.",
                "question", question
        ));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Map<String, String>> deleteQuestion(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(Map.of("message", "Question deleted."));
    }
}
