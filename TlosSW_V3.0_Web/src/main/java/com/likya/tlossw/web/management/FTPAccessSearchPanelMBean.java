package com.likya.tlossw.web.management;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.ftpadapter.ConnectionDocument.Connection;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "ftpAccessSearchPanelMBean")
@ViewScoped
public class FTPAccessSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -4552099028769226007L;

	private FtpProperties ftpProperties;

	private ArrayList<FtpProperties> searchFTPAccessList;
	private transient DataTable searchFTPAccessTable;

	private List<FtpProperties> filteredFTPAccessList;

	private String connName;

	public void dispose() {
		ftpProperties = null;
		searchFTPAccessList = null;
	}

	@PostConstruct
	public void init() {
		resetFTPAccessProfileAction();
	}

	public String getFTPPropertiesXML() {
		QName qName = FtpProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String ftpPropertiesXML = ftpProperties.xmlText(xmlOptions);

		return ftpPropertiesXML;
	}

	public void resetFTPAccessProfileAction() {
		ftpProperties = FtpProperties.Factory.newInstance();
		Connection connection = Connection.Factory.newInstance();
		ftpProperties.setConnection(connection);

		searchFTPAccessList = new ArrayList<FtpProperties>();

		connName = "";
	}

	public void searchFTPAccessAction(ActionEvent e) {
		ftpProperties.getConnection().setConnName(connName);

		searchFTPAccessList = getDbOperations().searchFTPAccessConnection(getFTPPropertiesXML());

		if (searchFTPAccessList == null || searchFTPAccessList.size() == 0) {
			addMessage("searchFTPAccessConnection", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}

		// TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "searchFTPAccessConnection", e.getComponent().getId(), resolveMessage("tlos.trace.ftpAccess.search.desc").toString());
	}

	public void deleteFTPAccessAction(ActionEvent e) {
		ftpProperties = (FtpProperties) searchFTPAccessTable.getRowData();
		// int id = ftpProperties.getId();

		if (getDbOperations().deleteFTPAccessConnection(getFTPPropertiesXML())) {
			searchFTPAccessList.remove(ftpProperties);
			ftpProperties = FtpProperties.Factory.newInstance();

			addMessage("deleteFTPAccessConnection", FacesMessage.SEVERITY_INFO, "tlos.success.ftpConnectionDef.delete", null);
		} else {
			addMessage("deleteFTPAccessConnection", FacesMessage.SEVERITY_ERROR, "tlos.error.ftpConnection.delete", null);
		}

		// TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + id + "deleteFTPAccessConnection", e.getComponent().getId(), resolveMessage("tlos.trace.ftpAccess.delete.desc").toString());

		ftpProperties = FtpProperties.Factory.newInstance();
		Connection connection = Connection.Factory.newInstance();
		ftpProperties.setConnection(connection);
	}

	public String getConnName() {
		return connName;
	}

	public void setConnName(String connName) {
		this.connName = connName;
	}

	public FtpProperties getFtpProperties() {
		return ftpProperties;
	}

	public void setFtpProperties(FtpProperties ftpProperties) {
		this.ftpProperties = ftpProperties;
	}

	public ArrayList<FtpProperties> getSearchFTPAccessList() {
		return searchFTPAccessList;
	}

	public void setSearchFTPAccessList(ArrayList<FtpProperties> searchFTPAccessList) {
		this.searchFTPAccessList = searchFTPAccessList;
	}

	public DataTable getSearchFTPAccessTable() {
		return searchFTPAccessTable;
	}

	public void setSearchFTPAccessTable(DataTable searchFTPAccessTable) {
		this.searchFTPAccessTable = searchFTPAccessTable;
	}

	public List<FtpProperties> getFilteredFTPAccessList() {
		return filteredFTPAccessList;
	}

	public void setFilteredFTPAccessList(List<FtpProperties> filteredFTPAccessList) {
		this.filteredFTPAccessList = filteredFTPAccessList;
	}

}
