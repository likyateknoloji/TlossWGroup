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
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likya.tlossw.utils.ParsingUtils;

public class PostgreSQLScriptExecuter extends SQLScriptExecuter {

	private static final long serialVersionUID = -1947157346281291622L;

	Logger myLogger = Logger.getLogger(this.getClass());
	transient Logger globalLogger;

	private boolean retryFlag = true;

	transient protected Process process;

	public PostgreSQLScriptExecuter(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
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

				String sqlScriptFileName = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getDbJobDefinition().getScriptProperties().getSqlScriptFileName();
				String sqlScriptFilePath = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getDbJobDefinition().getScriptProperties().getSqlScriptFilePath();

				psqlClientName = psqlClientName + remoteInfo + " -U " + userName + " -d " + dbName + " -f " + ParsingUtils.getConcatenatedPathAndFileName(sqlScriptFilePath, sqlScriptFileName);

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);

				sendStatusChangeInfo();

				
				Map<String, String> envVars = new HashMap<String, String>();
				envVars.put("PGPASSWORD", password);

				startShellProcess(psqlClientNamePath, psqlClientName, envVars, this.getClass().getName(), myLogger);

				// startShellProcess() metodu icinde isin basarili ya da basarisiz olma durumuna gore zaten state bilgisi giriliyor
				// asagidaki kisimdan dolayi basarisiz biten is bile en sonunda basarili gibi gorunuyor
				// onun icin kaldirdim
				// LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_SUCCESS);

			} catch (Exception err) {
				handleException(err, myLogger);
			}

			sendStatusChangeInfo();

			if (processJobResult(retryFlag, myLogger)) {
				retryFlag = false;
				continue;
			}

			break;
		}

		cleanUp(process, myLogger);

	}
}
