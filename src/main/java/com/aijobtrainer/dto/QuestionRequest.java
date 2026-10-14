package com.aijobtrainer.dto;

import java.util.ArrayList;
import java.util.List;

public class QuestionRequest {
    private String title;
    private String question;
    private String description;
    private String category = "Technical";
    private String difficulty = "Medium";
    private String type = "Technical";
    private String input;
    private String output;
    private List<String> examples = new ArrayList<>();
    private String constraints;
    private List<String> tags = new ArrayList<>();
    private String answer;

    public QuestionRequest() {
    }

    public String getTitle() {
        return (title != null && !title.isEmpty()) ? title : question;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return getTitle();
    }

    public void setQuestion(String question) {
        this.question = question;
        if (this.title == null || this.title.isEmpty()) {
            this.title = question;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
