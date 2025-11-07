package com.testpaper.demo.resources;

import com.testpaper.demo.dto.*;
import com.testpaper.demo.model.Tag;
import com.testpaper.demo.model.TagQuestion;
import com.testpaper.demo.repository.TagRepository;
import com.testpaper.demo.repository.TagQuestionRepository;
import com.testpaper.demo.repository.QuestionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tags")
public class TagResource {

	private final TagRepository tagRepository;
	private final TagQuestionRepository tagQuestionRepository;
	private final QuestionRepository questionRepository;

	public TagResource(TagRepository tagRepository, TagQuestionRepository tagQuestionRepository, 
			QuestionRepository questionRepository) {
		this.tagRepository = tagRepository;
		this.tagQuestionRepository = tagQuestionRepository;
		this.questionRepository = questionRepository;
	}

	@PostMapping
	public ResponseEntity<?> createTag(@RequestBody TagRequest request) {
		// Check if tag with same name already exists
		Optional<Tag> tagOptional = tagRepository.findByName(request.getName());
		if (tagOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(String.format("Tag with name '%s' already exists with id: '%s'", request.getName(), tagOptional.get().getId()));
		}
		
		Tag tag = new Tag(request.getName(), request.getDescription());
		tag = tagRepository.save(tag);
		
		TagResponse response = new TagResponse(
			tag.getId(),
			tag.getName(),
			tag.getDescription()
		);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/{tagId}/questions")
	public ResponseEntity<?> assignQuestionsToTag(
			@PathVariable Long tagId,
			@RequestBody List<String> questionIds) {
		
		// Verify tag exists
		Optional<Tag> tagOpt = tagRepository.findById(tagId);
		if (tagOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		Tag tag = tagOpt.get();
		
		if (questionIds == null || questionIds.isEmpty()) {
			return ResponseEntity.badRequest().body("Question IDs list cannot be empty");
		}
		
		// Verify all question IDs exist
		List<String> invalidQuestionIds = new ArrayList<>();
		for (String questionId : questionIds) {
			if (questionId == null || questionId.isEmpty()) {
				invalidQuestionIds.add("(null or empty)");
			} else if (!questionRepository.existsById(questionId)) {
				invalidQuestionIds.add(questionId);
			}
		}
		
		if (!invalidQuestionIds.isEmpty()) {
			return ResponseEntity.badRequest()
				.body("Invalid question IDs: " + String.join(", ", invalidQuestionIds));
		}
		
		// Create or update tag-question mappings
		List<TagQuestion> tagQuestions = new ArrayList<>();
		for (String questionId : questionIds) {
			// Check if mapping already exists
			Optional<TagQuestion> existing = tagQuestionRepository.findByTagIdAndQuestionId(tagId, questionId);
			
			TagQuestion tagQuestion;
			if (existing.isPresent()) {
				// Mapping already exists, skip
				tagQuestion = existing.get();
			} else {
				// Create new mapping
				tagQuestion = new TagQuestion(tag, questionId);
				tagQuestion = tagQuestionRepository.save(tagQuestion);
			}
			
			tagQuestions.add(tagQuestion);
		}
		
		// Return response with all question IDs for this tag
		List<String> allQuestionIds = tagQuestionRepository.findByTagId(tagId).stream()
			.map(TagQuestion::getQuestionId)
			.collect(Collectors.toList());
		
		TagQuestionResponse response = new TagQuestionResponse(
			tagId,
			tag.getName(),
			allQuestionIds
		);
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{tagId}/questions")
	public ResponseEntity<?> getQuestionsByTag(@PathVariable Long tagId) {
		// Verify tag exists
		Optional<Tag> tagOpt = tagRepository.findById(tagId);
		if (tagOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		Tag tag = tagOpt.get();
		
		// Get all question IDs for this tag
		List<String> questionIds = tagQuestionRepository.findByTagId(tagId).stream()
			.map(TagQuestion::getQuestionId)
			.collect(Collectors.toList());
		
		TagQuestionResponse response = new TagQuestionResponse(
			tagId,
			tag.getName(),
			questionIds
		);
		
		return ResponseEntity.ok(response);
	}
}

