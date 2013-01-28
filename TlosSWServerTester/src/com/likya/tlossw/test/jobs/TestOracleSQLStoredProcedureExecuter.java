package com.likya.tlossw.test.jobs;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlossw.core.spc.jobs.OracleSQLStoredProcedureExecuter;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.test.TestSuit;
import com.likya.tlossw.utils.ParsingUtils;

public class TestOracleSQLStoredProcedureExecuter extends TestSuit {

	static Logger globalLogger = Logger.getLogger(TestOracleSQLStoredProcedureExecuter.class);

	public static void main(String[] args) {
		new TestOracleSQLStoredProcedureExecuter().startTest();
	}

	public void startTest() {

		// JobProperties jobProperties = getJobPropertiesFromExist();
		String fileName = ParsingUtils.getConcatenatedPathAndFileName("src\\", "OracleSQLStoredProcedureExecuter.xml");
		JobProperties jobProperties = getJobPropertiesFromFile(fileName);
		
		fileName = ParsingUtils.getConcatenatedPathAndFileName("src\\", "DBOracleConnection.xml");
		DbProperties dbProperties = null;
		dbProperties = getDbPropertiesFromFile(fileName);
		
		fileName = ParsingUtils.getConcatenatedPathAndFileName("src\\", "DBOracleConnectionProfile.xml");
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

		
		OracleSQLStoredProcedureExecuter oracleSQLStoredProcedureExecuter = new OracleSQLStoredProcedureExecuter(getSpaceWideRegistry(), globalLogger, jobRuntimeProperties);

		Thread myRunner = new Thread(oracleSQLStoredProcedureExecuter);

		myRunner.start();
		
	}
}
