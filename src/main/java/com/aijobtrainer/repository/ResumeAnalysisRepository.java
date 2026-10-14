package com.aijobtrainer.repository;

import com.aijobtrainer.model.ResumeAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeAnalysisRepository extends MongoRepository<ResumeAnalysis, String> {
    List<ResumeAnalysis> findByUserIdOrderByCreatedAtDesc(String userId);
    void deleteByUserId(String userId);
}
