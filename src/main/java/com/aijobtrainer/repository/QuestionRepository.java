package com.aijobtrainer.repository;

import com.aijobtrainer.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByTypeIgnoreCase(String type);
    List<Question> findByCategoryIgnoreCase(String category);
    List<Question> findByDifficultyIgnoreCase(String difficulty);
}
