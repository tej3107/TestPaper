package com.testpaper.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
	
	private String id;
	private String stem;
	private Boolean multiCorrect;
	private List<OptionResponse> options;
	private List<TagResponse> tags;

}

