package com.aijobtrainer.dto;

import jakarta.validation.constraints.NotBlank;

public class CodingSubmitRequest {

    @NotBlank(message = "Problem ID is required")
    private String problemId;

    private String language = "Java";
    private String code;
    private Boolean solved;

    public CodingSubmitRequest() {
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getSolved() {
        return solved;
    }

    public void setSolved(Boolean solved) {
        this.solved = solved;
    }
}
