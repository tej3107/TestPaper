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
public class QuestionRequest {
	
	private String stem;
	private Boolean multiCorrect;
	private List<OptionRequest> optionRequests;
	private List<String> tagNames;

}

