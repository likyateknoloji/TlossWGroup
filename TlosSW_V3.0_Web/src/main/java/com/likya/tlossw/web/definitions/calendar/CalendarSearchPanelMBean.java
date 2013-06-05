package com.likya.tlossw.web.definitions.calendar;

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

import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.calendar.ValidFromDocument.ValidFrom;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "calendarSearchPanelMBean")
@ViewScoped
public class CalendarSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 2670253538995950928L;

	private CalendarProperties calendar;

	private ArrayList<CalendarProperties> searchCalendarList;
	private transient DataTable searchCalendarTable;
	private CalendarProperties selectedRow;

	private Date validFrom;

	private Collection<SelectItem> userList = null;
	private String user;

	private List<CalendarProperties> filteredCalendarList;

	public void dispose() {
		calendar = null;
		searchCalendarList = null;
		userList = null;
		validFrom = null;
	}

	@PostConstruct
	public void init() {
		calendar = CalendarProperties.Factory.newInstance();
		ValidFrom valFrom = ValidFrom.Factory.newInstance();
		calendar.setValidFrom(valFrom);

		searchCalendarList = new ArrayList<CalendarProperties>();

		ArrayList<Person> dbUserList = getDbOperations().getUsers();
		setUserList(WebInputUtils.fillUserList(dbUserList));
	}

	public String getCalendarPropertiesXML() {
		QName qName = CalendarProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String calendarPropertiesXML = calendar.xmlText(xmlOptions);

		return calendarPropertiesXML;
	}

	public void resetCalendarAction() {
		calendar = CalendarProperties.Factory.newInstance();
		searchCalendarList = new ArrayList<CalendarProperties>();
		user = "";
		validFrom = null;
	}

	public void searchCalendarAction(ActionEvent e) {
		if (user != null && !user.equals("")) {
			calendar.setUserId(Integer.valueOf(user));
		} else {
			calendar.setUserId(-1);
		}

		if (validFrom != null && !validFrom.equals("")) {
			Calendar validDate = Calendar.getInstance();
			validDate.setTime(validFrom);

			calendar.getValidFrom().setDate(validDate);
		}

		searchCalendarList = getDbOperations().searchCalendar(getCalendarPropertiesXML());

		if (searchCalendarList == null || searchCalendarList.size() == 0) {
			addMessage("searchCalendar", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", null);
		}
	}

	public void deleteCalendarAction(ActionEvent e) {
		// calendar = (CalendarProperties) searchCalendarTable.getRowData();
		calendar = selectedRow;

		if (getDbOperations().deleteCalendar(getCalendarPropertiesXML())) {
			searchCalendarList.remove(calendar);
			calendar = CalendarProperties.Factory.newInstance();

			addMessage("searchCalendar", FacesMessage.SEVERITY_INFO, "tlos.success.calendar.delete", null);
		} else {
			addMessage("searchCalendar", FacesMessage.SEVERITY_ERROR, "tlos.error.calendar.delete", null);
		}
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

	public CalendarProperties getCalendar() {
		return calendar;
	}

	public void setCalendar(CalendarProperties calendar) {
		this.calendar = calendar;
	}

	public ArrayList<CalendarProperties> getSearchCalendarList() {
		return searchCalendarList;
	}

	public void setSearchCalendarList(ArrayList<CalendarProperties> searchCalendarList) {
		this.searchCalendarList = searchCalendarList;
	}

	public DataTable getSearchCalendarTable() {
		return searchCalendarTable;
	}

	public void setSearchCalendarTable(DataTable searchCalendarTable) {
		this.searchCalendarTable = searchCalendarTable;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public List<CalendarProperties> getFilteredCalendarList() {
		return filteredCalendarList;
	}

	public void setFilteredCalendarList(List<CalendarProperties> filteredCalendarList) {
		this.filteredCalendarList = filteredCalendarList;
	}

	public CalendarProperties getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(CalendarProperties selectedRow) {
		this.selectedRow = selectedRow;
	}

}
