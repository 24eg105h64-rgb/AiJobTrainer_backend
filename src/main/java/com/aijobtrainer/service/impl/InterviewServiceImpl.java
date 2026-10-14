package com.aijobtrainer.service.impl;

import com.aijobtrainer.dto.InterviewSubmitRequest;
import com.aijobtrainer.exception.ResourceNotFoundException;
import com.aijobtrainer.model.Interview;
import com.aijobtrainer.model.InterviewQuestionItem;
import com.aijobtrainer.model.Question;
import com.aijobtrainer.repository.InterviewRepository;
import com.aijobtrainer.repository.QuestionRepository;
import com.aijobtrainer.service.AIService;
import com.aijobtrainer.service.InterviewService;
import com.aijobtrainer.service.ProgressService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final AIService aiService;
    private final ProgressService progressService;

    public InterviewServiceImpl(InterviewRepository interviewRepository,
                                QuestionRepository questionRepository,
                                AIService aiService,
                                ProgressService progressService) {
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
        this.aiService = aiService;
        this.progressService = progressService;
    }

    @Override
    public Interview submitInterview(String userId, InterviewSubmitRequest request) {
        String interviewType = request.getInterviewType();
        String difficulty = request.getDifficulty();

        List<InterviewQuestionItem> questionItems = new ArrayList<>();
        List<String> answerStrings = request.getAnswers() != null ? request.getAnswers() : new ArrayList<>();

        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            for (int i = 0; i < request.getQuestions().size(); i++) {
                InterviewSubmitRequest.InterviewQuestionDto dto = request.getQuestions().get(i);
                String ans = (i < answerStrings.size()) ? answerStrings.get(i) : (dto.getAnswer() != null ? dto.getAnswer() : "");
                questionItems.add(new InterviewQuestionItem(dto.getText(), ans));
            }
        } else {
            List<InterviewQuestionItem> generated = generateQuestions(interviewType, difficulty, request.getQuestionCount());
            for (int i = 0; i < generated.size(); i++) {
                String ans = (i < answerStrings.size()) ? answerStrings.get(i) : "";
                questionItems.add(new InterviewQuestionItem(generated.get(i).getText(), ans));
            }
        }

        AIService.InterviewEvaluation eval = aiService.evaluateInterview(interviewType, difficulty, questionItems, answerStrings);

        Interview interview = new Interview();
        interview.setUserId(userId);
        interview.setInterviewType(interviewType);
        interview.setDifficulty(difficulty);
        interview.setQuestions(questionItems);
        interview.setAnswers(answerStrings);
        interview.setScore(eval.overallScore);
        interview.setCommunicationScore(eval.communicationScore);
        interview.setTechnicalScore(eval.technicalScore);
        interview.setConfidenceScore(eval.confidenceScore);
        interview.setRelevanceScore(eval.relevanceScore);
        interview.setProblemSolvingScore(eval.problemSolvingScore);
        interview.setStrengths(eval.strengths);
        interview.setWeaknesses(eval.weaknesses);
        interview.setSuggestions(eval.suggestions);
        interview.setStatus("completed");
        interview.setCreatedAt(Instant.now());

        Interview saved = interviewRepository.save(interview);
        progressService.recordInterviewCompletion(userId, eval.overallScore);

        return saved;
    }

    @Override
    public List<Interview> getUserInterviews(String userId) {
        return interviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Interview getInterviewById(String id, String userId) {
        return interviewRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + id));
    }

    @Override
    public void deleteInterview(String id, String userId) {
        Interview interview = getInterviewById(id, userId);
        interviewRepository.delete(interview);
    }

    @Override
    public List<InterviewQuestionItem> generateQuestions(String type, String difficulty, int count) {
        int limit = (count > 0) ? count : 5;
        List<Question> repoQuestions = questionRepository.findAll();
        List<InterviewQuestionItem> result = new ArrayList<>();

        List<Question> filtered = new ArrayList<>();
        for (Question q : repoQuestions) {
            boolean typeMatches = type.equalsIgnoreCase("MIXED") ||
                    (q.getType() != null && q.getType().equalsIgnoreCase(type)) ||
                    (q.getCategory() != null && q.getCategory().equalsIgnoreCase(type));
            if (typeMatches) {
                filtered.add(q);
            }
        }

        Collections.shuffle(filtered);
        for (Question q : filtered) {
            if (result.size() >= limit) break;
            result.add(new InterviewQuestionItem(q.getTitle(), ""));
        }

        // If repo didn't have enough, populate standard mock questions
        if (result.size() < limit) {
            List<String> pool = getFallbackPool(type);
            Collections.shuffle(pool);
            for (String qText : pool) {
                if (result.size() >= limit) break;
                result.add(new InterviewQuestionItem(qText, ""));
            }
        }

        return result;
    }

    private List<String> getFallbackPool(String type) {
        return switch (type.toUpperCase()) {
            case "HR" -> List.of(
                    "Tell me about yourself, your educational background, and your key career aspirations.",
                    "Why are you interested in joining our engineering team and what value do you bring?",
                    "Describe a situation where you worked in a team and had to resolve an interpersonal conflict.",
                    "Where do you see yourself professionally in the next three to five years?",
                    "How do you prioritize multiple urgent deadlines during semester exams or project delivery?"
            );
            case "BEHAVIORAL" -> List.of(
                    "Describe a time you faced a difficult technical bug under a tight deadline. How did you handle it?",
                    "Give an example of a project where your initial approach failed and how you pivoted to succeed.",
                    "Tell me about a time you had to learn an unfamiliar technology or framework very quickly.",
                    "How do you handle receiving critical feedback from your peers or mentors?",
                    "Describe an experience where you showed initiative and led a project beyond expectations."
            );
            case "CODING" -> List.of(
                    "How would you find two numbers in an array that sum to a target value in O(n) time complexity?",
                    "Explain how you would reverse a singly linked list iteratively and recursively.",
                    "How do you detect a cycle in a directed vs undirected graph?",
                    "Explain how a hash map handles collisions and what occurs during resizing.",
                    "How would you implement a rate limiter algorithm like token bucket or sliding window?"
            );
            default -> List.of(
                    "Explain the core principles of Object-Oriented Programming and how they apply in Java.",
                    "What is the difference between SQL and NoSQL databases, and when would you choose MongoDB?",
                    "Describe how Spring Boot handles dependency injection and lifecycle management.",
                    "How does React's virtual DOM work, and how does reconciliation optimize rendering performance?",
                    "What are REST architectural constraints, and how do you secure REST APIs using JWT tokens?"
            );
        };
    }
}
