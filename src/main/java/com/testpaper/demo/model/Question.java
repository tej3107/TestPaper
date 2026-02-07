package com.testpaper.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {

	@Id
	@Column(length = 32)
	private String id;

	@Column(columnDefinition = "TEXT")
	private String stem;

	private Boolean multiCorrect;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Option> options = new ArrayList<>();

	public Question(String id, String stem, Boolean multiCorrect) {
		this.id = id;
		this.stem = stem;
		this.multiCorrect = multiCorrect;
		this.createdAt = LocalDateTime.now();
	}

}

