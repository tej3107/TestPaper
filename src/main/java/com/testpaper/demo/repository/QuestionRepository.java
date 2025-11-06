package com.testpaper.demo.repository;

import com.testpaper.demo.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, String> {
}

