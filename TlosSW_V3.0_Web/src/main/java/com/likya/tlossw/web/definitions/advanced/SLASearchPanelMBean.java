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

import com.likya.tlos.model.xmlbeans.sla.ResourcePoolDocument.ResourcePool;
import com.likya.tlos.model.xmlbeans.sla.SLADocument.SLA;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "slaSearchPanelMBean")
@ViewScoped
public class SLASearchPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 5578347263656801312L;

	private SLA sla;

	private ArrayList<SLA> searchSlaList;
	private transient DataTable searchSlaTable;

	private Date startDate;
	private Date endDate;

	private String resourceName;

	private Collection<SelectItem> userList = null;
	private String user;

	private List<SLA> filteredSlaList;

	public void dispose() {
		sla = null;
		searchSlaList = null;
		userList = null;
	}

	@PostConstruct
	public void init() {
		sla = SLA.Factory.newInstance();
		searchSlaList = new ArrayList<SLA>();

		ArrayList<Person> dbUserList = getDbOperations().getUsers();
		userList = WebInputUtils.fillUserList(dbUserList);
	}

	public String getSlaXML() {
		QName qName = SLA.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String slaXML = sla.xmlText(xmlOptions);

		return slaXML;
	}

	public void resetSlaAction() {
		sla = SLA.Factory.newInstance();

		searchSlaList = null;
		user = "";
		startDate = null;
		endDate = null;
		resourceName = null;
	}

	public void searchSlaAction(ActionEvent e) {
		if (user != null && !user.equals("")) {
			sla.setUserId(Integer.valueOf(user));
		} else {
			sla.setUserId(-1);
		}

		if (startDate != null && !startDate.equals("")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);

			sla.setStartDate(calendar);
		} else {
			sla.setStartDate(null);
		}

		if (endDate != null && !endDate.equals("")) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);

			sla.setEndDate(calendar);
		} else {
			sla.setEndDate(null);
		}

		if (resourceName != null && !resourceName.equals("")) {
			ResourcePool resourcePool = ResourcePool.Factory.newInstance();

			resourcePool.insertNewResource(0);
			resourcePool.getResourceArray(0).setStringValue(resourceName);

			sla.setResourcePool(resourcePool);
		}

		searchSlaList = getDbOperations().searchSla(getSlaXML());
		if (searchSlaList == null || searchSlaList.size() == 0) {
			addMessage("searchSLA", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}
	}

	public void deleteSlaAction(ActionEvent e) {
		sla = (SLA) searchSlaTable.getRowData();

		if (getDbOperations().deleteSla(getSlaXML())) {
			searchSlaList.remove(sla);
			sla = SLA.Factory.newInstance();

			addMessage("searchSLA", FacesMessage.SEVERITY_INFO, "tlos.success.sla.delete", null);
		} else {
			addMessage("searchSLA", FacesMessage.SEVERITY_ERROR, "tlos.error.sla.delete", null);
		}
	}

	public SLA getSla() {
		return sla;
	}

	public void setSla(SLA sla) {
		this.sla = sla;
	}

	public ArrayList<SLA> getSearchSlaList() {
		return searchSlaList;
	}

	public void setSearchSlaList(ArrayList<SLA> searchSlaList) {
		this.searchSlaList = searchSlaList;
	}

	public DataTable getSearchSlaTable() {
		return searchSlaTable;
	}

	public void setSearchSlaTable(DataTable searchSlaTable) {
		this.searchSlaTable = searchSlaTable;
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

	public List<SLA> getFilteredSlaList() {
		return filteredSlaList;
	}

	public void setFilteredSlaList(List<SLA> filteredSlaList) {
		this.filteredSlaList = filteredSlaList;
	}

}
