package com.cryptoassistant.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cryptoassistant.models.slack.Question;
import com.cryptoassistant.services.ResponsesService;
import com.google.gson.Gson;

@Path("api/v1/responses")
public class ResponsesResource {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String getResponse(String body) {
		
		ResponsesService rspService = new ResponsesService();
		Gson gson = new Gson();
		Question question = gson.fromJson(body, Question.class);
		
		rspService.proccessRequest(question);
		
		return gson.toJson(question);
	}
}
