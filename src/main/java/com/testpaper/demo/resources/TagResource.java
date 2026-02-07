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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.testpaper.demo.service.TagService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/v1/tags")
public class TagResource {

	private final TagService tagService;

	public TagResource(TagService tagService) {
		this.tagService = tagService;
	}

	@PostMapping
	public ResponseEntity<TagResponse> createTag(@RequestBody TagRequest request) {
		try {
			TagResponse response = tagService.createTag(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("Tag Already Exist")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(new TagResponse(null, e.getMessage(), null));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TagResponse(null, e.getMessage(), null));
			}
		}
	}

	@GetMapping("/{tagName}")
	public ResponseEntity<TagResponse> getTagOnName(@PathVariable String tagName) {
		try {
			TagResponse response = tagService.getTagByName(tagName);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("Tag not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		}
	}

	@PostMapping("/{tagName}/questions")
	public ResponseEntity<?> assignQuestionsToTag(
			@PathVariable String tagName,
			@RequestBody List<String> questionIds) {
		try {
			TagQuestionResponse response = tagService.assignQuestionsToTag(tagName, questionIds);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("Tag not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else if (e.getMessage().contains("Question IDs list cannot be empty") || e.getMessage().contains("Invalid question IDs")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
			}
		}
	}

	@GetMapping("/{tagId}/questions")
	public ResponseEntity<TagQuestionResponse> getQuestionsByTag(@PathVariable String tagId) {
		try {
			TagQuestionResponse response = tagService.getQuestionsByTag(tagId);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("Tag not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@GetMapping("/sample")
	public ResponseEntity<List<TagResponse>> getSampleTags(
			@RequestParam(value = "start", defaultValue = "0") Integer start,
			@RequestParam(value = "count", defaultValue = "20") Integer count) {
		try {
			List<TagResponse> tagResponses = tagService.getSampleTags(start, count);
			return ResponseEntity.ok(tagResponses);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PutMapping("/{tagId}")
	public ResponseEntity<TagResponse> updateTag(@PathVariable String tagId, @RequestBody TagRequest request) {
		try {
			TagResponse response = tagService.updateTag(tagId, request);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			if (e.getMessage().contains("Tag not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
			}
		}
	}

	@DeleteMapping("/{tagId}")
	public ResponseEntity<Void> deleteTag(@PathVariable String tagId) {
		try {
			tagService.deleteTag(tagId);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			if (e.getMessage().contains("Tag not found")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			} else if (e.getMessage().contains("Tag is associated with questions")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).build();
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}

