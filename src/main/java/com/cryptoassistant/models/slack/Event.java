package com.cryptoassistant.models.slack;

public class Event {

	private String client_msg_id;
	private String type;
	private String text;
	private String user;
	private String ts;
	private String team;
	private String channel;
	private String event_ts;
	private String channel_type;
	public String getClient_msg_id() {
		return client_msg_id;
	}
	public void setClient_msg_id(String client_msg_id) {
		this.client_msg_id = client_msg_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getEvent_ts() {
		return event_ts;
	}
	public void setEvent_ts(String event_ts) {
		this.event_ts = event_ts;
	}
	public String getChannel_type() {
		return channel_type;
	}
	public void setChannel_type(String channel_type) {
		this.channel_type = channel_type;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	
	
}
