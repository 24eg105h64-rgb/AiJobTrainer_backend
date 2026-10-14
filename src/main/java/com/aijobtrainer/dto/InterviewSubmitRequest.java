package com.aijobtrainer.dto;

import java.util.ArrayList;
import java.util.List;

public class InterviewSubmitRequest {
    private String interviewType = "Technical";
    private String type; // for backwards compatibility with frontend sending type
    private String difficulty = "Medium";
    private int questionCount = 5;
    private List<InterviewQuestionDto> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>();

    public InterviewSubmitRequest() {
    }

    public String getInterviewType() {
        if (interviewType != null && !interviewType.isEmpty()) {
            return interviewType;
        }
        return type != null ? type : "Technical";
    }

    public void setInterviewType(String interviewType) {
        this.interviewType = interviewType;
    }

    public String getType() {
        return getInterviewType();
    }

    public void setType(String type) {
        this.type = type;
        if (this.interviewType == null || this.interviewType.isEmpty()) {
            this.interviewType = type;
        }
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public List<InterviewQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<InterviewQuestionDto> questions) {
        this.questions = questions;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public static class InterviewQuestionDto {
        private String text;
        private String question;
        private String answer;

        public InterviewQuestionDto() {
        }

        public String getText() {
            return (text != null && !text.isEmpty()) ? text : question;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getQuestion() {
            return getText();
        }

        public void setQuestion(String question) {
            this.question = question;
            if (this.text == null || this.text.isEmpty()) {
                this.text = question;
            }
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
