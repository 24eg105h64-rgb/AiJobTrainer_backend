package com.aijobtrainer.service.impl;

import com.aijobtrainer.dto.QuestionRequest;
import com.aijobtrainer.exception.ResourceNotFoundException;
import com.aijobtrainer.model.Question;
import com.aijobtrainer.repository.QuestionRepository;
import com.aijobtrainer.service.QuestionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public List<Question> getQuestions(String type, String category, String difficulty, String search) {
        String normalizedType = type == null ? "" : type.trim();
        String normalizedCategory = category == null ? "" : category.trim();
        String normalizedDifficulty = difficulty == null ? "" : difficulty.trim();
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase();

        return questionRepository.findAll().stream()
            .filter(question -> normalizedType.isEmpty() || normalizedType.equalsIgnoreCase("All")
                || normalizedType.equalsIgnoreCase(question.getType())
                || normalizedType.equalsIgnoreCase(question.getCategory()))
            .filter(question -> normalizedCategory.isEmpty() || normalizedCategory.equalsIgnoreCase("All")
                || normalizedCategory.equalsIgnoreCase(question.getCategory()))
            .filter(question -> normalizedDifficulty.isEmpty() || normalizedDifficulty.equalsIgnoreCase("All")
                || normalizedDifficulty.equalsIgnoreCase(question.getDifficulty()))
            .filter(question -> normalizedSearch.isEmpty()
                || String.join(" ", question.getTitle(), question.getQuestion(), question.getDescription() == null ? "" : question.getDescription())
                    .toLowerCase().contains(normalizedSearch))
            .toList();
    }

    @Override
    public Question getQuestionById(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
    }

    @Override
    public Question createQuestion(QuestionRequest request) {
        Question q = new Question();
        q.setTitle(request.getTitle());
        q.setQuestion(request.getQuestion());
        q.setDescription(request.getDescription());
        q.setCategory(request.getCategory() != null ? request.getCategory() : "Technical");
        q.setDifficulty(request.getDifficulty() != null ? request.getDifficulty() : "Medium");
        q.setType(request.getType() != null ? request.getType() : "Technical");
        q.setInput(request.getInput());
        q.setOutput(request.getOutput());
        q.setExamples(request.getExamples());
        q.setConstraints(request.getConstraints());
        q.setTags(request.getTags());
        q.setAnswer(request.getAnswer());
        q.setCreatedAt(Instant.now());

        return questionRepository.save(q);
    }

    @Override
    public Question updateQuestion(String id, QuestionRequest request) {
        Question q = getQuestionById(id);

        if (request.getTitle() != null) q.setTitle(request.getTitle());
        if (request.getQuestion() != null) q.setQuestion(request.getQuestion());
        if (request.getDescription() != null) q.setDescription(request.getDescription());
        if (request.getCategory() != null) q.setCategory(request.getCategory());
        if (request.getDifficulty() != null) q.setDifficulty(request.getDifficulty());
        if (request.getType() != null) q.setType(request.getType());
        if (request.getInput() != null) q.setInput(request.getInput());
        if (request.getOutput() != null) q.setOutput(request.getOutput());
        if (request.getExamples() != null) q.setExamples(request.getExamples());
        if (request.getConstraints() != null) q.setConstraints(request.getConstraints());
        if (request.getTags() != null) q.setTags(request.getTags());
        if (request.getAnswer() != null) q.setAnswer(request.getAnswer());

        return questionRepository.save(q);
    }

    @Override
    public void deleteQuestion(String id) {
        Question q = getQuestionById(id);
        questionRepository.delete(q);
    }
}
