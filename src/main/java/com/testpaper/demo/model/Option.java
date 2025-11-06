package com.testpaper.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "options")
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

	public Option() {
	}

	public Option(String optionText, Boolean isCorrect) {
		this.optionText = optionText;
		this.isCorrect = isCorrect;
	}

	public String getOptionId() {
		return optionId;
	}

	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
		this.questionId = question != null ? question.getId() : null;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
}

