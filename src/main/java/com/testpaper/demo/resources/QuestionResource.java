package com.testpaper.demo.resources;

import com.testpaper.demo.dto.*;
import com.testpaper.demo.model.Question;
import com.testpaper.demo.model.Option;
import com.testpaper.demo.repository.QuestionRepository;
import com.testpaper.demo.repository.OptionRepository;
import com.testpaper.demo.repository.TagQuestionRepository;
import com.testpaper.demo.util.IdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/questions")
public class QuestionResource {

	private final QuestionRepository questionRepository;
	private final OptionRepository optionRepository;
	private final TagQuestionRepository tagQuestionRepository;

	public QuestionResource(QuestionRepository questionRepository, OptionRepository optionRepository, TagQuestionRepository tagQuestionRepository) {
		this.questionRepository = questionRepository;
		this.optionRepository = optionRepository;
		this.tagQuestionRepository = tagQuestionRepository;
	}

	@PostMapping
	public ResponseEntity<?> createQuestion(@RequestBody QuestionRequest request) {
		// Generate hash-based 32-character ID from question stem
		String questionId = IdGenerator.generateHashBasedId(request.getStem());

		if(questionRepository.existsById(questionId)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(String.format("Question Already Exist with Id: %s!!",questionId));
		}
		
		Question question = new Question(questionId, request.getStem(), 
			request.getMultiCorrect());
		question = questionRepository.save(question);
		
		QuestionResponse response = new QuestionResponse(
			question.getId(),
			question.getStem(),
			question.getMultiCorrect(),
			new ArrayList<>() // Empty options list for new question
			, new ArrayList<>() // Empty tags list for new question
		);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/{questionId}/options")
	public ResponseEntity<?> addOrUpdateOptions(
			@PathVariable String questionId,
			@RequestBody List<OptionRequest> optionRequests) {
		
		Optional<Question> questionOpt = questionRepository.findById(questionId);
		if (questionOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		Question question = questionOpt.get();
		
		// Get all existing options for this question
		List<Option> existingOptions = optionRepository.findByQuestionId(questionId);
		
		// Validate invalid option IDs first
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
			return ResponseEntity.badRequest().body("Invalid option IDs: " + String.join(", ", invalidOptionIds));
		}
		
		// Process all options - update existing by text or create new
		List<OptionResponse> responses = new ArrayList<>();
		List<Option> optionsToUpdate = new ArrayList<>();
		
		for (OptionRequest optionRequest : optionRequests) {
			Option option;
			String generatedOptionId;
			
			// Check if optionId is provided (for editing existing option by ID)
			if (optionRequest.getOptionId() != null && !optionRequest.getOptionId().isEmpty()) {
				// Find by optionId
				Optional<Option> existingOpt = optionRepository.findByOptionId(optionRequest.getOptionId());
				option = existingOpt.get();
				generatedOptionId = option.getOptionId();
			} else {
				// Check if option text already exists for this question - if so, update that option
				Optional<Option> existingByText = optionRepository.findByQuestionIdAndOptionText(
					questionId, optionRequest.getOptionText());
				
				if (existingByText.isPresent()) {
					// Update existing option with same text
					option = existingByText.get();
					generatedOptionId = option.getOptionId();
				} else {
					// Create new option with generated optionId
					option = new Option(optionRequest.getOptionText(), 
						optionRequest.getIsCorrect());
					option.setQuestion(question);
					
					// Generate optionId: questionId-XX (2 characters)
					String twoCharId = IdGenerator.generateRandomId(2);
					generatedOptionId = questionId + "-" + twoCharId;
					
					// Ensure optionId is unique
					while (optionRepository.findByOptionId(generatedOptionId).isPresent()) {
						twoCharId = IdGenerator.generateRandomId(2);
						generatedOptionId = questionId + "-" + twoCharId;
					}
					
					option.setOptionId(generatedOptionId);
				}
			}
			
			// Update option fields
			option.setOptionText(optionRequest.getOptionText());
			option.setIsCorrect(optionRequest.getIsCorrect());
			
			optionsToUpdate.add(option);
		}
		
		// If question is not multicorrect, validate only one correct option after updates
		if (!Boolean.TRUE.equals(question.getMultiCorrect())) {
			// Count how many correct options will exist after these updates
			// Get all options that won't be updated
			List<Option> otherOptions = existingOptions.stream()
				.filter(existing -> optionsToUpdate.stream()
					.noneMatch(toUpdate -> toUpdate.getOptionId().equals(existing.getOptionId())))
				.collect(Collectors.toList());
			
			// Count correct options in other options + options being updated
			long correctCount = otherOptions.stream()
				.filter(opt -> Boolean.TRUE.equals(opt.getIsCorrect()))
				.count();
			
			correctCount += optionsToUpdate.stream()
				.filter(opt -> Boolean.TRUE.equals(opt.getIsCorrect()))
				.count();
			
			if (correctCount > 1) {
				return ResponseEntity.badRequest().body("Question is single correct. Only 1 correct option is allowed, but " + correctCount + " correct options found.");
			}
		}
		
		// Save all options
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
		
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{questionId}")
	public ResponseEntity<?> getQuestion(@PathVariable String questionId) {
		Optional<Question> questionOpt = questionRepository.findById(questionId);
		if (questionOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		Question question = questionOpt.get();
		
		// Get all options for this question
		List<Option> options = optionRepository.findByQuestionId(questionId);
		
		// Check if there is at least one correct answer
		boolean hasCorrectAnswer = options.stream()
			.anyMatch(opt -> Boolean.TRUE.equals(opt.getIsCorrect()));
		
		if (!hasCorrectAnswer) {
			return ResponseEntity.badRequest().body("Error: Still correct option not entered");
		}
		
		List<OptionResponse> optionResponses = options.stream()
			.map(opt -> new OptionResponse(
				opt.getOptionId(),
				questionId,
				opt.getOptionText(),
				opt.getIsCorrect()
			))
			.collect(Collectors.toList());
		
		QuestionResponse response = new QuestionResponse(
			question.getId(),
			question.getStem(),
			question.getMultiCorrect(),
			optionResponses
		);

		List<TagResponse> tagResponses = tagQuestionRepository.findByQuestionId(questionId).stream()
				.map(tagQuestion -> new TagResponse(
					tagQuestion.getTag().getId(),
					tagQuestion.getTag().getName(),
					tagQuestion.getTag().getDescription()))
				.collect(Collectors.toList());

		response.setTags(tagResponses);
		
		return ResponseEntity.ok(response);
	}
}

