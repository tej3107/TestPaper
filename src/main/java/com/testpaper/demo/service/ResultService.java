package com.testpaper.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testpaper.demo.dto.*;
import com.testpaper.demo.model.Option;
import com.testpaper.demo.model.Question;
import com.testpaper.demo.model.TestUserResponse;
import com.testpaper.demo.repository.QuestionRepository;
import com.testpaper.demo.repository.TestUserResponseRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultService {

    private final QuestionPaperService questionPaperService;
    private final TestUserResponseRepository testUserResponseRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;

    public ResultService(QuestionPaperService questionPaperService, TestUserResponseRepository testUserResponseRepository, QuestionRepository questionRepository, ObjectMapper objectMapper) {
        this.questionPaperService = questionPaperService;
        this.testUserResponseRepository = testUserResponseRepository;
        this.questionRepository = questionRepository;
        this.objectMapper = objectMapper;
    }

    public TestResultResponse getTestResults(String userId, String questionPaperId) {
        QuestionPaperResponse questionPaper = questionPaperService.getQuestionPaper(questionPaperId);
        if (questionPaper == null) {
            throw new RuntimeException("Question paper not found.");
        }

        // Retrieve the user's answers
        Optional<TestUserResponse> userAnswerRequest = testUserResponseRepository.findByUserIdAndQuestionPaperId(userId, questionPaperId);
        if (userAnswerRequest.isEmpty()) {
            throw new RuntimeException("User answers not found for the given question paper.");
        }
        Map<String, List<String>> userAnswerMap;
        try {
            userAnswerMap = objectMapper.readValue(userAnswerRequest.get().getAnswersJson(), objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, List.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        int correctAnswers = 0;
        int wrongAnswers = 0;
        int unattemptedQuestions = 0;
        List<QuestionResult> questionResults = new ArrayList<>();

        for (QuestionPaperQuestionResponse question : questionPaper.getQuestions()) {
            Question ques = questionRepository.findById(question.getId()).get();
            List<String> correctOptionIds = ques.getOptions().stream()
                    .filter(Option::getIsCorrect)
                    .map(Option::getOptionId)
                    .collect(Collectors.toList());

            List<String> selectedOptionIds = userAnswerMap.getOrDefault(question.getId(), new ArrayList<>());

            boolean isAttempted = !selectedOptionIds.isEmpty();
            boolean isCorrect = false;

            if (isAttempted) {
                if (correctOptionIds.size() == selectedOptionIds.size() && correctOptionIds.containsAll(selectedOptionIds)) {
                    isCorrect = true;
                    correctAnswers++;
                } else {
                    wrongAnswers++;
                }
            } else {
                unattemptedQuestions++;
            }

            questionResults.add(new QuestionResult(
                    question.getId(),
                    question.getStem(),
                    question.getOptions(),
                    selectedOptionIds,
                    correctOptionIds,
                    isCorrect,
                    isAttempted
            ));
        }

        return new TestResultResponse(
                userId,
                questionPaperId,
                questionPaper.getName(),
                questionPaper.getQuestions().size(),
                correctAnswers,
                wrongAnswers,
                unattemptedQuestions,
                questionResults
        );
    }
}
