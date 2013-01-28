package com.likya.tlossw.model;

import java.io.Serializable;

import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;

public class DBAccessInfoTypeClient implements Serializable {

	private static final long serialVersionUID = -3132594939440065400L;

	private DbConnectionProfile dbConnectionProfile;
	
	private String connectionName;
	private String dbType;
	
	public String getConnectionName() {
		return connectionName;
	}
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public DbConnectionProfile getDbConnectionProfile() {
		return dbConnectionProfile;
	}
	public void setDbConnectionProfile(DbConnectionProfile dbConnectionProfile) {
		this.dbConnectionProfile = dbConnectionProfile;
	}
}
