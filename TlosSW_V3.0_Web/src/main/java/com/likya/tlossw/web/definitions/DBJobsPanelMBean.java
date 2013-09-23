package com.likya.tlossw.web.definitions;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbAccessMethodDocument.DbAccessMethod;
import com.likya.tlos.model.xmlbeans.dbjob.DbConnectionPropertiesDocument.DbConnectionProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbJobDefinitionDocument.DbJobDefinition;
import com.likya.tlos.model.xmlbeans.dbjob.DbJobTypeDocument.DbJobType;
import com.likya.tlos.model.xmlbeans.dbjob.FreeSQLPropertiesDocument.FreeSQLProperties;
import com.likya.tlos.model.xmlbeans.dbjob.ScriptPropertiesDocument.ScriptProperties;
import com.likya.tlos.model.xmlbeans.dbjob.StoreProcedurePropertiesDocument.StoreProcedureProperties;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "dbJobsPanelMBean")
@ViewScoped
public class DBJobsPanelMBean extends JobBasePanelBean implements Serializable {

	private static final Logger logger = Logger.getLogger(DBJobsPanelMBean.class);

	private static final long serialVersionUID = -4215231083488743582L;

	private DbJobDefinition dbJobDefinition;

	private Collection<SelectItem> dbDefinitionList = null;
	private String selectedDbDefinition;

	private Collection<SelectItem> dbJobTypeList = null;
	private String selectedDbJobType;

	private ArrayList<DbConnectionProfile> dbAccessProfileList = new ArrayList<DbConnectionProfile>();

	private String sqlScriptFileName;
	private String sqlScriptFilePath;

	private String sqlSentence;

	private String sqlStoredProcedureSchemaName;
	private String sqlStoredProcedurePackageName;
	private String sqlStoredProcedureName;

	public void dispose() {

	}

	public void init() {
		initJobPanel();

		fillDBJobTypeList();

		dbAccessProfileList = getDbOperations().getDBProfiles();
		ArrayList<DbProperties> dbConnections = getDbOperations().getDBConnections();
		setDbDefinitionList(WebInputUtils.fillDbDefinitionList(dbAccessProfileList, dbConnections));
	}

	public void fillTabs() {
		fillJobPanel();
		resetDBJobProperties();
		fillDBJobProperties();
	}

	private void resetDBJobProperties() {
		selectedDbDefinition = "";
		selectedDbJobType = "";
		sqlScriptFileName = "";
		sqlScriptFilePath = "";
		sqlSentence = "";
		sqlStoredProcedureSchemaName = "";
		sqlStoredProcedurePackageName = "";
		sqlStoredProcedureName = "";
	}

	private void fillDBJobProperties() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
		if (jobTypeDetails.getSpecialParameters() != null && jobTypeDetails.getSpecialParameters().getDbJobDefinition() != null) {
			dbJobDefinition = jobTypeDetails.getSpecialParameters().getDbJobDefinition();

			DbConnectionProperties dbConnectionProperties = dbJobDefinition.getDbConnectionProperties();

			selectedDbDefinition = dbConnectionProperties.getDbUserId() + "";
			selectedDbJobType = dbJobDefinition.getDbJobType().toString();

			if (selectedDbJobType.equals(DbJobType.FREE_SQL.toString())) {
				sqlSentence = dbJobDefinition.getFreeSQLProperties().getSqlSentence();

			} else if (selectedDbJobType.equals(DbJobType.SCRIPT.toString())) {
				ScriptProperties scriptProperties = dbJobDefinition.getScriptProperties();
				sqlScriptFileName = scriptProperties.getSqlScriptFileName();
				sqlScriptFilePath = scriptProperties.getSqlScriptFilePath();

			} else if (selectedDbJobType.equals(DbJobType.STORE_PROCEDURE.toString())) {
				StoreProcedureProperties storeProcedureProperties = dbJobDefinition.getStoreProcedureProperties();
				sqlStoredProcedureName = storeProcedureProperties.getSqlStoredProcedureName();
				sqlStoredProcedurePackageName = storeProcedureProperties.getSqlStoredProcedurePackageName();
				sqlStoredProcedureSchemaName = storeProcedureProperties.getSqlStoredProcedureSchemaName();
			}
		}
	}

	public void fillJobPropertyDetails() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
		SpecialParameters specialParameters;

		dbJobDefinition = DbJobDefinition.Factory.newInstance();

		// periyodik job alanlari doldurulurken bu alan olusturuldugu icin bu kontrol yapiliyor
		// ayrica dbAccessMethod alani set ediliyor
		if (jobTypeDetails.getSpecialParameters() == null) {
			specialParameters = SpecialParameters.Factory.newInstance();
			dbJobDefinition.setDbAccessMethod(DbAccessMethod.NATIVE);
		} else {
			specialParameters = jobTypeDetails.getSpecialParameters();
			dbJobDefinition.setDbAccessMethod(specialParameters.getDbJobDefinition().getDbAccessMethod());
		}

		DbConnectionProperties dbConnectionProperties = DbConnectionProperties.Factory.newInstance();
		dbConnectionProperties.setDbUserId(new BigInteger(selectedDbDefinition));

		for (int i = 0; i < dbAccessProfileList.size(); i++) {
			String id = dbAccessProfileList.get(i).getID().toString();

			if (id.equals(selectedDbDefinition)) {
				dbConnectionProperties.setDbPropertiesId(dbAccessProfileList.get(i).getDbDefinitionId());
				break;
			}
		}

		dbJobDefinition.setDbConnectionProperties(dbConnectionProperties);
		dbJobDefinition.setDbJobType(DbJobType.Enum.forString(selectedDbJobType));

		if (selectedDbJobType.equals(DbJobType.FREE_SQL.toString())) {
			FreeSQLProperties freeSQLProperties = FreeSQLProperties.Factory.newInstance();
			freeSQLProperties.setSqlSentence(sqlSentence);

			dbJobDefinition.setFreeSQLProperties(freeSQLProperties);

		} else if (selectedDbJobType.equals(DbJobType.SCRIPT.toString())) {
			ScriptProperties scriptProperties = ScriptProperties.Factory.newInstance();
			scriptProperties.setSqlScriptFileName(sqlScriptFileName);
			scriptProperties.setSqlScriptFilePath(sqlScriptFilePath);

			dbJobDefinition.setScriptProperties(scriptProperties);

		} else if (selectedDbJobType.equals(DbJobType.STORE_PROCEDURE.toString())) {
			StoreProcedureProperties storeProcedureProperties = StoreProcedureProperties.Factory.newInstance();
			storeProcedureProperties.setSqlStoredProcedureName(sqlStoredProcedureName);
			storeProcedureProperties.setSqlStoredProcedurePackageName(sqlStoredProcedurePackageName);
			storeProcedureProperties.setSqlStoredProcedureSchemaName(sqlStoredProcedureSchemaName);

			dbJobDefinition.setStoreProcedureProperties(storeProcedureProperties);
		}

		specialParameters.setDbJobDefinition(dbJobDefinition);
		jobTypeDetails.setSpecialParameters(specialParameters);
	}

	private void fillDBJobTypeList() {
		if (dbJobTypeList == null) {
			dbJobTypeList = WebInputUtils.fillDBJobTypeList();
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public Collection<SelectItem> getDbDefinitionList() {
		return dbDefinitionList;
	}

	public void setDbDefinitionList(Collection<SelectItem> dbDefinitionList) {
		this.dbDefinitionList = dbDefinitionList;
	}

	public String getSelectedDbDefinition() {
		return selectedDbDefinition;
	}

	public void setSelectedDbDefinition(String selectedDbDefinition) {
		this.selectedDbDefinition = selectedDbDefinition;
	}

	public Collection<SelectItem> getDbJobTypeList() {
		return dbJobTypeList;
	}

	public void setDbJobTypeList(Collection<SelectItem> dbJobTypeList) {
		this.dbJobTypeList = dbJobTypeList;
	}

	public String getSelectedDbJobType() {
		return selectedDbJobType;
	}

	public void setSelectedDbJobType(String selectedDbJobType) {
		this.selectedDbJobType = selectedDbJobType;
	}

	public String getSqlScriptFileName() {
		return sqlScriptFileName;
	}

	public void setSqlScriptFileName(String sqlScriptFileName) {
		this.sqlScriptFileName = sqlScriptFileName;
	}

	public String getSqlScriptFilePath() {
		return sqlScriptFilePath;
	}

	public void setSqlScriptFilePath(String sqlScriptFilePath) {
		this.sqlScriptFilePath = sqlScriptFilePath;
	}

	public String getSqlSentence() {
		return sqlSentence;
	}

	public void setSqlSentence(String sqlSentence) {
		this.sqlSentence = sqlSentence;
	}

	public String getSqlStoredProcedureSchemaName() {
		return sqlStoredProcedureSchemaName;
	}

	public void setSqlStoredProcedureSchemaName(String sqlStoredProcedureSchemaName) {
		this.sqlStoredProcedureSchemaName = sqlStoredProcedureSchemaName;
	}

	public String getSqlStoredProcedurePackageName() {
		return sqlStoredProcedurePackageName;
	}

	public void setSqlStoredProcedurePackageName(String sqlStoredProcedurePackageName) {
		this.sqlStoredProcedurePackageName = sqlStoredProcedurePackageName;
	}

	public String getSqlStoredProcedureName() {
		return sqlStoredProcedureName;
	}

	public void setSqlStoredProcedureName(String sqlStoredProcedureName) {
		this.sqlStoredProcedureName = sqlStoredProcedureName;
	}

	public DbJobDefinition getDbJobDefinition() {
		return dbJobDefinition;
	}

	public void setDbJobDefinition(DbJobDefinition dbJobDefinition) {
		this.dbJobDefinition = dbJobDefinition;
	}
}
