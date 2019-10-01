package com.cryptoassistant.listeners;

import javax.servlet.ServletContextEvent;  
import javax.servlet.ServletContextListener;

import org.quartz.SchedulerException;

import com.cryptoassistant.services.QuartzSchedulerService;

public class CryptoServletContextListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Servlet Context Initialized");
		
		QuartzSchedulerService scheduler = new QuartzSchedulerService();
		try {
			scheduler.scheduleAnalysis();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("Servlet Context Destroyed");
	}
}
