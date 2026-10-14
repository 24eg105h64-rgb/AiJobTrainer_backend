package com.aijobtrainer.service.impl;

import com.aijobtrainer.dto.QuestionRequest;
import com.aijobtrainer.exception.ResourceNotFoundException;
import com.aijobtrainer.model.Question;
import com.aijobtrainer.repository.QuestionRepository;
import com.aijobtrainer.service.QuestionService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final MongoTemplate mongoTemplate;

    public QuestionServiceImpl(QuestionRepository questionRepository, MongoTemplate mongoTemplate) {
        this.questionRepository = questionRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Question> getQuestions(String type, String category, String difficulty, String search) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (type != null && !type.isBlank() && !type.equalsIgnoreCase("All")) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("type").regex("^" + type + "$", "i"),
                    Criteria.where("category").regex("^" + type + "$", "i")
            ));
        }

        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("All")) {
            criteriaList.add(Criteria.where("category").regex("^" + category + "$", "i"));
        }

        if (difficulty != null && !difficulty.isBlank() && !difficulty.equalsIgnoreCase("All")) {
            criteriaList.add(Criteria.where("difficulty").regex("^" + difficulty + "$", "i"));
        }

        if (search != null && !search.isBlank()) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("title").regex(search, "i"),
                    Criteria.where("question").regex(search, "i"),
                    Criteria.where("description").regex(search, "i")
            ));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Question.class);
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
