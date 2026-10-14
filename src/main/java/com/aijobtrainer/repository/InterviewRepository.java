package com.aijobtrainer.repository;

import com.aijobtrainer.model.Interview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends MongoRepository<Interview, String> {
    List<Interview> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Interview> findByIdAndUserId(String id, String userId);
    void deleteByUserId(String userId);
    long countByUserId(String userId);
}
