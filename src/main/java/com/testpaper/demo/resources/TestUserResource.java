package com.testpaper.demo.resources;

import com.testpaper.demo.dto.TestUserAnswerRequest;
import com.testpaper.demo.model.TestUserResponse;
import com.testpaper.demo.service.TestUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/testuser")
public class TestUserResource {

    private final TestUserService testUserService;

    public TestUserResource(TestUserService testUserService) {
        this.testUserService = testUserService;
    }

    @PostMapping("/answers")
    public ResponseEntity<TestUserResponse> saveUserAnswers(@RequestBody TestUserAnswerRequest request) {
        try {
            TestUserResponse response = testUserService.saveUserAnswers(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("cannot be empty") || e.getMessage().contains("not found") ||
                e.getMessage().contains("not part of")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new TestUserResponse(null, e.getMessage(), null, null)); // Or a specific error DTO
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or a specific error DTO
            }
        }
    }
}
