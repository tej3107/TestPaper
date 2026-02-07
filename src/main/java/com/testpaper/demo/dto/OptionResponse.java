package com.testpaper.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionResponse {
	
	private String optionId;
	private String questionId;
	private String optionText;
	private Boolean isCorrect;

}

