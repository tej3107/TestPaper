package com.testpaper.demo.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Data
public class AttemptProgress {
    private QuestionPaperResponse questionPaper;
    private int currentQuestionIndex;
    private Map<String, List<String>> userAnswers = new HashMap<>();
    private String userId;

    public AttemptProgress(QuestionPaperResponse questionPaper, String userId) {
        this.questionPaper = questionPaper;
        this.userId = userId;
        this.currentQuestionIndex = 0;
    }
}
