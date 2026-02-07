package com.testpaper.demo.resources;

import com.testpaper.demo.dto.QuestionPaperRequest;
import com.testpaper.demo.dto.QuestionPaperResponse;
import com.testpaper.demo.dto.QuestionPaperSummaryResponse;
import com.testpaper.demo.service.QuestionPaperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/questionpapers")
public class QuestionPaperResource {

    private final QuestionPaperService questionPaperService;

    public QuestionPaperResource(QuestionPaperService questionPaperService) {
        this.questionPaperService = questionPaperService;
    }

    @PostMapping
    public ResponseEntity<QuestionPaperResponse> createQuestionPaper(@RequestBody QuestionPaperRequest request) {
        try {
            QuestionPaperResponse response = questionPaperService.createQuestionPaper(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("cannot be empty") || e.getMessage().contains("must be a positive number") ||
                e.getMessage().contains("exceeds available questions")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new QuestionPaperResponse(null, e.getMessage(), null));
            } else if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new QuestionPaperResponse(null, e.getMessage(), null));
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new QuestionPaperResponse(null, e.getMessage(), null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new QuestionPaperResponse(null, e.getMessage(), null));
            }
        }
    }

    @GetMapping("/{questionPaperId}")
    public ResponseEntity<QuestionPaperResponse> getQuestionPaper(@PathVariable String questionPaperId) {
        try {
            QuestionPaperResponse response = questionPaperService.getQuestionPaper(questionPaperId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Question Paper not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<List<QuestionPaperSummaryResponse>> getQuestionPaperSummaries(
            @RequestParam(value = "start", defaultValue = "0") Integer start,
            @RequestParam(value = "count", defaultValue = "20") Integer count) {
        try {
            List<QuestionPaperSummaryResponse> summaries = questionPaperService.getQuestionPaperSummaries(start, count);
            return ResponseEntity.ok(summaries);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or a specific error DTO
        }
    }
}
