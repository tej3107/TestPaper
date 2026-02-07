package com.testpaper.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
	private String tagId;

	@Column(name = "question_id", nullable = false, length = 32)
	private String questionId;

	@Column(name = "created_at")
	private java.time.LocalDateTime createdAt;

	public TagQuestion(Tag tag, String questionId) {
		this.tag = tag;
		this.questionId = questionId;
		this.tagId = tag != null ? tag.getId() : null;
		this.createdAt = java.time.LocalDateTime.now();
	}

}

