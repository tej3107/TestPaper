package com.testpaper.demo.dto;

public class OptionResponse {
	
	private String optionId;
	private String questionId;
	private String optionText;
	private Boolean isCorrect;
	
	public OptionResponse() {
	}
	
	public OptionResponse(String optionId, String questionId, String optionText, Boolean isCorrect) {
		this.optionId = optionId;
		this.questionId = questionId;
		this.optionText = optionText;
		this.isCorrect = isCorrect;
	}
	
	public String getOptionId() {
		return optionId;
	}
	
	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}
	
	public String getQuestionId() {
		return questionId;
	}
	
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	
	public String getOptionText() {
		return optionText;
	}
	
	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}
	
	public Boolean getIsCorrect() {
		return isCorrect;
	}
	
	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
}

