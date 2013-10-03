package com.likya.tlossw.test.jobs;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationTypeDocument.OperationType;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.jobs.ReadLocalFileProcess;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.model.path.ScenarioPathType;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.ParsingUtils;

public class TestFileProcessExecuter extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestFileProcessExecuter.class);

	public static void main(String[] args) {
		new TestFileProcessExecuter().startTest();
	}

	public void startTest() {

		// JobProperties jobProperties = getJobPropertiesFromExist();
		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src\\", "ReadLocalFileProcessExecuter.xml");
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
		jobRuntimeProperties.setJobProperties(jobProperties);
		
		String spcId = "testSpc-01";
		jobRuntimeProperties.setTreePath(new ScenarioPathType(spcId));

		Job fileProcessExecutor = null;
		
		int operationType = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getFileAdapterProperties().getOperation().getOperationType().intValue();

		switch (operationType) {

		case OperationType.INT_READ_FILE:
			fileProcessExecutor = new ReadLocalFileProcess(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);
			break;

		case OperationType.INT_WRITE_FILE:
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
