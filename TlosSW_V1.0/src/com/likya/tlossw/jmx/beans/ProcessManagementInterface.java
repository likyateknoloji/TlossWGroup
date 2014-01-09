/*
 * TlosFaz_V2.0
 * com.likya.tlos.jmx.mp.helper : ProcessInfoProvider.java
 * @author Serkan Taş
 * Tarih : Apr 6, 2009 2:19:17 PM
 */

package com.likya.tlossw.jmx.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.mortbay.jetty.Server;
import org.xmldb.api.base.Collection;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.agent.UserStopRequestDocument.UserStopRequest;
import com.likya.tlos.model.xmlbeans.common.DatetimeType;
import com.likya.tlos.model.xmlbeans.config.TlosConfigInfoDocument.TlosConfigInfo;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsPlannedTimeDocument.JsPlannedTime;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceDocument.Resource;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.core.agents.AgentOperations;
import com.likya.tlossw.core.cpc.model.AppState;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.core.spc.helpers.JobQueueOperations;
import com.likya.tlossw.core.spc.helpers.RunMapHelper;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.db.utils.AgentDbUtils;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.jmx.JMXTLSServer;
import com.likya.tlossw.model.MessagesCodeMapping;
import com.likya.tlossw.model.TlosJmxReturnValue;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.utils.CpcUtils;
import com.likya.tlossw.utils.PersistenceUtils;
import com.likya.tlossw.utils.RunUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;

public class ProcessManagementInterface implements ProcessManagementInterfaceMBean {

	private static Logger logger = SpaceWideRegistry.getGlobalLogger();

	private boolean checkScenarioForAcceptingCommands(String scenarioId) {
		SpcInfoType spcInfoType = RunMapHelper.findSpc(scenarioId, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());
		return (spcInfoType.getSpcReferance().isManagable());
	}

	/**
	 * TODO runId'yi db'den almak daha mantikli olabilir, job'i engine ve
	 * db'ye arka arkaya ekliyoruz onada bakiver
	 */

	public TlosJmxReturnValue addJob(JmxUser jmxUser, String jobPropertiesXML) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return new TlosJmxReturnValue(MessagesCodeMapping.fetchTlosGuiMessage(MessagesCodeMapping.JMX_USER_AUTHORIZATION_ERROR), null);
		}

		JobProperties jobProperties = null;

		try {
			jobProperties = JobPropertiesDocument.Factory.parse(jobPropertiesXML).getJobProperties();
		} catch (XmlException e) {
			e.printStackTrace();
			return new TlosJmxReturnValue(MessagesCodeMapping.fetchTlosGuiMessage(MessagesCodeMapping.ENGINE_FREEJOB_INSERT_ERROR), null);
		}

		ProcessInfoProvider processInfoProvider = new ProcessInfoProvider();
		String maxInstanceId = processInfoProvider.retrieveMaxRunId(jmxUser);
		if (maxInstanceId == null) {
			return new TlosJmxReturnValue(MessagesCodeMapping.fetchTlosGuiMessage(MessagesCodeMapping.ENGINE_INSTANCE_ABSENT), null);
		}
		
		SpcInfoType spcInfoType = RunMapHelper.findSpc(CpcUtils.getRootScenarioPath(maxInstanceId), TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());
		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().get(jobProperties.getID()) != null) {
			return new TlosJmxReturnValue(MessagesCodeMapping.fetchTlosGuiMessage(MessagesCodeMapping.SCENARIO_DUPLICATEJOBNAME), null);
		}

		if (spcInfoType != null) {
			spcInfoType.getSpcReferance().addJob(jobProperties);
			DBUtils.insertFreeJobToDailyXML(jobPropertiesXML, maxInstanceId);
			return new TlosJmxReturnValue(MessagesCodeMapping.fetchTlosGuiMessage(MessagesCodeMapping.ENGINE_FREEJOB_INSERT_SUCESS), null);
		} else {
			return new TlosJmxReturnValue(MessagesCodeMapping.fetchTlosGuiMessage(MessagesCodeMapping.ENGINE_FREEJOB_INSERT_ERROR), null);
		}
	}

	@Override
	public void stopJob(JmxUser jmxUser, String jobPath) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));
		logger.info("[stopJob] command received for job : " + jobId);

		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().containsKey(jobId)) {

			Job myJob = spcInfoType.getSpcReferance().getJobQueue().get(jobId);
			if (myJob.getJobRuntimeProperties().isStopable()) {
				myJob.stopMyDogBarking();

				myJob.insertNewLiveStateInfo(StateName.FINISHED.intValue(), SubstateName.STOPPED.intValue(), StatusName.BYUSER.intValue());
				
				Thread executerThread = myJob.getMyExecuter();
				if (executerThread != null) {
					myJob.getMyExecuter().interrupt();
				}
				logger.info("[stopJob] command exucuted ! New Status of " + jobId + " is " + myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName());
			}
		}
		return;
	}

	@Override
	public void retryJob(JmxUser jmxUser, String jobPath) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));

		if (!checkScenarioForAcceptingCommands(jobAbsolutePath)) {
			logger.info("Scenario is not available for  accepting commands !");
			return;
		}

		logger.info("[retryExecution] command received for job : " + jobId);

		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().containsKey(jobId)) {

			Job myJob = spcInfoType.getSpcReferance().getJobQueue().get(jobId);
			if (myJob.getJobRuntimeProperties().isRetriable()) {

				// merve: retry edilen jobin statusunu gecici olarak
				// PENDING/READY/WAITING yaptim, ileride duzenleme yapilacak.
				// WAITING i yapmamin sebebi onu set etmedigimde spc de joblar
				// taranirken o joba geldiginde status bilgisi atanmadigi icin
				// hata vermesi ---> artik LOOKFOR_RESOURCE statusu ile calisiyor 15.10.2012
				/* Hakan : TRANSFERING e cevirdim. */
				//myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, XmlBeansTransformer.generateLiveStateInfo(StateName.PENDING.intValue(), SubstateName.READY.intValue(), StatusName.TRANSFERING.intValue()));

				// Kütüphaneye ekledim
				myJob.insertNewLiveStateInfo(StateName.PENDING.intValue(), SubstateName.IDLED.intValue(), StatusName.BYTIME.intValue());
				
				logger.info("[retryExecution] command exucuted ! New Status of " + jobId + " is " + myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName());
			}
		}
	}

	@Override
	public void doSuccess(JmxUser jmxUser, String jobPath) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));

		if (!checkScenarioForAcceptingCommands(jobAbsolutePath)) {
			logger.info("Scenario is not available for  accepting commands !");
			return;
		}

		logger.info("[setSuccess] command received for job : " + jobId);

		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().containsKey(jobId)) {

			Job myJob = spcInfoType.getSpcReferance().getJobQueue().get(jobId);
			if (myJob.getJobRuntimeProperties().isSuccessable()) {
				// TODO Daha sonra kontrol etmek gerekebilir
				// Kütüphaneye ekledim
				myJob.insertNewLiveStateInfo(StateName.FINISHED.intValue(), SubstateName.COMPLETED.intValue(), StatusName.SUCCESS.intValue());

				logger.info("[setSuccess] command exucuted ! New Status of " + jobId + " is " + myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName());
			}
		}
	}

	@Override
	public void skipJob(JmxUser jmxUser, String jobPath) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));

		if (!checkScenarioForAcceptingCommands(jobAbsolutePath)) {
			logger.info("Scenario is not available for  accepting commands !");
			return;
		}

		logger.info("[skipJob] command received for job : " + jobId);

		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().containsKey(jobId)) {

			Job myJob = spcInfoType.getSpcReferance().getJobQueue().get(jobId);
			if (myJob.getJobRuntimeProperties().isSkippable()) {
				myJob.insertNewLiveStateInfo(StateName.FINISHED.intValue(), SubstateName.SKIPPED.intValue(), StatusName.INT_BYUSER);
				logger.info("[skipJob] command exucuted ! New Status of " + jobId + " is " + myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName());
			}
		}
	}

	@Override
	public void pauseJob(JmxUser jmxUser, String jobPath) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));

		if (!checkScenarioForAcceptingCommands(jobAbsolutePath)) {
			logger.info("Scenario is not available for  accepting commands !");
			return;
		}

		logger.info("[pauseJob] command received for job : " + jobId);

		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().containsKey(jobId)) {

			Job myJob = spcInfoType.getSpcReferance().getJobQueue().get(jobId);
			if (myJob.getJobRuntimeProperties().isPausable()) {
				myJob.insertNewLiveStateInfo(StateName.PENDING.intValue(), SubstateName.PAUSED.intValue(), StatusName.INT_BYUSER);
				logger.info("[pauseJob] command exucuted ! New Status of " + jobId + " is " + myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName());
			}
		}
	}

	@Override
	public void resumeJob(JmxUser jmxUser, String jobPath) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));

		if (!checkScenarioForAcceptingCommands(jobAbsolutePath)) {
			logger.info("Scenario is not available for  accepting commands !");
			return;
		}

		logger.info("[resumeJob] command received for job : " + jobId);

		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().containsKey(jobId)) {

			Job myJob = spcInfoType.getSpcReferance().getJobQueue().get(jobId);
			if (myJob.getJobRuntimeProperties().isResumable()) {
				// LiveStateInfo previousLiveStateInfo =
				// XmlBeansTransformer.cloneLiveStateInfo(myJob.getJobRuntimeProperties().getPreviousLiveStateInfo());

				// TODO merve: resume edilen jobin statusunu
				// PENDING/READY/WAITING yaptim. WAITING i yapmamin sebebi onu
				// set etmedigimde spc de joblar taranirken o joba geldiginde
				// status bilgisi atanmadigi icin hata vermesi
				myJob.insertNewLiveStateInfo(StateName.PENDING.intValue(), SubstateName.READY.intValue(), StatusName.WAITING.intValue());
				logger.info("[resumeJob] command exucuted ! New Status of " + jobId + " is " + myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName());
			}
		}
	}

	@Override
	public void startJob(JmxUser jmxUser, String jobPath) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		// path hesaplanirkenki '.' ayracini '|' ile degistirdim, cunku job adinda '.' oldugu zaman sorun cikiyor
		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));

		if (!checkScenarioForAcceptingCommands(jobAbsolutePath)) {
			logger.info("Scenario is not available for  accepting commands !");
			return;
		}

		logger.info("[startJob] command received for job : " + jobId);

		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		HashMap<String, Job> jobQueue = spcInfoType.getSpcReferance().getJobQueue();
		if (jobQueue.containsKey(jobId)) {
			Job myJob = jobQueue.get(jobId);
			if (myJob.getJobRuntimeProperties().isStartable()) {
				Calendar myCalendar = Calendar.getInstance();
				updateStartConditions(jobQueue, jobId, myCalendar);
				// TODO �lk eleman� ald�k ama pek i�ime sinmedi
				JsPlannedTime jobPlannedTime = myJob.getJobRuntimeProperties().getJobProperties().getManagement().getTimeManagement().getJsPlannedTime();

				// TODO Art�k date yok, g�n var.
				// jobPlanTime.setDate(new
				// org.exolab.castor.types.Date(myCalendar.getTime()));

				// TODO Tekrar de�i�ti ama �al���r m� bilmiyorum :(
				// short[] timeArray = {(short)
				// myCalendar.get(Calendar.HOUR_OF_DAY), (short)
				// myCalendar.get(Calendar.MINUTE), (short)
				// myCalendar.get(Calendar.SECOND), (short)
				// myCalendar.get(Calendar.MILLISECOND)};

				DatetimeType startTime = DatetimeType.Factory.newInstance();
				// startTime.setTime(DateUtils.nativeToXMLBeansTime(myCalendar.getTime()));
				startTime.setTime(myCalendar);
				jobPlannedTime.setStartTime(startTime);

//				if (myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().equals(SubstateName.IDLED)) {
//					myJob.insertNewLiveStateInfo(StateName.PENDING.intValue(), SubstateName.READY.intValue(), StatusName.WAITING.intValue());
//					logger.info("[startUserBasedJob] command exucuted ! New Status of " + jobId + " is " + myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName());
//				}
				
				logger.info("[startJob] command exucuted ! New Status of " + jobId + " is " + myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName());
			}
		}
	}

	@Override
	public void startUserBasedJob(JmxUser jmxUser, String jobPath) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));

		if (!checkScenarioForAcceptingCommands(jobAbsolutePath)) {
			logger.info("Scenario is not available for  accepting commands !");
			return;
		}

		logger.info("[startUserBasedJob] command received for job : " + jobId);

		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().containsKey(jobId)) {

			Job myJob = spcInfoType.getSpcReferance().getJobQueue().get(jobId);
			
			if (myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName().equals(StatusName.USER_WAITING)) {
				myJob.insertNewLiveStateInfo(StateName.PENDING.intValue(), SubstateName.READY.intValue(), StatusName.LOOKFOR_RESOURCE.intValue());
				logger.info("[startUserBasedJob] command exucuted ! New Status of " + jobId + " is " + myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName());
			}
		}
	}

	@Override
	public ArrayList<Resource> getAvailableResourcesForJob(JmxUser jmxUser, String jobPath) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return null;
		}

		ArrayList<Resource> resourceList = new ArrayList<Resource>();

		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));

		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().containsKey(jobId)) {

			Job myJob = spcInfoType.getSpcReferance().getJobQueue().get(jobId);

			if (myJob.getResourceAgentListTrue() != null && myJob.getResourceAgentListTrue().getResourceArray() != null && myJob.getResourceAgentList().sizeOfResourceArray() > 0) {
				for (Resource resource : myJob.getResourceAgentListTrue().getResourceArray()) {
					resourceList.add(resource);
				}
			}
		}

		return resourceList;
	}

	@Override
	public boolean assignAgentForJob(JmxUser jmxUser, String jobPath, String agentId) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return false;
		}

		String jobId = jobPath.substring(jobPath.lastIndexOf(".") + 1);
		String jobAbsolutePath = jobPath.substring(0, jobPath.lastIndexOf("."));

		if (!checkScenarioForAcceptingCommands(jobAbsolutePath)) {
			logger.info("Scenario is not available for  accepting commands !");
			return false;
		}

		HashMap<String, SWAgent> agentLookUpTable = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache();
		Iterator<String> keyIterator = agentLookUpTable.keySet().iterator();
		
		boolean agentAvailable = false;
		
		//registry'deki tum agentlari tariyor
		while (keyIterator.hasNext()) {

			String swAgentId = keyIterator.next();
			
			if(swAgentId.equals(agentId)) {
				SWAgent agent = agentLookUpTable.get(swAgentId);
				
				//agenta is atanabilecek durumda mi diye kontrol ediliyor
				if(agent.getInJmxAvailable()) {
					agentAvailable = true;
					break;
				}
			}
		}
		
		logger.info("[assignAgentForJob] command received for job : " + jobId + " to agent : " + agentId);
		
		SpcInfoType spcInfoType = RunMapHelper.findSpc(jobAbsolutePath, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null && spcInfoType.getSpcReferance().getJobQueue().containsKey(jobId)) {

			Job myJob = spcInfoType.getSpcReferance().getJobQueue().get(jobId);
			
			if (myJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName().equals(StatusName.USER_CHOOSE_RESOURCE)) {
				
				//kullanilabilirse waitinge cekiliyor isin statusu
				if(agentAvailable) {
					//ise ekrandan secilen agent ataniyor
					myJob.setSelectedAgentId(agentId);
					
					myJob.insertNewLiveStateInfo(StateName.PENDING.intValue(), SubstateName.READY.intValue(), StatusName.WAITING.intValue());
					return true;
				//kullanilabilir degilse o is icin tekrar kaynak aramasi yapilmasi icin lookForResource statusune cekiliyor
				} else {
					myJob.insertNewLiveStateInfo(StateName.PENDING.intValue(), SubstateName.READY.intValue(), StatusName.LOOKFOR_RESOURCE.intValue());
					return false;
				}
			}
		}
		
		return false;
	}
	
	private void updateStartConditions(HashMap<String, Job> jobQueue, String jobId, Calendar myCalendar) {
		ArrayList<Job> dependencyList = JobQueueOperations.getDependencyList(jobQueue, jobId);
		if (dependencyList == null) {
			return;
		}
		Iterator<Job> dependencyListIterator = dependencyList.iterator();
		while (dependencyListIterator.hasNext()) {
			Job scheduledJob = dependencyListIterator.next();
			String tmpJobId = scheduledJob.getJobRuntimeProperties().getJobProperties().getID().toString();
			ArrayList<Job> tempJobList = JobQueueOperations.getDependencyList(jobQueue, tmpJobId);
			if ((tempJobList != null) && (tempJobList.size() > 0)) {
				updateStartConditions(jobQueue, tmpJobId, myCalendar);
			}
			// TODO ilk eleman� al�yoruz ama emin de�ilim
			JsPlannedTime jobPlannedTime = scheduledJob.getJobRuntimeProperties().getJobProperties().getManagement().getTimeManagement().getJsPlannedTime();

			// TODO Art�k date yok, g�n var.
			// jobPlanTime.setDate(new
			// org.exolab.castor.types.Date(myCalendar.getTime()));

			// / TODO Tekrar de�i�ti ama �al���r m� bilmiyorum :(
			// short[] timeArray = {(short)
			// myCalendar.get(Calendar.HOUR_OF_DAY), (short)
			// myCalendar.get(Calendar.MINUTE), (short)
			// myCalendar.get(Calendar.SECOND), (short)
			// myCalendar.get(Calendar.MILLISECOND)};
			// jobPlanTime.setTime(new Time(timeArray));

			DatetimeType startTime = DatetimeType.Factory.newInstance();
			// startTime.setTime(DateUtils.nativeToXMLBeansTime(myCalendar.getTime()));
			startTime.setTime(myCalendar);
			jobPlannedTime.setStartTime(startTime);
		}
	}

	@Override
	public void shutdown(JmxUser jmxUser) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		TlosSpaceWide.stopSpacewide();
	}

	@Override
	public int getNbChanges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setState(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void restartScenario(JmxUser jmxUser, String scenarioId) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		SpcInfoType spcInfoType = RunMapHelper.findSpc(scenarioId, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null) {
			Spc spc = spcInfoType.getSpcReferance();
			if (spc.isStartable()) {
				spc.prepareJobsForManuelScenarioExecution();

				Thread thread = new Thread(spc);
				spc.setExecuterThread(thread);
				spc.setExecutionPermission(true, false);
				// spc.getLiveStateInfo().setStateName(StateName.WORKING);
				spc.getLiveStateInfo().setStateName(StateName.RUNNING);
				spc.getLiveStateInfo().setSubstateName(SubstateName.STAGE_IN);

				// TODO Serkan Tas asagidaki substate tanimli degil
				// spc.getLiveStateInfo().setSubstateName(SubstateNameType.RESTARTED);
				spc.getExecuterThread().start();
			}
		}
	}

	@Override
	public void resumeScenario(JmxUser jmxUser, String scenarioId) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		SpcInfoType spcInfoType = RunMapHelper.findSpc(scenarioId, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null) {
			Spc spc = spcInfoType.getSpcReferance();
			spc.resume();
		}
	}

	@Override
	public void stopScenario(JmxUser jmxUser, String scenarioId, Boolean isForced) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		SpcInfoType spcInfoType = RunMapHelper.findSpc(scenarioId, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null) {
			Spc spc = spcInfoType.getSpcReferance();
			if (spc.isStopable()) {
				spc.setExecutionPermission(false, isForced.booleanValue());
				// while (spc.getExecuterThread().isAlive()) {
				// try {
				// Thread.sleep(100);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// }
				// spc.setExecuterThread(null);
				spc.getLiveStateInfo().setStateName(StateName.FINISHED);
				spc.getLiveStateInfo().setSubstateName(SubstateName.STOPPED);
			}
		}
	}

	@Override
	public void suspendScenario(JmxUser jmxUser, String scenarioId) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		SpcInfoType spcInfoType = RunMapHelper.findSpc(scenarioId, TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable());

		if (spcInfoType != null) {
			Spc spc = spcInfoType.getSpcReferance();
			spc.pause();
		}
	}

	public void recover(JmxUser jmxUser) {

		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		if (TlosSpaceWide.isRecoverable()) {

			TlosConfigInfo tlosConfigInfo = TlosSpaceWide.getSpaceWideRegistry().getTlosSWConfigInfo();
			ResourceBundle resourceBundle = TlosSpaceWide.getSpaceWideRegistry().getApplicationResources();
			TlosSpaceWide spaceWideReference = TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference();
			Collection collection = TlosSpaceWide.getSpaceWideRegistry().getEXistColllection();
			Server server = TlosSpaceWide.getSpaceWideRegistry().getHttpServer();
			
			boolean isRecovered = PersistenceUtils.recoverSWRegistry();
			
			if (isRecovered) {
				TlosSpaceWide.setSpaceWideRegistry(SpaceWideRegistry.getInstance());
				TlosSpaceWide.getSpaceWideRegistry().setTlosSWConfigInfo(tlosConfigInfo);
				TlosSpaceWide.getSpaceWideRegistry().setApplicationResources(resourceBundle);
				TlosSpaceWide.getSpaceWideRegistry().setSpaceWideReference(spaceWideReference);
				TlosSpaceWide.getSpaceWideRegistry().setEXistColllection(collection);
				TlosSpaceWide.getSpaceWideRegistry().setHttpServer(server);
				TlosSpaceWide.getSpaceWideRegistry().setJmxUser(jmxUser);
				TlosSpaceWide.getSpaceWideRegistry().setRecovered(true);
			} else {
				logger.error(" > isPersist = true olduğu halde recover işlemi başarısız oldu !");
				logger.error(" > devam etmek için tempo dizini temizleyin ya da isPersist = false yapıp uygulamayı tekrar başlatın !");
				System.exit(-1);
			}
			
			TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference().processQueueStarters();

			TlosSpaceWide.getSpaceWideRegistry().setWaitConfirmOfGUI(false);
			TlosSpaceWide.getSpaceWideRegistry().setUserSelectedRecover(true);
			TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference().startDayKeeper();
			TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference().startCpc();

			while (!RunUtils.checkRecovery(TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable())) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			logger.warn("isPersisten = false olduğu halde recover talebi istenemez !");
		}
	}

	public void shiftTransitionTime(JmxUser jmxUser, boolean backupReports) {
		
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}
		
		shiftTransitionTime(backupReports);

		return; 
	}
	
	public void shiftTransitionTime(boolean backupReports) {

		TlosSpaceWide.getSpaceWideRegistry().setWaitConfirmOfGUI(false);

		if (!TlosSpaceWide.getSpaceWideRegistry().isFIRST_TIME()) {
			if (backupReports) {
				DBUtils.backupCurrentStatusOfSpcsAndJobs(TlosSpaceWide.getSpaceWideRegistry());
			}
		}

		TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().clear();

		TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference().initDayKeeper();
		TlosSpaceWide.getSpaceWideRegistry().getDayKeeperReference().shiftTransitionTime();
		TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference().startDayKeeper();
	}

	public void startOver(JmxUser jmxUser, boolean backupReports) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}
		
		startOver(backupReports);

	}
	
	public void simulateGunDonumu() {

		TlosSpaceWide.changeApplicationState(AppState.INT_SUSPENDED);

		TlosSpaceWide.getSpaceWideRegistry().getDayKeeperReference().setForced(true);
		
	}
	
	public void startOver(boolean backupReports) {

		TlosSpaceWide.getSpaceWideRegistry().setWaitConfirmOfGUI(false);

		if (!TlosSpaceWide.getSpaceWideRegistry().isFIRST_TIME()) {
			if (backupReports) {
				DBUtils.backupCurrentStatusOfSpcsAndJobs(TlosSpaceWide.getSpaceWideRegistry());
			}
		}

		TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().clear();

		TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference().startDayKeeper();
	}

	public void forceCpcStart(JmxUser jmxUser) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		if (TlosSpaceWide.getSpaceWideRegistry().isFIRST_TIME() && TlosSpaceWide.getSpaceWideRegistry().getRunLookupTable().size() == 0) {
			TlosSpaceWide.getSpaceWideRegistry().getDayKeeperReference().setForced(true);
		} else {
			TlosSpaceWide.println("Command not excepted!");
		}
	}

	// web ekranindan tlos agentlarda devre disina alma butonlari secildiginde
	// buraya geliyor, forced olup olmama durumuna gore aksiyon aliniyor
	public void deactivateTlosAgent(JmxUser jmxUser, int agentId, Boolean isForced) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		SWAgent agent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache().get(agentId + "");

		// agenta yeni job atanmamasi icin injmxavailable false yapiliyor
		agent.setInJmxAvailable(false);
		AgentDbUtils.updateAgentJmxValue(agent.getId(), false, "inJMX");

		// forced degilse o agentta calismakta olan job bilgilerinin sunucuya
		// ulasmasi icin outjmx ve jmx hemen kapatilmiyor
		if (isForced) {
			agent.setOutJmxAvailable(false);
			AgentDbUtils.updateAgentJmxValue(agent.getId(), false, "outJMX");

			agent.setJmxAvailable(false);
			AgentDbUtils.updateAgentJmxValue(agent.getId(), false, "JMX");

			// kullanicidan devre disi birakma talebi geldigi icin ilgili
			// parametre set ediliyor
			agent.setUserStopRequest(UserStopRequest.FORCED);
			AgentDbUtils.updateUserStopRequestValue(agentId, UserStopRequest.FORCED.toString());

			// Agent in uzerindeki tum isleri fail e cekiyor
			AgentOperations.failJobsForAgent(agent.getId());
		} else {
			// kullanicidan devre disi birakma talebi geldigi icin ilgili
			// parametre set ediliyor
			agent.setUserStopRequest(UserStopRequest.NORMAL);
			AgentDbUtils.updateUserStopRequestValue(agentId, UserStopRequest.NORMAL.toString());
		}
	}

	// web ekranindan tlos agentlarda devreye alma butonu secildiginde buraya
	// geliyor
	public void activateTlosAgent(JmxUser jmxUser, int agentId) {
		if (!JMXTLSServer.authorizeWeb(jmxUser)) {
			return;
		}

		SWAgent agent = TlosSpaceWide.getSpaceWideRegistry().getAgentManagerReference().getSwAgentsCache().get(agentId + "");

		agent.setInJmxAvailable(true);
		agent.setOutJmxAvailable(true);
		agent.setJmxAvailable(true);
		AgentDbUtils.updateAgentJmxValue(agent.getId(), true, "inJMX");
		AgentDbUtils.updateAgentJmxValue(agent.getId(), true, "outJMX");
		AgentDbUtils.updateAgentJmxValue(agent.getId(), true, "JMX");

		// kullanicidan devreye alma talebi geldigi icin ilgili parametre set
		// ediliyor
		agent.setUserStopRequest(UserStopRequest.NULL);
		AgentDbUtils.updateUserStopRequestValue(agentId, UserStopRequest.NULL.toString());
	}

}
