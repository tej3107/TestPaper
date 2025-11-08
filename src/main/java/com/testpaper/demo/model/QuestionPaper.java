package com.testpaper.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "question_papers")
public class QuestionPaper {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @ElementCollection
    @CollectionTable(name = "question_paper_questions", joinColumns = @JoinColumn(name = "question_paper_id"))
    @Column(name = "question_id")
    private List<String> questionIds = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public QuestionPaper(String id, String name, List<String> questionIds) {
        this.id = id;
        this.name = name;
        this.questionIds = questionIds;
        this.createdAt = LocalDateTime.now();
    }

}
