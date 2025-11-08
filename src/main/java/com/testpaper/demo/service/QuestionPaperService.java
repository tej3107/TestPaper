package com.testpaper.demo.service;

import com.testpaper.demo.dto.QuestionPaperOptionResponse;
import com.testpaper.demo.dto.QuestionPaperQuestionResponse;
import com.testpaper.demo.dto.QuestionPaperRequest;
import com.testpaper.demo.dto.QuestionPaperResponse;
import com.testpaper.demo.model.*;
import com.testpaper.demo.repository.OptionRepository;
import com.testpaper.demo.repository.QuestionPaperRepository;
import com.testpaper.demo.repository.QuestionRepository;
import com.testpaper.demo.repository.TagQuestionRepository;
import com.testpaper.demo.repository.TagRepository;
import com.testpaper.demo.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionPaperService {

    private final QuestionPaperRepository questionPaperRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final TagQuestionRepository tagQuestionRepository;
    private final OptionRepository optionRepository;

    public QuestionPaperService(QuestionPaperRepository questionPaperRepository,
                                TagRepository tagRepository,
                                QuestionRepository questionRepository,
                                TagQuestionRepository tagQuestionRepository,
                                OptionRepository optionRepository) {
        this.questionPaperRepository = questionPaperRepository;
        this.tagRepository = tagRepository;
        this.questionRepository = questionRepository;
        this.tagQuestionRepository = tagQuestionRepository;
        this.optionRepository = optionRepository;
    }

    public QuestionPaperResponse createQuestionPaper(QuestionPaperRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Question paper name cannot be empty");
        }
        if (request.getTagName() == null || request.getTagName().trim().isEmpty()) {
            throw new RuntimeException("Tag name cannot be empty");
        }
        if (request.getQuestionCount() == null || request.getQuestionCount() <= 0) {
            throw new RuntimeException("Question count must be a positive number");
        }

        Optional<Tag> tagOpt = tagRepository.findByName(request.getTagName());
        if (tagOpt.isEmpty()) {
            throw new RuntimeException(String.format("Tag '%s' not found", request.getTagName()));
        }
        String tagId = tagOpt.get().getId();

        List<String> availableQuestionIds = tagQuestionRepository.findQuestionIdByTagId(tagId);

        if (availableQuestionIds.isEmpty()) {
            throw new RuntimeException(String.format("No questions found for tag '%s'", request.getTagName()));
        }

        if (request.getQuestionCount() > availableQuestionIds.size()) {
            throw new RuntimeException(String.format("Requested question count (%d) exceeds available questions (%d) for tag '%s'",
                    request.getQuestionCount(), availableQuestionIds.size(), request.getTagName()));
        }

        Collections.shuffle(availableQuestionIds);
        List<String> selectedQuestionIds = availableQuestionIds.subList(0, request.getQuestionCount());

        String questionPaperId = IdGenerator.generateRandomId(6);
        QuestionPaper questionPaper = new QuestionPaper(questionPaperId, request.getName(), selectedQuestionIds);
        questionPaper = questionPaperRepository.save(questionPaper);

        // For response, we need to fetch the actual questions and options
        List<QuestionPaperQuestionResponse> questionPaperQuestions = new ArrayList<>();
        for (String qId : selectedQuestionIds) {
            Optional<Question> questionOpt = questionRepository.findById(qId);
            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                List<Option> options = optionRepository.findByQuestionId(qId);
                List<QuestionPaperOptionResponse> questionPaperOptions = options.stream()
                        .map(opt -> new QuestionPaperOptionResponse(opt.getOptionId(), opt.getOptionText()))
                        .collect(Collectors.toList());
                questionPaperQuestions.add(new QuestionPaperQuestionResponse(question.getId(), question.getStem(), questionPaperOptions));
            }
        }

        return new QuestionPaperResponse(questionPaper.getId(), questionPaper.getName(), questionPaperQuestions);
    }

    public QuestionPaperResponse getQuestionPaper(String questionPaperId) {
        Optional<QuestionPaper> questionPaperOpt = questionPaperRepository.findById(questionPaperId);
        if (questionPaperOpt.isEmpty()) {
            throw new RuntimeException("Question Paper not found");
        }
        QuestionPaper questionPaper = questionPaperOpt.get();

        List<QuestionPaperQuestionResponse> questionPaperQuestions = new ArrayList<>();
        for (String qId : questionPaper.getQuestionIds()) {
            Optional<Question> questionOpt = questionRepository.findById(qId);
            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                List<Option> options = optionRepository.findByQuestionId(qId);
                List<QuestionPaperOptionResponse> questionPaperOptions = options.stream()
                        .map(opt -> new QuestionPaperOptionResponse(opt.getOptionId(), opt.getOptionText()))
                        .collect(Collectors.toList());
                questionPaperQuestions.add(new QuestionPaperQuestionResponse(question.getId(), question.getStem(), questionPaperOptions));
            }
        }
        return new QuestionPaperResponse(questionPaper.getId(), questionPaper.getName(), questionPaperQuestions);
    }
}
