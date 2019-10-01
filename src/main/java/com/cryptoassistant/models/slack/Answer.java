package com.cryptoassistant.models.slack;

import java.sql.Date;

public class Answer {

	private String token;
	private String channel;
	private String text;
	private Attachment attachments;
	private String challenge;
	private String question_id;
	private Long answer_date;
	private Date answer_date_parsed;
	private Boolean success;
	private String error_given;
	
	public Answer() {}
	
	public Answer(Question input) {
		this.token = input.getToken();
		this.channel = input.getEvent().getChannel();
		this.challenge = input.getChallenge();
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public String getChallenge() {
		return challenge;
	}

	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	public String getQuestion_id() {
		return question_id;
	}

	public void setQuestion_id(String question_id) {
		this.question_id = question_id;
	}

	public Long getAnswer_date() {
		return answer_date;
	}

	public void setAnswer_date(Long answer_date) {
		this.answer_date = answer_date;
	}

	public Date getAnswer_date_parsed() {
		return answer_date_parsed;
	}

	public void setAnswer_date_parsed(Date answer_date_parsed) {
		this.answer_date_parsed = answer_date_parsed;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getError_given() {
		return error_given;
	}

	public void setError_given(String error_given) {
		this.error_given = error_given;
	}

	public Attachment getAttachments() {
		return attachments;
	}

	public void setAttachments(Attachment attachments) {
		this.attachments = attachments;
	}

	
	
}
