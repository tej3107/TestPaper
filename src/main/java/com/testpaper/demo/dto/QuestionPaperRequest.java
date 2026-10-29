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
public class QuestionPaperRequest {

    private String name;
    private String tagName; // single tag, kept for backward compatibility
    private List<String> tagNames;
    private Integer questionCount;

}
