package com.cryptoassistant.services;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.cryptoassistant.jobs.AnalysisJob;

public class QuartzSchedulerService {

	public void scheduleAnalysis() throws SchedulerException {
		
		JobDetail analysisJob = JobBuilder.newJob(AnalysisJob.class).build();
		
		//Trigger t1  = TriggerBuilder.newTrigger().withIdentity("RunOnce").startNow().build();
		
		// www.cronmaker.com
		Trigger t1 = TriggerBuilder.newTrigger().withIdentity("1MinuteTrigger").withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *")).build();
		//Trigger t1 = TriggerBuilder.newTrigger().withIdentity("5MinutesTrigger").withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * 1/1 * ? *")).build();
		
		Scheduler sc = StdSchedulerFactory.getDefaultScheduler();
		
		sc.start();
		sc.scheduleJob(analysisJob, t1);
		
	}
}
