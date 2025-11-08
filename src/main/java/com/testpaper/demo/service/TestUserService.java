package com.testpaper.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testpaper.demo.dto.QuestionAnswer;
import com.testpaper.demo.dto.TestUserAnswerRequest;
import com.testpaper.demo.model.Option;
import com.testpaper.demo.model.Question;
import com.testpaper.demo.model.QuestionPaper;
import com.testpaper.demo.model.TestUserResponse;
import com.testpaper.demo.model.TestUserResponseId;
import com.testpaper.demo.repository.OptionRepository;
import com.testpaper.demo.repository.QuestionPaperRepository;
import com.testpaper.demo.repository.QuestionRepository;
import com.testpaper.demo.repository.TestUserResponseRepository;
import com.testpaper.demo.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestUserService {

    private final TestUserResponseRepository testUserResponseRepository;
    private final QuestionPaperRepository questionPaperRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final ObjectMapper objectMapper;

    public TestUserService(TestUserResponseRepository testUserResponseRepository,
                           QuestionPaperRepository questionPaperRepository,
                           QuestionRepository questionRepository,
                           OptionRepository optionRepository,
                           ObjectMapper objectMapper) {
        this.testUserResponseRepository = testUserResponseRepository;
        this.questionPaperRepository = questionPaperRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.objectMapper = objectMapper;
    }

    public TestUserResponse saveUserAnswers(TestUserAnswerRequest request) {
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new RuntimeException("User ID cannot be empty");
        }
        if (request.getQuestionPaperId() == null || request.getQuestionPaperId().trim().isEmpty()) {
            throw new RuntimeException("Question Paper ID cannot be empty");
        }
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("Answers list cannot be empty");
        }

        // Validate Question Paper exists
        Optional<QuestionPaper> questionPaperOpt = questionPaperRepository.findById(request.getQuestionPaperId());
        if (questionPaperOpt.isEmpty()) {
            throw new RuntimeException(String.format("Question Paper with ID '%s' not found", request.getQuestionPaperId()));
        }
        QuestionPaper questionPaper = questionPaperOpt.get();

        // Validate questions and options exist within the question paper and check multi-correct status
        for (QuestionAnswer answer : request.getAnswers()) {
            Optional<Question> questionOpt = questionRepository.findById(answer.getQuestionId());
            if (questionOpt.isEmpty()) {
                throw new RuntimeException(String.format("Question with ID '%s' not found", answer.getQuestionId()));
            }
            Question question = questionOpt.get();

            if (!questionPaper.getQuestionIds().contains(answer.getQuestionId())) {
                throw new RuntimeException(String.format("Question with ID '%s' is not part of Question Paper '%s'", answer.getQuestionId(), request.getQuestionPaperId()));
            }

            if (answer.getSelectedOptionIds() == null || answer.getSelectedOptionIds().isEmpty()) {
                throw new RuntimeException(String.format("Selected options for question '%s' cannot be empty", answer.getQuestionId()));
            }

            if (!question.getMultiCorrect() && answer.getSelectedOptionIds().size() > 1) {
                throw new RuntimeException(String.format("Question '%s' is single correct, but multiple options were selected", answer.getQuestionId()));
            }

            List<String> validOptionIdsForQuestion = optionRepository.findByQuestionId(answer.getQuestionId()).stream()
                    .map(Option::getOptionId)
                    .collect(Collectors.toList());

            for (String selectedOptionId : answer.getSelectedOptionIds()) {
                if (!validOptionIdsForQuestion.contains(selectedOptionId)) {
                    throw new RuntimeException(String.format("Option '%s' does not belong to Question '%s'", selectedOptionId, answer.getQuestionId()));
                }
            }
        }

        TestUserResponseId compositeId = new TestUserResponseId(request.getUserId(), request.getQuestionPaperId());
        Optional<TestUserResponse> existingResponseOpt = testUserResponseRepository.findById(compositeId);

        TestUserResponse testUserResponse;
        Map<String, List<String>> currentAnswers = new HashMap<>();

        if (existingResponseOpt.isPresent()) {
            testUserResponse = existingResponseOpt.get();
            try {
                // Ensure correct type for deserialization
                currentAnswers = objectMapper.readValue(testUserResponse.getAnswersJson(), objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, List.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing existing answers JSON", e);
            }
        } else {
            testUserResponse = new TestUserResponse(compositeId, "{}");
        }

        for (QuestionAnswer answer : request.getAnswers()) {
            currentAnswers.put(answer.getQuestionId(), answer.getSelectedOptionIds());
        }

        try {
            testUserResponse.setAnswersJson(objectMapper.writeValueAsString(currentAnswers));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing answers to JSON", e);
        }
        testUserResponse.setUpdatedAt(LocalDateTime.now());
        return testUserResponseRepository.save(testUserResponse);
    }
}
