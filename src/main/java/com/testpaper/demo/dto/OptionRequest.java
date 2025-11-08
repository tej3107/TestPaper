package com.testpaper.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionRequest {
	
	private String optionId;
	private String optionText;
	private Boolean isCorrect;

}

