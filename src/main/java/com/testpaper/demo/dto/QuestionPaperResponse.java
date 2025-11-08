package com.testpaper.demo.dto;

import java.util.List;
import java.util.ArrayList;

public class QuestionPaperResponse {
    private String id;
    private String name;
    private List<QuestionPaperQuestionResponse> questions = new ArrayList<>();

    public QuestionPaperResponse() {
    }

    public QuestionPaperResponse(String id, String name, List<QuestionPaperQuestionResponse> questions) {
        this.id = id;
        this.name = name;
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QuestionPaperQuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionPaperQuestionResponse> questions) {
        this.questions = questions;
    }
}
