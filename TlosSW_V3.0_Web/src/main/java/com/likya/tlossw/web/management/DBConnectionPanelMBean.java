package com.likya.tlossw.web.management;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbTypeDocument.DbType;
import com.likya.tlos.model.xmlbeans.dbconnections.SqlClientAppNameDocument.SqlClientAppName;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "dbConnectionPanelMBean")
@RequestScoped
public class DBConnectionPanelMBean extends TlosSWBaseBean implements Serializable {

	@ManagedProperty(value = "#{param.selectedDBConnectionID}")
	private String selectedDBConnectionID;

	@ManagedProperty(value = "#{param.insertCheck}")
	private String insertCheck;

	@ManagedProperty(value = "#{param.iCheck}")
	private String iCheck;

	private static final long serialVersionUID = 1L;

	private DbProperties dbProperties;

	private String dbType = null;
	private Collection<SelectItem> dbTypeList = null;

	private String portNumber;

	private String sqlClientAppName = null;
	private Collection<SelectItem> sqlClientAppNameList = null;

	private boolean insertButton;

	@PostConstruct
	public void init() {

		dbProperties = DbProperties.Factory.newInstance();
		fillDBTypeList();
		fillSqlClientAppNameList();

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {

			if (insertCheck.equals("update")) {
				insertButton = false;

				dbType = "";
				sqlClientAppName = "";

				dbProperties = getDbOperations().searchDBByID(selectedDBConnectionID);

				if (dbProperties != null) {
					dbType = dbProperties.getDbType().toString();
					portNumber = dbProperties.getListenerPortNumber() + "";
					sqlClientAppName = dbProperties.getSqlClientAppName().toString();
				}

			} else {
				insertButton = true;
			}
		}
	}

	public void fillDBTypeList() {
		String dbTypeValue = null;
		dbTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < DbType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			dbTypeValue = DbType.Enum.forInt(i + 1).toString();
			item.setValue(dbTypeValue);
			item.setLabel(dbTypeValue);
			dbTypeList.add(item);
		}
	}

	public void fillSqlClientAppNameList() {
		sqlClientAppNameList = new ArrayList<SelectItem>();

		String appName = null;

		for (int i = 0; i < SqlClientAppName.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			appName = SqlClientAppName.Enum.forInt(i + 1).toString();
			item.setValue(appName);
			item.setLabel(appName);
			sqlClientAppNameList.add(item);
		}
	}

	public void resetDBConnectionAction() {
		dbProperties = DbProperties.Factory.newInstance();

		dbType = "";
		sqlClientAppName = "";
		portNumber = null;
	}

	public String getDBPropertiesXML() {
		QName qName = DbProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String dbPropertiesXML = dbProperties.xmlText(xmlOptions);

		return dbPropertiesXML;
	}

	public void updateDBConnectionAction(ActionEvent e) {
		fillDBProperties();

		// baglanti ismi tekil olmali
		if (!getDbOperations().checkDBConnectionName(getDBPropertiesXML())) {
			addMessage("insertDBConnection", FacesMessage.SEVERITY_INFO, "tlos.error.dbConnection.dublicateName", null);
			return;
		}

		if (getDbOperations().updateDBConnection(getDBPropertiesXML())) {
			addMessage("insertDBConnection", FacesMessage.SEVERITY_INFO, "tlos.success.dbConnectionDef.update", null);
		} else {
			addMessage("insertDBConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.dbConnection.update", null);
		}

	}

	public void insertDBConnectionAction(ActionEvent e) {
		fillDBProperties();

		if (setDBConnectionID()) {

			// baglanti ismi tekil olmali
			if (!getDbOperations().checkDBConnectionName(getDBPropertiesXML())) {
				addMessage("insertDBConnection", FacesMessage.SEVERITY_INFO, "tlos.error.dbConnection.dublicateName", null);
				return;
			}

			if (getDbOperations().insertDBConnection(getDBPropertiesXML())) {
				addMessage("insertDBConnection", FacesMessage.SEVERITY_INFO, "tlos.success.dbConnectionDef.insert", null);
				resetDBConnectionAction();
			} else {
				addMessage("insertDBConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.dbConnection.insert", null);
			}
		}

	}

	private void fillDBProperties() {
		if (portNumber != null && !portNumber.equals("")) {
			dbProperties.setListenerPortNumber(new Integer(portNumber));
		}

		if (!dbType.equals("")) {
			dbProperties.setDbType(DbType.Enum.forString(dbType));
		} else {
			dbProperties.setDbType(null);
		}

		if (!sqlClientAppName.equals("")) {
			dbProperties.setSqlClientAppName(SqlClientAppName.Enum.forString(sqlClientAppName));
		} else {
			dbProperties.setSqlClientAppName(null);
		}
	}

	// veri tabaninda kayitli siradaki id degerini set ediyor
	public boolean setDBConnectionID() {
		int dbConnectionId = getDbOperations().getNextId(CommonConstantDefinitions.DBCONNECTION_ID);

		if (dbConnectionId < 0) {
			addMessage("insertDBConnection", FacesMessage.SEVERITY_ERROR, "tlos.info.dbConnection.getId", null);
			return false;
		}
		dbProperties.setID(new BigInteger(dbConnectionId + ""));
		return true;
	}

	public boolean isInsertButton() {
		return insertButton;
	}

	public void setInsertButton(boolean insertButton) {
		this.insertButton = insertButton;
	}

	public String getInsertCheck() {
		return insertCheck;
	}

	public void setInsertCheck(String insertCheck) {
		this.insertCheck = insertCheck;
	}

	public String getiCheck() {
		return iCheck;
	}

	public void setiCheck(String iCheck) {
		this.iCheck = iCheck;
	}

	public DbProperties getDbProperties() {
		return dbProperties;
	}

	public void setDbProperties(DbProperties dbProperties) {
		this.dbProperties = dbProperties;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public Collection<SelectItem> getDbTypeList() {
		return dbTypeList;
	}

	public void setDbTypeList(Collection<SelectItem> dbTypeList) {
		this.dbTypeList = dbTypeList;
	}

	public String getSqlClientAppName() {
		return sqlClientAppName;
	}

	public void setSqlClientAppName(String sqlClientAppName) {
		this.sqlClientAppName = sqlClientAppName;
	}

	public Collection<SelectItem> getSqlClientAppNameList() {
		return sqlClientAppNameList;
	}

	public void setSqlClientAppNameList(Collection<SelectItem> sqlClientAppNameList) {
		this.sqlClientAppNameList = sqlClientAppNameList;
	}

	public String getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	public String getSelectedDBConnectionID() {
		return selectedDBConnectionID;
	}

	public void setSelectedDBConnectionID(String selectedDBConnectionID) {
		this.selectedDBConnectionID = selectedDBConnectionID;
	}

}
