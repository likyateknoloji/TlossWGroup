package com.likya.tlossw.test.jobs;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.core.spc.jobs.FileListenerExecuter;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.ParsingUtils;

public class TestFileListenerExecuter extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestFileListenerExecuter.class);

	public static void main(String[] args) {
		new TestFileListenerExecuter().startTest();
	}

	public void startTest() {
	
		// JobProperties jobProperties = getJobPropertiesFromExist();
		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src\\", "FileListenerExecuter.xml");
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
		jobRuntimeProperties.setJobProperties(jobProperties);

		String spcId = "testSpc-01";
		jobRuntimeProperties.setTreePath(spcId);

		FileListenerExecuter fileListenerExecuter = new FileListenerExecuter(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);

		Thread myRunner = new Thread(fileListenerExecuter);

		myRunner.start();
	}
}
