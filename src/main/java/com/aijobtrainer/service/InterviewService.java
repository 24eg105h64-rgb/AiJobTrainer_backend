package com.aijobtrainer.service;

import com.aijobtrainer.dto.InterviewSubmitRequest;
import com.aijobtrainer.model.Interview;
import com.aijobtrainer.model.InterviewQuestionItem;

import java.util.List;

public interface InterviewService {
    Interview submitInterview(String userId, InterviewSubmitRequest request);
    List<Interview> getUserInterviews(String userId);
    Interview getInterviewById(String id, String userId);
    void deleteInterview(String id, String userId);
    List<InterviewQuestionItem> generateQuestions(String type, String difficulty, int count);
}
