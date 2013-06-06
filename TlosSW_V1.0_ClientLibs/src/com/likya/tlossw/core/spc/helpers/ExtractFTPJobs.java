package com.likya.tlossw.core.spc.helpers;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationTypeDocument;
import com.likya.tlossw.core.spc.jobs.FtpGetFile;
import com.likya.tlossw.core.spc.jobs.FtpListRemoteFiles;
import com.likya.tlossw.core.spc.jobs.FtpPutFile;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;

public class ExtractFTPJobs {

	private static Logger myLogger = Logger.getLogger(ExtractFTPJobs.class);

	public static Job evaluate(GlobalRegistry globalRegistry, int operationType, JobRuntimeProperties jobRuntimeProperties, Job myJob, Logger gobalLogger, FtpProperties ftpProperties, int ftpConnectionId) {

		if (ftpProperties != null) {
			jobRuntimeProperties.setFtpProperties(ftpProperties);
		} else {
			myLogger.error(jobRuntimeProperties.getJobProperties().getBaseJobInfos().getJsName() + " icin tanimli ftp baglanti bilgileri alinamadi !");
			myLogger.error("ftpProperties -> id=" + ftpConnectionId + "bulunamadi !");
		}

		switch (operationType) {

		case OperationTypeDocument.OperationType.INT_READ_FILE:
			myJob = new FtpGetFile(globalRegistry, gobalLogger, jobRuntimeProperties);
			break;

		case OperationTypeDocument.OperationType.INT_WRITE_FILE:
			myJob = new FtpPutFile(globalRegistry, gobalLogger, jobRuntimeProperties);
			break;

		case OperationTypeDocument.OperationType.INT_LIST_FILES:
			myJob = new FtpListRemoteFiles(globalRegistry, gobalLogger, jobRuntimeProperties);
			break;

		default:
			break;
		}
		
		return myJob;

	}

}
