package com.likya.tlossw.utils;

import org.apache.log4j.Logger;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.common.JobCommandTypeDocument.JobCommandType;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbConnectionPropertiesDocument.DbConnectionProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlossw.core.spc.helpers.ExtractDBJobs;
import com.likya.tlossw.core.spc.helpers.ExtractFTPJobs;
import com.likya.tlossw.core.spc.helpers.ExtractFileProcessJobs;
import com.likya.tlossw.core.spc.jobs.ExecuteAsProcess;
import com.likya.tlossw.core.spc.jobs.ExecuteInRemoteSch;
import com.likya.tlossw.core.spc.jobs.ExecuteInShell;
import com.likya.tlossw.core.spc.jobs.FileListenerExecuter;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.jobs.ProcessNode;
import com.likya.tlossw.core.spc.jobs.WebServiceExecuter;
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
			
		case JobCommandType.INT_REMOTE_SHELL:
			myJob = new ExecuteInRemoteSch(spaceWideRegistry, SpaceWideRegistry.getGlobalLogger(), jobRuntimeProperties);
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
				break;
			}
			
			if (ftpProperties != null) {
				jobRuntimeProperties.setFtpProperties(ftpProperties);
			} else {
				myLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " icin tanimli ftp baglanti bilgileri alinamadi !");
				myLogger.error("ftpProperties -> id=" + ftpConnectionId + "bulunamadi !");
				
				break;
			}
			
			myJob = ExtractFTPJobs.evaluate(spaceWideRegistry, operationType, jobRuntimeProperties, myJob, SpaceWideRegistry.getGlobalLogger());
			
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
			
			myJob = ExtractFileProcessJobs.evaluate(spaceWideRegistry, jobRuntimeProperties, myJob, SpaceWideRegistry.getGlobalLogger());
			
			break;

		default:
			break;
		}

		return myJob;
	
	}

}
