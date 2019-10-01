package com.cryptoassistant.models.slack;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class Question {

	private String token;
	private String team_id;
	private String api_app_id;
	private Event event;
	private String type;
	private String event_id;
	private Long event_time;
	private Date event_time_parsed;
	private List<String> authed_users;
	private transient String authed_users_treated;
	private String challenge;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getChallenge() {
		return challenge;
	}
	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}
	public String getTeam_id() {
		return team_id;
	}
	public void setTeam_id(String team_id) {
		this.team_id = team_id;
	}
	public String getApi_app_id() {
		return api_app_id;
	}
	public void setApi_app_id(String api_app_id) {
		this.api_app_id = api_app_id;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public String getEvent_id() {
		return event_id;
	}
	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}
	public Long getEvent_time() {
		return event_time;
	}
	public void setEvent_time(Long event_time) {
		this.event_time = event_time;
	}
	public List<String> getAuthed_users() {
		return authed_users;
	}
	public void setAuthed_users(List<String> authed_users) {
		this.authed_users = authed_users;
	}
	public Date getEvent_time_parsed() {
		Date date = new Date(this.event_time*1000);
		this.event_time_parsed = date;
		return event_time_parsed;
	}
	public void setEvent_time_parsed(Date event_time_parsed) {
		this.event_time_parsed = event_time_parsed;
	}
	public String getAuthed_users_treated() {
		String treated = String.join(";", this.authed_users);
		this.authed_users_treated = treated;
		return authed_users_treated;
	}
	public void setAuthed_users_treated(String authed_users_treated) {
		this.authed_users_treated = authed_users_treated;
	}
		
}
