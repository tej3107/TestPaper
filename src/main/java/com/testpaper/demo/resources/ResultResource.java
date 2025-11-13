package com.testpaper.demo.resources;

import com.testpaper.demo.dto.TestResultResponse;
import com.testpaper.demo.service.ResultService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ResultResource {

    private final ResultService resultService;

    public ResultResource(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/result")
    public ResponseEntity<TestResultResponse> getUserTestResult(@RequestParam("userId") String userId,
                                                                @RequestParam("paperId") String paperId) {

        try {
            TestResultResponse testResult = resultService.getTestResults(userId, paperId);
            return ResponseEntity.ok(testResult);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
