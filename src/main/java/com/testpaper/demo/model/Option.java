package com.testpaper.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "options")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Option {

	@Id
	@Column(name = "option_id", length = 35)
	private String optionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "question_id",
			nullable = false,
			columnDefinition = "varchar(255)",
			foreignKey = @ForeignKey(name = "fk_options_question")
	)
	private Question question;

	@Column(name = "question_id", length = 35, insertable = false, updatable = false)
	private String questionId;

	@Column(name = "option_text", columnDefinition = "TEXT")
	private String optionText;

	@Column(name = "is_correct")
	private Boolean isCorrect;

	public Option(String optionText, Boolean isCorrect) {
		this.optionText = optionText;
		this.isCorrect = isCorrect;
	}
}

