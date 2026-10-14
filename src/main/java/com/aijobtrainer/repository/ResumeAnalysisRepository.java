package com.aijobtrainer.repository;

import com.aijobtrainer.model.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, String> {
    List<ResumeAnalysis> findByUserIdOrderByCreatedAtDesc(String userId);
    void deleteByUserId(String userId);
}
