package com.likya.tlossw.web.definitions.calendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "calendarSearchPanelMBean")
@ViewScoped
public class CalendarSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 2670253538995950928L;

	private CalendarProperties calendar;

	private ArrayList<CalendarProperties> searchCalendarList;
	private transient DataTable searchCalendarTable;

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
		searchCalendarList = new ArrayList<CalendarProperties>();
		fillUserList();
	}

	private void fillUserList() {
		Collection<SelectItem> userList = new ArrayList<SelectItem>();

		try {
			for (Person person : getDbOperations().getUsers()) {
				SelectItem item = new SelectItem();
				item.setValue(person.getId() + "");
				item.setLabel(person.getUserName());
				userList.add(item);
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		setUserList(userList);
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

		// if (validFrom != null && !validFrom.equals("")) {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(validFrom);
		//
		// calendar.setStartDate(calendar);
		// } else {
		// calendar.setStartDate(null);
		// }
		//
		// searchCalendarList =
		// getDbOperations().searchCalendar(getCalendarPropertiesXML());
		// if (searchCalendarList == null || searchCalendarList.size() == 0) {
		// addMessage("searchCalendar", FacesMessage.SEVERITY_INFO,
		// "tlos.info.search.noRecord", null);
		// }
	}

	public void deleteCalendarAction(ActionEvent e) {
		calendar = (CalendarProperties) searchCalendarTable.getRowData();

		// if (getDbOperations().deleteCalendar(getCalendarPropertiesXML())) {
		// searchCalendarList.remove(calendar);
		// calendar = CalendarProperties.Factory.newInstance();
		//
		// addMessage("searchCalendar", FacesMessage.SEVERITY_INFO,
		// "tlos.success.calendar.delete", null);
		// } else {
		// addMessage("searchCalendar", FacesMessage.SEVERITY_ERROR,
		// "tlos.error.calendar.delete", null);
		// }
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

}
