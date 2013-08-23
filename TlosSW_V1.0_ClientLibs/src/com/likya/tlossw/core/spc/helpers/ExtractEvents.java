package com.likya.tlossw.core.spc.helpers;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.CodeType;
import com.likya.tlos.model.xmlbeans.data.EventDocument.Event;
import com.likya.tlossw.core.events.types.EmailSenderEvent;
import com.likya.tlossw.core.events.types.JobStartEvent;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;

public class ExtractEvents {

	private static Logger myLogger = Logger.getLogger(ExtractEvents.class);

	// public static Job evaluate(GlobalRegistry globalRegistry, DbProperties dbProperties, JobRuntimeProperties jobRuntimeProperties, Job myJob, Logger gobalLogger) {
	public static void evaluate(Job job) {

		try {
			
			
			GlobalRegistry globalRegistry = job.getGlobalRegistry();
			
			JobRuntimeProperties jobRuntimeProperties = job.getJobRuntimeProperties();
			
			Event logEvent = jobRuntimeProperties.getJobProperties().getLogAnalysis().getAction().getThen().getEvent();
			
			int eventCode = logEvent.getCode().intValue();

			String [] distList = {};
			
			if(logEvent.getEmailList() != null && logEvent.getEmailList().getEmailArray() != null) {
				distList = logEvent.getEmailList().getEmailArray();
			}

			switch (eventCode) {
			case CodeType.INT_EMAIL:
				job.addObserver(new EmailSenderEvent(globalRegistry, distList));
				break;
			case CodeType.INT_WAIT_ME:
				job.addObserver(new JobStartEvent(globalRegistry));
				break;

			default:
				break;
			}

		} catch (NullPointerException ne) {
			System.err.println("One of the parameters of JobId=" + job.getJobRuntimeProperties().getJobProperties().getID() + " jobRuntimeProperties.getJobProperties().getLogAnalysis().getAction().getThen().getEvent() is NULL !");
			myLogger.error("One of the parameters of JobId=" + job.getJobRuntimeProperties().getJobProperties().getID() + " jobRuntimeProperties.getJobProperties().getLogAnalysis().getAction().getThen().getEvent() is NULL !");
		}
		
	}

}
