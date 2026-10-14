package com.aijobtrainer.controller;

import com.aijobtrainer.dto.QuestionRequest;
import com.aijobtrainer.model.Question;
import com.aijobtrainer.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getQuestions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search
    ) {
        List<Question> questions = questionService.getQuestions(type, category, difficulty, search);
        return ResponseEntity.ok(Map.of("questions", questions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getQuestionById(@PathVariable String id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(Map.of("question", question));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createQuestion(@RequestBody QuestionRequest request) {
        Question question = questionService.createQuestion(request);
        return new ResponseEntity<>(Map.of(
                "message", "Question created.",
                "question", question
        ), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateQuestion(@PathVariable String id, @RequestBody QuestionRequest request) {
        Question question = questionService.updateQuestion(id, request);
        return ResponseEntity.ok(Map.of(
                "message", "Question updated.",
                "question", question
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteQuestion(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(Map.of("message", "Question deleted."));
    }
}
