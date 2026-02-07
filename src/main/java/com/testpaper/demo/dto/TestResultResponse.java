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
public class TestResultResponse {
    private String userId;
    private String questionPaperId;
    private String questionPaperName;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private int unattemptedQuestions;
    private List<QuestionResult> questionResults;
}
