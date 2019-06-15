package com.cryptoassistant.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cryptoassistant.models.ResponsesRequestModel;
import com.google.gson.Gson;

@Path("api/v1/responses")
public class ResponsesResource {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String getResponse(String body) {
		
		Gson gson = new Gson();
		ResponsesRequestModel input = gson.fromJson(body, ResponsesRequestModel.class);
		
		return "Hello, Diogo!";
	}
}
