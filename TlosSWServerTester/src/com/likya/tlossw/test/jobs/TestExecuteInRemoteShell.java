package com.likya.tlossw.test.jobs;

import java.io.File;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.core.spc.jobs.ExecuteInRemoteSch;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.ParsingUtils;

public class TestExecuteInRemoteShell extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestExecuteInRemoteShell.class);

	public static void main(String[] args) {
		new TestExecuteInRemoteShell().startTest();
	}

	public void startTest() {

		// JobProperties jobProperties = getJobPropertiesFromExist();
		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src" + File.separator, "ExecuteInRemoteShell.xml");
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
		jobRuntimeProperties.setJobProperties(jobProperties);

		String spcId = "testSpc-01";
		jobRuntimeProperties.setTreePath(new TlosSWPathType(spcId).getAbsolutePath());

		
		ExecuteInRemoteSch executeInRemoteShell = new ExecuteInRemoteSch(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);

		Thread myRunner = new Thread(executeInRemoteShell);

		myRunner.start();
		
	}
}
