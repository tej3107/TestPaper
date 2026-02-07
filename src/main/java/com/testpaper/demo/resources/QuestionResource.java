package com.testpaper.demo.resources;

import com.testpaper.demo.dto.*;
import com.testpaper.demo.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/v1/questions")
public class QuestionResource {

	private final QuestionService questionService;

	public QuestionResource(QuestionService questionService) {
		this.questionService = questionService;
	}

	@PostMapping
	public ResponseEntity<QuestionResponse> createQuestion(@RequestBody QuestionRequest request) {
		try {
			QuestionResponse response = questionService.createQuestion(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("Question Already Exist")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(new QuestionResponse(null, e.getMessage(), null, null, null));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new QuestionResponse(null, e.getMessage(), null, null, null));
			}
		}
	}

	@PostMapping("/multiInsert")
	public ResponseEntity<List<QuestionResponse>> createQuestions(@RequestBody List<QuestionRequest> request) {
		try {
			List<QuestionResponse> responses = new ArrayList<>();
			request.stream().forEach(req -> {
				try {
					QuestionResponse response = questionService.createQuestion(req);
					responses.add(response);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			});
			return ResponseEntity.status(HttpStatus.CREATED).body(responses);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	@PostMapping("/{questionId}/options")
	public ResponseEntity<List<OptionResponse>> addOrUpdateOptions(@PathVariable String questionId, @RequestBody List<OptionRequest> optionRequests) {
		try {
			List<OptionResponse> responses = questionService.addOrUpdateOptions(questionId, optionRequests);
			return ResponseEntity.ok(responses);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("Question not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else if (e.getMessage().contains("Invalid option IDs") || e.getMessage().contains("Question is single correct")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return appropriate error response
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		}
	}

	@GetMapping("/{questionId}")
	public ResponseEntity<QuestionResponse> getQuestion(@PathVariable String questionId) {
		try {
			QuestionResponse response = questionService.getQuestion(questionId);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Question not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else if (e.getMessage().equals("Error: Still correct option not entered")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new QuestionResponse(null, e.getMessage(), null, null, null)); // You might want a more specific error DTO here
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new QuestionResponse(null, e.getMessage(), null, null, null));
			}
		}
	}

	@GetMapping("/sample")
	public ResponseEntity<List<QuestionResponse>> getSampleQuestions(
			@RequestParam(value = "start", defaultValue = "0") Integer start,
			@RequestParam(value = "count", defaultValue = "20") Integer count) {
		try {
			List<QuestionResponse> questionResponses = questionService.getSampleQuestions(start, count);
			return ResponseEntity.ok(questionResponses);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or a more specific error DTO
		}
	}

	@DeleteMapping("/{questionId}")
	public ResponseEntity<Void> deleteQuestion(@PathVariable String questionId) {
		try {
			questionService.deleteQuestion(questionId);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Question not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}

