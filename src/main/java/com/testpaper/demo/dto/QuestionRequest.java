package com.testpaper.demo.dto;

import java.util.List;

public class QuestionRequest {
	
	private String stem;
	private Boolean multiCorrect;
	private List<OptionRequest> optionRequests;
	private List<String> tagNames;

	public QuestionRequest(String stem, Boolean multiCorrect, List<OptionRequest> optionRequests, List<String> tagNames) {
		this.stem = stem;
		this.multiCorrect = multiCorrect;
		this.optionRequests = optionRequests;
		this.tagNames = tagNames;
	}

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

	public List<OptionRequest> getOptionRequests() {
		return optionRequests;
	}

	public void setOptionRequests(List<OptionRequest> optionRequests) {
		this.optionRequests = optionRequests;
	}

	public List<String> getTagNames() {
		return tagNames;
	}

	public void setTagNames(List<String> tagNames) {
		this.tagNames = tagNames;
	}
}

