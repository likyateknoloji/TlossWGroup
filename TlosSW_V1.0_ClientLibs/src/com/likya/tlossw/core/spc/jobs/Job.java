package com.likya.tlossw.core.spc.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Observable;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.UnitDocument.Unit;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsRealTimeDocument.JsRealTime;
import com.likya.tlos.model.xmlbeans.data.StopTimeDocument.StopTime;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceAgentListDocument.ResourceAgentList;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceDocument.Resource;
import com.likya.tlossw.core.spc.helpers.ExtractEvents;
import com.likya.tlossw.core.spc.helpers.GenericInfoSender;
import com.likya.tlossw.core.spc.helpers.LogAnalyser;
import com.likya.tlossw.core.spc.helpers.ParamList;
import com.likya.tlossw.core.spc.helpers.StreamGrabber;
import com.likya.tlossw.core.spc.helpers.WatchDogTimer;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.model.infobus.JobAllInfo;
import com.likya.tlossw.model.infobus.JobInfo;
import com.likya.tlossw.model.infobus.JobStart;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;
import com.likya.tlossw.utils.date.DateUtils;
import com.likya.tlossw.utils.transform.OutputParameterPassing;

public abstract class Job extends Observable implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;

	private String jobKey;

	protected static String logLabel;

	transient private Thread myExecuter;
	transient protected WatchDogTimer watchDogTimer = null;

	transient private GlobalRegistry globalRegistry;
	transient private Logger globalLogger;

	transient protected StreamGrabber errorGobbler;
	transient protected StreamGrabber outputGobbler;

	private JobRuntimeProperties jobRuntimeProperties;

	protected boolean isExecuterOver = false;
	protected ResourceAgentList resourceAgentListTrue = null;

	private ResourceAgentList resourceAgentList;

	private Boolean firstLoop = true;

	private String selectedAgentId;

	// Joblarda kullanilmak icin gerekebiliyor. Ornek XSLT kodu, DB den aliniyor, agentlardan DB ye erisim olmadigindan oncesinde alinip buraya konacak. HS
	private StreamSource requestedStream;

	private Calendar startTime;
	private JsRealTime jobRealTime;

	private GenericInfoSender genericInfoSender;

	public Job(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		this.globalRegistry = globalRegistry;
		this.jobRuntimeProperties = jobRuntimeProperties;
		this.globalLogger = globalLogger;

		ExtractEvents.evaluate(this);
	}
	
	public void setChanged() {
		super.setChanged();
	}

	public final void run() {

		localRun();

		new LogAnalyser().evaluate(this);

	}

	protected abstract void localRun();

	public Thread getMyExecuter() {
		return myExecuter;
	}

	public void setMyExecuter(Thread myExecuter) {
		this.myExecuter = myExecuter;
	}

	public String getJobInfo() {
		LiveStateInfo liveStateInfo = jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

		String jobInfoStrt = jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName();
		jobInfoStrt += ": [State:" + (liveStateInfo.getStateName() == null ? "NA" : liveStateInfo.getStateName().toString()) + "]";
		jobInfoStrt += "[SubState:" + (liveStateInfo.getSubstateName() == null ? "NA" : liveStateInfo.getSubstateName().toString()) + "]";
		jobInfoStrt += "[Statu:" + (liveStateInfo.getStatusName() == null ? "NA" : liveStateInfo.getStatusName().toString()) + "]";
		jobInfoStrt += "[ReturnCode:" + (liveStateInfo.getReturnCode() == null ? "NA" : liveStateInfo.getReturnCode()) + "]";

		return jobInfoStrt;
	}

	public JobRuntimeProperties getJobRuntimeProperties() {
		return jobRuntimeProperties;
	}

	public void setJobRuntimeProperties(JobRuntimeProperties jobRuntimeProperties) {
		this.jobRuntimeProperties = jobRuntimeProperties;
	}

	public GlobalRegistry getGlobalRegistry() {
		return globalRegistry;
	}

	public ResourceAgentList getResourceAgentList() {
		return resourceAgentList;
	}

	public void setResourceAgentList(ResourceAgentList resourceAgentList) {
		this.resourceAgentList = resourceAgentList;
	}

	protected synchronized void sendStartInfo(Date startTime) {

		if (genericInfoSender != null) {
			// If agent, we should implement the functionality for agent.
		} else {
			JobStart jobStart = new JobStart();

			JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

			String jobPath = ParsingUtils.getJobXFullPath(getJobRuntimeProperties().getTreePath(), jobProperties.getID(), jobProperties.getAgentId() + "", jobProperties.getLSIDateTime());
			jobStart.setTreePath(jobPath);
			// jobStart.setJobKey(getJobRuntimeProperties().getJobProperties().getID());
			jobStart.setJobName(getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());
			jobStart.setJobID(getJobRuntimeProperties().getJobProperties().getID());
			jobStart.setUserID(getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getUserId());

			jobStart.setLiveLiveStateInfo(getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0));
			jobStart.setInfoDate(startTime);
			if (getGlobalRegistry().getInfoBus() != null) {
				getGlobalRegistry().getInfoBus().addInfo(jobStart);
			} else {
				System.out.println("getGlobalRegistry().getInfoBusManager() == null !");
			}
		}
	}

	public synchronized void sendEndInfo(String spcId, JobProperties jobProperties) {

		if (genericInfoSender != null) {
			genericInfoSender.sendEndInfo(Thread.currentThread().getName(), getJobRuntimeProperties());
		} else {

			JobAllInfo jobAllInfo = new JobAllInfo();
			jobAllInfo.setJobProperties(jobProperties);
			jobAllInfo.setSpcId(spcId);

			if (getGlobalRegistry().getInfoBus() != null) {
				getGlobalRegistry().getInfoBus().addInfo(jobAllInfo);
			} else {
				// System.out.println("Job.sendEndInfo : getGlobalRegistry().getInfoBusManager() == null !");
				System.out.println("Agent da calisiyorum, ClientLibs de sendEndInfo icindeyim !!");
			}
		}
	}

	public synchronized void sendStatusChangeInfo(String messageId) {

		if (genericInfoSender != null) {
			if (messageId.equals("")) {
				messageId = Thread.currentThread().getName();
			}
			genericInfoSender.sendStatusChangeInfo(messageId, getJobRuntimeProperties());
		} else {
			sendServerStatusChangeInfo();
		}
	}

	public synchronized void sendStatusChangeInfo() {
		sendStatusChangeInfo("");
	}

	private synchronized void sendServerStatusChangeInfo() {

		JobInfo jobInfo = new JobInfo();

		jobInfo.setTreePath(ParsingUtils.getJobXFullPath(getJobRuntimeProperties().getTreePath(), getJobRuntimeProperties().getJobProperties().getID(), getJobRuntimeProperties().getJobProperties().getAgentId() + "", getJobRuntimeProperties().getJobProperties().getLSIDateTime()));
		//jobInfo.setJobKey(getJobRuntimeProperties().getJobProperties().getID());
		jobInfo.setJobName(getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());
		jobInfo.setJobID(getJobRuntimeProperties().getJobProperties().getID());
		jobInfo.setUserID(getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getUserId());
		jobInfo.setAgentID(getJobRuntimeProperties().getJobProperties().getAgentId());
		jobInfo.setLiveLiveStateInfo(getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0));
		jobInfo.getLiveLiveStateInfo().setLSIDateTime(DateUtils.getW3CDateTime());

		Date infoTime = Calendar.getInstance().getTime();
		jobInfo.setInfoDate(infoTime);

		if (getGlobalRegistry().getInfoBus() != null) {
			getGlobalRegistry().getInfoBus().addInfo(jobInfo);
			// GlobalRegistery.getSpaceWideLogger().info("infoBusManager bilgilendirildi !");
		} else {
			System.out.println("getGlobalRegistry().getInfoBusManager() is null !!!");
		}
	}

	public synchronized void changeStateInfo(LiveStateInfo liveStateInfo) {
		LiveStateInfo jobLiveStateInfo = getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

		if (liveStateInfo.getStateName() != null) {
			jobLiveStateInfo.setStateName(liveStateInfo.getStateName());
		}
		if (liveStateInfo.getSubstateName() != null) {
			jobLiveStateInfo.setSubstateName(liveStateInfo.getSubstateName());
		}
		if (liveStateInfo.getStatusName() != null) {
			jobLiveStateInfo.setStatusName(liveStateInfo.getStatusName());
		}

		sendStatusChangeInfo(liveStateInfo);
	}

	public synchronized void addStateInfo(LiveStateInfo liveStateInfo) {

		/*
		 * jobRuntimeProperties.getJobProperties().getLiveStateInfos(). insertNewLiveStateInfo(0);
		 * jobRuntimeProperties.getJobProperties().getLiveStateInfos ().getLiveStateInfoArray
		 * (0).setLSIDateTime(DateUtils.getW3CDateTime()); jobRuntimeProperties.getJobProperties
		 * ().getLiveStateInfos().getLiveStateInfoArray
		 * (0).setStateName(liveStateInfo.getStateName()); jobRuntimeProperties.getJobProperties
		 * ().getLiveStateInfos().getLiveStateInfoArray
		 * (0).setSubstateName(liveStateInfo.getSubstateName()); jobRuntimeProperties
		 * .getJobProperties().getLiveStateInfos().getLiveStateInfoArray
		 * (0).setStatusName(liveStateInfo.getStatusName());
		 */
		// TODO daha iyi bir yol lazim !!synchronized {
		jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().insertNewLiveStateInfo(0);
		jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, liveStateInfo);
		sendStatusChangeInfo(liveStateInfo);
	}

	public synchronized void sendStatusChangeInfo(LiveStateInfo liveStateInfo) {

		JobInfo jobInfo = new JobInfo();

		ParsingUtils.getJobXFullPath(getJobRuntimeProperties().getTreePath(), getJobRuntimeProperties().getJobProperties().getID(), getJobRuntimeProperties().getJobProperties().getAgentId() + "", getJobRuntimeProperties().getJobProperties().getLSIDateTime());
		jobInfo.setTreePath(ParsingUtils.getJobXFullPath(getJobRuntimeProperties().getTreePath(), getJobRuntimeProperties().getJobProperties().getID(), getJobRuntimeProperties().getJobProperties().getAgentId() + "", getJobRuntimeProperties().getJobProperties().getLSIDateTime()));
		// jobInfo.setJobKey(getJobRuntimeProperties().getJobProperties().getID());
		jobInfo.setJobName(getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());
		jobInfo.setLiveLiveStateInfo(liveStateInfo);
		// jobInfo.getLiveLiveStateInfo().setLSIDateTime(DateUtils.getW3CDateTime());
		Date infoTime = Calendar.getInstance().getTime();
		jobInfo.setInfoDate(infoTime);
		// TODO addInfo da bir problem olabilir. arastir? hakan
		if (getGlobalRegistry().getInfoBus() != null) {
			getGlobalRegistry().getInfoBus().addInfo(jobInfo);
		} else {
			System.out.println("getGlobalRegistry().getInfoBusManager() is null !!!");
		}
	}

	public void setResourceToFalse() {
		for (Resource resource : getResourceAgentList().getResourceArray()) {
			if ((Boolean.parseBoolean(resource.getStringValue()))) {
				resource.setStringValue("FALSE");
				break;
			}
		}
	}

	protected void initStartUp(Logger logger) {

		logLabel = Thread.currentThread().getName() + ":";

		jobKey = jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName();

		startTime = Calendar.getInstance();

		jobRealTime = JsRealTime.Factory.newInstance();

		com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime startTimeTemp = com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime.Factory.newInstance();
		startTimeTemp.setTime(startTime);
		startTimeTemp.setDate(startTime);
		jobRealTime.setStartTime(startTimeTemp);

		getJobRuntimeProperties().getJobProperties().getTimeManagement().setJsRealTime(jobRealTime);

		// TODO Buray� incelememiz gerekiyor 01.08.2012 Serkan Ta�
		sendEndInfo(Thread.currentThread().getName(), jobRuntimeProperties.getJobProperties());

		String startLog = jobKey + " Baslatildi. Baslangic zamani : " + DateUtils.getDate(startTime.getTime());
		getJobRuntimeProperties().setPlannedExecutionDate(startTime);
		logger.info(" >>" + logLabel + ">> " + startLog);

		// sendStartInfo(startTime.getTime());

	}

	protected void cleanUp(Process process, Logger myLogger) {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		myLogger.debug(" >>" + logLabel + ">> " + "Terminating Error for " + jobProperties.getBaseJobInfos().getJsName());
		stopErrorGobbler(myLogger);

		myLogger.debug(" >>" + logLabel + ">> " + "Terminating Output for " + jobProperties.getBaseJobInfos().getJsName());
		stopOutputGobbler(myLogger);

		Calendar endTime = Calendar.getInstance();

		long timeDiff = endTime.getTime().getTime() - startTime.getTime().getTime();

		String endLog = getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName() + ":Bitis zamani : " + DateUtils.getDate(endTime.getTime());
		String duration = getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName() + ": islem suresi : " + DateUtils.getFormattedElapsedTime((int) timeDiff / 1000);
		getJobRuntimeProperties().setCompletionDate(endTime);
		getJobRuntimeProperties().setWorkDuration(DateUtils.getUnFormattedElapsedTime((int) timeDiff / 1000));

		StopTime stopTimeTemp = StopTime.Factory.newInstance();
		stopTimeTemp.setTime(endTime);
		stopTimeTemp.setDate(endTime);
		jobRealTime.setStopTime(stopTimeTemp);

		getJobRuntimeProperties().getJobProperties().getTimeManagement().getJsRealTime().setStopTime(stopTimeTemp);
		// getJobRuntimeProperties().getJobProperties().getTimeManagement().setJsRealTime(jobRealTime);

		sendEndInfo(Thread.currentThread().getName(), getJobRuntimeProperties().getJobProperties());

		// GlobalRegistery.getSpaceWideLogger().info(logLabel + endLog);
		// GlobalRegistery.getSpaceWideLogger().info(logLabel + duration);
		myLogger.info(" >>" + logLabel + ">> " + endLog);
		myLogger.info(" >>" + logLabel + ">> " + duration);

		if (watchDogTimer != null) {
			myLogger.debug(" >>" + logLabel + ">> " + "Terminating Watchdog for " + getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());
			stopMyDogBarking();
		}

		process = null;
		isExecuterOver = true;
		myLogger.info(" >>" + logLabel + ">> ExecuterThread:" + Thread.currentThread().getName() + " is over");

	}

	protected void stopErrorGobbler(Logger logger) {
		if (errorGobbler != null && errorGobbler.isAlive()) {
			errorGobbler.stopStreamGobbler();
			errorGobbler.interrupt();
			logger.debug("  > ExternalProgram -> errorGobbler.isAlive ->" + errorGobbler.isAlive());
			errorGobbler = null;
		}
	}

	protected void stopOutputGobbler(Logger logger) {
		if (outputGobbler != null && outputGobbler.isAlive()) {
			outputGobbler.stopStreamGobbler();
			outputGobbler.interrupt();
			logger.debug("  > ExternalProgram -> outputGobbler.isAlive ->" + outputGobbler.isAlive());
			outputGobbler = null;
		}
	}

	public ResourceAgentList getResourceAgentListTrue() {
		return resourceAgentListTrue;
	}

	public void setResourceAgentListTrue(ResourceAgentList resourceAgentListTrue) {
		this.resourceAgentListTrue = resourceAgentListTrue;
	}

	public void setSelectedAgentId(String selectedAgentId) {
		this.selectedAgentId = selectedAgentId;
	}

	public String getSelectedAgentId() {
		return selectedAgentId;
	}

	public Boolean getFirstLoop() {
		return firstLoop;
	}

	public void setFirstLoop(Boolean firstLoop) {
		this.firstLoop = firstLoop;
	}

	public String getJobKey() {
		return jobKey;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	public void stopMyDogBarking() {
		if (watchDogTimer != null) {
			watchDogTimer.interrupt();
			watchDogTimer = null;
		}
	}

	public Logger getGlobalLogger() {
		return globalLogger;
	}

	public void setGlobalRegistry(GlobalRegistry globalRegistry) {
		this.globalRegistry = globalRegistry;
	}

	public void startWathcDogTimer() {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		Long timeOut = jobProperties.getTimeManagement().getJsTimeOut().getValueInteger().longValue();

		if (jobProperties.getTimeManagement().getJsTimeOut().getUnit() == Unit.HOURS) {
			timeOut = timeOut * 3600;
		} else if (jobProperties.getTimeManagement().getJsTimeOut().getUnit() == Unit.MINUTES) {
			timeOut = timeOut * 60;
		}

		watchDogTimer = new WatchDogTimer(this, jobKey, Thread.currentThread(), timeOut * 1000, globalLogger);
		watchDogTimer.setName(jobKey + ".WatchDogTimer.id." + watchDogTimer.getId());
		watchDogTimer.start();
	}

	public void handleException(Exception err, Logger myLogger) {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		/* FINISHED state i yoksa ekle */
		if (!(jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED) && jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().equals(SubstateName.COMPLETED))) {
			jobProperties.getStateInfos().getLiveStateInfos().insertNewLiveStateInfo(0);
		}

		LiveStateInfo liveStateInfo = LiveStateInfoUtils.generateLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);
		jobProperties.getStateInfos().getLiveStateInfos().setLiveStateInfoArray(0, liveStateInfo);

		globalLogger.error(err.getMessage());

		globalLogger.error(" >>" + logLabel + ">> " + err.getMessage());
		myLogger.error(" >>" + logLabel + ">> " + err.getMessage());
		err.printStackTrace();

	}

	public boolean processJobResult(boolean retryFlag, Logger myLogger) {
		return processJobResult(retryFlag, myLogger, null);
	}

	public boolean processJobResult(boolean retryFlag, Logger myLogger, ArrayList<ParamList> paramList) {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		if (jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED)) {

			String logStr = "islem bitirildi : " + jobKey + " => ";
			logStr += StateName.FINISHED.toString() + ":" + jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().toString() + ":" + jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName().toString();
			myLogger.info(" >>>>" + logStr + "<<<<");

			// Parametre output atamasi burada yapiliyor !!
			// Case 1 : Var olan bir output parametresine atama
			// Case 2 : Var olMAYan bir output parametresine atama
			if (paramList != null) {
				Iterator<ParamList> itr = paramList.iterator();
				while (itr.hasNext()) {
					ParamList element = itr.next();
					boolean yapildimi = OutputParameterPassing.putOutputParameter(jobProperties, element.getParamRef(), element.getParamName());
					if (yapildimi) {
						System.out.println("isin sonucu output parametreye yazildi !!");
					} else {
						System.out.println("isin sonucu output parametre uretmedi !!");
					}
				}
			}

		} else {

			// TODO Hoşuma gitmedi ama tip dönüşümü yaptım
			if (Boolean.parseBoolean(jobProperties.getCascadingConditions().getJobAutoRetry().toString()) && retryFlag) {

				myLogger.info(" >> " + "ExecuteInShell : Job Failed ! Restarting " + getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

				sendStatusChangeInfo();

				return true;

			} else {

				myLogger.info(" >>" + logLabel + ">> " + jobKey + ":Job Failed ! ");
				myLogger.debug(" >>" + logLabel + ">> " + jobKey + "ExecuteInShell : Job Failed !");

			}
		}

		return false;
	}

	public boolean isExecuterOver() {
		return isExecuterOver;
	}

	public GenericInfoSender getGenericInfoSender() {
		return genericInfoSender;
	}

	public void setGenericInfoSender(GenericInfoSender genericInfoSender) {
		this.genericInfoSender = genericInfoSender;
	}

	public StreamSource getRequestedStream() {
		return requestedStream;
	}

	public void setRequestedStream(StreamSource requestedStream) {
		this.requestedStream = requestedStream;
	}

}