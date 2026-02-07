package com.testpaper.demo.repository;

import com.testpaper.demo.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, String> {
	
	List<Option> findByQuestionId(String questionId);
	
	Optional<Option> findByOptionId(String optionId);
	
	Optional<Option> findByQuestionIdAndOptionText(String questionId, String optionText);

	@Modifying
	@Transactional
	@Query("DELETE FROM Option o WHERE o.question.id = :questionId")
	void deleteByQuestionId(String questionId);
}

