package com.testpaper.demo.dto;

public class QuestionPaperRequest {
    private String name;
    private String tagName;
    private Integer questionCount;

    public QuestionPaperRequest() {
    }

    public QuestionPaperRequest(String name, String tagName, Integer questionCount) {
        this.name = name;
        this.tagName = tagName;
        this.questionCount = questionCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }
}
