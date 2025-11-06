package com.testpaper.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	public Question() {
	}

	public Question(String id, String stem, Boolean multiCorrect) {
		this.id = id;
		this.stem = stem;
		this.multiCorrect = multiCorrect;
		this.createdAt = LocalDateTime.now();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStem() {
		return stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	public Boolean getMultiCorrect() {
		return multiCorrect;
	}

	public void setMultiCorrect(Boolean multiCorrect) {
		this.multiCorrect = multiCorrect;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
}

