package com.testpaper.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TestUserResponseId implements Serializable {

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String questionPaperId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestUserResponseId that = (TestUserResponseId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(questionPaperId, that.questionPaperId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, questionPaperId);
    }
}
