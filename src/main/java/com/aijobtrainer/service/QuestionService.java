package com.aijobtrainer.service;

import com.aijobtrainer.dto.QuestionRequest;
import com.aijobtrainer.model.Question;

import java.util.List;

public interface QuestionService {
    List<Question> getQuestions(String type, String category, String difficulty, String search);
    Question getQuestionById(String id);
    Question createQuestion(QuestionRequest request);
    Question updateQuestion(String id, QuestionRequest request);
    void deleteQuestion(String id);
}
