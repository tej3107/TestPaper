package com.testpaper.demo.dto;

public class QuestionPaperOptionResponse {
    private String optionId;
    private String optionText;

    public QuestionPaperOptionResponse() {
    }

    public QuestionPaperOptionResponse(String optionId, String optionText) {
        this.optionId = optionId;
        this.optionText = optionText;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
}
