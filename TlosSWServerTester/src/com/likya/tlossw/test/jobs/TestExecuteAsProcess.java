package com.likya.tlossw.test.jobs;

import java.io.File;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.core.spc.jobs.ExecuteAsProcess;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.ParsingUtils;

public class TestExecuteAsProcess extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestExecuteAsProcess.class);

	public static void main(String[] args) {
		new TestExecuteAsProcess().startTest();
	}

	public void startTest() {

		// JobProperties jobProperties = getJobPropertiesFromExist();
		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src" + File.separator, "ExecuteAsProcess.xml");
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
		jobRuntimeProperties.setJobProperties(jobProperties);

		String spcId = "testSpc-01";
		jobRuntimeProperties.setTreePath(new TlosSWPathType(spcId).getAbsolutePath());
		
		ExecuteAsProcess executeAsProcess = new ExecuteAsProcess(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);

		Thread myRunner = new Thread(executeAsProcess);

		myRunner.start();
		
	}
}
