package com.likya.tlossw.core.spc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.data.AdvancedScenarioInfosDocument.AdvancedScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.AlarmPreferenceDocument.AlarmPreference;
import com.likya.tlos.model.xmlbeans.data.BaseScenarioInfosDocument.BaseScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.ConcurrencyManagementDocument.ConcurrencyManagement;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsRealTimeDocument.JsRealTime;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfosDocument.LiveStateInfos;
import com.likya.tlos.model.xmlbeans.state.ScenarioStatusListDocument.ScenarioStatusList;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.CpcBase;
import com.likya.tlossw.core.cpc.model.SpcInfoType;
import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.jobs.ExecuteInShell;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.utils.ExtractMajorJobTypesOnServer;
import com.likya.tlossw.utils.SpaceWideRegistry;

public abstract class SpcBase implements Runnable, Serializable {

	private static final long serialVersionUID = 3839154843189778398L;

	private LiveStateInfo liveStateInfo;

	private String spcId;
	private String jsName;
	private String comment;
	private String instanceId;
	private boolean concurrent;
	// private DependencyList dependencyList;
	// private ScenarioStatusList scenarioStatusList;
	private String userId;

	private BaseScenarioInfos baseScenarioInfos;
	private DependencyList dependencyList;
	private ScenarioStatusList scenarioStatusList;
	private AlarmPreference alarmPreference;
	private TimeManagement timeManagement;
	private AdvancedScenarioInfos advancedScenarioInfos;
	private ConcurrencyManagement concurrencyManagement;
	private LocalParameters localParameters;

	// Senaryolarin baslama ve bitis bilgilerini de raporlama amacli dolduralim.
	// ilk aklima gelen job da nasil yapildigina bakip oradan kopya cekmek oldu.
	// daha iyi bir yontem varsa onunla degistirelim. hs

	protected Calendar startTime;
	protected JsRealTime scenarioRealTime;

	private boolean isPausable;
	private boolean isResumable;
	private boolean isStopable;
	private boolean isStartable;

	private boolean isManagable;

	transient private SpaceWideRegistry spaceWideRegistry;

	private ArrayList<JobRuntimeProperties> taskList;

	private ArrayList<SortType> jobQueueIndex;
	private HashMap<String, Job> jobQueue;
	
	private static String logLabel;

	transient private Logger globalLogger;
	transient private Logger myLogger;

	transient private Thread executerThread;

	protected boolean executionPermission = true;

	protected boolean isForced = false;
	
	protected boolean isTester = false;

	public SpcBase(String spcId, SpaceWideRegistry spaceWideRegistry, ArrayList<JobRuntimeProperties> taskList, boolean isTester) {

		this.isTester = isTester;
		this.spcId = spcId;
		this.taskList = taskList;
		this.spaceWideRegistry = spaceWideRegistry;

		jobQueue = new HashMap<String, Job>();
		jobQueueIndex = new ArrayList<SortType>();

		logLabel = "Spc_" + this.getSpcId();

		globalLogger = SpaceWideRegistry.getGlobalLogger();
		myLogger = Logger.getLogger(SpcBase.class);
	}

	public boolean initScenarioInfo() { // Senaryolarin ilk baslatilmalari icin

		myLogger.info("  >> Senaryo ismi : " + this.getBaseScenarioInfos().getJsName());
		Iterator<JobRuntimeProperties> taskListIterator = taskList.iterator();

		while (taskListIterator.hasNext()) { // Senaryodaki herbir is icin
			JobRuntimeProperties jobRuntimeProperties = taskListIterator.next();
			jobRuntimeProperties.setTreePath(getSpcId());
			// TODO Hosuma gitmedi ama tip d�n�s�m� uyguladim.
			// isleri onceliklerine gore siraya dizdigimiz bir dizi
			// tanimlamistik, ona ekleyip siralandiralim.
			jobQueueIndex.add(new SortType(jobRuntimeProperties.getJobProperties().getID(), jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobPriority().intValue()));
			// Su anda oncelikli isi daha once calistirma ile ilgili bir kontrol
			// yok. Koyulacak.

			Job myJob = null;

			myLogger.info("   > Is ismi : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
			myLogger.info("   > is Tipi : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobBaseType().toString());

			if (jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobBaseType().intValue() == JobBaseType.PERIODIC.intValue()) {
				// PERIYODIK bir is ise;
				if (!jobRuntimeProperties.getTreePath().equals(CpcBase.getRootPath() + "." + this.getConcurrencyManagement().getInstanceId() + "." + EngineeConstants.LONELY_JOBS)) {
					globalLogger.warn("     > Periodik job root disinda kullanilamaz ! Base : " + CpcBase.getRootPath());
					globalLogger.warn("     > JobName : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
					globalLogger.warn("     > TreePath : " + jobRuntimeProperties.getTreePath());

				} else { // TODO PARAMETRE ekleme
					myLogger.info("     > Periodik is geldi ! period : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters());
					myLogger.info("     > Periyodik is calitirma kismi henuz aktif degil. Burada olacak !!");
					// myJob = new PeriodicExternalProgram(getSwAgentRegistry(),
					// jobRuntimeProperties);
				}
			} else {
				// myLogger.info("     << Peryodik olmayan "+jobRuntimeProperties.getJobProperties().getJsName()+" isi calistirilmaya hazir ! >>");
				myLogger.info("     > Peryodik olmayan " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " isi calistirilmaya hazir !");
				myJob = getMyJob(jobRuntimeProperties);
			}
			if (myJob != null) {
				// isi jobQueue ya ID si ile birlikte koyalim.
				getJobQueue().put(jobRuntimeProperties.getJobProperties().getID(), myJob);
			}
		}

		Collections.sort(jobQueueIndex);
		myLogger.info(" >");
		return true;
	}

	public void setExecutionPermission(boolean executionPermission, boolean isForced) {
		synchronized (this) {
			this.executionPermission = executionPermission;
			this.isForced = isForced;
		}

	}

	public boolean addJob(JobProperties jobProperties) {

		// Senaryonun ilk isi icin aksiyon alindi, diger isler icin senkronize
		// method cagriliyor.

		synchronized (this) {
			JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
			jobRuntimeProperties.setJobProperties(jobProperties);

			jobRuntimeProperties.setTreePath(getSpcId());

			// TODO Ho�uma gitmedi ama tip d�n�s�m� yaptim
			jobQueueIndex.add(new SortType(jobRuntimeProperties.getJobProperties().getID(), jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobPriority().intValue()));

			Job myJob = null;

			myLogger.info("   > Is ismi : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
			myLogger.info("   > is Tipi : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobBaseType().toString());
			if (jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobBaseType().intValue() == JobBaseType.PERIODIC.intValue()) {
				if (!jobRuntimeProperties.getTreePath().equals(CpcBase.getRootPath() + "." + this.getConcurrencyManagement().getInstanceId() + "." + EngineeConstants.LONELY_JOBS)) {
					globalLogger.warn("Periodik job root disinda kullanilamaz ! Base : " + CpcBase.getRootPath());
					globalLogger.warn("JobName : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
					globalLogger.warn("TreePath : " + jobRuntimeProperties.getTreePath());

				} else { // TODO PARAMETRE ekleme
					myLogger.info("     > Periodik is geldi ! period : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters());
					myLogger.info("     > Periyodik is calitirma kismi henuz aktif degil. Burada olacak !!");
					// myJob = new PeriodicExternalProgram(getSwAgentRegistry(),
					// jobRuntimeProperties);
				}
			} else {
				myLogger.info("     > Peryodik olmayan " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " isi calistirilmaya hazir !");
				myJob = new ExecuteInShell(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				// TODO myJob = getMyJob(jobRuntimeProperties); olmasi gerekmez mi ? Hakan
			}

			if (myJob != null) {
				// isi jobQueue ya id si ile birlikte koyalim.
				getJobQueue().put(jobRuntimeProperties.getJobProperties().getID(), myJob);
			}

			Collections.sort(jobQueueIndex);
			myLogger.info(" >");
			// isi taskList e ekleyelim
			this.taskList.add(jobRuntimeProperties);
		}

		return true;
	}
	
	public int getNumOfJobs() {
		return getJobQueue().size();
	}

	public int getNumOfJobs(String stateNameType) {

		int counter = 0;

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

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

	public int getNumOfJobs(SubstateName substateNameType) {

		int counter = 0;

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			String tmpSubstateNameType = scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName().toString();
			if (tmpSubstateNameType != null && tmpSubstateNameType.equals(substateNameType)) {
				counter += 1;
			}

		}

		return counter;
	}

	public int getNumOfJobs(StateName stateNameType) {

		int counter = 0;

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

		while (jobsIterator.hasNext()) {
			Job scheduledJob = jobsIterator.next();
			if (scheduledJob.getJobRuntimeProperties().getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(stateNameType)) {
				counter += 1;
			}
		}

		return counter;
	}
	
	/*
	 * state yapisinda time-out statusu running statusunun substate i oldugu icin 
	 * hem state i running olanlari hem de substate i timeout olanlari toplarsak 
	 * timeout olanlari iki kere saymis olacagiz. bunun icin o kismi kaldirdim
	 */
	public int getNumOfActiveJobs() {

		int numOfWorkingJobs = getNumOfJobs(StateName.RUNNING.toString());

		return numOfWorkingJobs;
	}
	
	public int getNumOfJobsByAgent(int agentId) {

		int counter = 0;

		Iterator<Job> jobsIterator = getJobQueue().values().iterator();

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
	
	public LiveStateInfo getLastStateOfJob(LiveStateInfos liveStateInfos) {

		/*
		 * TODO Burada tarihe gore siralama yapmaya gerek var mi?
		 * Varsa asagidakine benzer birsey yapmamiz lazim.
		 * tarih cevriminde bir problem var, onu cozmemiz lazim tabii once
		 * 
		 * int boyut = liveStateInfos.sizeOfLiveStateInfoArray();
		 * Date refDate = DateUtils.getDateTime( liveStateInfos.getLiveStateInfoArray(0).getLSIDateTime());
		 * LiveStateInfo lastStateInfo = liveStateInfos.getLiveStateInfoArray(0);
		 * 
		 * for (int i=0; i<boyut; i++) {
		 * System.out.println(liveStateInfos.getLiveStateInfoArray(i));
		 * System.out.println(liveStateInfos.getLiveStateInfoArray(i));
		 * //com.likya.tlossw.utils.date.DateUtils
		 * String dateTimeInString = liveStateInfos.getLiveStateInfoArray(i).getLSIDateTime();
		 * if(DateUtils.getDateTime(dateTimeInString).after(refDate)) {
		 * refDate = DateUtils.getDateTime(dateTimeInString);
		 * lastStateInfo = liveStateInfos.getLiveStateInfoArray(i);
		 * }
		 * }
		 */
		LiveStateInfo lastStateInfo = liveStateInfos.getLiveStateInfoArray(0);

		return lastStateInfo;
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

	private Job getMyJob(JobRuntimeProperties jobRuntimeProperties) {

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

	public TimeManagement getTimeManagement() {
		return timeManagement;
	}

	public void setTimeManagement(TimeManagement timeManagement) {
		this.timeManagement = timeManagement;
	}

	public AdvancedScenarioInfos getAdvancedScenarioInfos() {
		return advancedScenarioInfos;
	}

	public void setAdvancedScenarioInfos(AdvancedScenarioInfos advancedScenarioInfos) {
		this.advancedScenarioInfos = advancedScenarioInfos;
	}

	public ConcurrencyManagement getConcurrencyManagement() {
		return concurrencyManagement;
	}

	public void setConcurrencyManagement(ConcurrencyManagement concurrencyManagement) {
		this.concurrencyManagement = concurrencyManagement;
	}

	public LocalParameters getLocalParameters() {
		return localParameters;
	}

	public void setLocalParameters(LocalParameters localParameters) {
		this.localParameters = localParameters;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Logger getGlobalLogger() {
		return globalLogger;
	}

	public SpaceWideRegistry getSpaceWideRegistry() {
		return spaceWideRegistry;
	}

	public String getSpcId() {
		return spcId;
	}

	public HashMap<String, Job> getJobQueue() {
		return jobQueue;
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

	public HashMap<String, SpcInfoType> getSpcLookupTable() {
		
		if(isTester) {
			return getSpaceWideRegistry().getCpcTesterReference().getSpcLookupTable(userId);			
		}
		
		return getSpaceWideRegistry().getInstanceLookupTable().get(getInstanceId()).getSpcLookupTable();
		
	}

}
