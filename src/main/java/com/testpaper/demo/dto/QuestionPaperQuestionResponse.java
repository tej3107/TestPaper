package com.testpaper.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionPaperQuestionResponse {

    private String id;
    private String stem;
    private Boolean multiCorrect;
    private List<QuestionPaperOptionResponse> options = new ArrayList<>();

}
