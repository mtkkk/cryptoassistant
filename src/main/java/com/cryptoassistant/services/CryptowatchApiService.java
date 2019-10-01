package com.cryptoassistant.services;

import com.cryptoassistant.models.cryptowatch.OHLCResponse;
import com.cryptoassistant.models.slack.PostMessageResponse;
import com.google.gson.Gson;

import kong.unirest.GsonObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class CryptowatchApiService {

	public OHLCResponse getHistoryData() {
		
		Unirest.config().setObjectMapper(new GsonObjectMapper());
		HttpResponse<JsonNode> rsp = Unirest.get("https://api.cryptowat.ch/markets/bitfinex/btcusd/ohlc?periods=3600,7200,14400,86400&after=1556755200").asJson();
		
		Gson gson = new Gson();
		OHLCResponse historyData = gson.fromJson(rsp.getBody().toString(), OHLCResponse.class);
		
		return historyData;
	}
	
	public OHLCResponse getDataFromDateOnwards(long date) {
		
		Unirest.config().setObjectMapper(new GsonObjectMapper());
		HttpResponse<JsonNode> rsp = Unirest.get("https://api.cryptowat.ch/markets/bitfinex/btcusd/ohlc?periods=3600,7200,14400,86400,604800&after=" + date).asJson();
		
		Gson gson = new Gson();
		OHLCResponse historyData = gson.fromJson(rsp.getBody().toString(), OHLCResponse.class);
		
		return historyData;
	}
}
