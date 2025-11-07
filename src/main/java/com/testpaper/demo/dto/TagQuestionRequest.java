package com.testpaper.demo.dto;

import java.util.List;

public class TagQuestionRequest {
	
	private Long tagId;
	private List<String> questionIds;
	
	public TagQuestionRequest() {
	}
	
	public TagQuestionRequest(Long tagId, List<String> questionIds) {
		this.tagId = tagId;
		this.questionIds = questionIds;
	}
	
	public Long getTagId() {
		return tagId;
	}
	
	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
	
	public List<String> getQuestionIds() {
		return questionIds;
	}
	
	public void setQuestionIds(List<String> questionIds) {
		this.questionIds = questionIds;
	}
}

