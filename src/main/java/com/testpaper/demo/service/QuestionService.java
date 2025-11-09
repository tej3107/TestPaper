package com.testpaper.demo.service;

import com.testpaper.demo.dto.QuestionRequest;
import com.testpaper.demo.dto.QuestionResponse;
import com.testpaper.demo.dto.OptionRequest;
import com.testpaper.demo.dto.OptionResponse;
import com.testpaper.demo.dto.TagResponse;
import com.testpaper.demo.model.Option;
import com.testpaper.demo.model.Question;
import com.testpaper.demo.model.Tag;
import com.testpaper.demo.repository.OptionRepository;
import com.testpaper.demo.repository.QuestionRepository;
import com.testpaper.demo.repository.TagQuestionRepository;
import com.testpaper.demo.repository.TagRepository;
import com.testpaper.demo.util.IdGenerator;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final TagQuestionRepository tagQuestionRepository;
    private final TagRepository tagRepository;

    public QuestionService(QuestionRepository questionRepository, OptionRepository optionRepository, TagQuestionRepository tagQuestionRepository, TagRepository tagRepository) {
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.tagQuestionRepository = tagQuestionRepository;
        this.tagRepository = tagRepository;
    }

    public QuestionResponse createQuestion(QuestionRequest request) {
        String questionId = IdGenerator.generateHashBasedId(request.getStem());
        if (questionRepository.existsById(questionId)) {
            throw new RuntimeException(String.format("Question Already Exist with Id: %s!!", questionId));
        }
        Question question = new Question(questionId, request.getStem(), request.getMultiCorrect());
        question = questionRepository.save(question);

        List<OptionResponse> optionResponses = this.addOrUpdateOptions(questionId, request.getOptionRequests());

        List<Tag> tagList = tagRepository.findByNameIn(request.getTagNames());
        if(tagList == null || tagList.isEmpty()) {
            throw new RuntimeException("No Tags with given names found in system");
        }

        for (Tag tag : tagList) {
            if (tagQuestionRepository.findByTagIdAndQuestionId(tag.getId(), questionId).isEmpty()) {
                tagQuestionRepository.save(new com.testpaper.demo.model.TagQuestion(tag, questionId));
            }
        }

        return new QuestionResponse(
                question.getId(),
                question.getStem(),
                question.getMultiCorrect(),
                optionResponses,
                tagList.stream().map(tag -> new TagResponse(tag.getId(), tag.getName(), tag.getDescription())).collect(Collectors.toList())
        );
    }

    public List<OptionResponse> addOrUpdateOptions(String questionId, List<OptionRequest> optionRequests) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            throw new RuntimeException("Question not found");
        }
        Question question = questionOpt.get();

        List<Option> existingOptions = optionRepository.findByQuestionId(questionId);

        List<String> invalidOptionIds = new ArrayList<>();
        for (OptionRequest optionRequest : optionRequests) {
            if (optionRequest.getOptionId() != null && !optionRequest.getOptionId().isEmpty()) {
                Optional<Option> existingOpt = optionRepository.findByOptionId(optionRequest.getOptionId());
                if (existingOpt.isEmpty()) {
                    invalidOptionIds.add(optionRequest.getOptionId());
                }
            }
        }

        if (!invalidOptionIds.isEmpty()) {
            throw new RuntimeException("Invalid option IDs: " + String.join(", ", invalidOptionIds));
        }

        List<Option> optionsToUpdate = new ArrayList<>();

        for (OptionRequest optionRequest : optionRequests) {
            Option option;
            String generatedOptionId;

            if (optionRequest.getOptionId() != null && !optionRequest.getOptionId().isEmpty()) {
                Optional<Option> existingOpt = optionRepository.findByOptionId(optionRequest.getOptionId());
                option = existingOpt.get();
                generatedOptionId = option.getOptionId();
            } else {
                Optional<Option> existingByText = optionRepository.findByQuestionIdAndOptionText(
                        questionId, optionRequest.getOptionText());

                if (existingByText.isPresent()) {
                    option = existingByText.get();
                    generatedOptionId = option.getOptionId();
                } else {
                    option = new Option(optionRequest.getOptionText(), optionRequest.getIsCorrect());
                    option.setQuestion(question);

                    String twoCharId = IdGenerator.generateRandomId(2);
                    generatedOptionId = questionId + "-" + twoCharId;

                    while (optionRepository.findByOptionId(generatedOptionId).isPresent()) {
                        twoCharId = IdGenerator.generateRandomId(2);
                        generatedOptionId = questionId + "-" + twoCharId;
                    }
                    option.setOptionId(generatedOptionId);
                }
            }
            option.setOptionText(optionRequest.getOptionText());
            option.setIsCorrect(optionRequest.getIsCorrect());
            optionsToUpdate.add(option);
        }

        if (!Boolean.TRUE.equals(question.getMultiCorrect())) {
            long correctCount = existingOptions.stream()
                    .filter(existing -> optionsToUpdate.stream()
                            .noneMatch(toUpdate -> toUpdate.getOptionId().equals(existing.getOptionId())))
                    .filter(opt -> Boolean.TRUE.equals(opt.getIsCorrect()))
                    .count();

            correctCount += optionsToUpdate.stream()
                    .filter(opt -> Boolean.TRUE.equals(opt.getIsCorrect()))
                    .count();

            if (correctCount > 1) {
                throw new RuntimeException("Question is single correct. Only 1 correct option is allowed, but " + correctCount + " correct options found.");
            }
        }
        List<OptionResponse> responses = new ArrayList<>();
        for (Option option : optionsToUpdate) {
            option = optionRepository.save(option);
            OptionResponse response = new OptionResponse(
                    option.getOptionId(),
                    questionId,
                    option.getOptionText(),
                    option.getIsCorrect()
            );
            responses.add(response);
        }
        return responses;
    }

    @Deprecated
//    public void importQuestionsCsv(MultipartFile file) {
//        if (file.isEmpty()) {
//            throw new RuntimeException("Please upload a CSV file!");
//        }
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            String line;
//            boolean isFirstLine = true; // Skip header
//            int rowNum = 0;
//            while ((line = reader.readLine()) != null) {
//                if (isFirstLine) {
//                    isFirstLine = false;
//                    continue;
//                }
//                rowNum++;
//
//                // Custom CSV parsing to handle commas within double quotes
//                List<String> columns = new ArrayList<>();
//                StringBuilder currentField = new StringBuilder();
//                boolean inQuotes = false;
//
//                for (int i = 0; i < line.length(); i++) {
//                    char ch = line.charAt(i);
//
//                    if (ch == '"') {
//                        if (inQuotes && i < line.length() - 1 && line.charAt(i + 1) == '"') {
//                            currentField.append('"');
//                            i++; // Skip the next double quote
//                        } else {
//                            inQuotes = !inQuotes;
//                        }
//                    } else if (ch == ',' && !inQuotes) {
//                        columns.add(currentField.toString());
//                        currentField = new StringBuilder();
//                    } else {
//                        currentField.append(ch);
//                    }
//                }
//                columns.add(currentField.toString()); // Add the last field
//
//                if (columns.size() < 13) {
//                    throw new RuntimeException("CSV format error at row " + rowNum + ": Insufficient columns. Found " + columns.size() + ", expected at least 13.");
//                }
//
//                String questionStem = columns.get(1).trim();
//                Boolean multiCorrect = "1".equals(columns.get(2).trim());
//
//                String questionId = IdGenerator.generateHashBasedId(questionStem);
//                Question question = questionRepository.findById(questionId).orElseGet(() -> {
//                    Question newQuestion = new Question(questionId, questionStem, multiCorrect);
//                    return questionRepository.save(newQuestion);
//                });
//
//                List<String> correctOptionsChars = new ArrayList<>();
//                String correctAnswersCell = columns.get(11).trim();
//                if (!correctAnswersCell.isEmpty()) {
//                    String[] correctArr = correctAnswersCell.split(",");
//                    for (String c : correctArr) {
//                        correctOptionsChars.add(c.trim());
//                    }
//                }
//
//                optionRepository.deleteByQuestionId(questionId);
//
//                char optionChar = 'A';
//                for (int i = 3; i < 11; i++) { // Options from column 3 to 10
//                    String optionText = columns.get(i).trim();
//                    if (!optionText.isEmpty()) {
//                        boolean isCorrect = correctOptionsChars.contains(String.valueOf(optionChar));
//                        Option option = new Option(optionText, isCorrect);
//                        option.setQuestion(question);
//
//                        String twoCharId = IdGenerator.generateRandomId(2);
//                        String generatedOptionId = questionId + "-" + twoCharId;
//
//                        while (optionRepository.findByOptionId(generatedOptionId).isPresent()) {
//                            twoCharId = IdGenerator.generateRandomId(2);
//                            generatedOptionId = questionId + "-" + twoCharId;
//                        }
//                        option.setOptionId(generatedOptionId);
//                        optionRepository.save(option);
//                    }
//                    optionChar++;
//                }
//
//                String tagsString = columns.get(12).trim();
//                if (!tagsString.isEmpty()) {
//                    String[] tagNames = tagsString.split(",");
//
//                    tagQuestionRepository.deleteByQuestionId(questionId);
//
//                    for (String tagName : tagNames) {
//                        String trimmedTagName = tagName.trim();
//                        if (!trimmedTagName.isEmpty()) {
//                            Tag tag = tagRepository.findByName(trimmedTagName).orElseGet(() -> {
//                                Tag newTag = new Tag(trimmedTagName, null);
//                                return tagRepository.save(newTag);
//                            });
//
//                            if (tagQuestionRepository.findByTagIdAndQuestionId(tag.getId(), questionId).isEmpty()) {
//                                tagQuestionRepository.save(new TagQuestion(tag, questionId));
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
//        }
//    }

    public QuestionResponse getQuestion(String questionId) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            throw new RuntimeException("Question not found");
        }
        Question question = questionOpt.get();

        List<Option> options = optionRepository.findByQuestionId(questionId);

        boolean hasCorrectAnswer = options.stream().anyMatch(opt -> Boolean.TRUE.equals(opt.getIsCorrect()));
        if (!hasCorrectAnswer) {
            throw new RuntimeException("Error: Still correct option not entered");
        }

        List<OptionResponse> optionResponses = options.stream()
                .map(opt -> new OptionResponse(
                        opt.getOptionId(),
                        questionId,
                        opt.getOptionText(),
                        opt.getIsCorrect()
                ))
                .collect(Collectors.toList());

        List<TagResponse> tagResponses = tagQuestionRepository.findByQuestionId(questionId).stream()
                .map(tagQuestion -> new TagResponse(
                        tagQuestion.getTag().getId(),
                        tagQuestion.getTag().getName(),
                        tagQuestion.getTag().getDescription()))
                .collect(Collectors.toList());

        QuestionResponse response = new QuestionResponse(
                question.getId(),
                question.getStem(),
                question.getMultiCorrect(),
                optionResponses,
                tagResponses
        );
        return response;
    }

    public List<QuestionResponse> getSampleQuestions(Integer start, Integer count) {
        List<Question> questions;
        if (start == null || count == null || (start == 0 && count == 0)) {
            // Return top 20 questions if start and count are not sent or are 0
            Pageable pageable = PageRequest.of(0, 20);
            questions = questionRepository.findAll(pageable).getContent();
        } else {
            Pageable pageable = PageRequest.of(start, count);
            questions = questionRepository.findAll(pageable).getContent();
        }

        return questions.stream().map(question -> {
            List<OptionResponse> optionResponses = optionRepository.findByQuestionId(question.getId()).stream()
                    .map(opt -> new OptionResponse(
                            opt.getOptionId(),
                            question.getId(),
                            opt.getOptionText(),
                            opt.getIsCorrect()))
                    .collect(Collectors.toList());

            List<TagResponse> tagResponses = tagQuestionRepository.findByQuestionId(question.getId()).stream()
                    .map(tagQuestion -> new TagResponse(
                            tagQuestion.getTag().getId(),
                            tagQuestion.getTag().getName(),
                            tagQuestion.getTag().getDescription()))
                    .collect(Collectors.toList());

            return new QuestionResponse(
                    question.getId(),
                    question.getStem(),
                    question.getMultiCorrect(),
                    optionResponses,
                    tagResponses);
        }).collect(Collectors.toList());
    }

    public void deleteQuestion(String questionId) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            throw new RuntimeException("Question not found");
        }
        optionRepository.deleteByQuestionId(questionId);
        tagQuestionRepository.deleteByQuestionId(questionId);
        questionRepository.deleteById(questionId);
    }
}
