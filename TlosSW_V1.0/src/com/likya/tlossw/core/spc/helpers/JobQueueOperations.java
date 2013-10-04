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

import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.ItemDocument.Item;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.model.path.ScenarioPathType;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class JobQueueOperations {

	/**
	 * İş kuyruğunda bitmeyen bir iş var mı yok mu ona bakıyor. Eğer yok ise,
	 * bütün işler başarı ile bitmiş sayılıyor, ve true değeri dönüyor.
	 * 
	 * @param jobQueue
	 * @return true, false
	 */
	public static boolean isJobQueueOver(HashMap<String, Job> jobQueue) {

		if (jobQueue != null) {
			Iterator<Job> jobsIterator = jobQueue.values().iterator();
			while (jobsIterator.hasNext()) {
				Job scheduledJob = jobsIterator.next();

				JobProperties jobProperties = scheduledJob.getJobRuntimeProperties().getJobProperties();

				if (JobBaseType.PERIODIC.equals(jobProperties.getBaseJobInfos().getJobInfos().getJobBaseType())) {
					return false;
				}
				// SpaceWideRegistry.getSpaceWideLogger().info("   > JobQueue element jobsIterator: " + jobsIterator);
				// SpaceWideRegistry.getSpaceWideLogger().info("   > JobQueue element scheduledJob: " + scheduledJob.getJobRuntimeProperties());
				try {
					if (jobProperties.getStateInfos() != null) {
						if (!jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED)) {
							return false;
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
		return true;
	}

	public static void dumpJobQueue(ScenarioPathType spcId, HashMap<String, Job> jobQueue) {

		@SuppressWarnings("unused")
		String queueDumpInfo = " JOB QUEUE >> ";
		String queueDumpDebug = "";
		int allJobCounter = 0, finishedJobCounter = 0, runningJobCounter = 0;

		SpaceWideRegistry.getGlobalLogger().info(" JOB QUEUE for scenario : " + spcId.getFullPath() + " >> ");

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
			queueDumpDebug += "[" + currentStatus + ":" + scheduledJob.getJobRuntimeProperties().getJobProperties().getTimeManagement().getJsPlannedTime().getStartTime().getTime() + "]";
			// SpaceWideRegistry.getSpaceWideLogger().debug(queueDumpDebug);
			// SpaceWideRegistry.getSpaceWideLogger().info(queueDumpDebug);

		}
		if (jobsIterator != null) {
			SpaceWideRegistry.getGlobalLogger().debug(queueDumpDebug);
			SpaceWideRegistry.getGlobalLogger().info(queueDumpDebug);
			// Logger.getLogger(SpcBase.class).info("     > "+ this.getJsName()
			// + " senaryosunda guncel is Sayisi : " + getJobQueue().size());
			if (SpaceWideRegistry.isDebug) {
				SpaceWideRegistry.getGlobalLogger().info("     > " + spcId.getFullPath() + " icin guncel is Sayisi (Fin : Run : All): (" + finishedJobCounter + " : " + runningJobCounter + " : " + allJobCounter + ")");
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
	public static boolean persistJobQueue(ScenarioPathType scenarioPathType, HashMap<String, Job> jobQueue, ArrayList<SortType> jobQueueIndex) {

		String fileName = scenarioPathType.getFullPath();

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
	public static boolean recoverJobQueue(ScenarioPathType scenarioPathType, HashMap<String, Job> jobQueue, ArrayList<SortType> jobQueueIndex) {

		String fileName = scenarioPathType.getFullPath();

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
			scheduledJob.setGlobalRegistry(SpaceWideRegistry.getInstance());
			LiveStateInfo myLiveStateInfo = scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

			if (!(myLiveStateInfo.getStateName().equals(StateName.FINISHED) && myLiveStateInfo.getSubstateName().equals(SubstateName.COMPLETED) && !myLiveStateInfo.getStatusName().equals(StatusName.FAILED))) {
				myLiveStateInfo.setStateName(StateName.RUNNING);
				myLiveStateInfo.setSubstateName(SubstateName.ON_RESOURCE);
				myLiveStateInfo.setStatusName(StatusName.TIME_IN);
				scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, myLiveStateInfo);
				scheduledJob.sendStatusChangeInfo();
			}
			// if(scheduledJob.getJobQueue() == null) {
			// /**
			// * jobQueue transient oldu�unudun, serialize etmiyor
			// * Recover ederken, bu alan null geliyor. Bu nedenle null ise
			// yeninde okumak gerekiyor.
			// */
			// scheduledJob.setJobQueue(jobQueue);
			// }
			//
			// jobQueue.get(scheduledJob.getJobProperties().getKey()).getJobProperties().setStatus(JobProperties.READY);
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
}
