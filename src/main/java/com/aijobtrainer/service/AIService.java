package com.aijobtrainer.service;

import com.aijobtrainer.model.InterviewQuestionItem;

import java.util.List;

public interface AIService {

    InterviewEvaluation evaluateInterview(String interviewType, String difficulty, List<InterviewQuestionItem> questions, List<String> answers);

    ResumeEvaluation evaluateResume(String resumeText, String jobTitle, List<String> skills);

    String extractText(byte[] fileBytes, String fileName);

    class InterviewEvaluation {
        public int overallScore;
        public int communicationScore;
        public int technicalScore;
        public int confidenceScore;
        public int relevanceScore;
        public int problemSolvingScore;
        public List<String> strengths;
        public List<String> weaknesses;
        public List<String> suggestions;
    }

    class ResumeEvaluation {
        public int score;
        public int skillsScore;
        public int educationScore;
        public int projectsScore;
        public int experienceScore;
        public int formattingScore;
        public int keywordScore;
        public List<String> strengths;
        public List<String> missingSkills;
        public List<String> suggestions;
    }
}
