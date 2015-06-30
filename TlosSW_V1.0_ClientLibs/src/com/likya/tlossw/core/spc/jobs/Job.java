package com.likya.tlossw.core.spc.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Observable;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.DatetimeType;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsRealTimeDocument.JsRealTime;
import com.likya.tlos.model.xmlbeans.data.LogAnalysisDocument.LogAnalysis;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
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
import com.likya.tlossw.core.spc.helpers.PeriodCalculations;
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

	public final static String LOG_RESULT = "LogOutput";
	public final static String ERR_RESULT = "ErrorOutput";
	
	private JobRuntimeProperties jobRuntimeProperties;

	protected boolean isExecuterOver = false;
	protected ResourceAgentList resourceAgentListTrue = null;

	private ResourceAgentList resourceAgentList;

	// private Boolean firstLoop = true;

	private String selectedAgentId;

	// Joblarda kullanilmak icin gerekebiliyor. Ornek XSLT kodu, DB den aliniyor, agentlardan DB ye erisim olmadigindan oncesinde alinip buraya konacak. HS
	private String transformXsl;
	// private StreamSource requestedStream;

	private Calendar startTime;
	private JsRealTime jobRealTime;

	private GenericInfoSender genericInfoSender;

	// GÃ¼n dÃ¶nÃ¼mÃ¼ sonrasÄ± takip eden iÅŸlerle ilgili parametreler
	// private Job followerJob;
	// private boolean hasActiveFollower = false;
	// private boolean safeToRemove = false;

	// GÃ¼n dÃ¶nÃ¼mÃ¼ sonrasÄ± takip eden iÅŸlerle ilgili parametreler

	boolean updateMySelfAfterMe = false;
	private boolean stopRepeatativity = false;

	public Job(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		this.globalRegistry = globalRegistry;
		this.jobRuntimeProperties = jobRuntimeProperties;
		this.globalLogger = globalLogger;

		initLogEvents();
	}

	private void initLogEvents() {

		JobProperties jobProperties = jobRuntimeProperties.getJobProperties();
		LogAnalysis logAnalysis = jobProperties.getLogAnalysis();

		if (logAnalysis != null && logAnalysis.getActive()) {
			ExtractEvents.evaluate(this);
		}

	}

	public void setChanged() {
		super.setChanged();
	}

	public final void run() {

		localRun();

		performLogAnalyze();

		periodicRepeatativity();
	}

	private void performLogAnalyze() {
		JobProperties jobProperties = jobRuntimeProperties.getJobProperties();
		LogAnalysis logAnalysis = jobProperties.getLogAnalysis();

		if (logAnalysis != null && logAnalysis.getActive()) {
			new LogAnalyser().evaluate(this);
		}
	}

	private void periodicRepeatativity() {

		if (isUpdateMySelfAfterMe()) {
			forceSpcToHandleGDIssuesForMe();
		}

		JobProperties jobProperties = jobRuntimeProperties.getJobProperties();
		if (!stopRepeatativity && jobProperties.getManagement().getPeriodInfo() != null) {
			Calendar nextPeriodTime = PeriodCalculations.forward(jobProperties);
			// Serkan burada ayni ise state eklendigi icin uzun bir state listesi ortaya cikiyor.
			// JobProperties in coklanmasi gerekiyor, yoksa hepsi tek bir job olarak gorunecek, her bir run i ayri bir job olarak dusunmek mi gerekir acaba?
			if (nextPeriodTime != null) {
				// yeni zamana kuruldu
				insertNewLiveStateInfo(StateName.INT_PENDING, SubstateName.INT_IDLED, StatusName.INT_BYTIME);
			} else {
				// yeni zamana kurulmadÄ±, artÄ±k Ã§alÄ±ÅŸmayacak
				insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED);
			}
		}

	}

	private void forceSpcToHandleGDIssuesForMe() {
		try {
			synchronized (this) {
				this.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected abstract void localRun();

	public Thread getMyExecuter() {
		return myExecuter;
	}

	public void setMyExecuter(Thread myExecuter) {
		this.myExecuter = myExecuter;
	}

	public void insertNewLiveStateInfo(StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum, StatusName.Enum statusNameEnum, int returnCode, String returnDecription) {

		LiveStateInfoUtils.insertNewLiveStateInfo(getJobRuntimeProperties().getJobProperties(), stateNameEnum, substateNameEnum, statusNameEnum, returnCode, returnDecription);
		sendStatusChangeInfo();
		return;
	}

	public LiveStateInfo insertNewLiveStateInfo(int enumStateName, int enumSubstateName, int enumStatusName, String retCodeDesc) {
		LiveStateInfo liveStateInfo = LiveStateInfoUtils.insertNewLiveStateInfo(getJobRuntimeProperties().getJobProperties(), enumStateName, enumSubstateName, enumStatusName);
		liveStateInfo.addNewReturnCode().setDesc(retCodeDesc);
		sendStatusChangeInfo();
		return liveStateInfo;
	}

	public LiveStateInfo insertNewLiveStateInfo(int enumStateName, int enumSubstateName, int enumStatusName) {

		LiveStateInfo liveStateInfo = LiveStateInfoUtils.insertNewLiveStateInfo(getJobRuntimeProperties().getJobProperties(), enumStateName, enumSubstateName, enumStatusName);
		sendStatusChangeInfo();
		return liveStateInfo;
	}

	public LiveStateInfo insertNewLiveStateInfo(int enumStateName, int enumSubstateName) {
		LiveStateInfo liveStateInfo = insertNewLiveStateInfo(enumStateName, enumSubstateName, 0);
		sendStatusChangeInfo();
		return liveStateInfo;
	}

	public synchronized void insertNewLiveStateInfo(LiveStateInfo liveStateInfo) {
		LiveStateInfoUtils.insertNewLiveStateInfo(getJobRuntimeProperties().getJobProperties(), liveStateInfo);
		sendStatusChangeInfo();
		return;
	}

	public String getJobInfo() {
		LiveStateInfo liveStateInfo = jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

		String jobInfoStrt = "Job[id:" + jobRuntimeProperties.getJobProperties().getID();
		jobInfoStrt += ",name:" + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + "]";
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

			String jobPath = ParsingUtils.getJobXFullPath(getJobRuntimeProperties().getNativeFullJobPath().getFullPath(), jobProperties.getID(), jobProperties.getAgentId() + "", jobProperties.getLSIDateTime());
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

	public synchronized void sendEndInfo(String spcNativeFullPath, JobProperties jobProperties) {

		if (genericInfoSender != null) {
			genericInfoSender.sendEndInfo(Thread.currentThread().getName(), getJobRuntimeProperties());
		} else {

			JobAllInfo jobAllInfo = new JobAllInfo();
			jobAllInfo.setJobProperties(jobProperties);
			jobAllInfo.setSpcNativeFullPath(spcNativeFullPath);

			if (getGlobalRegistry().getInfoBus() != null) {
				getGlobalRegistry().getInfoBus().addInfo(jobAllInfo);
			} else {
				// System.out.println("Job.sendEndInfo : getGlobalRegistry().getInfoBusManager() == null !");
				System.out.println("Agent da calisiyorum, ClientLibs de sendEndInfo icindeyim !!");
			}
		}
	}

	public synchronized void sendFirstJobInfo(String spcNativeFullPath, JobProperties jobProperties) {

		JobAllInfo jobAllInfo = new JobAllInfo();
		jobAllInfo.setJobProperties(jobProperties);
		jobAllInfo.setSpcNativeFullPath(spcNativeFullPath);
		jobAllInfo.setFirstJobInfo(true);

		if (getGlobalRegistry().getInfoBus() != null) {
			getGlobalRegistry().getInfoBus().addInfo(jobAllInfo);
		} else {
			// System.out.println("Job.sendEndInfo : getGlobalRegistry().getInfoBusManager() == null !");
			System.out.println("sendFirstJobInfo yapamadÄ±m,  getGlobalRegistry().getInfoBus() == null !!");
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

	private synchronized void sendStatusChangeInfo() {
		sendStatusChangeInfo("");
	}

	private synchronized void sendServerStatusChangeInfo() {

		JobInfo jobInfo = new JobInfo();

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		jobInfo.setTreePath(ParsingUtils.getJobXFullPath(getJobRuntimeProperties().getNativeFullJobPath().getFullPath(), jobProperties.getID(), jobProperties.getAgentId() + "", jobProperties.getLSIDateTime()));
		// jobInfo.setJobKey(getJobRuntimeProperties().getJobProperties().getID());
		jobInfo.setJobName(getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());
		jobInfo.setJobID(getJobRuntimeProperties().getJobProperties().getID());
		jobInfo.setUserID(getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getUserId());
		jobInfo.setAgentID(getJobRuntimeProperties().getJobProperties().getAgentId());
		jobInfo.setLiveLiveStateInfo(getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0));
		jobInfo.getLiveLiveStateInfo().setLSIDateTime(DateUtils.getServerW3CDateTime());

		Date infoTime = Calendar.getInstance().getTime();
		jobInfo.setInfoDate(infoTime);

		if (getGlobalRegistry().getInfoBus() != null) {
			getGlobalRegistry().getInfoBus().addInfo(jobInfo);
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

	public synchronized void sendStatusChangeInfo(LiveStateInfo liveStateInfo) {

		JobInfo jobInfo = new JobInfo();

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		jobInfo.setTreePath(ParsingUtils.getJobXFullPath(getJobRuntimeProperties().getNativeFullJobPath().getFullPath(), jobProperties.getID(), jobProperties.getAgentId() + "", jobProperties.getLSIDateTime()));
		jobInfo.setJobName(jobProperties.getBaseJobInfos().getJsName());
		jobInfo.setLiveLiveStateInfo(liveStateInfo);
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

		jobKey = jobRuntimeProperties.getJobProperties().getID(); //BaseJobInfos().getJsName();

		startTime = Calendar.getInstance();

		jobRealTime = JsRealTime.Factory.newInstance();

		DatetimeType startTimeTemp = DatetimeType.Factory.newInstance();
		startTimeTemp.setTime(startTime);
		startTimeTemp.setDate(startTime);
		jobRealTime.setStartTime(startTimeTemp);

		getJobRuntimeProperties().getJobProperties().getManagement().getTimeManagement().setJsRealTime(jobRealTime);

		// TODO BurayÄ± incelememiz gerekiyor 01.08.2012 Serkan TaÅŸ
		// System.err.println("jobRuntimeProperties : " + jobRuntimeProperties);
		// System.err.println("jobRuntimeProperties.getNativeFullJobPath() : " + jobRuntimeProperties.getNativeFullJobPath());
		// System.err.println("jobRuntimeProperties.getNativeFullJobPath().getFullPath() : " + jobRuntimeProperties.getNativeFullJobPath().getFullPath());

		sendEndInfo(jobRuntimeProperties.getNativeFullJobPath().getFullPath(), jobRuntimeProperties.getJobProperties());

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

		String endLog = jobProperties.getBaseJobInfos().getJsName() + ":Bitis zamani : " + DateUtils.getDate(endTime.getTime());
		String duration = jobProperties.getBaseJobInfos().getJsName() + ": islem suresi : " + DateUtils.getFormattedElapsedTime((int) timeDiff / 1000);
		getJobRuntimeProperties().setCompletionDate(endTime);
		getJobRuntimeProperties().setWorkDuration(DateUtils.getUnFormattedElapsedTime((int) timeDiff / 1000));

		DatetimeType stopTimeTemp = DatetimeType.Factory.newInstance();
		stopTimeTemp.setTime(endTime);
		stopTimeTemp.setDate(endTime);
		jobRealTime.setStopTime(stopTimeTemp);

		jobProperties.getManagement().getTimeManagement().getJsRealTime().setStopTime(stopTimeTemp);
		// getJobRuntimeProperties().getJobProperties().getTimeManagement().setJsRealTime(jobRealTime);

		sendEndInfo(jobRuntimeProperties.getNativeFullJobPath().getFullPath(), jobProperties);

		// GlobalRegistery.getSpaceWideLogger().info(logLabel + endLog);
		// GlobalRegistery.getSpaceWideLogger().info(logLabel + duration);
		myLogger.info(" >>" + logLabel + ">> " + endLog);
		myLogger.info(" >>" + logLabel + ">> " + duration);

		if (watchDogTimer != null) {
			myLogger.debug(" >>" + logLabel + ">> " + "Terminating Watchdog for " + jobProperties.getBaseJobInfos().getJsName());
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

	// public Boolean getFirstLoop() {
	// return firstLoop;
	// }
	//
	// public void setFirstLoop(Boolean firstLoop) {
	// this.firstLoop = firstLoop;
	// }

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

		Long timeOut = jobProperties.getManagement().getTimeControl().getJsTimeOut().getTime().getTimeInMillis();
		PeriodCalculations.getTimeInMilliSecs(jobProperties.getManagement().getTimeControl().getJsTimeOut().getTime());
		// if (jobProperties.getTimeManagement().getJsTimeOut().getUnit() == Unit.HOURS) {
		// timeOut = timeOut * 3600;
		// } else if (jobProperties.getTimeManagement().getJsTimeOut().getUnit() == Unit.MINUTES) {
		// timeOut = timeOut * 60;
		// }

		watchDogTimer = new WatchDogTimer(this, jobKey, Thread.currentThread(), timeOut, globalLogger);
		watchDogTimer.setName(jobKey + ".WatchDogTimer.id." + watchDogTimer.getId());
		watchDogTimer.start();
	}

	public void handleException(Exception err, Logger myLogger) {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		/* FINISHED state i yoksa ekle */
		if (!LiveStateInfoUtils.equalStates(jobProperties, StateName.FINISHED, SubstateName.COMPLETED)) {
			insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED, err.getMessage() != null ? err.getMessage() : " Unkonwn Error, Null pointer exception !");
		}

		globalLogger.error(err.getMessage());

		globalLogger.error(" >>" + logLabel + ">> " + err.getMessage());
		myLogger.error(" >>" + logLabel + ">> " + err.getMessage());
		err.printStackTrace();

	}

	public void handleLogException(Exception err, Logger myLogger) {
		globalLogger.error(err.getMessage());
		err.printStackTrace();
	}
	
	public void processParameters(ArrayList<ParamList> paramList) {
		processParameters(paramList, null, null);
		return;
	}

	public String[] getInputParameters(JobProperties jobProperties) {
		
		LocalParameters localParameters = jobProperties.getLocalParameters();
        String[] inputArray = new String[3];
        
		if (localParameters != null && localParameters.getInParam() != null) {

			Parameter[] inParamList = localParameters.getInParam().getParameterArray();
			ArrayList<Parameter> parameterList = new ArrayList<Parameter>(Arrays.asList(inParamList));

			Iterator<Parameter> parameterIterator = parameterList.iterator();

			while (parameterIterator.hasNext()) {

				Parameter parameter = parameterIterator.next();

				if (parameter.getIoName() == null || !parameter.getIoName().contains("input"))
					continue;
				/*
				 * if (parameter.getName().equals(ProcessNode.XSLT_CODE)) {
				 * 
				 * StringReader xslReader = new StringReader(parameter.getValueString());
				 * 
				 * XSLTCode = new StreamSource(xslReader);
				 * //break; illaki islenecek birseyler bulmasi lazim. hs
				 * 
				 * }
				 */
				// if (parameter.getIoType()) { // OutPut
				// if (parameter.getName().equals(FileProcessExecuter.READ_FILE_RESULT)) {
				// fileContent = parameter.getValueString();
				// // break; sonuclardan birini alacagiz, nasil bir mantik kurgulamali? hs
				//
				// } else if (parameter.getName().equals(WebServiceExecuter.WS_RESULT)) {
				// fileContent = parameter.getValueString();
				// // break;
				// } else if (parameter.getName().equals(JDBCPostgreSQLSentenceExecuter.DB_RESULT)) {
				// fileContent = parameter.getValueString();
				// // break;
				// } else if (parameter.getName().equals(PN_RESULT)) {
				// fileContent = parameter.getValueString();
				// // break;
				// }
				// }
				// input parametresi ilgili degiskene ataniyor.
				if (!parameter.getIoType() && parameter.getIoName().equalsIgnoreCase("input1")) { // OutPut
					inputArray[0] = parameter.getValueString();
				}
				if (!parameter.getIoType() && parameter.getIoName().equalsIgnoreCase("input2")) { // OutPut
					inputArray[1] = parameter.getValueString();
				}
				if (!parameter.getIoType() && parameter.getIoName().equalsIgnoreCase("input3")) { // OutPut
					inputArray[2] = parameter.getValueString();
				}
			}
		}
		return inputArray;
	}
	
	public void processParameters(ArrayList<ParamList> paramList, StringBuffer stringBufferForOUTPUT, StringBuffer stringBufferForERROR) {

		if (stringBufferForOUTPUT != null && stringBufferForOUTPUT.length() > 1) {
			ParamList thisParam = new ParamList(LOG_RESULT, "STRING", "VARIABLE", stringBufferForOUTPUT.toString());
			paramList.add(thisParam);
		}

		if (stringBufferForERROR != null && stringBufferForERROR.length() > 1) {
			ParamList thisParam = new ParamList(ERR_RESULT, "STRING", "VARIABLE", stringBufferForERROR.toString());
			paramList.add(thisParam);
		}
		
		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		// boolean sonuc = false;
		
		// Parametre output atamasi burada yapiliyor !!
		// Case 1 : Var olan bir output parametresine atama
		// Case 2 : Var olMAYan bir output parametresine atama
		if (paramList != null) {
			Iterator<ParamList> itr = paramList.iterator();
			while (itr.hasNext()) {
				ParamList element = itr.next();
				boolean yapildimi = OutputParameterPassing.putOutputParameter(jobProperties, element.getParamRef(), element.getParamName(), element.getType());
				if (yapildimi) {
					// sonuc = true;
					System.out.println("isin sonucu output parametreye yazildi !!");
				} else {
					// sonuc = false;
					System.out.println("isin sonucu output parametre uretmedi !!");
				}
			}
		}
		return; // sonuc;
	}

	public boolean processJobResult(boolean retryFlag, Logger myLogger) {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();

		if (jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.FINISHED)) {

			sendStatusChangeInfo();

			String logStr = "islem bitirildi : " + jobKey + " => ";
			logStr += StateName.FINISHED.toString() + ":" + jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().toString() + ":" + jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName().toString();
			myLogger.info(" >>>>" + logStr + "<<<<");

		} else {

			if (jobProperties.getManagement().getCascadingConditions().getJobAutoRetry().getBooleanValue() && retryFlag) {

				myLogger.info(" >> " + "ExecuteInShell : Job Failed ! Restarting " + getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJsName());

				insertNewLiveStateInfo(StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

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

	// public StreamSource getRequestedStream() {
	// return requestedStream;
	// }
	//
	// public void setRequestedStream(StreamSource requestedStream) {
	// this.requestedStream = requestedStream;
	// }

	// public Job getFollowerJob() {
	// return followerJob;
	// }
	//
	// public void setFollowerJob(Job followerJob) {
	// this.followerJob = followerJob;
	// }
	//
	// public boolean isStopRepeatativity() {
	// return stopRepeatativity;
	// }
	//
	// public void setStopRepeatativity(boolean stopRepeatativity) {
	// this.stopRepeatativity = stopRepeatativity;
	// }
	//
	// public boolean hasActiveFollower() {
	// return hasActiveFollower;
	// }
	//
	// public void setHasActiveFollower(boolean hasActiveFollower) {
	// this.hasActiveFollower = hasActiveFollower;
	// }
	//
	// public boolean isSafeToRemove() {
	// return safeToRemove;
	// }
	//
	// public void setSafeToRemove(boolean safeToRemove) {
	// this.safeToRemove = safeToRemove;
	// }

	public boolean isUpdateMySelfAfterMe() {
		return updateMySelfAfterMe;
	}

	public void setUpdateMySelfAfterMe(boolean updateMySelfAfterMe) {
		this.updateMySelfAfterMe = updateMySelfAfterMe;
	}

	public boolean isStopRepeatativity() {
		return stopRepeatativity;
	}

	public void setStopRepeatativity(boolean stopRepeatativity) {
		this.stopRepeatativity = stopRepeatativity;
	}

	public String getTransformXsl() {
		return transformXsl;
	}

	public void setTransformXsl(String transformXsl) {
		this.transformXsl = transformXsl;
	}

}