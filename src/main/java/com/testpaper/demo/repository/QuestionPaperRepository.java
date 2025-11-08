package com.testpaper.demo.repository;

import com.testpaper.demo.model.QuestionPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionPaperRepository extends JpaRepository<QuestionPaper, String> {

    Optional<QuestionPaper> findByName(String name);

}
