package com.likya.tlossw.test.jobs;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlossw.core.spc.jobs.JDBCPostgreSQLSentenceExecuter;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.ParsingUtils;

public class TestPostgreSQLSentenceExecuter extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestPostgreSQLSentenceExecuter.class);

	public static void main(String[] args) {
		new TestPostgreSQLSentenceExecuter().startTest();
	}

	public void startTest() {

		String xmlFilePath = "/Users/serkan/programlar/dev/workspace/TlosSWServerTester/src/";
		// JobProperties jobProperties = getJobPropertiesFromExist();
		String fileName = ParsingUtils.getConcatenatedPathAndFileName(xmlFilePath, "PostgreSQLSentenceExecuter.xml");
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		fileName = ParsingUtils.getConcatenatedPathAndFileName(xmlFilePath, "DBPostgreConnection.xml");
		DbProperties dbProperties = null;
		dbProperties = getDbPropertiesFromFile(fileName);
		
		fileName = ParsingUtils.getConcatenatedPathAndFileName(xmlFilePath, "DBPostgreConnectionProfile.xml");
		DbConnectionProfile dbConnectionProfile = null;
		dbConnectionProfile = getDbConnectionProfileFromFile(fileName);
		
//		try {
//			dbProperties = getDBPropertiesFromExist().get("1");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
		
		
		
		JobRuntimeProperties jobRuntimeProperties = new JobRuntimeProperties();
		jobRuntimeProperties.setJobProperties(jobProperties);
		jobRuntimeProperties.setDbProperties(dbProperties);
		jobRuntimeProperties.setDbConnectionProfile(dbConnectionProfile);

		String spcId = "testSpc-01";
		jobRuntimeProperties.setTreePath(spcId);

		
		JDBCPostgreSQLSentenceExecuter postgreSQLSentenceExecuter = new JDBCPostgreSQLSentenceExecuter(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);

		Thread myRunner = new Thread(postgreSQLSentenceExecuter);

		myRunner.start();
		
	}
}
