package com.likya.tlossw.core.spc.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.ParamList;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.LiveStateInfoUtils;

public class JDBCPostgreSQLSentenceExecuter extends JDBCSQLSentenceExecuter {

	private static final long serialVersionUID = -1947157346281291622L;

	Logger myLogger = Logger.getLogger(this.getClass());
	transient Logger globalLogger;

	private boolean retryFlag = true;

	transient protected Process process;

	public JDBCPostgreSQLSentenceExecuter(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public void run() {

		initStartUp(myLogger);

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		DbProperties dbProperties = getJobRuntimeProperties().getDbProperties();
		DbConnectionProfile dbConnectionProfile = getJobRuntimeProperties().getDbConnectionProfile();
		ArrayList<ParamList> myParamList = new ArrayList<ParamList>();

		while (true) {

			try {

				startWathcDogTimer();

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_RUNNING, SubstateName.INT_ON_RESOURCE, StatusName.INT_TIME_IN);
				sendStatusChangeInfo();

				initDbConnection(dbProperties, dbConnectionProfile);

				// String sqlStoredProcedureSchemaName = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getInParam().getParameterArray(0).getValueString();
				// qString="select * from public.test_m()";
				String sqlSentence = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getDbJobDefinition().getFreeSQLProperties().getSqlSentence();

				Statement statement = getStatement();

				ResultSet resultSet = statement.executeQuery(sqlSentence);
				
				System.out.println("");
				System.out.println("*****************************************");
				System.out.println("Query'nizin sonucu ...");

				String resultData = fetchResultSet(resultSet);
				
				ParamList thisParam = new ParamList(DB_RESULT, "STRING", "VARIABLE", resultData);
				myParamList.add(thisParam);

				System.out.println("*****************************************");
				System.out.println("");
				
				statement.close();

				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_SUCCESS);
				sendStatusChangeInfo();

			} catch (Exception err) {
				LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, StatusName.INT_FAILED);
				sendStatusChangeInfo();

				try {
					if(getStatement() != null) {
						getStatement().close();
					}
					if(getStatement() != null) {
						getConnection().close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				handleException(err, myLogger);
			}

			if (processJobResult(retryFlag, myLogger, myParamList)) {
				retryFlag = false;
				continue;
			}

			break;
		}

		cleanUp(process, myLogger);

	}
}
