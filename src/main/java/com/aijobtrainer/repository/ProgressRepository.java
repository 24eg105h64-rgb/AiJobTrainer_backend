package com.aijobtrainer.repository;

import com.aijobtrainer.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, String> {
    Optional<Progress> findByUserId(String userId);
    void deleteByUserId(String userId);
}
