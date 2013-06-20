package com.likya.tlossw.core.spc.jobs;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.log4j.Logger;

import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;

public abstract class JDBCSQLSentenceExecuter extends DbJob {

	private static final long serialVersionUID = 1L;

	public final static String DB_RESULT = "dbResult";
	
	public JDBCSQLSentenceExecuter(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public String fetchResultSet(ResultSet resultSet) throws Exception {

		StringBuffer resultData = new StringBuffer();
		// Get the metadata
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		// Print the column labels
		for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
			// System.out.print(resultSetMetaData.getColumnLabel(i) + " ");
			resultData.append(resultSetMetaData.getColumnLabel(i) + ", ");
		}
		
		resultData.deleteCharAt(resultData.lastIndexOf(","));
		resultData.append("\n");
		
		// System.out.println();

		// Loop through the result set
		while (resultSet.next()) {
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				// System.out.print(resultSet.getString(i) + " ");
				resultData.append(resultSet.getString(i) + ", ");
			}
			resultData.deleteCharAt(resultData.lastIndexOf(","));
			resultData.append("\n");
			// System.out.println();
		}

		return resultData.toString();

	}

}
