package com.likya.tlossw.core.spc.jobs;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;

public class PostgreSQLSentenceExecuter extends SQLScriptExecuter {

	private static final long serialVersionUID = -1947157346281291622L;

	Logger myLogger = Logger.getLogger(this.getClass());
	transient Logger globalLogger;

	private boolean retryFlag = true;

	transient protected Process process;

	public PostgreSQLSentenceExecuter(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public void localRun() {

		initStartUp(myLogger);

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		DbProperties dbProperties = getJobRuntimeProperties().getDbProperties();
		DbConnectionProfile dbConnectionProfile = getJobRuntimeProperties().getDbConnectionProfile();
		String ipAddress = dbProperties.getIpAddress();
		int port = dbProperties.getListenerPortNumber();
		String remoteInfo = "";

		while (true) {

			try {

				startWathcDogTimer();

				String psqlClientNamePath = dbProperties.getSqlClientAppPath();
				String psqlClientName = dbProperties.getSqlClientAppName().toString();

				String userName = dbConnectionProfile.getUserName(); // "postgres"; // Connection profile dan alacak.
				String password = dbConnectionProfile.getUserPassword(); // "ad0215"; // Connection profile dan alacak.
				String dbName = dbProperties.getDbName(); // "Carre"; // Connection profile dan alacak.
				
				if(ipAddress != null && !ipAddress.equals("") && port != 0) {
					remoteInfo = " -h " + ipAddress + " -p " + port;
				}
				
				String sqlSentence = jobProperties.getBaseJobInfos().getJobTypeDetails().getSpecialParameters().getDbJobDefinition().getFreeSQLProperties().getSqlSentence();

				psqlClientName = psqlClientName + remoteInfo + " -U " + userName + " -d " + dbName + " -c " + sqlSentence;

				insertNewLiveStateInfo(StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

				Map<String, String> env = new HashMap<String, String>();
				env.put("PGPASSWORD", password);

				startShellProcess(psqlClientNamePath, psqlClientName, env, this.getClass().getName(), myLogger);

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
