package com.aijobtrainer.service;

import com.aijobtrainer.dto.CodingSubmitRequest;
import com.aijobtrainer.model.CodingProblem;
import com.aijobtrainer.model.CodingSubmission;

import java.util.List;

public interface CodingService {
    List<CodingProblem> getAllProblems();
    CodingProblem getProblemById(String id);
    CodingSubmission submitCode(String userId, CodingSubmitRequest request);
}
