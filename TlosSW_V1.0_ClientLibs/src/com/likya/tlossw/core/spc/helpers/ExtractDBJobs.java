package com.likya.tlossw.core.spc.helpers;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbTypeDocument.DbType;
import com.likya.tlos.model.xmlbeans.dbjob.DbAccessMethodDocument.DbAccessMethod;
import com.likya.tlos.model.xmlbeans.dbjob.DbJobDefinitionDocument.DbJobDefinition;
import com.likya.tlossw.core.spc.jobs.JDBCOracleSQLSentenceExecuter;
import com.likya.tlossw.core.spc.jobs.Job;
import com.likya.tlossw.core.spc.jobs.OracleSQLScriptExecuter;
import com.likya.tlossw.core.spc.jobs.OracleSQLStoredProcedureExecuter;
import com.likya.tlossw.core.spc.jobs.PostgreSQLScriptExecuter;
import com.likya.tlossw.core.spc.jobs.JDBCPostgreSQLSentenceExecuter;
import com.likya.tlossw.core.spc.jobs.PostgreSQLStoredProcedureExecuter;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.TypeUtils;

public class ExtractDBJobs {

	private static Logger myLogger = Logger.getLogger(ExtractDBJobs.class);

	public static void evaluate(GlobalRegistry globalRegistry, DbProperties dbProperties, JobRuntimeProperties jobRuntimeProperties, Job myJob, Logger gobalLogger) {

		DbJobDefinition dbJobDefinition = TypeUtils.resolveDbJobDefinition(jobRuntimeProperties.getJobProperties());

		int dbType = dbProperties.getDbType().intValue();

		switch (dbType) {

		case DbType.INT_ORACLE:

			int dbAccessMethod = dbJobDefinition.getDbAccessMethod().intValue();

			if (dbJobDefinition.getFreeSQLProperties() != null) {

				switch (dbAccessMethod) {

				case DbAccessMethod.INT_JDBC:
					myJob = new JDBCOracleSQLSentenceExecuter(globalRegistry, gobalLogger, jobRuntimeProperties);
					break;

				default:
					myLogger.error(" Access method " + DbAccessMethod.Enum.forInt(dbAccessMethod) + " for " + DbType.Enum.forInt(dbType) + " is not defined !");
				}

			} else if (dbJobDefinition.getScriptProperties() != null) {

				switch (dbAccessMethod) {

				case DbAccessMethod.INT_JDBC:
					myLogger.error(" Access method " + DbAccessMethod.Enum.forInt(dbAccessMethod) + " for " + DbType.Enum.forInt(dbType) + " is not defined !");
					break;

				default:
					myJob = new OracleSQLScriptExecuter(globalRegistry, gobalLogger, jobRuntimeProperties);
				}

			} else if (dbJobDefinition.getStoreProcedureProperties() != null) {
				switch (dbAccessMethod) {

				case DbAccessMethod.INT_JDBC:
					myLogger.error(" Access method " + DbAccessMethod.Enum.forInt(dbAccessMethod) + " for " + DbType.Enum.forInt(dbType) + " is not defined !");
					break;

				default:
					myJob = new OracleSQLStoredProcedureExecuter(globalRegistry, gobalLogger, jobRuntimeProperties);
				}
			}

			break;

		case DbType.INT_POSTGRE_SQL:
			
			dbAccessMethod = dbJobDefinition.getDbAccessMethod().intValue();
			
			if (dbJobDefinition.getFreeSQLProperties() != null) {

				switch (dbAccessMethod) {

				case DbAccessMethod.INT_JDBC:
					myLogger.error(" Access method " + DbAccessMethod.Enum.forInt(dbAccessMethod) + " for " + DbType.Enum.forInt(dbType) + " is not defined !");
					break;

				default:
					myJob = new JDBCPostgreSQLSentenceExecuter(globalRegistry, gobalLogger, jobRuntimeProperties);
				}

			} else if (dbJobDefinition.getScriptProperties() != null) {
				
				switch (dbAccessMethod) {

				case DbAccessMethod.INT_JDBC:
					myLogger.error(" Access method " + DbAccessMethod.Enum.forInt(dbAccessMethod) + " for " + DbType.Enum.forInt(dbType) + " is not defined !");
					break;

				default:
					myJob = new PostgreSQLScriptExecuter(globalRegistry, gobalLogger, jobRuntimeProperties);
				}

			} else if (dbJobDefinition.getStoreProcedureProperties() != null) {
				
				switch (dbAccessMethod) {

				case DbAccessMethod.INT_JDBC:
					myLogger.error(" Access method " + DbAccessMethod.Enum.forInt(dbAccessMethod) + " for " + DbType.Enum.forInt(dbType) + " is not defined !");
					break;

				default:
					myJob = new PostgreSQLStoredProcedureExecuter(globalRegistry, gobalLogger, jobRuntimeProperties);
				}
			}

			break;

		case DbType.INT_DB_2:
			// TODO Gelistirme yapilacak.
			break;

		case DbType.INT_FIREBIRD:
			// TODO Gelistirme yapilacak.
			break;

		case DbType.INT_INFORMIX:
			// TODO Gelistirme yapilacak.
			break;

		case DbType.INT_MY_SQL:
			// TODO Gelistirme yapilacak.
			break;

		case DbType.INT_SAS:
			// TODO Gelistirme yapilacak.
			break;

		case DbType.INT_SQL_SERVER:
			// TODO Gelistirme yapilacak.
			break;

		case DbType.INT_SYBASE:
			// TODO Gelistirme yapilacak.
			break;
		// TODO Gelistirme yapilacak.
		case DbType.INT_TERADATA:
			// TODO Gelistirme yapilacak.
			break;

		default:
			break;
		}

	}

}
