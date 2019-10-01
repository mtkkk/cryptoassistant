package com.cryptoassistant.services;

import com.cryptoassistant.models.slack.Answer;
import com.cryptoassistant.dao.SlackDAO;
import com.cryptoassistant.models.slack.PostMessageResponse;
import com.cryptoassistant.models.slack.Question;
import com.google.gson.Gson;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.GsonObjectMapper;

public class ResponsesService {

	public void proccessRequest(Question question) {
		
		// Direct Message Event
		if(question.getEvent() != null && question.getEvent().getChannel_type().equalsIgnoreCase("im")) {
			
			// The bot response message activates the event, so we check if there is a user asking the question
			if(question.getEvent().getUser() != null) {
				
				SlackDAO dao = new SlackDAO();
				dao.insertQuestion(question);
				SlackMessageService slackMessage = new SlackMessageService(dao);
				
				Answer answer = slackMessage.detectIntentAndAnswer(question);
				
				slackMessage.sendMessage(answer);
				
			}
		}
		
	}

}
