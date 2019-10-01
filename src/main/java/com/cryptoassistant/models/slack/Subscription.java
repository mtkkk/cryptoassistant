package com.cryptoassistant.models.slack;

import java.sql.Date;

public class Subscription {

	private int subscription_id;
	private String user;
	private Boolean active;
	private String token;
	private String channel;
	private Date subscribe_date;
	private Date unsubscribe_date;
	private String subscription_type;
	
	public int getSubscription_id() {
		return subscription_id;
	}
	public void setSubscription_id(int subscription_id) {
		this.subscription_id = subscription_id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
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
	public Date getSubscribe_date() {
		return subscribe_date;
	}
	public void setSubscribe_date(Date subscribe_date) {
		this.subscribe_date = subscribe_date;
	}
	public Date getUnsubscribe_date() {
		return unsubscribe_date;
	}
	public void setUnsubscribe_date(Date unsubscribe_date) {
		this.unsubscribe_date = unsubscribe_date;
	}
	public String getSubscription_type() {
		return subscription_type;
	}
	public void setSubscription_type(String subscription_type) {
		this.subscription_type = subscription_type;
	}
	
}
