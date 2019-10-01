package com.cryptoassistant.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cryptoassistant.models.cryptowatch.CandleStick;
import com.cryptoassistant.models.cryptowatch.CandleStickHolder;
import com.cryptoassistant.models.slack.Answer;
import com.cryptoassistant.models.slack.Question;
import com.cryptoassistant.models.slack.Subscription;
import com.cryptoassistant.sql.Statements;


public class SlackDAO {
	private Connection con;
	Savepoint save;
	
	public SlackDAO() {
		try{
			this.con = new ConnectionFactory().getConnection();					
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public SlackDAO(Connection con) {
		this.con = con;
	}
	
	public void insertQuestion(Question question) {
		
		try{
			String eventQuery = Statements.insertEvent();
			String questionQuery = Statements.insertQuestion();
			PreparedStatement eventStmt = con.prepareStatement(eventQuery);
			PreparedStatement questionStmt = con.prepareStatement(questionQuery);
			
			save = con.setSavepoint();
			con.setAutoCommit(false);
					
			/*
			 * Event 
			 * 	event_id, client_msg_id, type, text, user, ts, team, channel, event_ts, channel_type
			 * 		1			2		   3	 4	  5	    6	 7 		8		  9			10	
			 */		
			eventStmt.setString(1, question.getEvent_id());
			eventStmt.setString(2, question.getEvent().getClient_msg_id());
			eventStmt.setString(3, question.getEvent().getType());
			eventStmt.setString(4, question.getEvent().getText());
			eventStmt.setString(5, question.getEvent().getUser());
			eventStmt.setString(6, question.getEvent().getTs());
			eventStmt.setString(7, question.getEvent().getTeam());
			eventStmt.setString(8, question.getEvent().getChannel());
			eventStmt.setString(9, question.getEvent().getEvent_ts());
			eventStmt.setString(10, question.getEvent().getChannel_type());
			
			eventStmt.execute();
			eventStmt.close();
			
			/*
			 * Question
			 *  token, team_id, api_app_id, type, event_id, event_time, authed_users
			 *    1		  2			3		 4		 5			6			7
			 */
			questionStmt.setString(1, question.getToken());
			questionStmt.setString(2, question.getTeam_id());
			questionStmt.setString(3, question.getApi_app_id());
			questionStmt.setString(4, question.getType());
			questionStmt.setString(5, question.getEvent_id());
			questionStmt.setLong(6, question.getEvent_time());
			questionStmt.setTimestamp(7, new Timestamp(question.getEvent_time()*1000));
			questionStmt.setString(8, question.getAuthed_users_treated());
			
			questionStmt.execute();
			questionStmt.close();
			
			con.commit();
			con.setAutoCommit(true);
			
		} catch (SQLException ex){
			throw new RuntimeException(ex);
		}
		
	}
	
	public void insertAnswer(Answer answer) {
		
		try {
			
			String answerQuery = Statements.insertAnswer();
			PreparedStatement answerStmt = con.prepareStatement(answerQuery);
			
			save = con.setSavepoint();
			con.setAutoCommit(false);
			
			/*
			 *  Answer
			 *  token, channel, text, answer_date, success, error_given
			 *     1	   2	  3		    4		  5			 6
			 */
			answerStmt.setString(1, answer.getToken());
			answerStmt.setString(2, answer.getChannel());
			answerStmt.setString(3, answer.getText());
			answerStmt.setTimestamp(4, new Timestamp(answer.getAnswer_date()));
			answerStmt.setBoolean(5, answer.getSuccess());
			answerStmt.setString(6, answer.getError_given());
			
			answerStmt.execute();
			answerStmt.close();
			
			con.commit();
			con.setAutoCommit(true);
			
		} catch (SQLException ex){
			throw new RuntimeException(ex);
		}
	}
	
	public List<Subscription> getActiveSubscriptions() {
		
		List<Subscription> subbedUsers = new ArrayList<Subscription>();
		
		try {
			
			String sql = Statements.getActiveSubscriptions();
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				Subscription sub = new Subscription();
				sub.setUser(rs.getString("user"));
				sub.setActive(rs.getBoolean("active"));
				sub.setChannel(rs.getString("channel"));
				sub.setToken(rs.getString("token"));
				sub.setSubscription_type(rs.getString("subscription_type"));
				subbedUsers.add(sub);
			}
			
			rs.close();
			stmt.close();
			
			return subbedUsers;
		} catch (SQLException ex){
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public void saveCandles(CandleStickHolder candleHolder) {
		
		try {
			String sql = Statements.saveCandle();
			PreparedStatement stmt = con.prepareStatement(sql);
			
			save = con.setSavepoint();
			con.setAutoCommit(false);
			
			/*
			 *  candle_history
			 *  closetime, closetime_date, period, openprice, highprice, lowprice, closeprice, volume
			 *  	1			  2			  3		   4	      5			 6		   7		  8
			 */
			
			for(int i = 3 ; i >= 0 ; i--) {

				String period = "";
				List<CandleStick> currentPeriod = new ArrayList<CandleStick>();
				
				switch (i) {
				case 3:
					period = "1 Day";
					currentPeriod = candleHolder.getOneDay();
					break;
				case 2:
					period = "4 Hours";
					currentPeriod = candleHolder.getFourhour();
					break;
				case 1:
					period = "2 Hours";
					currentPeriod = candleHolder.getTwohour();
					break;
				case 0:
					period = "1 Hour";
					currentPeriod = candleHolder.getOnehour();
					break;
				}
				
				int commitCounter = 0;
				
				if(!currentPeriod.isEmpty()) {
					for(CandleStick c : currentPeriod) {
						
						if(commitCounter == 999) {
							con.commit();
							commitCounter = 0;
						}
						
						stmt.setInt(1, (int) c.getCloseTime());
						stmt.setTimestamp(2, new Timestamp(c.getCloseTime()*1000));
						stmt.setString(3, period);
						stmt.setDouble(4, c.getOpenPrice());
						stmt.setDouble(5, c.getHighPrice());
						stmt.setDouble(6, c.getLowPrice());
						stmt.setDouble(7, c.getClosePrice());
						stmt.setDouble(8, c.getVolume());
						
						stmt.execute();
						
						
						commitCounter++;
					}
				}
			}
			con.commit();
			stmt.close();
			con.setAutoCommit(true);
		} catch (SQLException ex){
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	public Map<String, Long> getLastClosetime() {
		
		Map<String, Long> closeTimes = new HashMap<String, Long>();
		
		try {
			for(int i = 3 ; i >= 0 ; i--) {
				String period = "";
				
				switch (i) {
				case 3:
					period = "1 Day";
					break;
				case 2:
					period = "4 Hours";
					break;
				case 1:
					period = "2 Hours";
					break;
				case 0:
					period = "1 Hour";
					break;
				}
				
				String sql = Statements.getLastClosetime();
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setString(1, period);
				
				ResultSet rs = stmt.executeQuery();
				
				while(rs.next()){
					closeTimes.put(period, (long) rs.getInt("closetime"));
				}
				
				rs.close();
				stmt.close();
			}
			
			return closeTimes;
		} catch (SQLException ex){
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public void updateCandle(CandleStick candle, String period) {

		try {
			
			String answerQuery = Statements.updateCandle();
			PreparedStatement stmt = con.prepareStatement(answerQuery);
			
			save = con.setSavepoint();
			con.setAutoCommit(false);
			
			/*
			 *  Answer
			 *  openprice, highprice, lowprice, closeprice, volume, period, closetime
			 *      1	  	   2	 	 3		    4		  5		  6			7
			 */
			stmt.setDouble(1, candle.getOpenPrice());
			stmt.setDouble(2, candle.getHighPrice());
			stmt.setDouble(3, candle.getLowPrice());
			stmt.setDouble(4, candle.getClosePrice());
			stmt.setDouble(5, candle.getVolume());
			stmt.setString(6, period);
			stmt.setInt(7, (int) candle.getCloseTime());
			
			stmt.execute();
			stmt.close();
			
			con.commit();
			con.setAutoCommit(true);
			
		} catch (SQLException ex){
			throw new RuntimeException(ex);
		}
		
	}
	
	public List<CandleStick> getXdaysHistory(String period, int nDays) {
		
		List<CandleStick> candles = new ArrayList<CandleStick>();
		
		try {
			
			String sql = Statements.getXdaysHistory();
			PreparedStatement stmt = con.prepareStatement(sql);
			
			stmt.setString(1, period);
			stmt.setInt(2, nDays-1);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				CandleStick candle = new CandleStick();
				candle.setCloseTime(rs.getLong("closetime"));
				candle.setOpenPrice(rs.getDouble("openprice"));
				candle.setHighPrice(rs.getDouble("highprice"));
				candle.setLowPrice(rs.getDouble("lowprice"));
				candle.setClosePrice(rs.getDouble("closeprice"));
				candle.setVolume(rs.getDouble("volume"));
				
				candles.add(candle);
			}
			
			rs.close();
			stmt.close();
			
			return candles;
		} catch (SQLException ex){
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	public List<CandleStick> getXdaysHistoryForDate(String period, int nDays, String date) {
		
		List<CandleStick> candles = new ArrayList<CandleStick>();
		
		try {
			
			String sql = Statements.getXdaysHistoryForDate();
			PreparedStatement stmt = con.prepareStatement(sql);
			
			/*
			 * period, closetime_date, closetime_date, days
			 * 	  1		      2				 3			 4
			 */
			stmt.setString(1, period);
			stmt.setString(2, date);
			stmt.setString(3, date);
			stmt.setInt(4, nDays-1);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				CandleStick candle = new CandleStick();
				candle.setCloseTime(rs.getLong("closetime"));
				candle.setOpenPrice(rs.getDouble("openprice"));
				candle.setHighPrice(rs.getDouble("highprice"));
				candle.setLowPrice(rs.getDouble("lowprice"));
				candle.setClosePrice(rs.getDouble("closeprice"));
				candle.setVolume(rs.getDouble("volume"));
				
				candles.add(candle);
			}
			
			rs.close();
			stmt.close();
			
			return candles;
		} catch (SQLException ex){
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public double getCurrentPrice(String period) {
		
		try {
			double currentPrice = 0;
			
			String sql = Statements.getCurrentPrice();
			PreparedStatement stmt = con.prepareStatement(sql);
			
			stmt.setString(1, period);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				currentPrice = rs.getDouble("closeprice");
			}
			
			return currentPrice;
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	public double getCurrentPriceForDate(String period, String date) {
		
		try {
			double currentPrice = 0;
			
			String sql = Statements.getCurrentPriceForDate();
			PreparedStatement stmt = con.prepareStatement(sql);
			
			stmt.setString(1, period);
			stmt.setString(2, date);
			
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				currentPrice = rs.getDouble("closeprice");
			}
			
			return currentPrice;
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public Subscription getSubscriptionStatus(Question q) {
		
		String user = q.getEvent().getUser();
		Subscription sub = new Subscription();
		
		try {
			
			String sql = Statements.getSubscriptionStatus();
			PreparedStatement stmt = con.prepareStatement(sql);
			
			stmt.setString(1, user);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				sub.setActive(rs.getBoolean("active"));
				sub.setSubscription_type(rs.getString("subscription_type"));
			}
			
			return sub;
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		
	}

	public void deactivateSubscription(Question q) {
		
		String user = q.getEvent().getUser();
		
		try {
			
			String sql = Statements.deactivateSubscription();
			PreparedStatement stmt = con.prepareStatement(sql);
			
			save = con.setSavepoint();
			con.setAutoCommit(false);
			
			stmt.setString(1, user);
			
			stmt.execute();
			stmt.close();
			
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public void subscribeUser(Question q, String subType) {
		
		String user = q.getEvent().getUser();
		
		try {
			
			String sql = Statements.getSubscriptionStatus();
			PreparedStatement stmt = con.prepareStatement(sql);
			
			save = con.setSavepoint();
			con.setAutoCommit(false);
			
			stmt.setString(1, user);
			
			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				sql = Statements.updateSubscription();
				
				/*
				 * Subscriptions
				 * active, subscription_type, user
				 *    1				2			3
				 */
				stmt = con.prepareStatement(sql);
				stmt.setBoolean(1, true);
				stmt.setString(2, subType);
				stmt.setString(3, user);
			} else {
				sql = Statements.subscribeNewUser();
				
				/*
				 * Subscriptions
				 * user, active, token, channel, subscribe_date, unsubscribe_date, subscription_type
				 *   1		2	   3		4			5				6					7
				 */
				stmt = con.prepareStatement(sql);
				stmt.setString(1, user);
				stmt.setBoolean(2, true);
				stmt.setString(3, q.getToken());
				stmt.setString(4, q.getEvent().getChannel());
				stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
				stmt.setTimestamp(6, null);
				stmt.setString(7, subType);
			}
			
			stmt.execute();
			stmt.close();
			
			con.commit();
			con.setAutoCommit(true);
			
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		
	}

	public void getXdaysHistoryFromDate(String formattedDate, int i) {
		// TODO Auto-generated method stub
		
	}
}
