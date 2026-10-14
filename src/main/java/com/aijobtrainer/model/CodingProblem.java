package com.aijobtrainer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "codingProblems")
public class CodingProblem {

    @Id
    private String id;

    private String title;
    private String description;
    private String category;
    private String difficulty; // EASY, MEDIUM, HARD
    private String input;
    private String output;
    private List<String> examples = new ArrayList<>();
    private String constraints;
    private List<String> tags = new ArrayList<>();
    private Map<String, String> starterTemplates = new HashMap<>();

    public CodingProblem() {
    }

    public CodingProblem(String id, String title, String description, String category, String difficulty, String input, String output, List<String> examples, String constraints, List<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.input = input;
        this.output = output;
        this.examples = examples;
        this.constraints = constraints;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Map<String, String> getStarterTemplates() {
        return starterTemplates;
    }

    public void setStarterTemplates(Map<String, String> starterTemplates) {
        this.starterTemplates = starterTemplates;
    }
}
