package com.likya.tlossw.test;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationTypeDocument.OperationType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.jobs.ReadLocalFileProcess;
import com.likya.tlossw.core.spc.jobs.WriteLocalFileProcess;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.ParsingUtils;

public class TestFileProcessExecuter extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestFileProcessExecuter.class);

	public TestFileProcessExecuter() {
		super();
	}
	
	public static void main(String[] args) {
		
		new TestFileProcessExecuter().startTest();

	}
	
	public void startTest() {
		
//		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src\\", "ReadLocalFileProcessExecuter.xml");
		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src\\", "WriteLocalFileProcessExecuter.xml");
		
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
		jobRuntimeProperties.setJobProperties(jobProperties);
		
		String spcId = "testSpc-01";
		jobRuntimeProperties.setTreePath(spcId);

		Job fileProcessExecutor = null;
		
		int operationType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getFileAdapterProperties().getOperation().getOperationType().intValue();

		switch (operationType) {

		case OperationType.INT_READ_FILE:
			fileProcessExecutor = new ReadLocalFileProcess(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);
			break;

		case OperationType.INT_WRITE_FILE:
			fileProcessExecutor = new WriteLocalFileProcess(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);
			break;

		case OperationType.INT_LIST_FILES:
			break;

		default:
			break;
		}

		Thread myRunner = new Thread(fileProcessExecutor);

		myRunner.start();

	}
}
