package com.cryptoassistant.sql;

public class Statements {

	/*
	 * Statements Slack
	 */
	public static String insertEvent() {
		String sql = "insert into events (event_id, client_msg_id, type, text, user, ts, team, channel, event_ts, channel_type)" + 
						"values (?,?,?,?,?,?,?,?,?,?)";
		
		return sql;
	}
	
	public static String insertQuestion() {
		String sql = "insert into questions (token, team_id, api_app_id, type, event_id, event_time, event_time_parsed, authed_users) " +
						"values (?,?,?,?,?,?,?,?)";
		
		return sql;
	}
	
	public static String insertAnswer() {
		String sql = "insert into answers (token, channel, text, answer_date, success, error_given) " +
						"values (?,?,?,?,?,?)";
		
		return sql;
	}
	
	/*
	 * Subscriptions
	 */
	public static String getSubscriptionByUser() {
		String sql = "select subscription_id, user, active, subscribe_date, unsubscribe_date " + 
						"from subscriptions " + 
						"where user = ?";
		
		return sql;
	}
	
	public static String getActiveSubscriptions() {
		String sql = "select subscription_id, user, active, token, channel, subscribe_date, unsubscribe_date, subscription_type " +
						"from subscriptions " +
						"where active = true";
		
		return sql;
	}
	
	public static String getSubscriptionStatus() {
		String sql = "select active, subscription_type " +
						"from subscriptions " +
						"where user = ? ";
		
		return sql;
	}

	public static String deactivateSubscription() {
		String sql = "update subscriptions " +
						"set active = false " +
						"where user = ? ";
		
		return sql;
	}

	public static String subscribeNewUser() {
		String sql = "insert into subscriptions (user, active, token, channel, subscribe_date, unsubscribe_date, subscription_type) " +
						"values (?,?,?,?,?,?,?) ";
		
		return sql;
	}
	
	/*
	 * Candles
	 */
	public static String saveCandle() {
		String sql = "insert into candle_history (closetime, closetime_date, period, openprice, highprice, lowprice, closeprice, volume) " +
						"values (?,?,?,?,?,?,?,?)";
		
		return sql;
	}
	
	public static String getLastClosetime() {
		String sql = "select MAX(closetime) as closetime " +
						"from candle_history " + 
						"where period = ?";
		
		return sql;
	}
	
	public static String updateCandle() {
		String sql = "update candle_history " + 
						"set openprice = ? , highprice = ? , lowprice = ? , closeprice = ? , volume = ? " +
						"where period = ? " +
						"  and closetime = ? ";
		
		return sql;
	}
	
	public static String getXdaysHistory() {
		String sql = "select closetime, openprice, highprice, lowprice, closeprice, volume " + 
						"from candle_history " + 
						"where period = ? " +
						"and closetime_date >= CURDATE() - INTERVAL ? DAY " +
						"order by closetime_date asc";
		
		return sql;
	}
	
	public static String getXdaysHistoryForDate() {
		String sql = "select closetime, openprice, highprice, lowprice, closeprice, volume " + 
						"from candle_history " + 
						"where period = ? " +
						"and closetime_date <= ? " +
						"and closetime_date >= ? - INTERVAL ? DAY " +
						"order by closetime_date asc";
		
		return sql;
	}

	public static String getCurrentPrice() {
		String sql = "select closeprice " +
						"from candle_history " +
						"where period = ? " +
						"order by closetime desc " + 
						"limit 1";
		
		return sql;
	}

	public static String updateSubscription() {
		String sql = "update subscriptions " +
						"set active = ? , subscription_type = ? " +
						"where user = ? ";
				
		return sql;
	}

	public static String getCurrentPriceForDate() {
		String sql = "select closeprice " +
						"from candle_history " +
						"where period = ? " +
						"and closetime_date >= ? " +
						"order by closetime asc " +
						"limit 1";
		
		return sql;
	}

	
}
