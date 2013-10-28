package com.likya.tlossw.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.core.spc.SpcBase;
import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.jobs.Job;

public class JobIndexUtils {

	public static void add(SpcBase spc, String jobId, JobProperties jobProperties) {

		spc.getJobQueueIndex().add(new SortType(jobId, jobProperties.getBaseJobInfos().getJobPriority().intValue()));

		if (JobBaseType.PERIODIC.equals(jobProperties.getBaseJobInfos().getJobInfos().getJobBaseType())) {
			spc.getNonDailyJobQueueIndex().add(new SortType(jobId, jobProperties.getBaseJobInfos().getJobPriority().intValue()));
		} else {
			spc.getDailyJobQueueIndex().add(new SortType(jobId, jobProperties.getBaseJobInfos().getJobPriority().intValue()));
		}
	}

	public static void sort(SpcBase spc) {
		
		Collections.sort(spc.getJobQueueIndex());
		Collections.sort(spc.getNonDailyJobQueueIndex());
		Collections.sort(spc.getDailyJobQueueIndex());
		
	}
	
	public static void reIndexJobQueue(SpcBase spc) {

		HashMap<String, Job> jobQueue = spc.getJobQueue();
		ArrayList<SortType> jobQueueIndex = spc.getJobQueueIndex();
		
		synchronized (jobQueue) {

			synchronized (jobQueueIndex) {

				jobQueueIndex = new ArrayList<SortType>();

				Iterator<Job> jobsIterator = jobQueue.values().iterator();
				while (jobsIterator.hasNext()) {
					Job scheduledJob = jobsIterator.next();
					JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();

					jobQueueIndex.add(new SortType(jobProperties.getID(), jobProperties.getBaseJobInfos().getJobPriority().intValue()));
				}

				Collections.sort(jobQueueIndex);

			}
		}
	}
}
