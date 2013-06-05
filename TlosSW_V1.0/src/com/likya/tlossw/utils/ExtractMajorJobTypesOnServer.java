package com.likya.tlossw.utils;

import org.apache.log4j.Logger;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbConnectionPropertiesDocument.DbConnectionProperties;
import com.likya.tlos.model.xmlbeans.fileadapter.FileAdapterPropertiesDocument.FileAdapterProperties;
import com.likya.tlos.model.xmlbeans.fileadapter.OperationTypeDocument.OperationType;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationTypeDocument;
import com.likya.tlossw.core.spc.helpers.ExtractDBJobs;
import com.likya.tlossw.core.spc.jobs.ExecuteAsProcess;
import com.likya.tlossw.core.spc.jobs.ExecuteInShell;
import com.likya.tlossw.core.spc.jobs.FileListenerExecuter;
import com.likya.tlossw.core.spc.jobs.FtpGetFile;
import com.likya.tlossw.core.spc.jobs.FtpListRemoteFiles;
import com.likya.tlossw.core.spc.jobs.FtpPutFile;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.jobs.ProcessNode;
import com.likya.tlossw.core.spc.jobs.ReadLocalFileProcess;
import com.likya.tlossw.core.spc.jobs.WebServiceExecuter;
import com.likya.tlossw.core.spc.jobs.WriteLocalFileProcess;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.db.utils.DBUtils;

public class ExtractMajorJobTypesOnServer {

	public static Job evaluate(JobRuntimeProperties jobRuntimeProperties, SpaceWideRegistry spaceWideRegistry, Logger myLogger) {


		Job myJob = null;

		int jobType = jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType().intValue();

		switch (jobType) {

		case JobCommandType.INT_SYSTEM_COMMAND:
			myJob = new ExecuteAsProcess(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;

		case JobCommandType.INT_BATCH_PROCESS:
			myJob = new ExecuteInShell(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;

		case JobCommandType.INT_SHELL_SCRIPT:
			myJob = new ExecuteInShell(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;

		case JobCommandType.INT_SAP:
			// TODO Gelistirme yapilacak.
			break;

		case JobCommandType.INT_SAS:
			// TODO Gelistirme yapilacak.
			break;

		case JobCommandType.INT_ETL_TOOL_JOBS:
			// TODO Gelistirme yapilacak.
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
				myJob = new FtpGetFile(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				break;

			case OperationTypeDocument.OperationType.INT_WRITE_FILE:
				myJob = new FtpPutFile(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				break;

			case OperationTypeDocument.OperationType.INT_LIST_FILES:
				myJob = new FtpListRemoteFiles(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				break;

			default:
				break;
			}

			break;

		case JobCommandType.INT_WEB_SERVICE:
			myJob = new WebServiceExecuter(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
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
				break;
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
				break;
			}
			
			myJob = ExtractDBJobs.evaluate(spaceWideRegistry, dbProperties, jobRuntimeProperties, myJob, SpaceWideRegistry.getGlobalLogger());
			
			break;

		case JobCommandType.INT_FILE_LISTENER:
			myJob = new FileListenerExecuter(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;

		case JobCommandType.INT_PROCESS_NODE:
			myJob = new ProcessNode(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
			break;

		case JobCommandType.INT_FILE_PROCESS:
			FileAdapterProperties fileAdapterProperties = TypeUtils.resolveFileAdapterProperties(jobRuntimeProperties.getJobProperties());

			int fileProcessOperationType = fileAdapterProperties.getOperation().getOperationType().intValue();

			switch (fileProcessOperationType) {

			case OperationType.INT_READ_FILE:
				myJob = new ReadLocalFileProcess(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
				break;

			case OperationType.INT_WRITE_FILE:
				myJob = new WriteLocalFileProcess(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
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

}
