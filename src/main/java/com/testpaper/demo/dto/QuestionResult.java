package com.testpaper.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResult {
    private String questionId;
    private String questionStem;
    private List<QuestionPaperOptionResponse> allOptions;
    private List<String> selectedOptionIds;
    private List<String> correctOptionIds;
    private boolean isCorrect;
    private boolean isAttempted;
}
