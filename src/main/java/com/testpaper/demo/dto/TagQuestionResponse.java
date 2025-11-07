package com.testpaper.demo.dto;

import java.util.List;

public class TagQuestionResponse {
	
	private Long tagId;
	private String tagName;
	private List<String> questionIds;
	
	public TagQuestionResponse() {
	}
	
	public TagQuestionResponse(Long tagId, String tagName, List<String> questionIds) {
		this.tagId = tagId;
		this.tagName = tagName;
		this.questionIds = questionIds;
	}
	
	public Long getTagId() {
		return tagId;
	}
	
	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public List<String> getQuestionIds() {
		return questionIds;
	}
	
	public void setQuestionIds(List<String> questionIds) {
		this.questionIds = questionIds;
	}
}

