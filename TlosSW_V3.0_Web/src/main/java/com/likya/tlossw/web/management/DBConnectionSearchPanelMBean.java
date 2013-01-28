package com.likya.tlossw.web.management;

import java.io.Serializable;
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

import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbTypeDocument.DbType;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "dbConnectionSearchPanelMBean")
@ViewScoped
public class DBConnectionSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 1313328412118735289L;

	private DbProperties dbProperties;

	private ArrayList<DbProperties> searchDBList;
	private transient DataTable searchDBTable;

	private List<Person> filteredDBList;

	private String dbType = null;
	private Collection<SelectItem> dbTypeList = null;

	public void dispose() {
		dbProperties = null;
		dbTypeList = null;
		searchDBList = null;
	}

	@PostConstruct
	public void init() {
		dbProperties = DbProperties.Factory.newInstance();
		fillDBTypeList();
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

	public void resetDBConnectionAction() {
		dbProperties = DbProperties.Factory.newInstance();

		dbType = "";
		searchDBList = null;

	}

	public String getDBPropertiesXML() {
		QName qName = DbProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String dbPropertiesXML = dbProperties.xmlText(xmlOptions);

		return dbPropertiesXML;
	}

	public void searchDBConnectionAction(ActionEvent e) {
		if (!dbType.equals("")) {
			dbProperties.setDbType(DbType.Enum.forString(dbType));
		} else {
			dbProperties.setDbType(null);
		}

		searchDBList = getDbOperations().searchDBConnection(getDBPropertiesXML());
		if (searchDBList == null || searchDBList.size() == 0) {
			addMessage("searchDBConnection", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}
	}

	public void deleteDBConnectionAction(ActionEvent e) {
		dbProperties = (DbProperties) searchDBTable.getRowData();

		if (getDbOperations().deleteDBConnection(getDBPropertiesXML())) {
			searchDBList.remove(dbProperties);
			dbProperties = DbProperties.Factory.newInstance();
			addMessage("searchDBConnection", FacesMessage.SEVERITY_INFO, "tlos.success.dbConnectionDef.delete", null);
		} else {
			addMessage("searchDBConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.dbConnection.delete", null);
		}
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

	public DbProperties getDbProperties() {
		return dbProperties;
	}

	public void setDbProperties(DbProperties dbProperties) {
		this.dbProperties = dbProperties;
	}

	public ArrayList<DbProperties> getSearchDBList() {
		return searchDBList;
	}

	public void setSearchDBList(ArrayList<DbProperties> searchDBList) {
		this.searchDBList = searchDBList;
	}

	public DataTable getSearchDBTable() {
		return searchDBTable;
	}

	public void setSearchDBTable(DataTable searchDBTable) {
		this.searchDBTable = searchDBTable;
	}

	public List<Person> getFilteredDBList() {
		return filteredDBList;
	}

	public void setFilteredDBList(List<Person> filteredDBList) {
		this.filteredDBList = filteredDBList;
	}

}
