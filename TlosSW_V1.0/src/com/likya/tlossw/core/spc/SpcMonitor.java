package com.likya.tlossw.core.spc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument;
import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.date.DateUtils;

public class SpcMonitor implements Runnable {
	
	private HashMap<String, Job> jobQueue;
	private ArrayList<SortType> jobQueueIndex;
	
	private Thread myExecuter;
	
	private Logger myLogger = Logger.getLogger(getClass());

	public SpcMonitor(HashMap<String, Job> jobQueue, ArrayList<SortType> jobQueueIndex) {
		this.jobQueue = jobQueue;
		this.jobQueueIndex = jobQueueIndex;
	}
	
	@Override
	public void run() {
		
		while(true) {
		
			Iterator<SortType> jobQueueIndexIterator = jobQueueIndex.iterator();
			
			while (jobQueueIndexIterator.hasNext()) {
				
				SortType sortType = jobQueueIndexIterator.next();
				
				Job scheduledJob = jobQueue.get(sortType.getJobKey());
				
				JobRuntimeProperties jobRuntimeProperties = scheduledJob.getJobRuntimeProperties();
				
				JobProperties jobProperties = jobRuntimeProperties.getJobProperties();	
				
				StateNameDocument.StateName.Enum stateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName();
				SubstateNameDocument.SubstateName.Enum  substateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName();
				StatusNameDocument.StatusName.Enum  statusName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName();
			
				if(SpaceWideRegistry.isDebug) {
					String logString = " [Spc Name : " + jobRuntimeProperties.getTreePath() + "]";
					logString += " [Job Name : " + jobProperties.getID() + "]";
					logString += " [Tarih : " + DateUtils.getW3CDateTime() + "]";
					logString += " [State Name : " + (stateName == null ? "" : stateName) + "]";
					logString += " [Substattate Name : " + (substateName == null ? "" : substateName) + "]";
					logString += " [Status Name : " + (statusName == null ? "" : statusName) + "]";
					myLogger.info(logString);
				}
			}
			

			
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				//	e.printStackTrace();
				myLogger.info("SpcMonitor harici olarak akamete uğratıldı !");
				break;
			}
		}

	}

	public Thread getMyExecuter() {
		return myExecuter;
	}

	public void setMyExecuter(Thread myExecuter) {
		this.myExecuter = myExecuter;
	}

}
