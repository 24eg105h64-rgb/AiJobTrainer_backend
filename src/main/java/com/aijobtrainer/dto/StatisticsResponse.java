package com.aijobtrainer.dto;

public class StatisticsResponse {
    private long totalUsers;
    private long totalInterviews;
    private int averageScore;
    private long totalQuestions;
    private long activeUsers;

    public StatisticsResponse() {
    }

    public StatisticsResponse(long totalUsers, long totalInterviews, int averageScore, long totalQuestions, long activeUsers) {
        this.totalUsers = totalUsers;
        this.totalInterviews = totalInterviews;
        this.averageScore = averageScore;
        this.totalQuestions = totalQuestions;
        this.activeUsers = activeUsers;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalInterviews() {
        return totalInterviews;
    }

    public void setTotalInterviews(long totalInterviews) {
        this.totalInterviews = totalInterviews;
    }

    public int getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(int averageScore) {
        this.averageScore = averageScore;
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }
}
