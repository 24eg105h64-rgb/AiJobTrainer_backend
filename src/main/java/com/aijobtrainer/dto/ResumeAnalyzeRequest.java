package com.aijobtrainer.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumeAnalyzeRequest {
    private String summary;
    private String jobTitle;
    private List<String> skills = new ArrayList<>();

    public ResumeAnalyzeRequest() {
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
