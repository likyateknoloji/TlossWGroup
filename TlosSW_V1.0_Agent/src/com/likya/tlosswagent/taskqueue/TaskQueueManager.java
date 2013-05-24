package com.likya.tlosswagent.taskqueue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agent.RxMessageDocument.RxMessage;
import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
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
import com.likya.tlossw.core.spc.helpers.GenericInfoSender;
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
import com.likya.tlossw.utils.FileUtils;
import com.likya.tlossw.utils.TypeUtils;
import com.likya.tlosswagent.core.model.AgentGlobalRegistry;
import com.likya.tlosswagent.utils.AgentStatusChangeInfoSender;
import com.likya.tlosswagent.utils.SWAgentRegistry;

public class TaskQueueManager implements Runnable, Serializable {

	private static final long serialVersionUID = 7261799461744166685L;

	private AgentGlobalRegistry agentGlobalRegistry;

	private boolean executionPermission = true;

	transient private Thread executerThread;

	transient private Logger taskQueueLogger;

	private HashMap<String, Object> taskInputQueue = new HashMap<String, Object>();

	private Boolean isIndexExpired = false;

	public boolean isRecoverAction = false;

	public TaskQueueManager(AgentGlobalRegistry agentGlobalRegistry) {
		super();
		this.agentGlobalRegistry = agentGlobalRegistry;
		this.taskQueueLogger = SWAgentRegistry.getsWAgentLogger();
		
		String fileName = "taskQueue";

		boolean isRecoverAction = agentGlobalRegistry.getAgentConfigInfo().getSettings().getIsPersistent().getValueBoolean();
		boolean recoverFileExist = FileUtils.checkFile(System.getProperty("tlosAgent.tmpdir") + "/" + fileName);
		if (isRecoverAction && recoverFileExist) {
			TaskQueueOperations.recoverTaskInputQueue(fileName, taskInputQueue);
			isIndexExpired = true;
		}
	}

	public void run() {

		Thread.currentThread().setName("TaskQueManager");

		while (executionPermission) {

			try {
				cleanUp();
				synchronized (this.getExecuterThread()) {
					this.getExecuterThread().wait(10000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public synchronized boolean addTask(RxMessage rxMessage) {
		
		JobProperties jobProperties = rxMessage.getRxMessageBodyType().getJobProperties();
		
		JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
		jobRuntimeProperties.setJobProperties(jobProperties);

		// jobRuntimeProperties.setTreePath(getSpcId());
		Job myJob = null;
		taskQueueLogger.info("is Adi : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName());
		taskQueueLogger.info("is Tipi : " + jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobBaseType().toString());

		//myJob = new ExecuteInShell(getGlobalRegistery(), taskQueueLogger, jobRuntimeProperties);
		GenericInfoSender genericInfoSender = new AgentStatusChangeInfoSender();
		
		//job tipine gore ayrim yapiliyor
		myJob = extractJobTypes(jobRuntimeProperties);

		myJob.setGenericInfoSender(genericInfoSender);
		if (myJob != null) {
			taskInputQueue.put(rxMessage.getId(), myJob);
		}

		isIndexExpired = true;

		return true;
	}
	
	private Job extractJobTypes(JobRuntimeProperties jobRuntimeProperties) {

		Job myJob = null;

		int jobType = jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue();

		switch (jobType) {
		
		case JobCommandType.INT_SYSTEM_COMMAND:
			myJob = new ExecuteAsProcess(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
			break;
			
		case JobCommandType.INT_SHELL_SCRIPT:
			myJob = new ExecuteInShell(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
			break;
			
		case JobCommandType.INT_SAP:
			//TODO Gelistirme yapilacak.
			break;
			
		case JobCommandType.INT_SAS:
			//TODO Gelistirme yapilacak.
			break;
			
		case JobCommandType.INT_BATCH_PROCESS:
			myJob = new ExecuteInShell(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
			break;
			
		case JobCommandType.INT_ETL_TOOL_JOBS:
			// TODO ilgili ayarlama yapilacak
			break;
			
		case JobCommandType.INT_FTP:

			FtpAdapterProperties adapterProperties = TypeUtils.resolveFtpAdapterProperties(jobRuntimeProperties.getJobProperties());
			int operationType = adapterProperties.getOperation().getOperationType().intValue();

			// ftp baglanti bilgileri set ediliyor
			FtpProperties ftpProperties = adapterProperties.getRemoteTransferProperties().getFtpProperties();

			if (ftpProperties != null) {
				jobRuntimeProperties.setFtpProperties(ftpProperties);
			} else {
				taskQueueLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " tanimi icerisinde ftp baglanti bilgileri bulunamadi !");
			}

			switch (operationType) {

			case OperationTypeDocument.OperationType.INT_READ_FILE:
				myJob = new FtpGetFile(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
				break;

			case OperationTypeDocument.OperationType.INT_WRITE_FILE:
				myJob = new FtpPutFile(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
				break;

			case OperationTypeDocument.OperationType.INT_LIST_FILES:
				myJob = new FtpListRemoteFiles(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
				break;

			default:
				break;
			}

			break;

		case JobCommandType.INT_WEB_SERVICE:
			myJob = new WebServiceExecuter(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
			break;

		case JobCommandType.INT_DB_JOBS:
			// TODO db joblari ile ilgili ayarlama yapilacak

			DbJobDefinition dbJobDefinition = TypeUtils.resolveDbJobDefinition(jobRuntimeProperties.getJobProperties());

			DbProperties dbProperties = dbJobDefinition.getDbProperties();
			DbConnectionProfile dbConnectionProfile = dbJobDefinition.getDbConnectionProfile();

			if (dbProperties != null && dbConnectionProfile != null) {
				jobRuntimeProperties.setDbProperties(dbProperties);
				jobRuntimeProperties.setDbConnectionProfile(dbConnectionProfile);
			} else {
				taskQueueLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " tanimi icerisinde db connection bilgileri bulunamadi !");
			}

			DbConnectionProperties dbConnectionProperties = TypeUtils.resolvedbConnectionProperties(jobRuntimeProperties.getJobProperties());

			int dbPropertiesID = dbConnectionProperties.getDbPropertiesId().intValue();

			if (dbProperties != null) {
				jobRuntimeProperties.setDbProperties(dbProperties);
			} else {
				taskQueueLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " icin tanimli Db baglanti bilgileri alinamadi !");
				taskQueueLogger.error("dbProperties -> id=" + dbPropertiesID + "bulunamadi !");
			}

			// DB Connection Profile

			int dbCPID = dbConnectionProperties.getDbUserId().intValue();


			if (dbConnectionProfile != null) {
				jobRuntimeProperties.setDbConnectionProfile(dbConnectionProfile);
			} else {
				taskQueueLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " icin tanimli DbConnectionProfile baglanti bilgileri alinamadi !");
				taskQueueLogger.error("dbConnectionProfile -> id=" + dbCPID + "bulunamadi !");
			}

			int dbType = dbProperties.getDbType().intValue();

			switch (dbType) {

			case DbType.INT_ORACLE:
				if (dbJobDefinition.getFreeSQLProperties() != null) {
					myJob = new OracleSQLSentenceExecuter(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);

				} else if (dbJobDefinition.getScriptProperties() != null) {
					myJob = new OracleSQLScriptExecuter(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);

				} else if (dbJobDefinition.getStoreProcedureProperties() != null) {
					myJob = new OracleSQLStoredProcedureExecuter(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
				}

				break;

			case DbType.INT_POSTGRE_SQL:
				if (dbJobDefinition.getFreeSQLProperties() != null) {
					myJob = new PostgreSQLSentenceExecuter(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);

				} else if (dbJobDefinition.getScriptProperties() != null) {
					myJob = new PostgreSQLScriptExecuter(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);

				} else if (dbJobDefinition.getStoreProcedureProperties() != null) {
					myJob = new PostgreSQLStoredProcedureExecuter(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
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
			myJob = new FileListenerExecuter(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
			break;
			
		case JobCommandType.INT_PROCESS_NODE:
			myJob = new ProcessNode(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
			break;
			
		case JobCommandType.INT_FILE_PROCESS:
			FileAdapterProperties fileAdapterProperties = TypeUtils.resolveFileAdapterProperties(jobRuntimeProperties.getJobProperties());
			
			int fileProcessOperationType = fileAdapterProperties.getOperation().getOperationType().intValue();
			
			switch (fileProcessOperationType) {

			case OperationType.INT_READ_FILE:
				myJob = new ReadLocalFileProcess(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
				break;

			case OperationType.INT_WRITE_FILE:
				myJob = new WriteLocalFileProcess(agentGlobalRegistry, taskQueueLogger, jobRuntimeProperties);
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

	public synchronized ArrayList<SortType> createTaskQueueIndex() {
		ArrayList<SortType> taskQueueIndex = new ArrayList<SortType>();
		synchronized (taskInputQueue) {
			taskQueueIndex.removeAll(taskQueueIndex);
			for (String taskKey : taskInputQueue.keySet()) {
				taskQueueIndex.add(new SortType(taskKey, ((Job) taskInputQueue.get(taskKey)).getJobRuntimeProperties().getJobProperties().getBaseJobInfos().getJobPriority().intValue()));
			}
			Collections.sort(taskQueueIndex);
		}
		return taskQueueIndex;
	}

	public synchronized boolean cleanUp() {

		for (String taskKey : taskInputQueue.keySet()) {
			Job task = (Job) taskInputQueue.get(taskKey);
			if (task.isExecuterOver()) {
				taskInputQueue.remove(taskKey);
				//synchronized (isIndexExpired) {
					isIndexExpired = true;
				//}

			}

		}
		return true;
	}

	public Object getTask(String taskKey) {
		return taskInputQueue.get(taskKey);
	}

	public synchronized Object runningJobsXML() {
		ArrayList<String> taskKeySet = new ArrayList<String>();

		taskKeySet.addAll(taskInputQueue.keySet());

		String jobList = "<jobList>";

		for (String taskKey : taskKeySet) {
			Job task = (Job) taskInputQueue.get(taskKey);
			jobList += task.getJobRuntimeProperties().getJobProperties().toString();
		}

		jobList += "</jobList>";
		return jobList;
	}

	public synchronized Object runningJobs() {
		ArrayList<String> taskKeySet = new ArrayList<String>();
		ArrayList<JobProperties> runningJobs = new ArrayList<JobProperties>();

		taskKeySet.addAll(taskInputQueue.keySet());

		for (String taskKey : taskKeySet) {
			Job task = (Job) taskInputQueue.get(taskKey);
			runningJobs.add(task.getJobRuntimeProperties().getJobProperties());
		}

		return runningJobs.toString();
	}

	public synchronized void resetTaskQueue() {
		ArrayList<String> taskKeySet = new ArrayList<String>();

		taskKeySet.addAll(taskInputQueue.keySet());

		for (String taskKey : taskKeySet) {
			Job task = (Job) taskInputQueue.get(taskKey);

			task.setMyExecuter(null);
			taskInputQueue.remove(taskKey);
			synchronized (isIndexExpired) {
				isIndexExpired = true;
			}

		}
	}

	public boolean isExecutionPermission() {
		return executionPermission;
	}

	public void setExecutionPermission(boolean executionPermission) {
		this.executionPermission = executionPermission;
	}

	public Thread getExecuterThread() {
		return executerThread;
	}

	public void setExecuterThread(Thread executerThread) {
		this.executerThread = executerThread;
	}

	public Logger getTaskQueueLogger() {
		return taskQueueLogger;
	}

	public HashMap<String, Object> getTaskInputQueue() {
		return taskInputQueue;
	}

	public void setTaskInputQueue(HashMap<String, Object> taskInputQueue) {
		this.taskInputQueue = taskInputQueue;
	}

	public Boolean getIsIndexExpired() {
		return isIndexExpired;
	}

	public AgentGlobalRegistry getAgentGlobalRegistry() {
		return agentGlobalRegistry;
	}

}
