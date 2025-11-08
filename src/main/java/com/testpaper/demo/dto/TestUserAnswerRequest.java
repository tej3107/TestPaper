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
public class TestUserAnswerRequest {
    private String userId;
    private String questionPaperId;
    private List<QuestionAnswer> answers = new ArrayList<>();
}
