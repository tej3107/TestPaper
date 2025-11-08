package com.testpaper.demo.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

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

    public QuestionPaper() {
    }

    public QuestionPaper(String id, String name, List<String> questionIds) {
        this.id = id;
        this.name = name;
        this.questionIds = questionIds;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
