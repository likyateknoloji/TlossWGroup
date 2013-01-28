package com.likya.tlossw.core.spc.jobs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;

public abstract class DbJob extends Job {

	private static final long serialVersionUID = 1906790210440338404L;

	private Connection connection;
	private Statement statement;

	public DbJob(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public void initDbConnection(DbProperties dbProperties, DbConnectionProfile dbConnectionProfile) throws SQLException {

		String url = dbProperties.getDbUrl();
		url += dbProperties.getHostName() + "/" + dbProperties.getDbName();
		Properties props = new Properties();

		String userName = dbConnectionProfile.getUserName(); // "postgres"; // Connection profile dan alacak.
		String password = dbConnectionProfile.getUserPassword(); // "ad0215"; // Connection profile dan alacak.

		props.setProperty("user", userName);
		props.setProperty("password", password);

		// props.setProperty("user", dbProperties.getUserName());
		// props.setProperty("password", dbProperties.getUserPassword());
		try {
			connection = DriverManager.getConnection(url, props);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		statement = connection.createStatement();

	}

	public void initOracleDbConnection(DbProperties dbProperties, DbConnectionProfile dbConnectionProfile) throws SQLException, ClassNotFoundException {

		Class.forName("oracle.jdbc.OracleDriver");

		String url = dbProperties.getDbUrl();
		url += "@//" + dbProperties.getHostName() + ":" + dbProperties.getFtpPortNumber() + "/" + dbProperties.getDbName();

		Properties props = new Properties();

		String userName = dbConnectionProfile.getUserName(); // "postgres"; // Connection profile dan alacak.
		String password = dbConnectionProfile.getUserPassword(); // "ad0215"; // Connection profile dan alacak.

		props.setProperty("user", userName);
		props.setProperty("password", password);

		// props.setProperty("user", dbProperties.getUserName());
		// props.setProperty("password", dbProperties.getUserPassword());

		connection = DriverManager.getConnection(url, props);

		statement = connection.createStatement();

	}

	@Override
	protected void finalize() throws Throwable {

		statement.close();
		connection.close();

		super.finalize();
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public Connection getConnection() {
		return connection;
	}

}
