package com.likya.tlossw.core.spc.helpers;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.utils.LiveStateInfoUtils;

public class WatchDogTimer extends Thread {

	private Thread ownerOfTimer;
	private long timeout;
	//	private int counter = 0;

	private Job job;

	private Logger myLogger;

	public WatchDogTimer(Job job, String name, Thread ownerOfTimer, long timeout, Logger logger) {
		super(name);
		this.ownerOfTimer = ownerOfTimer;
		this.timeout = timeout;
		this.job = job;
		myLogger = logger;
	}

	public void run() {
		//		while (true) {
		try {
			if (timeout < 0) {
				System.out.println("WARNING : TIME-OUT for " + job.getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName() + "[XXXX " + timeout + "XXXX");
				timeout = 36000;
				System.out.println("WARNING : TIME-OUT is negative setting it to deafault " + timeout);
			}
			Thread.sleep(timeout);
			if ( !job.getJobRuntimeProperties().getJobProperties().getManagement().getCascadingConditions().getJobAutoRetry().getBooleanValue() ) {
				//					if (counter < 1) {
				LiveStateInfoUtils.insertNewLiveStateInfo(job.getJobRuntimeProperties().getJobProperties(), StateName.RUNNING, SubstateName.ON_RESOURCE, StatusName.TIME_OUT);
				//						++counter;
				myLogger.info("WatchDogTimer : Auto restart is false, waiting forever !");
				job.sendStatusChangeInfo(ownerOfTimer.getName());
				//						continue;
				//					}
			} else {

				myLogger.info("WatchDogTimer : Interrupting job executer !" + job.getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());
				ownerOfTimer.interrupt();
				// job.terminate();
			}
		} catch (InterruptedException e) {
			// e.printStackTrace();
		}
		this.job = null;
		this.ownerOfTimer = null;
		//			break;
	}
	//	}
}
