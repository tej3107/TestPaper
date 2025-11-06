package com.testpaper.demo.repository;

import com.testpaper.demo.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, String> {
	
	List<Option> findByQuestionId(String questionId);
	
	Optional<Option> findByOptionId(String optionId);
	
	Optional<Option> findByQuestionIdAndOptionText(String questionId, String optionText);
}

