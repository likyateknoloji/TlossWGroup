package com.likya.tlossw.core.spc.jobs;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;

public class OracleSQLSentenceExecuter extends SQLScriptExecuter {

	private static final long serialVersionUID = -1947157346281291622L;

	Logger myLogger = Logger.getLogger(this.getClass());
	transient Logger globalLogger;

	private boolean retryFlag = true;

	transient protected Process process;

	public OracleSQLSentenceExecuter(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public void localRun() {

		initStartUp(myLogger);

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		DbProperties dbProperties = getJobRuntimeProperties().getDbProperties();
		DbConnectionProfile dbConnectionProfile = getJobRuntimeProperties().getDbConnectionProfile();

		while (true) {

			try {

				startWathcDogTimer();
				
				String dbPath = "";

				String osqlClientNamePath = dbProperties.getSqlClientAppPath();
				String osqlClientName = dbProperties.getSqlClientAppName().toString();
				String ipAddress = dbProperties.getIpAddress();
				int port = dbProperties.getListenerPortNumber();
				
				String userName = dbConnectionProfile.getUserName(); // "postgres"; // Connection profile dan alacak.
				String password = dbConnectionProfile.getUserPassword(); // "ad0215"; // Connection profile dan alacak.

				// "postgres"; // Connection profile dan alacak.
				// String userName = dbProperties.getUserName();

				// "ad0215"; // Connection profile dan alacak.
				// String password = dbProperties.getUserPassword();

				// "Carre"; // Connection profile dan alacak.
				String dbName = dbProperties.getDbName();

				if(ipAddress != null && !ipAddress.equals("") && port != 0) {
					// 192.168.1.75:1521/orcl
					dbPath = ipAddress + ":" + port + "/" + dbName;
				} else {
					dbPath = dbName;
				}

				//echo SQL_Clause; | sqlplus username/password@ipadres:portnumber/oracle_sid
				 
				String sqlSentence = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getDbJobDefinition().getFreeSQLProperties().getSqlSentence();

				osqlClientName = "echo execute " + sqlSentence + "; | " + osqlClientName + " " + userName + "/" + password + "@" + dbPath;

				insertNewLiveStateInfo(StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

				startShellProcess(osqlClientNamePath, osqlClientName, null, this.getClass().getName(), myLogger);

			} catch (Exception err) {
				handleException(err, myLogger);
			}

			if (processJobResult(retryFlag, myLogger)) {
				retryFlag = false;
				continue;
			}

			break;
		}

		cleanUp(process, myLogger);

	}
}
