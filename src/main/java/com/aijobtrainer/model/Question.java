package com.aijobtrainer.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "questions")
public class Question {

    @Id
    private String id;

    private String title;
    private String question; // For compatibility with existing question text
    private String description;
    private String category; // Technical, HR, Behavioral, DSA, SQL, Aptitude
    private String difficulty; // Easy, Medium, Hard
    private String type;
    private String input;
    private String output;
    private List<String> examples = new ArrayList<>();
    private String constraints;
    private List<String> tags = new ArrayList<>();
    private String answer;

    @CreatedDate
    private Instant createdAt = Instant.now();

    public Question() {
    }

    public Question(String title, String question, String description, String category, String difficulty, String type, String answer) {
        this.title = title;
        this.question = question;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.type = type;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return (title != null && !title.isEmpty()) ? title : question;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return (question != null && !question.isEmpty()) ? question : title;
    }

    public void setQuestion(String question) {
        this.question = question;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
