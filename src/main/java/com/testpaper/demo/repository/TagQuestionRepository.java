package com.testpaper.demo.repository;

import com.testpaper.demo.model.TagQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagQuestionRepository extends JpaRepository<TagQuestion, Long> {
	
	List<TagQuestion> findByTagId(Long tagId);
	
	List<TagQuestion> findByQuestionId(String questionId);
	
	@Query("SELECT tq FROM TagQuestion tq WHERE tq.tagId = :tagId AND tq.questionId = :questionId")
	Optional<TagQuestion> findByTagIdAndQuestionId(@Param("tagId") Long tagId, @Param("questionId") String questionId);

	@Query("SELECT tq FROM TagQuestion tq WHERE tq.questionId = :questionId")
	List<TagQuestion> findByTagIdByQuestionId(@Param("questionId") String questionId);
	
	@Modifying
	@Query("DELETE FROM TagQuestion tq WHERE tq.tagId = :tagId")
	void deleteByTagId(@Param("tagId") Long tagId);
}

