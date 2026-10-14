package com.aijobtrainer.model;

public class InterviewQuestionItem {
    private String text;
    private String answer;

    public InterviewQuestionItem() {
    }

    public InterviewQuestionItem(String text, String answer) {
        this.text = text;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
