package com.likya.tlossw.web.definitions.advanced;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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

import com.likya.tlos.model.xmlbeans.programprovision.LicenseDocument.License;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "ppSearchPanelMBean")
@ViewScoped
public class ProgramProvisionSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 5578347263656801312L;

	private License license;

	private ArrayList<License> searchLicenseList;
	private transient DataTable searchLicenseTable;

	private Date startDate;
	private Date endDate;

	private String resourceName;

	private Collection<SelectItem> userList = null;
	private String user;

	private List<License> filteredLicenseList;

	public void dispose() {
		license = null;
		searchLicenseList = null;
		userList = null;
	}

	@PostConstruct
	public void init() {
		license = License.Factory.newInstance();
		searchLicenseList = new ArrayList<License>();
		
		ArrayList<Person> dbUserList = getDbOperations().getUsers();
		userList = WebInputUtils.fillUserList(dbUserList);
	}

	public String getLicenseXML() {
		QName qName = License.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String licenseXML = license.xmlText(xmlOptions);

		return licenseXML;
	}

	public void resetProvisionAction() {
		license = License.Factory.newInstance();

		searchLicenseList = null;
		user = "";
		startDate = null;
		endDate = null;
		resourceName = null;
	}

	public void searchProvisionAction(ActionEvent e) {
		if (user != null && !user.equals("")) {
			license.setUserId(Integer.valueOf(user));
		} else {
			license.setUserId(-1);
		}

		if (startDate != null && !startDate.equals("")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);

			license.setStartDate(calendar);
		} else {
			license.setStartDate(null);
		}

		if (endDate != null && !endDate.equals("")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);

			license.setEndDate(calendar);
		} else {
			license.setEndDate(null);
		}

		searchLicenseList = getDbOperations().searchProvision(getLicenseXML());
		if (searchLicenseList == null || searchLicenseList.size() == 0) {
			addMessage("searchProgramProvision", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}
	}

	public void deleteProvisionAction(ActionEvent e) {
		license = (License) searchLicenseTable.getRowData();

		if (getDbOperations().deleteProvision(getLicenseXML())) {
			searchLicenseList.remove(license);
			license = License.Factory.newInstance();

			addMessage("searchProgramProvision", FacesMessage.SEVERITY_INFO, "tlos.success.provision.delete", null);
		} else {
			addMessage("searchProgramProvision", FacesMessage.SEVERITY_ERROR, "tlos.error.provision.delete", null);
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Collection<SelectItem> getUserList() {
		return userList;
	}

	public void setUserList(Collection<SelectItem> userList) {
		this.userList = userList;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public ArrayList<License> getSearchLicenseList() {
		return searchLicenseList;
	}

	public void setSearchLicenseList(ArrayList<License> searchLicenseList) {
		this.searchLicenseList = searchLicenseList;
	}

	public DataTable getSearchLicenseTable() {
		return searchLicenseTable;
	}

	public void setSearchLicenseTable(DataTable searchLicenseTable) {
		this.searchLicenseTable = searchLicenseTable;
	}

	public List<License> getFilteredLicenseList() {
		return filteredLicenseList;
	}

	public void setFilteredLicenseList(List<License> filteredLicenseList) {
		this.filteredLicenseList = filteredLicenseList;
	}

}
