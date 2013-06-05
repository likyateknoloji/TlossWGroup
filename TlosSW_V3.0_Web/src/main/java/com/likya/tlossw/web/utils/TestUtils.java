package com.likya.tlossw.web.utils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbTypeDocument.DbType;
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "testUtils")
@ViewScoped
public class TestUtils implements Serializable {

	private static final long serialVersionUID = 5922867836144571771L;

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	public boolean testDBConnection(DbConnectionProfile dbConnectionProfile) {
		String dbPropertiesID = dbConnectionProfile.getDbDefinitionId() + "";

		DbProperties dbProperties = null;

		dbProperties = dbOperations.searchDBByID(dbPropertiesID);

		if (dbProperties.getDbType().equals(DbType.ORACLE)) {
			return initOracleDbConnection(dbProperties, dbConnectionProfile);

		} else if (dbProperties.getDbType().equals(DbType.POSTGRE_SQL)) {
			return initDbConnection(dbProperties, dbConnectionProfile);
		}

		return false;
	}

	private static boolean initDbConnection(DbProperties dbProperties, DbConnectionProfile dbConnectionProfile) {
		boolean result = false;

		String url = dbProperties.getDbUrl();
		url += dbProperties.getHostName() + "/" + dbProperties.getDbName();
		Properties props = new Properties();

		String userName = dbConnectionProfile.getUserName();
		String password = dbConnectionProfile.getUserPassword();

		props.setProperty("user", userName);
		props.setProperty("password", password);

		Connection connection;
		try {
			connection = DriverManager.getConnection(url, props);

			result = true;

			connection.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return result;
	}

	private static boolean initOracleDbConnection(DbProperties dbProperties, DbConnectionProfile dbConnectionProfile) {
		boolean result = false;

		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String url = dbProperties.getDbUrl();
		url += "@//" + dbProperties.getHostName() + ":" + dbProperties.getListenerPortNumber() + "/" + dbProperties.getDbName();

		Properties props = new Properties();

		String userName = dbConnectionProfile.getUserName();
		String password = dbConnectionProfile.getUserPassword();

		props.setProperty("user", userName);
		props.setProperty("password", password);

		Connection connection;
		try {
			connection = DriverManager.getConnection(url, props);

			result = true;

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}
}
