package com.likya.tlossw.test.jobs;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.core.spc.jobs.WebServiceExecuter;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.ParsingUtils;

public class TestWebServiceExecuter extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestWebServiceExecuter.class);

	public static void main(String[] args) {
		new TestWebServiceExecuter().startTest();
	}

	public void startTest() {

		// JobProperties jobProperties = getJobPropertiesFromExist();
		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src\\", "WebServiceExecuter.xml");
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
		jobRuntimeProperties.setJobProperties(jobProperties);

		String spcId = "testSpc-01";
		jobRuntimeProperties.setTreePath(new TlosSWPathType(spcId));

		WebServiceExecuter webServiceExecuter = new WebServiceExecuter(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);
		
		Thread myRunner = new Thread(webServiceExecuter);

		myRunner.start();
	}
}
