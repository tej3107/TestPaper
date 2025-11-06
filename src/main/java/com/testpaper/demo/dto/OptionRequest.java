package com.testpaper.demo.dto;

public class OptionRequest {
	
	private String optionId;
	private String optionText;
	private Boolean isCorrect;
	
	public OptionRequest() {
	}
	
	public OptionRequest(String optionId, String optionText, Boolean isCorrect) {
		this.optionId = optionId;
		this.optionText = optionText;
		this.isCorrect = isCorrect;
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
	
	public Boolean getIsCorrect() {
		return isCorrect;
	}
	
	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
}

