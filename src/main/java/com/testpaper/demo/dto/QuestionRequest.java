package com.testpaper.demo.dto;

public class QuestionRequest {
	
	private String stem;
	private Boolean multiCorrect;
	
	public QuestionRequest() {
	}
	
	public QuestionRequest(String stem, Boolean multiCorrect) {
		this.stem = stem;
		this.multiCorrect = multiCorrect;
	}
	
	public String getStem() {
		return stem;
	}
	
	public void setStem(String stem) {
		this.stem = stem;
	}
	
	public Boolean getMultiCorrect() {
		return multiCorrect;
	}
	
	public void setMultiCorrect(Boolean multiCorrect) {
		this.multiCorrect = multiCorrect;
	}
}

