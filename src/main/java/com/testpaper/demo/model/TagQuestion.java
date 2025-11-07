package com.testpaper.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tag_questions", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"tag_id", "question_id"})
})
public class TagQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id", nullable = false)
	private Tag tag;

	@Column(name = "tag_id", insertable = false, updatable = false)
	private Long tagId;

	@Column(name = "question_id", nullable = false, length = 32)
	private String questionId;

	@Column(name = "created_at")
	private java.time.LocalDateTime createdAt;

	public TagQuestion() {
	}

	public TagQuestion(Tag tag, String questionId) {
		this.tag = tag;
		this.questionId = questionId;
		this.tagId = tag != null ? tag.getId() : null;
		this.createdAt = java.time.LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
		this.tagId = tag != null ? tag.getId() : null;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public java.time.LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.time.LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}

