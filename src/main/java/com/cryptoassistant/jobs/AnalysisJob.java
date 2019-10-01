package com.cryptoassistant.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cryptoassistant.dao.SlackDAO;
import com.cryptoassistant.models.analysis.Analysis;
import com.cryptoassistant.models.slack.Answer;
import com.cryptoassistant.models.slack.Subscription;
import com.cryptoassistant.services.AnalysisService;
import com.cryptoassistant.services.SlackMessageService;

public class AnalysisJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			
			SlackDAO slackDAO = new SlackDAO();
			List<Subscription> subbedUsers = slackDAO.getActiveSubscriptions();
			
			AnalysisService analysisService = new AnalysisService();
			Analysis analysis = analysisService.makeAnalysis();
			
			// Send the analysis to the user
			for(Subscription sub : subbedUsers) {
				
				// Only seds to active subscribers
				if(sub.getActive()) {
					Answer answer = analysisService.buildAnswersWithAnalysis(analysis, sub.getToken(), sub.getChannel());

					// Sends analysis if the user is type 1 and the analysis is worth (high confidence)
					// or
					// Sends analysis if user is type 2 (receive all)
					if((sub.getSubscription_type().equals("1") && analysis.isWorthy()) || sub.getSubscription_type().equals("2")) {
						SlackMessageService messageService = new SlackMessageService(slackDAO);
						messageService.sendMessage(answer);
					}
				}
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
