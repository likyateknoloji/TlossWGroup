package com.likya.tlossw.core.spc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.DatetimeType;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.data.AdvancedScenarioInfosDocument.AdvancedScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.AlarmPreferenceDocument.AlarmPreference;
import com.likya.tlos.model.xmlbeans.data.BaseScenarioInfosDocument.BaseScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsRealTimeDocument.JsRealTime;
import com.likya.tlos.model.xmlbeans.data.ManagementDocument.Management;
import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.ScenarioStatusListDocument.ScenarioStatusList;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.model.RunInfoType;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.jobs.ExecuteInShell;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.model.SpcLookupTable;
import com.likya.tlossw.model.path.BasePathType;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.utils.CpcUtils;
import com.likya.tlossw.utils.ExtractMajorJobTypesOnServer;
import com.likya.tlossw.utils.JobIndexUtils;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.date.DateUtils;

public abstract class SpcBase implements Runnable, Serializable {

	private static final long serialVersionUID = 3839154843189778398L;

	private LiveStateInfo liveStateInfo;

	private String spcAbsolutePath;
	private String jsName;
	private String comment;
	private String currentRunId;
	private String nativeRunId;
	private boolean concurrent;
	private String userId;
    private String LSIDateTime;
    
	private BaseScenarioInfos baseScenarioInfos;
	private DependencyList dependencyList;
	private ScenarioStatusList scenarioStatusList;
	private AlarmPreference alarmPreference;
	private Management management;
	private AdvancedScenarioInfos advancedScenarioInfos;
	private LocalParameters localParameters;
	private Scenario scenario;

	// Senaryolarin baslama ve bitis bilgilerini de raporlama amacli dolduralim.
	// ilk aklima gelen job da nasil yapildigina bakip oradan kopya cekmek oldu.
	// daha iyi bir yontem varsa onunla degistirelim. hs

	protected JsRealTime scenarioRealTime;

	protected Calendar startTime;
	private boolean isPausable;
	private boolean isResumable;
	private boolean isStopable;
	private boolean isStartable;

	private boolean isManagable;

	transient private SpaceWideRegistry spaceWideRegistry;

	private ArrayList<JobRuntimeProperties> taskList;

	private ArrayList<SortType> jobQueueIndex;
	
	private ArrayList<SortType> dailyJobQueueIndex;
	private ArrayList<SortType> nonDailyJobQueueIndex;
	
	private HashMap<String, Job> jobQueue;

	private static String logLabel;

	transient private Logger globalLogger = SpaceWideRegistry.getGlobalLogger();
	transient private Logger myLogger = Logger.getLogger(SpcBase.class);

	transient private Thread executerThread;

	protected boolean executionPermission = true;

	private boolean isSpcSuspended = false;
	
	protected boolean isForced = false;

	protected boolean isTester = false;

	// Gün dönümü sonrası takip eden işlerle ilgili parametreler

	boolean updateMySelfAfterMe = false;
	
	private SpcMonitor spcMonitor;

	public SpcBase(String nativeRunId, String spcAbsolutePath, SpaceWideRegistry spaceWideRegistry, ArrayList<JobRuntimeProperties> taskList, boolean isTester) {

		this.nativeRunId = nativeRunId;
		this.currentRunId = nativeRunId;
		this.isTester = isTester;
		this.spcAbsolutePath = spcAbsolutePath;
		this.taskList = taskList;
		this.spaceWideRegistry = spaceWideRegistry;

		jobQueue = new HashMap<String, Job>();
		jobQueueIndex = new ArrayList<SortType>();
		dailyJobQueueIndex = new ArrayList<SortType>();
		nonDailyJobQueueIndex = new ArrayList<SortType>();

		logLabel = getCommonName();

	}

	public TlosSWPathType getSpcFullPath() {

		TlosSWPathType tlosSWPathType = new TlosSWPathType(BasePathType.getRootPath() + "." + getCurrentRunId() + "." + getSpcAbsolutePath());

		return tlosSWPathType;
	}

	public TlosSWPathType getSpcNativeFullPath() {

		TlosSWPathType tlosSWPathType = new TlosSWPathType(BasePathType.getRootPath() + "." + getNativeRunId() + "." + getSpcAbsolutePath());

		return tlosSWPathType;
	}

	public String getCommonName() {
		return "Spc_" + BasePathType.getRootPath() + "." + getNativeRunId();
	}

	public boolean initScenarioInfo() { // Senaryolarin ilk baslatilmalari icin

		myLogger.info("  >> Senaryo ismi : " + this.getBaseScenarioInfos().getJsName());
		Iterator<JobRuntimeProperties> taskListIterator = taskList.iterator();

		scenario = null;
		
		while (taskListIterator.hasNext()) { // Senaryodaki herbir is icin
			
			JobRuntimeProperties jobRuntimeProperties = taskListIterator.next();
			
			JobProperties jobProperties = jobRuntimeProperties.getJobProperties();
			

			jobRuntimeProperties.setNativeFullJobPath(getSpcNativeFullPath());

			String jobId = jobProperties.getID();

			Job myJob = null;

			myLogger.info("   > Is ismi : " + jobProperties.getBaseJobInfos().getJsName());
			myLogger.info("   > is Tipi : " + jobProperties.getManagement().getPeriodInfo() != null ? "PERIODIC" : "NORMAL");

			myJob = getMyJob(jobRuntimeProperties);

			if (myJob != null && jobId != null) {

				if (jobProperties.getRunId() == null || "".equals(jobProperties.getRunId())) {
					jobProperties.setRunId(nativeRunId);
				}

				JobIndexUtils.add(this, jobId, jobProperties);
				
				getJobQueue().put(jobId, myJob);
			}
		}

		JobIndexUtils.sort(this);
		
		myLogger.info(" >");
		
		return true;
	}

	public void setExecutionPermission(boolean executionPermission, boolean isForced) {
		synchronized (this) {
			this.executionPermission = executionPermission;
			this.isForced = isForced;
		}

	}
	
	public SpcLookupTable getSpcLookupTable() {

		if (isTester) {
			return getSpaceWideRegistry().getCpcTesterReference().getSpcLookupTable(userId);
		}

		RunInfoType runInfoType = getSpaceWideRegistry().getRunLookupTable().get(getCurrentRunId());

		if (runInfoType == null) {
			System.out.println("my pointer : " + this + " run id : " + getCurrentRunId());
		}

		return runInfoType.getSpcLookupTable();

	}

	public boolean addJob(JobProperties jobProperties) {

		// Senaryonun ilk isi icin aksiyon alindi, diger isler icin senkronize
		// method cagriliyor.

		synchronized (this) {
			JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
			jobRuntimeProperties.setJobProperties(jobProperties);

			jobRuntimeProperties.setNativeFullJobPath(getSpcNativeFullPath());

			String jobId = jobRuntimeProperties.getJobProperties().getID();

			jobQueueIndex.add(new SortType(jobId, jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobPriority().intValue()));

			Job myJob = null;

			boolean isPeriodic = jobRuntimeProperties.getJobProperties().getManagement().getPeriodInfo() != null ? true : false;
			
			myLogger.info("   > Is ismi : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
			myLogger.info("   > is Tipi : " + (isPeriodic ? "PERIODIC" : "NORMAL"));
			
			if (isPeriodic) {
				if (!jobRuntimeProperties.getAbsoluteJobPath().equals(CpcUtils.getRootScenarioPath(getManagement().getConcurrencyManagement().getRunningId()))) {
					globalLogger.warn("Periodik job root disinda kullanilamaz ! Base : " + BasePathType.getRootPath());
					globalLogger.warn("JobName : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
					globalLogger.warn("TreePath : " + jobRuntimeProperties.getAbsoluteJobPath());

				} else { // TODO PARAMETRE ekleme
					myLogger.info("     > Periodik is geldi ! period : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobTypeDetails().getSpecialParameters());
					myLogger.info("     > Periyodik is calistirma kismi henuz aktif degil. Burada olacak !!");
					// myJob = new PeriodicExternalProgram(getSwAgentRegistry(),
					// jobRuntimeProperties);
				}
			} else {
				myLogger.info("     > Peryodik olmayan " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " isi calistirilmaya hazir !");
				myJob = new ExecuteInShell(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				// TODO myJob = getMyJob(jobRuntimeProperties); olmasi gerekmez mi ? Hakan
			}

			String jobIdStr = jobRuntimeProperties.getJobProperties().getID();

			if (myJob != null && jobIdStr != null) {
				// isi jobQueue ya id si ile birlikte koyalim.
				getJobQueue().put(jobId, myJob);
			}

			Collections.sort(jobQueueIndex);
			myLogger.info(" >");
			// isi taskList e ekleyelim
			this.taskList.add(jobRuntimeProperties);
		}

		return true;
	}
	
	protected void setJSRealTime() {

		// Senaryolarin baslama ve bitis bilgilerini de raporlama amacli dolduralim.
		// ilk aklima gelen job da nasil yapildigina bakip oradan kopya cekmek oldu.
		// daha iyi bir yontem varsa onunla degistirelim. hs

		startTime = Calendar.getInstance();

		scenarioRealTime = JsRealTime.Factory.newInstance();

		DatetimeType startTimeTemp = DatetimeType.Factory.newInstance();
		startTimeTemp.setTime(startTime);
		startTimeTemp.setDate(startTime);
		scenarioRealTime.setStartTime(startTimeTemp);

		if(getManagement().getTimeManagement() == null) {
			TimeManagement timeManagement = TimeManagement.Factory.newInstance();
			getManagement().setTimeManagement(timeManagement);
		}
		getManagement().getTimeManagement().setJsRealTime(scenarioRealTime);
	}

	protected void setJSRealTimeStopTime() {

		// Senaryolarin baslama ve bitis bilgilerini de raporlama amacli dolduralim.
		// ilk aklima gelen job da nasil yapildigina bakip oradan kopya cekmek oldu.
		// daha iyi bir yontem varsa onunla degistirelim. hs

		Calendar endTime = Calendar.getInstance();

		long timeDiff = endTime.getTime().getTime() - startTime.getTime().getTime();

		String endLog = getJsName() + ":Bitis zamani : " + DateUtils.getDate(endTime.getTime());
		String duration = getJsName() + ": islem suresi : " + DateUtils.getFormattedElapsedTime((int) timeDiff / 1000);
		// getJobRuntimeProperties().setCompletionDate(endTime);
		// getJobRuntimeProperties().setWorkDuration(DateUtils.getUnFormattedElapsedTime((int) timeDiff / 1000));

		DatetimeType stopTimeTemp = DatetimeType.Factory.newInstance();
		stopTimeTemp.setTime(endTime);
		stopTimeTemp.setDate(endTime);
		// scenarioRealTime.setStopTime(stopTimeTemp);

		getManagement().getTimeManagement().getJsRealTime().setStopTime(stopTimeTemp);

		getMyLogger().info(" >>" + "Spc_" + getSpcAbsolutePath() + ">> " + endLog);
		getMyLogger().info(" >>" + "Spc_" + getSpcAbsolutePath() + ">> " + duration);
	}

	// Bir senaryonun icindeki senaryoları veriyor
	public HashMap<String, SpcInfoType> getSetOfScenarios() {
		HashMap<String, SpcInfoType> map = new HashMap<String, SpcInfoType>();
		Set<String> set = this.getSpcLookupTable().getTable().keySet();

		for (String i : set)
			if (i.indexOf(spcAbsolutePath + ".") != -1) {
				map.put(i, this.getSpcLookupTable().getTable().get(i));
			}

		return map;
	}
	
	protected void insertLastStateInfo(Job scheduledJob, StateName.Enum stateNameEnum, SubstateName.Enum substateNameEnum, StatusName.Enum statusNameEnum) {
		JobRuntimeProperties jobRuntimeProperties = scheduledJob.getJobRuntimeProperties();
		LiveStateInfo previousLiveStateInfo = jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0);

		// System.out.println("Son durum : " + jobRuntimeProperties.getJobProperties().getID() + " : " + jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).toString());

		if (previousLiveStateInfo == null || !LiveStateInfoUtils.equalStates(previousLiveStateInfo, stateNameEnum, substateNameEnum, statusNameEnum)) {
			scheduledJob.insertNewLiveStateInfo(stateNameEnum.intValue(), substateNameEnum.intValue(), statusNameEnum.intValue());
			// System.out.println("Değiştikten sonraki son durum : " + jobRuntimeProperties.getJobProperties().getID() + " : " + jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).toString());
		}

		// System.out.println("Önceki durum : " + jobRuntimeProperties.getJobProperties().getID() + " : " + jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(1).toString());
	}
	
	public boolean isPausable() {
		if ((getLiveStateInfo().getStateName().equals(StateName.PENDING) && getLiveStateInfo().getSubstateName().equals(SubstateName.READY)) || getLiveStateInfo().getStateName().equals(StateName.RUNNING)) {
			isPausable = true;
		} else {
			isPausable = false;
		}
		return isPausable;
	}

	public boolean isResumable() {
		if (getLiveStateInfo().getStateName().equals(StateName.PENDING) && getLiveStateInfo().getSubstateName().equals(SubstateName.PAUSED)) {
			isResumable = true;
		} else {
			isResumable = false;
		}
		return isResumable;
	}

	public boolean isStopable() {
		if (getLiveStateInfo().getStateName().equals(StateName.PENDING) || getLiveStateInfo().getStateName().equals(StateName.RUNNING)) {
			isStopable = true;
		} else {
			isStopable = false;
		}
		return isStopable;
	}

	public boolean isStartable() {
		if (getLiveStateInfo().getStateName().equals(StateName.FINISHED)) {
			isStartable = true;
		} else {
			isStartable = false;
		}
		return isStartable;
	}

	public boolean isManagable() {
		if (getLiveStateInfo().getStateName().equals(StateName.PENDING) || getLiveStateInfo().getStateName().equals(StateName.RUNNING) || getLiveStateInfo().getStateName().equals(StateName.FINISHED)) {
			isManagable = true;
		} else {
			isManagable = false;
		}
		return isManagable;
	}

	protected Job getMyJob(JobRuntimeProperties jobRuntimeProperties) {

		Job myJob = extractJobTypes(jobRuntimeProperties);

		return myJob;
	}

	private Job extractJobTypes(JobRuntimeProperties jobRuntimeProperties) {
		return ExtractMajorJobTypesOnServer.evaluate(jobRuntimeProperties, getSpaceWideRegistry(), myLogger);
	}

	public BaseScenarioInfos getBaseScenarioInfos() {
		return baseScenarioInfos;
	}

	public void setBaseScenarioInfos(BaseScenarioInfos baseScenarioInfos) {
		this.baseScenarioInfos = baseScenarioInfos;
	}

	public String getJsName() {
		return jsName;
	}

	public void setJsName(String jsName) {
		this.jsName = jsName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isConcurrent() {
		return concurrent;
	}

	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}

	public DependencyList getDependencyList() {
		return dependencyList;
	}

	public void setDependencyList(DependencyList dependencyList) {
		this.dependencyList = dependencyList;
	}

	public ScenarioStatusList getScenarioStatusList() {
		return scenarioStatusList;
	}

	public void setScenarioStatusList(ScenarioStatusList scenarioStatusList) {
		this.scenarioStatusList = scenarioStatusList;
	}

	public AlarmPreference getAlarmPreference() {
		return alarmPreference;
	}

	public void setAlarmPreference(AlarmPreference alarmPreference) {
		this.alarmPreference = alarmPreference;
	}

	public AdvancedScenarioInfos getAdvancedScenarioInfos() {
		return advancedScenarioInfos;
	}

	public void setAdvancedScenarioInfos(AdvancedScenarioInfos advancedScenarioInfos) {
		this.advancedScenarioInfos = advancedScenarioInfos;
	}

	public LocalParameters getLocalParameters() {
		return localParameters;
	}

	public void setLocalParameters(LocalParameters localParameters) {
		this.localParameters = localParameters;
	}

	public Logger getGlobalLogger() {
		return globalLogger;
	}

	public SpaceWideRegistry getSpaceWideRegistry() {
		return spaceWideRegistry;
	}

	public ArrayList<SortType> getJobQueueIndex() {
		return jobQueueIndex;
	}

	public static String getLogLabel() {
		return logLabel;
	}

	public Thread getExecuterThread() {
		return executerThread;
	}

	public void setExecuterThread(Thread executerThread) {
		this.executerThread = executerThread;
	}

	public Logger getMyLogger() {
		return myLogger;
	}

	public LiveStateInfo getLiveStateInfo() {
		return liveStateInfo;
	}

	public void setLiveStateInfo(LiveStateInfo liveStateInfo) {
		this.liveStateInfo = liveStateInfo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public HashMap<String, Job> getJobQueue() {
		return jobQueue;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public String getSpcAbsolutePath() {
		return spcAbsolutePath;
	}

//	protected boolean isSpcPermittedToExecute() {
//		return (getSpaceWideRegistry().getCurrentState() == AppState.INT_RUNNING) && !LiveStateInfoUtils.equalStates(getLiveStateInfo(), StateName.PENDING);
//	}

	public String getCurrentRunId() {
		return currentRunId;
	}

	public void setCurrentRunId(String currentRunId) {
		this.currentRunId = currentRunId;
	}

	public String getNativeRunId() {
		return nativeRunId;
	}

	public boolean isUpdateMySelfAfterMe() {
		return updateMySelfAfterMe;
	}

	public void setUpdateMySelfAfterMe(boolean updateMySelfAfterMe) {
		this.updateMySelfAfterMe = updateMySelfAfterMe;
	}

	public ArrayList<SortType> getDailyJobQueueIndex() {
		return dailyJobQueueIndex;
	}

	public ArrayList<SortType> getNonDailyJobQueueIndex() {
		return nonDailyJobQueueIndex;
	}

	public SpcMonitor getSpcMonitor() {
		return spcMonitor;
	}

	public void setSpcMonitor(SpcMonitor spcMonitor) {
		this.spcMonitor = spcMonitor;
	}

	public boolean isSpcSuspended() {
		return isSpcSuspended;
	}

	public void setSpcSuspended(boolean isSpcSuspended) {
		this.isSpcSuspended = isSpcSuspended;
	}

	public Management getManagement() {
		return management;
	}

	public void setManagement(Management management) {
		this.management = management;
	}

	public String getLSIDateTime() {
		return LSIDateTime;
	}

	public void setLSIDateTime(String lSIDateTime) {
		LSIDateTime = lSIDateTime;
	}

}
