package com.testpaper.demo.dto;

import java.util.List;

public class QuestionResponse {
	
	private String id;
	private String stem;
	private Boolean multiCorrect;
	private List<OptionResponse> options;
	private List<TagResponse> tags;
	
	public QuestionResponse() {
	}
	
	public QuestionResponse(String id, String stem, Boolean multiCorrect, List<OptionResponse> options, List<TagResponse> tags) {
		this.id = id;
		this.stem = stem;
		this.multiCorrect = multiCorrect;
		this.options = options;
		this.tags = tags;
	}
	public QuestionResponse(String id, String stem, Boolean multiCorrect, List<OptionResponse> options) {
		this.id = id;
		this.stem = stem;
		this.multiCorrect = multiCorrect;
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
	
	public Boolean getMultiCorrect() {
		return multiCorrect;
	}
	
	public void setMultiCorrect(Boolean multiCorrect) {
		this.multiCorrect = multiCorrect;
	}
	
	public List<OptionResponse> getOptions() {
		return options;
	}
	
	public void setOptions(List<OptionResponse> options) {
		this.options = options;
	}

	public List<TagResponse> getTags() {
		return tags;
	}

	public void setTags(List<TagResponse> tags) {
		this.tags = tags;
	}
}

