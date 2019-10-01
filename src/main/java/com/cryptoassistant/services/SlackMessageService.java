package com.cryptoassistant.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cryptoassistant.dao.SlackDAO;
import com.cryptoassistant.models.analysis.Analysis;
import com.cryptoassistant.models.slack.Answer;
import com.cryptoassistant.models.slack.PostMessageResponse;
import com.cryptoassistant.models.slack.Question;
import com.cryptoassistant.models.slack.Subscription;
import com.google.gson.Gson;

import kong.unirest.GsonObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

public class SlackMessageService {
	
	SlackDAO slackDAO;
	
	public SlackMessageService(SlackDAO dao) {
		this.slackDAO = dao;
	}

	public void sendMessage(Answer ans) {
		
		try {
			Gson gson = new Gson();
			ans.setAnswer_date(System.currentTimeMillis());
			
			Unirest.config().setObjectMapper(new GsonObjectMapper());
			HttpResponse<JsonNode> response = Unirest.post("https://slack.com/api/chat.postMessage")
				      .header("content-type", "application/json; charset=utf-8")
				      .header("Authorization", "Bearer xoxb-532001220037-665912026852-mGs6CM6PYfpPIeXZXwTbYZaS")
				      .body(ans)
				      .asJson();
			
			
			
			PostMessageResponse  ansBody = gson.fromJson(response.getBody().toString(), PostMessageResponse.class);
			ans.setSuccess(ansBody.getOk());
			
			// An error occurred while trying to respond
			if(!ans.getSuccess()) {
				ans.setError_given(ansBody.getError());
			}
			
			slackDAO.insertAnswer(ans);
			
		} catch(UnirestException e) {
			e.printStackTrace();
		}
		
	}
	
	public Answer detectIntentAndAnswer(Question q) {
		
		Answer ans = new Answer(q);
		
		try {
			String question = q.getEvent().getText();
			
			if(question.indexOf("!subscription") != -1) {
				
				// Command !subscription
				String[] splitQ = question.split(" ");
				
				if(splitQ.length > 1) {
					String command = splitQ[1];
					if(command.equalsIgnoreCase("subscribe")) {
						if(splitQ.length > 3) {
							// Check for subscription type
							if(splitQ[2].equalsIgnoreCase("type") && splitQ[3].equalsIgnoreCase("1")) {
								slackDAO.subscribeUser(q, "1");
								
								ans.setText("You are now subscribed!");
							} else if(splitQ[2].equalsIgnoreCase("type") && splitQ[3].equalsIgnoreCase("2")) {
								slackDAO.subscribeUser(q, "2");
								
								ans.setText("You are now subscribed!");
							} else {
								throw new Exception();
							}
						} else {
							// Activate default subscription
							slackDAO.subscribeUser(q, "1");
							
							ans.setText("You are now subscribed!");
						}
					} else if(command.equalsIgnoreCase("unsubscribe")) {
						// Deactivate subscription
						slackDAO.deactivateSubscription(q);
						
						ans.setText("You have been unsubscribed and will no longer receive analysis.");
					} else if(command.equalsIgnoreCase("status")) {
						// User wishes to verify his subscription status 
						Subscription sub = slackDAO.getSubscriptionStatus(q);
						
						String active = "";
						if(sub.getActive()) {
							active = "active";
						} else {
							active = "inactive";
						}
						
						ans.setText("Your subscription is currently " + active + ". Type: " + sub.getSubscription_type());
					} else {
						throw new Exception();
					}
				} else {
					throw new Exception();
				}
				
			} else if(question.indexOf("!analysis") != -1) {
				
				// Command !analysis
				String[] splitQ = question.split(" ");
				
				if(splitQ.length > 1) {
					
					String command = splitQ[1];
					
					if(command.equalsIgnoreCase("now")) {
						// Make analysis for current time
						AnalysisService analysisService = new AnalysisService();
						Analysis analysis = analysisService.makeAnalysis();
						
						ans = analysisService.buildAnswersWithAnalysis(analysis, q.getToken(), q.getEvent().getChannel());
					} else if(command.equalsIgnoreCase("date")){
						// Make analysis for specified date
						
						if(splitQ.length > 3) {
							String dateStr = splitQ[2];
							String timeStr = splitQ[3];

							SimpleDateFormat formatInput = new SimpleDateFormat("dd/MM/yyyy HH:mm");
							String dateToParse = dateStr + " " + timeStr;
							Date d = formatInput.parse(dateToParse);
							SimpleDateFormat formatOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String formattedDate = formatOutput.format(d);
							
							AnalysisService analysisService = new AnalysisService();
							Analysis analysis = analysisService.makeAnalysis(formattedDate, 94);

							ans = analysisService.buildAnswersWithAnalysis(analysis, q.getToken(), q.getEvent().getChannel());
						} else {
							throw new Exception();
						}							
					} else {
						throw new Exception();
					}
				} else {
					throw new Exception();
				}
			} 
		} catch (Exception e) {
			ans.setText("Sorry, that doesn't seem like a valid command :(");
		}
		
		return ans;
	}
}
