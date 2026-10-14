package com.aijobtrainer.repository;

import com.aijobtrainer.model.CodingSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodingSubmissionRepository extends MongoRepository<CodingSubmission, String> {
    List<CodingSubmission> findByUserIdOrderByCreatedAtDesc(String userId);
    List<CodingSubmission> findByUserIdAndStatus(String userId, String status);
    List<CodingSubmission> findByUserId(String userId);
    void deleteByUserId(String userId);
}
