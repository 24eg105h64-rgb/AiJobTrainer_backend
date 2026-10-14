package com.aijobtrainer.repository;

import com.aijobtrainer.model.CodingProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodingProblemRepository extends JpaRepository<CodingProblem, String> {
    List<CodingProblem> findByCategoryIgnoreCase(String category);
    List<CodingProblem> findByDifficultyIgnoreCase(String difficulty);
}
