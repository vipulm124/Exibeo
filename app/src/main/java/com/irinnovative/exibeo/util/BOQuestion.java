package com.irinnovative.exibeo.util;

public class BOQuestion {

	private String questionID;

	public String getQuestionID() {
		return questionID;
	}

	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	private String question;

	private String answer;

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isCommentRequired() {
		return isCommentRequired;
	}

	private boolean isCommentRequired;

	private boolean isAnswered;

	public boolean isAnswered() {
		return isAnswered;
	}

	public void setAnswered(boolean isAnswered) {
		this.isAnswered = isAnswered;
	}

	private String optionsRaw;

	public void setOptionsRaw(String optionsRaw) {
		this.optionsRaw = optionsRaw;
	}

	public String[] getOptions() {
		try {
			options = optionsRaw.split("\\|");
		} catch (Exception ex) {
			options[0] = "Error";
		}
		return options;
	}

	private String[] options;
}
