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
public class QuestionPaperResponse {

    private String id;
    private String name;
    private List<QuestionPaperQuestionResponse> questions = new ArrayList<>();

}
