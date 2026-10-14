package com.aijobtrainer.repository;

import com.aijobtrainer.model.CodingProblem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodingProblemRepository extends MongoRepository<CodingProblem, String> {
    List<CodingProblem> findByCategoryIgnoreCase(String category);
    List<CodingProblem> findByDifficultyIgnoreCase(String difficulty);
}
