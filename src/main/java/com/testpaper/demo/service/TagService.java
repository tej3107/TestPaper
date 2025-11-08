package com.testpaper.demo.service;

import com.testpaper.demo.dto.TagRequest;
import com.testpaper.demo.dto.TagResponse;
import com.testpaper.demo.model.Tag;
import com.testpaper.demo.model.TagQuestion;
import com.testpaper.demo.repository.TagRepository;
import com.testpaper.demo.repository.TagQuestionRepository;
import com.testpaper.demo.util.IdGenerator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import com.testpaper.demo.repository.QuestionRepository;
import com.testpaper.demo.dto.TagQuestionResponse;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final TagQuestionRepository tagQuestionRepository;
    private final QuestionRepository questionRepository;

    public TagService(TagRepository tagRepository, TagQuestionRepository tagQuestionRepository, QuestionRepository questionRepository) {
        this.tagRepository = tagRepository;
        this.tagQuestionRepository = tagQuestionRepository;
        this.questionRepository = questionRepository;
    }

    public TagResponse createTag(TagRequest request) {
        String tagId = IdGenerator.generateRandomId(10);
        if (tagRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException(String.format("Tag Already Exist with name: %s!!", request.getName()));
        }
        Tag tag = new Tag(tagId, request.getName(), request.getDescription());
        tag = tagRepository.save(tag);
        return new TagResponse(tag.getId(), tag.getName(), tag.getDescription());
    }

    public TagResponse getTag(String tagId) {
        Optional<Tag> tagOptional = tagRepository.findById(tagId);
        if (tagOptional.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }
        Tag tag = tagOptional.get();
        return new TagResponse(tag.getId(), tag.getName(), tag.getDescription());
    }

    public TagResponse getTagByName(String tagName) {
        Optional<Tag> tagOptional = tagRepository.findByName(tagName);
        if (tagOptional.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }
        Tag tag = tagOptional.get();
        return new TagResponse(tag.getId(), tag.getName(), tag.getDescription());
    }

    public TagResponse updateTag(String tagId, TagRequest request) {
        Optional<Tag> tagOptional = tagRepository.findById(tagId);
        if (tagOptional.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }
        Tag tag = tagOptional.get();
        tag.setName(request.getName());
        tag.setDescription(request.getDescription());
        tag = tagRepository.save(tag);
        return new TagResponse(tag.getId(), tag.getName(), tag.getDescription());
    }

    public void deleteTag(String tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new RuntimeException("Tag not found");
        }
        List<TagQuestion> tagQuestions = tagQuestionRepository.findByTagId(tagId);
        if (!tagQuestions.isEmpty()) {
            throw new RuntimeException("Tag is associated with questions and cannot be deleted");
        }
        tagRepository.deleteById(tagId);
    }

    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName(), tag.getDescription()))
                .collect(Collectors.toList());
    }

    public List<TagResponse> getSampleTags(Integer start, Integer count) {
        List<Tag> tags;
        if (start == null || count == null || (start == 0 && count == 0)) {
            Pageable pageable = PageRequest.of(0, 20);
            tags = tagRepository.findAll(pageable).getContent();
        } else {
            Pageable pageable = PageRequest.of(start, count);
            tags = tagRepository.findAll(pageable).getContent();
        }
        return tags.stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName(), tag.getDescription()))
                .collect(Collectors.toList());
    }

    public TagQuestionResponse assignQuestionsToTag(String tagName, List<String> questionIds) {
        Optional<Tag> tagOpt = tagRepository.findByName(tagName);
        if (tagOpt.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }
        Tag tag = tagOpt.get();

        if (questionIds == null || questionIds.isEmpty()) {
            throw new RuntimeException("Question IDs list cannot be empty");
        }

        List<String> invalidQuestionIds = new ArrayList<>();
        for (String questionId : questionIds) {
            if (questionId == null || questionId.isEmpty()) {
                invalidQuestionIds.add("(null or empty)");
            } else if (!questionRepository.existsById(questionId)) {
                invalidQuestionIds.add(questionId);
            }
        }

        if (!invalidQuestionIds.isEmpty()) {
            throw new RuntimeException("Invalid question IDs: " + String.join(", ", invalidQuestionIds));
        }

        for (String questionId : questionIds) {
            if (tagQuestionRepository.findByTagIdAndQuestionId(tag.getId(), questionId).isEmpty()) {
                tagQuestionRepository.save(new TagQuestion(tag, questionId));
            }
        }

        List<String> allQuestionIds = tagQuestionRepository.findByTagId(tag.getId()).stream()
                .map(TagQuestion::getQuestionId)
                .collect(Collectors.toList());

        return new TagQuestionResponse(
                tag.getId(),
                tag.getName(),
                allQuestionIds
        );
    }

    public TagQuestionResponse getQuestionsByTag(String tagId) {
        Optional<Tag> tagOpt = tagRepository.findById(tagId);
        if (tagOpt.isEmpty()) {
            throw new RuntimeException("Tag not found");
        }
        Tag tag = tagOpt.get();

        List<String> questionIds = tagQuestionRepository.findByTagId(tagId).stream()
                .map(TagQuestion::getQuestionId)
                .collect(Collectors.toList());

        return new TagQuestionResponse(
                tagId,
                tag.getName(),
                questionIds
        );
    }
}
