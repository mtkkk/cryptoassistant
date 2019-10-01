package com.cryptoassistant.models.slack;

public class Message {

	private String subtype;
	private String text;
	private String type;
	private String bot_id;
	private String ts;
	private String username;
	
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBot_id() {
		return bot_id;
	}
	public void setBot_id(String bot_id) {
		this.bot_id = bot_id;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
