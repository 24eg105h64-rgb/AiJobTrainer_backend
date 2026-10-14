package com.aijobtrainer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "spring_progress")
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String userId;

    private int interviewsCompleted;
    private int averageInterviewScore;
    private int resumeScore;
    private int codingProblemsSolved;
    private int currentStreak;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Integer> skills = new HashMap<>();
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Integer> weeklyActivity = new ArrayList<>();

    private Instant updatedAt = Instant.now();

    public Progress() {
    }

    public Progress(String userId) {
        this.userId = userId;
        this.interviewsCompleted = 0;
        this.averageInterviewScore = 0;
        this.resumeScore = 0;
        this.codingProblemsSolved = 0;
        this.currentStreak = 1;
        this.skills = new HashMap<>();
        this.weeklyActivity = List.of(2, 4, 5, 3, 6, 2, 1);
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getInterviewsCompleted() {
        return interviewsCompleted;
    }

    public void setInterviewsCompleted(int interviewsCompleted) {
        this.interviewsCompleted = interviewsCompleted;
    }

    public int getAverageInterviewScore() {
        return averageInterviewScore;
    }

    public void setAverageInterviewScore(int averageInterviewScore) {
        this.averageInterviewScore = averageInterviewScore;
    }

    public int getResumeScore() {
        return resumeScore;
    }

    public void setResumeScore(int resumeScore) {
        this.resumeScore = resumeScore;
    }

    public int getCodingProblemsSolved() {
        return codingProblemsSolved;
    }

    public void setCodingProblemsSolved(int codingProblemsSolved) {
        this.codingProblemsSolved = codingProblemsSolved;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Map<String, Integer> getSkills() {
        return skills;
    }

    public void setSkills(Map<String, Integer> skills) {
        this.skills = skills;
    }

    public List<Integer> getWeeklyActivity() {
        return weeklyActivity;
    }

    public void setWeeklyActivity(List<Integer> weeklyActivity) {
        this.weeklyActivity = weeklyActivity;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
