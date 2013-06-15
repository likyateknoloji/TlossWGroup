package com.likya.tlossw.test.jobs;

import java.io.File;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationTypeDocument.OperationType;
import com.likya.tlossw.core.spc.jobs.FtpGetFile;
import com.likya.tlossw.core.spc.jobs.FtpListRemoteFiles;
import com.likya.tlossw.core.spc.jobs.FtpPutFile;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.ParsingUtils;

public class TestFTPProcessExecuter extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestFTPProcessExecuter.class);

	public static void main(String[] args) {
		new TestFTPProcessExecuter().startTest();
	}

	public void startTest() {


		// JobProperties jobProperties = getJobPropertiesFromExist();
		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src" + File.separator, "FTPProcessExecuter.xml");
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
		jobRuntimeProperties.setJobProperties(jobProperties);
		
		fileName = ParsingUtils.getConcatenatedPathAndFileName("src" + File.separator, "FTPProperties.xml");
		
		FtpProperties ftpProperties = null;
		ftpProperties = getFtpPropertiesFromFile(fileName);
		
		jobRuntimeProperties.setFtpProperties(ftpProperties);

		String spcId = "testSpc-01";
		jobRuntimeProperties.setTreePath(spcId);

		Job ftpExecutor = null;
		
		int operationType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getFtpAdapterProperties().getOperation().getOperationType().intValue();

		switch (operationType) {

		case OperationType.INT_READ_FILE:
			ftpExecutor = new FtpGetFile(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);
			break;

		case OperationType.INT_WRITE_FILE:
			ftpExecutor = new FtpPutFile(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);
			break;

		case OperationType.INT_LIST_FILES:
			ftpExecutor = new FtpListRemoteFiles(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);
			break;

		default:
			break;
		}

		Thread myRunner = new Thread(ftpExecutor);

		myRunner.start();
		
	}
}
