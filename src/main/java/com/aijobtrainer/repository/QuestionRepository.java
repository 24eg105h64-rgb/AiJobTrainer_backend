package com.aijobtrainer.repository;

import com.aijobtrainer.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    List<Question> findByTypeIgnoreCase(String type);
    List<Question> findByCategoryIgnoreCase(String category);
    List<Question> findByDifficultyIgnoreCase(String difficulty);
}
