package com.testpaper.demo.dto;

import java.util.List;
import java.util.ArrayList;

public class QuestionPaperQuestionResponse {
    private String id;
    private String stem;
    private List<QuestionPaperOptionResponse> options = new ArrayList<>();

    public QuestionPaperQuestionResponse() {
    }

    public QuestionPaperQuestionResponse(String id, String stem, List<QuestionPaperOptionResponse> options) {
        this.id = id;
        this.stem = stem;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public List<QuestionPaperOptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionPaperOptionResponse> options) {
        this.options = options;
    }
}
