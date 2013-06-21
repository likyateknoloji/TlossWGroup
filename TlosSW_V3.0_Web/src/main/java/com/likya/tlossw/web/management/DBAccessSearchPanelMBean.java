package com.likya.tlossw.web.management;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.common.ActiveDocument.Active;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DeployedDocument.Deployed;
import com.likya.tlos.model.xmlbeans.dbconnections.JdbcConnectionPoolParamsDocument.JdbcConnectionPoolParams;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.model.DBAccessInfoTypeClient;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "dbAccessSearchPanelMBean")
@ViewScoped
public class DBAccessSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 5578347263656801312L;

	private DbConnectionProfile dbConnectionProfile;
	private DBAccessInfoTypeClient dbAccessInfoTypeClient;

	private String dbConnectionName = null;
	private Collection<SelectItem> dbConnectionNameList = null;

	private String deployed;
	private String active;

	private ArrayList<DBAccessInfoTypeClient> searchDBAccessProfileList;
	private transient DataTable searchDBAccessProfileTable;
	private DBAccessInfoTypeClient selectedRow;

	private List<Person> filteredDBAccessList;

	public void dispose() {
		dbConnectionProfile = null;
		dbAccessInfoTypeClient = null;
		dbConnectionNameList = null;
	}

	@PostConstruct
	public void init() {
		dbConnectionProfile = DbConnectionProfile.Factory.newInstance();
		searchDBAccessProfileList = new ArrayList<DBAccessInfoTypeClient>();
		fillDBConnectionNameList();
	}

	public void fillDBConnectionNameList() {
		dbConnectionNameList = new ArrayList<SelectItem>();

		for (DbProperties dbProperties : getDbOperations().getDBConnections()) {
			SelectItem item = new SelectItem();
			item.setValue(dbProperties.getID());
			item.setLabel(dbProperties.getConnectionName());

			dbConnectionNameList.add(item);
		}
	}

	public String getDBAccessXML() {
		QName qName = DbConnectionProfile.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String dbProfileXML = dbConnectionProfile.xmlText(xmlOptions);

		return dbProfileXML;
	}

	public void resetDBAccessPropertiesAction() {
		dbConnectionProfile = DbConnectionProfile.Factory.newInstance();

		JdbcConnectionPoolParams jdbcConnectionPoolParams = JdbcConnectionPoolParams.Factory.newInstance();
		dbConnectionProfile.setJdbcConnectionPoolParams(jdbcConnectionPoolParams);

		deployed = "";
		active = "";
		searchDBAccessProfileList = null;
		dbConnectionName = "";
	}

	public void searchDBAccessAction(ActionEvent e) {
		if (!dbConnectionName.equals("")) {
			dbConnectionProfile.setDbDefinitionId(new BigInteger(dbConnectionName));
		} else {
			dbConnectionProfile.setDbDefinitionId(null);
		}

		if (!deployed.equals("")) {
			dbConnectionProfile.setDeployed(Deployed.Enum.forString(deployed));
		} else {
			dbConnectionProfile.setDeployed(null);
		}

		if (!active.equals("")) {
			dbConnectionProfile.setActive(Active.Enum.forString(active));
		} else {
			dbConnectionProfile.setActive(null);
		}

		searchDBAccessProfileList = getDbOperations().searchDBAccessProfile(getDBAccessXML());
		if (searchDBAccessProfileList == null || searchDBAccessProfileList.size() == 0) {
			addMessage("searchDBAccess", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}
	}

	public void deleteDBAccessAction(ActionEvent e) {
		// dbAccessInfoTypeClient = (DBAccessInfoTypeClient) searchDBAccessProfileTable.getRowData();
		dbAccessInfoTypeClient = selectedRow;

		dbConnectionProfile = dbAccessInfoTypeClient.getDbConnectionProfile();

		if (getDbOperations().deleteDBAccessProfile(getDBAccessXML())) {
			searchDBAccessProfileList.remove(dbAccessInfoTypeClient);
			dbAccessInfoTypeClient = new DBAccessInfoTypeClient();

			addMessage("searchDBAccess", FacesMessage.SEVERITY_INFO, "tlos.success.dbAccessDef.delete", null);
		} else {
			addMessage("searchDBAccess", FacesMessage.SEVERITY_ERROR, "tlos.error.dbConnection.delete", null);
		}
	}

	public void deployDBAccessAction(ActionEvent e) {
		// TODO deploy yapilacak
	}

	public void undeployDBAccessAction(ActionEvent e) {
		// TODO undeploy yapilacak
	}

	public DbConnectionProfile getDbConnectionProfile() {
		return dbConnectionProfile;
	}

	public void setDbConnectionProfile(DbConnectionProfile dbConnectionProfile) {
		this.dbConnectionProfile = dbConnectionProfile;
	}

	public DBAccessInfoTypeClient getDbAccessInfoTypeClient() {
		return dbAccessInfoTypeClient;
	}

	public void setDbAccessInfoTypeClient(DBAccessInfoTypeClient dbAccessInfoTypeClient) {
		this.dbAccessInfoTypeClient = dbAccessInfoTypeClient;
	}

	public String getDbConnectionName() {
		return dbConnectionName;
	}

	public void setDbConnectionName(String dbConnectionName) {
		this.dbConnectionName = dbConnectionName;
	}

	public Collection<SelectItem> getDbConnectionNameList() {
		return dbConnectionNameList;
	}

	public void setDbConnectionNameList(Collection<SelectItem> dbConnectionNameList) {
		this.dbConnectionNameList = dbConnectionNameList;
	}

	public String getDeployed() {
		return deployed;
	}

	public void setDeployed(String deployed) {
		this.deployed = deployed;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public ArrayList<DBAccessInfoTypeClient> getSearchDBAccessProfileList() {
		return searchDBAccessProfileList;
	}

	public void setSearchDBAccessProfileList(ArrayList<DBAccessInfoTypeClient> searchDBAccessProfileList) {
		this.searchDBAccessProfileList = searchDBAccessProfileList;
	}

	public DataTable getSearchDBAccessProfileTable() {
		return searchDBAccessProfileTable;
	}

	public void setSearchDBAccessProfileTable(DataTable searchDBAccessProfileTable) {
		this.searchDBAccessProfileTable = searchDBAccessProfileTable;
	}

	public List<Person> getFilteredDBAccessList() {
		return filteredDBAccessList;
	}

	public void setFilteredDBAccessList(List<Person> filteredDBAccessList) {
		this.filteredDBAccessList = filteredDBAccessList;
	}

	public DBAccessInfoTypeClient getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(DBAccessInfoTypeClient selectedRow) {
		this.selectedRow = selectedRow;
	}

}
