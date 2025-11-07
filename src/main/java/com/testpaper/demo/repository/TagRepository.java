package com.testpaper.demo.repository;

import com.testpaper.demo.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
	
	Optional<Tag> findByName(String name);
}

