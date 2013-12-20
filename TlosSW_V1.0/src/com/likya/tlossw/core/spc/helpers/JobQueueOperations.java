package com.likya.tlossw.core.spc.helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfosType;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.SpcBase;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.model.JobQueueResult;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.NullChecker;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class JobQueueOperations {

	/**
	 * İş kuyruğunda bitmeyen bir iş var mı yok mu ona bakıyor. Eğer yok ise,
	 * bütün işler başarı ile bitmiş sayılıyor, ve true değeri dönüyor.
	 * 
	 * @param jobQueue
	 * @return true, false
	 */
	public static JobQueueResult isJobQueueOver(HashMap<String, Job> jobQueue) {

		JobQueueResult jobQueueResult = new JobQueueResult();

		if (jobQueue != null) {

			Iterator<Job> jobsIterator = jobQueue.values().iterator();

			while (jobsIterator.hasNext()) {

				Job scheduledJob = jobsIterator.next();

				JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
				
				boolean isPeriodic = jobProperties.getManagement().getPeriodInfo() != null ? true : false;
				
				if (scheduledJob.isStopRepeatativity() && isPeriodic) {
					jobQueueResult.setJobQueueOver(false);
					return jobQueueResult;
				}
				// SpaceWideRegistry.getSpaceWideLogger().info("   > JobQueue element jobsIterator: " + jobsIterator);
				// SpaceWideRegistry.getSpaceWideLogger().info("   > JobQueue element scheduledJob: " + scheduledJob.getJobRuntimeProperties());

				try {
					
					if (NullChecker.checkNull(jobProperties)) {

						if (!LiveStateInfoUtils.equalStates(jobProperties, StateName.FINISHED)) {
							if (isPeriodic) {
								jobQueueResult.setNumOfNonDailyJobsNotOver(jobQueueResult.getNumOfNonDailyJobsNotOver() + 1);
							} else {
								jobQueueResult.setNumOfDailyJobsNotOver(jobQueueResult.getNumOfDailyJobsNotOver() + 1);
							}
						}

					} else {
						SpaceWideRegistry.getGlobalLogger().error("  > isJobQueueOver fonksiyonunda problem2 : " + scheduledJob.getJobRuntimeProperties().getJobProperties());
					}
				} catch (Exception e) {
					SpaceWideRegistry.getGlobalLogger().error("  > isJobQueueOver fonksiyonunda problem : " + scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos());
					e.printStackTrace();
				}
			}
		}
		
		if(jobQueueResult.getNumOfNonDailyJobsNotOver() != 0 || jobQueueResult.getNumOfDailyJobsNotOver() != 0) {
			jobQueueResult.setJobQueueOver(false);
		}
			

		return jobQueueResult;

	}

	public static void dumpJobQueue(String spcAbsolutePath, HashMap<String, Job> jobQueue) {

		@SuppressWarnings("unused")
		String queueDumpInfo = " JOB QUEUE >> ";
		String queueDumpDebug = "";
		int allJobCounter = 0, finishedJobCounter = 0, runningJobCounter = 0;

		SpaceWideRegistry.getGlobalLogger().info(" JOB QUEUE for scenario : " + spcAbsolutePath + " >> ");

		Iterator<Job> jobsIterator = jobQueue.values().iterator();
		// jobsIterator.next().getJobRuntimeProperties().getJobProperties().getJsName();
		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			// queueDump += "[" + scheduledJob.getCurrentStatus() + " : " +
			// DateUtils.getDate(scheduledJob.getJobProperties().getTime()) + "]
			// ";
			allJobCounter++;
			String currentStatus = scheduledJob.getJobInfo();
			if (scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName() != null) {
				if (scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.RUNNING)) {
					queueDumpInfo += '\n';
					queueDumpDebug += '\n';
					runningJobCounter++;
				}
				if (scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED)) {
					finishedJobCounter++;
				}
			}
			queueDumpInfo += "[" + currentStatus + "]";
			// queueDumpDebug += "[" + currentStatus + ":" +
			// DateUtils.getDate(DateUtils.castorToNative(scheduledJob.getJobRuntimeProperties().getJobProperties().getJobPlannedTime().getStartTime().getTime()))
			// + "]";

			// queueDumpDebug += "[" + currentStatus + ":" +
			// scheduledJob.getJobRuntimeProperties().getJobProperties().getJobPlannedTime().getStartTime().getTime().getTime()
			// + "]";
			queueDumpDebug += "[" + currentStatus + ":" + scheduledJob.getJobRuntimeProperties().getJobProperties().getManagement().getTimeManagement().getJsPlannedTime().getStartTime().getTime() + "]";
			// SpaceWideRegistry.getSpaceWideLogger().debug(queueDumpDebug);
			// SpaceWideRegistry.getSpaceWideLogger().info(queueDumpDebug);

		}
		if (jobsIterator != null) {
			SpaceWideRegistry.getGlobalLogger().debug(queueDumpDebug);
			SpaceWideRegistry.getGlobalLogger().info(queueDumpDebug);
			// Logger.getLogger(SpcBase.class).info("     > "+ this.getJsName()
			// + " senaryosunda guncel is Sayisi : " + getJobQueue().size());
			if (SpaceWideRegistry.isDebug) {
				SpaceWideRegistry.getGlobalLogger().info("     > " + spcAbsolutePath + " icin guncel is Sayisi (Fin : Run : All): (" + finishedJobCounter + " : " + runningJobCounter + " : " + allJobCounter + ")");
			}
		}

		return;
	}

	/**
	 * Spc : Sub Process Controller a ait iş listesinin diske yazılması işlevini görür
	 * 
	 * @param fileName
	 *            : Diskte tutlacak dosya ismi ki bu isim her Spc için farklı olmalı
	 * @param jobQueue
	 *            : Spc'ye ait iş listesi
	 * @return
	 */
	public static boolean persistJobQueue(String spcAbsolutePath, HashMap<String, Job> jobQueue, ArrayList<SortType> jobQueueIndex) {

		String fileName = spcAbsolutePath;

		FileOutputStream fos = null;
		FileOutputStream fosIdx = null;
		ObjectOutputStream out = null;

		if (jobQueue.size() == 0) {
			SpaceWideRegistry.getGlobalLogger().fatal("is kuyrugu bos olmamali !");
			SpaceWideRegistry.getGlobalLogger().fatal("Program sona erdi !");
			return false;
		}
		try {
			fos = new FileOutputStream(System.getProperty("tlos.tmpdir") + "/" + fileName);

			out = new ObjectOutputStream(fos);
			out.writeObject(jobQueue);
			out.close();

			fosIdx = new FileOutputStream(System.getProperty("tlos.tmpdir") + "/" + fileName + ".idx");
			out = new ObjectOutputStream(fosIdx);
			out.writeObject(jobQueueIndex);
			out.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return true;

	}

	@SuppressWarnings("unchecked")
	public static boolean recoverJobQueue(String scenarioPathType, HashMap<String, Job> jobQueue, ArrayList<SortType> jobQueueIndex) {

		String fileName = scenarioPathType;

		FileInputStream fis = null;
		FileInputStream fisIdx = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(System.getProperty("tlos.tmpdir") + "/" + fileName);
			in = new ObjectInputStream(fis);
			Object input = in.readObject();

			jobQueue.putAll((HashMap<String, Job>) input);
			in.close();

			fisIdx = new FileInputStream(System.getProperty("tlos.tmpdir") + "/" + fileName + ".idx");
			in = new ObjectInputStream(fisIdx);
			input = in.readObject();
			jobQueueIndex.addAll((ArrayList<SortType>) input);
			in.close();

			// TODO Serkan : ???????????????????????
			resetJobQueue(jobQueue);

		} catch (FileNotFoundException fnf) {
			return false;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void resetJobQueue(HashMap<String, Job> jobQueue) {
		Iterator<Job> jobsIterator = jobQueue.values().iterator();
		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
			scheduledJob.setGlobalRegistry(SpaceWideRegistry.getInstance());

			if (!LiveStateInfoUtils.equalStates(jobProperties, StateName.FINISHED, SubstateName.COMPLETED, StatusName.FAILED)) {
				scheduledJob.insertNewLiveStateInfo(StateName.FINISHED.intValue(), SubstateName.COMPLETED.intValue(), StatusName.FAILED.intValue());
			}

		}

		return;

	}

	public static ArrayList<Job> getDependencyList(HashMap<String, Job> jobQueue, Object jobKey) {

		ArrayList<Job> jobList = new ArrayList<Job>();

		try {
			Iterator<Job> jobsIterator = jobQueue.values().iterator();
			while (jobsIterator.hasNext()) {
				Job scheduledJob = jobsIterator.next();
				DependencyList dependentJobList = scheduledJob.getJobRuntimeProperties().getJobProperties().getDependencyList();
				if (dependentJobList != null) {
					// ArrayList<Item>itemsDependent = new
					// ArrayList<Item>(Collections.list(dependentJobList.getenumerateItem()));
					List<Item> t = Arrays.asList(dependentJobList.getItemArray());
					ArrayList<Item> itemsDependent = new ArrayList<Item>(t);

					int indexOfJob = itemsDependent.indexOf(jobKey);
					if (indexOfJob > -1) {
						jobList.add(scheduledJob);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobList.size() == 0 ? null : jobList;

	}

	public static int getNumOfJobs(HashMap<String, Job> jobQueue) {
		return jobQueue.size();
	}

	public static int getNumOfJobs(HashMap<String, Job> jobQueue, String stateNameType) {

		int counter = 0;

		Iterator<Job> jobsIterator = jobQueue.values().iterator();

		while (jobsIterator.hasNext()) {

			Job scheduledJob = jobsIterator.next();
			JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();
			StateName.Enum stateName = jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName();

			if (stateName != null && stateName.toString().equals(stateNameType)) {
				counter += 1;
			}

		}

		return counter;
	}

	public static LiveStateInfo getLastStateOfJob(LiveStateInfosType liveStateInfos) {

		LiveStateInfo lastStateInfo = liveStateInfos.getLiveStateInfoArray(0);

		return lastStateInfo;
	}

	public static int getNumOfJobsByAgent(HashMap<String, Job> jobQueue, int agentId) {

		int counter = 0;

		Iterator<Job> jobsIterator = jobQueue.values().iterator();

		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			LiveStateInfo currentStateInfo = null;
			if (scheduledJob.getJobRuntimeProperties().getJobProperties().getAgentId() != 0 && scheduledJob.getJobRuntimeProperties().getJobProperties().getAgentId() == agentId) {
				currentStateInfo = getLastStateOfJob(scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos());
				if (currentStateInfo.getStateName().toString().equalsIgnoreCase(StateName.RUNNING.toString())) {
					// System.out.println("OK !");
					counter += 1;
				}
			}

		}

		return counter;
	}

	/*
	 * state yapisinda time-out statusu running statusunun substate i oldugu icin
	 * hem state i running olanlari hem de substate i timeout olanlari toplarsak
	 * timeout olanlari iki kere saymis olacagiz. bunun icin o kismi kaldirdim
	 */
	public static int getNumOfActiveJobs(HashMap<String, Job> jobQueue) {

		int numOfWorkingJobs = getNumOfJobs(jobQueue, StateName.RUNNING);

		return numOfWorkingJobs;
	}

	public static int getNumOfJobs(HashMap<String, Job> jobQueue, SubstateName.Enum substateNameType) {

		int counter = 0;

		Iterator<Job> jobsIterator = jobQueue.values().iterator();

		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			String tmpSubstateNameType = scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().toString();
			if (tmpSubstateNameType != null && tmpSubstateNameType.equals(substateNameType)) {
				counter += 1;
			}

		}

		return counter;
	}

	public static int getNumOfJobs(HashMap<String, Job> jobQueue, StateName.Enum stateNameType) {

		int counter = 0;

		Iterator<Job> jobsIterator = jobQueue.values().iterator();

		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();

			if (LiveStateInfoUtils.equalStates(scheduledJob.getJobRuntimeProperties().getJobProperties(), stateNameType)) {
				counter += 1;
			}

		}

		return counter;
	}
	
	public static void setAllNonNormalJobsUpdateMySelfAfterMe(SpcBase spc, boolean statu) {
		HashMap<String, Job> jobQueue = spc.getJobQueue();
		ArrayList<SortType> jobQueueIndex = spc.getNonDailyJobQueueIndex();
		for(Object sortType : jobQueueIndex.toArray()) {
			jobQueue.get(((SortType)sortType).getJobId()).setUpdateMySelfAfterMe(statu);
		}
	}
}
