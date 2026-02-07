package com.testpaper.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "test_user_responses")
public class TestUserResponse {

    @EmbeddedId
    private TestUserResponseId id;

    @Column(columnDefinition = "TEXT")
    private String answersJson; // Stores a JSON string of questionId to selectedOptionId mappings

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TestUserResponse(TestUserResponseId id, String answersJson) {
        this.id = id;
        this.answersJson = answersJson;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
