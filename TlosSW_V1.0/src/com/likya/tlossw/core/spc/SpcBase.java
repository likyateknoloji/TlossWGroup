package com.likya.tlossw.core.spc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.data.AdvancedScenarioInfosDocument.AdvancedScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.AlarmPreferenceDocument.AlarmPreference;
import com.likya.tlos.model.xmlbeans.data.BaseScenarioInfosDocument.BaseScenarioInfos;
import com.likya.tlos.model.xmlbeans.data.ConcurrencyManagementDocument.ConcurrencyManagement;
import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsRealTimeDocument.JsRealTime;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbTypeDocument.DbType;
import com.likya.tlos.model.xmlbeans.dbjob.DbConnectionPropertiesDocument.DbConnectionProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbJobDefinitionDocument.DbJobDefinition;
import com.likya.tlos.model.xmlbeans.fileadapter.FileAdapterPropertiesDocument.FileAdapterProperties;
import com.likya.tlos.model.xmlbeans.fileadapter.OperationTypeDocument.OperationType;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationTypeDocument;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.ScenarioStatusListDocument.ScenarioStatusList;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.cpc.CpcBase;
import com.likya.tlossw.core.spc.helpers.SortType;
import com.likya.tlossw.core.spc.jobs.ExecuteAsProcess;
import com.likya.tlossw.core.spc.jobs.ExecuteInShell;
import com.likya.tlossw.core.spc.jobs.FileListenerExecuter;
import com.likya.tlossw.core.spc.jobs.FtpGetFile;
import com.likya.tlossw.core.spc.jobs.FtpListRemoteFiles;
import com.likya.tlossw.core.spc.jobs.FtpPutFile;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.jobs.OracleSQLScriptExecuter;
import com.likya.tlossw.core.spc.jobs.OracleSQLSentenceExecuter;
import com.likya.tlossw.core.spc.jobs.OracleSQLStoredProcedureExecuter;
import com.likya.tlossw.core.spc.jobs.PostgreSQLScriptExecuter;
import com.likya.tlossw.core.spc.jobs.PostgreSQLSentenceExecuter;
import com.likya.tlossw.core.spc.jobs.PostgreSQLStoredProcedureExecuter;
import com.likya.tlossw.core.spc.jobs.ProcessNode;
import com.likya.tlossw.core.spc.jobs.ReadLocalFileProcess;
import com.likya.tlossw.core.spc.jobs.WebServiceExecuter;
import com.likya.tlossw.core.spc.jobs.WriteLocalFileProcess;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.db.utils.DBUtils;
import com.likya.tlossw.model.engine.EngineeConstants;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.TypeUtils;

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
	private String userName;

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

	public SpcBase(String spcId, SpaceWideRegistry spaceWideRegistry, ArrayList<JobRuntimeProperties> taskList) {

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
			jobQueueIndex.add(new SortType(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName(), jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobPriority().intValue()));
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
				// isi jobQueue ya ismi ile birlikte koyalim.
				getJobQueue().put(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName(), myJob);
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
			jobQueueIndex.add(new SortType(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName(), jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobPriority().intValue()));

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
				// isi jobQueue ya ismi ile birlikte koyalim.
				getJobQueue().put(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName(), myJob);
			}

			Collections.sort(jobQueueIndex);
			myLogger.info(" >");
			// isi taskList e ekleyelim
			this.taskList.add(jobRuntimeProperties);
		}

		return true;
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

		SpaceWideRegistry.getInstance().getTlosProcessData();

		Job myJob = extractJobTypes(jobRuntimeProperties);

		return myJob;
	}

	private Job extractJobTypes(JobRuntimeProperties jobRuntimeProperties) {

		Job myJob = null;

		int jobType = jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue();

		switch (jobType) {
		
		case JobCommandType.INT_SYSTEM_COMMAND:
			myJob = new ExecuteAsProcess(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;
			
		case JobCommandType.INT_BATCH_PROCESS:
			myJob = new ExecuteInShell(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;
			
		case JobCommandType.INT_SHELL_SCRIPT:
			myJob = new ExecuteInShell(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;
			
		case JobCommandType.INT_SAP:
			//TODO Gelistirme yapilacak.
			break;
			
		case JobCommandType.INT_SAS:
			//TODO Gelistirme yapilacak.
			break;
			
		case JobCommandType.INT_ETL_TOOL_JOBS:

			// DB Connection
			DbConnectionProperties dbConnectionProperties2 = TypeUtils.resolvedbConnectionProperties(jobRuntimeProperties.getJobProperties());

			int dbPropertiesID2 = dbConnectionProperties2.getDbPropertiesId().intValue();

			DbProperties dbProperties2 = null;

			try {
				dbProperties2 = DBUtils.searchDBPropertiesById(dbPropertiesID2);
			} catch (XMLDBException e) {
				e.printStackTrace();
			}

			if (dbProperties2 != null) {
				jobRuntimeProperties.setDbProperties(dbProperties2);
			} else {
				myLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " icin tanimli Db baglanti bilgileri alinamadi !");
				myLogger.error("dbProperties -> id=" + dbPropertiesID2 + "bulunamadi !");
			}

			// DB Connection Profile

			int dbCPID2 = dbConnectionProperties2.getDbUserId().intValue();

			DbConnectionProfile dbConnectionProfile2 = null;

			try {
				dbConnectionProfile2 = DBUtils.searchDBConnectionProfilesById(dbCPID2);
			} catch (XMLDBException e) {
				e.printStackTrace();
			}

			if (dbConnectionProfile2 != null) {
				jobRuntimeProperties.setDbConnectionProfile(dbConnectionProfile2);
			} else {
				myLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " icin tanimli DbConnectionProfile baglanti bilgileri alinamadi !");
				myLogger.error("dbConnectionProfile -> id=" + dbCPID2 + "bulunamadi !");
			}

			myJob = new PostgreSQLSentenceExecuter(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);			
			break;
			
		case JobCommandType.INT_FTP:

			FtpAdapterProperties adapterProperties = TypeUtils.resolveFtpAdapterProperties(jobRuntimeProperties.getJobProperties());
			int operationType = adapterProperties.getOperation().getOperationType().intValue();

			// ftp baglanti bilgileri set ediliyor
			int ftpConnectionId = adapterProperties.getRemoteTransferProperties().getFtpPropertiesId().intValue();

			FtpProperties ftpProperties = null;

			try {
				ftpProperties = DBUtils.searchFTPConnectionById(ftpConnectionId);
			} catch (XMLDBException e) {
				e.printStackTrace();
			}

			if (ftpProperties != null) {
				jobRuntimeProperties.setFtpProperties(ftpProperties);
			} else {
				myLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " icin tanimli ftp baglanti bilgileri alinamadi !");
				myLogger.error("ftpProperties -> id=" + ftpConnectionId + "bulunamadi !");
			}

			switch (operationType) {

			case OperationTypeDocument.OperationType.INT_READ_FILE:
				myJob = new FtpGetFile(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				break;

			case OperationTypeDocument.OperationType.INT_WRITE_FILE:
				myJob = new FtpPutFile(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				break;

			case OperationTypeDocument.OperationType.INT_LIST_FILES:
				myJob = new FtpListRemoteFiles(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				break;

			default:
				break;
			}

			break;

		case JobCommandType.INT_WEB_SERVICE:
			myJob = new WebServiceExecuter(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;

		case JobCommandType.INT_DB_JOBS:
			// TODO db joblari ile ilgili ayarlama yapilacak
			// TODO ilgili ayarlama yapilacak
            // Simdilik DB JOBS da olani koydum. hs.
			// DB Connection
			DbConnectionProperties dbConnectionProperties = TypeUtils.resolvedbConnectionProperties(jobRuntimeProperties.getJobProperties());

			int dbPropertiesID = dbConnectionProperties.getDbPropertiesId().intValue();

			DbProperties dbProperties = null;

			try {
				dbProperties = DBUtils.searchDBPropertiesById(dbPropertiesID);
			} catch (XMLDBException e) {
				e.printStackTrace();
			}

			if (dbProperties != null) {
				jobRuntimeProperties.setDbProperties(dbProperties);
			} else {
				myLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " icin tanimli Db baglanti bilgileri alinamadi !");
				myLogger.error("dbProperties -> id=" + dbPropertiesID + "bulunamadi !");
			}

			// DB Connection Profile

			int dbCPID = dbConnectionProperties.getDbUserId().intValue();

			DbConnectionProfile dbConnectionProfile = null;

			try {
				dbConnectionProfile = DBUtils.searchDBConnectionProfilesById(dbCPID);
			} catch (XMLDBException e) {
				e.printStackTrace();
			}

			if (dbConnectionProfile != null) {
				jobRuntimeProperties.setDbConnectionProfile(dbConnectionProfile);
			} else {
				myLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " icin tanimli DbConnectionProfile baglanti bilgileri alinamadi !");
				myLogger.error("dbConnectionProfile -> id=" + dbCPID + "bulunamadi !");
			}

			DbJobDefinition dbJobDefinition = TypeUtils.resolveDbJobDefinition(jobRuntimeProperties.getJobProperties());

			int dbType = dbProperties.getDbType().intValue();

			switch (dbType) {

			case DbType.INT_ORACLE:
				if (dbJobDefinition.getFreeSQLProperties() != null) {
					myJob = new OracleSQLSentenceExecuter(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);

				} else if (dbJobDefinition.getScriptProperties() != null) {
					myJob = new OracleSQLScriptExecuter(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);

				} else if (dbJobDefinition.getStoreProcedureProperties() != null) {
					myJob = new OracleSQLStoredProcedureExecuter(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				}

				break;

			case DbType.INT_POSTGRE_SQL:
				if (dbJobDefinition.getFreeSQLProperties() != null) {
					myJob = new PostgreSQLSentenceExecuter(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);

				} else if (dbJobDefinition.getScriptProperties() != null) {
					myJob = new PostgreSQLScriptExecuter(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);

				} else if (dbJobDefinition.getStoreProcedureProperties() != null) {
					myJob = new PostgreSQLStoredProcedureExecuter(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				}

				break;

			case DbType.INT_DB_2:
				//TODO Gelistirme yapilacak.
				break;

			case DbType.INT_FIREBIRD:
				//TODO Gelistirme yapilacak.
				break;

			case DbType.INT_INFORMIX:
				//TODO Gelistirme yapilacak.
				break;

			case DbType.INT_MY_SQL:
				//TODO Gelistirme yapilacak.
				break;

			case DbType.INT_SAS:
				//TODO Gelistirme yapilacak.
				break;

			case DbType.INT_SQL_SERVER:
				//TODO Gelistirme yapilacak.
				break;

			case DbType.INT_SYBASE:
				//TODO Gelistirme yapilacak.
				break;
				//TODO Gelistirme yapilacak.
			case DbType.INT_TERADATA:
				//TODO Gelistirme yapilacak.
				break;

			default:
				break;
			}

			break;

		case JobCommandType.INT_FILE_LISTENER:
			myJob = new FileListenerExecuter(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;

		case JobCommandType.INT_PROCESS_NODE:
			myJob = new ProcessNode(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;
			
		case JobCommandType.INT_FILE_PROCESS:
			FileAdapterProperties fileAdapterProperties = TypeUtils.resolveFileAdapterProperties(jobRuntimeProperties.getJobProperties());

			int fileProcessOperationType = fileAdapterProperties.getOperation().getOperationType().intValue();

			switch (fileProcessOperationType) {

			case OperationType.INT_READ_FILE:
				myJob = new ReadLocalFileProcess(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				break;

			case OperationType.INT_WRITE_FILE:
				myJob = new WriteLocalFileProcess(getSpaceWideRegistry(), SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				break;

			case OperationType.INT_LIST_FILES:
				// TODO
				break;

			case OperationType.INT_INSERT_RECORD:
				// TODO
				break;

			case OperationType.INT_UPDATE_RECORD:
				// TODO
				break;

			case OperationType.INT_DELETE_RECORD:
				// TODO
				break;

			default:
				break;
			}

			break;

		default:
			break;
		}

		return myJob;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
}
