package com.likya.tlossw.web.definitions.calendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.calendar.DayOfWeek;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.calendar.NameDocument;
import com.likya.tlos.model.xmlbeans.common.DayDefDocument.DayDef;
import com.likya.tlos.model.xmlbeans.common.DaySpecialDocument.DaySpecial;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "calendarPanelMBean")
@RequestScoped
public class CalendarPanelMBean extends TlosSWBaseBean implements Serializable {

	@ManagedProperty(value = "#{param.selectedCalendarID}")
	private String selectedCalendarID;

	@ManagedProperty(value = "#{param.insertCheck}")
	private String insertCheck;

	@ManagedProperty(value = "#{param.iCheck}")
	private String iCheck;

	private static final long serialVersionUID = 1L;

	private CalendarProperties calendar;

	private Collection<SelectItem> daySpecialList = null;
	private String daySpecial;
	private Collection<SelectItem> dayDefList = null;
	private String dayDef;

	private int howManyTimes = 1;

	private Collection<SelectItem> whichOnesList;
	private String[] selectedWhichOnesList;

	private Date specificDate;

	private List<SelectItem> specificDayList;
	private String[] selectedSpecificDayList;

	private Date exceptionDate;

	private List<SelectItem> exceptionDayList;
	private String[] selectedExceptionDayList;

	private Date validFrom;
	private Date validTo;

	private String validFromTime;
	private String validToTime;

	private boolean insertButton;

	@PostConstruct
	public void init() {
		resetCalendarAction();

		fillDaySpecialList();
		fillDayDefList();
		fillWhichNameList();

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {

			if (insertCheck.equals("update")) {
				insertButton = false;

				// calendar =
				// getDbOperations().searchCalendarByID(selectedCalendarID);
				//
				// if (calendar != null) {
				// fillPanelFromCalendar();
				// }

			} else {
				insertButton = true;
			}
		}
	}

	public void fillDaySpecialList() {
		String day = null;
		daySpecialList = new ArrayList<SelectItem>();

		for (int i = 1; i <= DaySpecial.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			day = DaySpecial.Enum.forInt(i).toString();
			item.setValue(day);
			item.setLabel(day);
			daySpecialList.add(item);
		}
	}

	public void fillDayDefList() {
		String day = null;
		dayDefList = new ArrayList<SelectItem>();

		for (int i = 1; i <= DayDef.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			day = DayDef.Enum.forInt(i).toString();
			item.setValue(day);
			item.setLabel(day);
			dayDefList.add(item);
		}
	}

	public void fillWhichNameList() {
		whichOnesList = new ArrayList<SelectItem>();

		for (int i = 1; i <= DayOfWeek.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			item.setValue(NameDocument.Name.ID.Enum.forInt(i).toString());
			item.setLabel(DayOfWeek.Enum.forInt(i).toString());
			whichOnesList.add(item);
		}
	}

	public String getCalendarPropertiesXML() {
		QName qName = CalendarProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String calendarPropertiesXML = calendar.xmlText(xmlOptions);

		return calendarPropertiesXML;
	}

	public void resetCalendarAction() {
		calendar = CalendarProperties.Factory.newInstance();
		validFrom = null;
	}

	private void fillPanelFromCalendar() {

	}

	public void updateCalendarAction(ActionEvent e) {
		fillCalendarProperties();

		// if (getDbOperations().updateCalendar(getCalendarPropertiesXML())) {
		// addMessage("insertCalendar", FacesMessage.SEVERITY_INFO,
		// "tlos.success.dbAccessDef.update", null);
		// } else {
		// addMessage("insertCalendar", FacesMessage.SEVERITY_ERROR,
		// "tlos.error.dbConnection.update", null);
		// }
	}

	public void insertCalendarAction(ActionEvent e) {
		fillCalendarProperties();

		// if (getDbOperations().insertCalendar(getCalendarPropertiesXML())) {
		// addMessage("insertCalendar", FacesMessage.SEVERITY_INFO,
		// "tlos.success.provision.insert", null);
		// resetCalendarAction();
		// } else {
		// addMessage("insertCalendar", FacesMessage.SEVERITY_ERROR,
		// "tlos.error.provision.insert", null);
		// }
	}

	private void fillCalendarProperties() {

		// TODO ekranlara login sayfasi eklendikten sonra userId kismina login
		// olan kullanicinin id degeri set edilecek
		calendar.setUserId(1);
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

	public String getSelectedCalendarID() {
		return selectedCalendarID;
	}

	public void setSelectedCalendarID(String selectedCalendarID) {
		this.selectedCalendarID = selectedCalendarID;
	}

	public CalendarProperties getCalendar() {
		return calendar;
	}

	public void setCalendar(CalendarProperties calendar) {
		this.calendar = calendar;
	}

	public Collection<SelectItem> getDaySpecialList() {
		return daySpecialList;
	}

	public void setDaySpecialList(Collection<SelectItem> daySpecialList) {
		this.daySpecialList = daySpecialList;
	}

	public String getDaySpecial() {
		return daySpecial;
	}

	public void setDaySpecial(String daySpecial) {
		this.daySpecial = daySpecial;
	}

	public Collection<SelectItem> getDayDefList() {
		return dayDefList;
	}

	public void setDayDefList(Collection<SelectItem> dayDefList) {
		this.dayDefList = dayDefList;
	}

	public String getDayDef() {
		return dayDef;
	}

	public void setDayDef(String dayDef) {
		this.dayDef = dayDef;
	}

	public String[] getSelectedWhichOnesList() {
		return selectedWhichOnesList;
	}

	public void setSelectedWhichOnesList(String[] selectedWhichOnesList) {
		this.selectedWhichOnesList = selectedWhichOnesList;
	}

	public Date getSpecificDate() {
		return specificDate;
	}

	public void setSpecificDate(Date specificDate) {
		this.specificDate = specificDate;
	}

	public List<SelectItem> getSpecificDayList() {
		return specificDayList;
	}

	public void setSpecificDayList(List<SelectItem> specificDayList) {
		this.specificDayList = specificDayList;
	}

	public String[] getSelectedSpecificDayList() {
		return selectedSpecificDayList;
	}

	public void setSelectedSpecificDayList(String[] selectedSpecificDayList) {
		this.selectedSpecificDayList = selectedSpecificDayList;
	}

	public Date getExceptionDate() {
		return exceptionDate;
	}

	public void setExceptionDate(Date exceptionDate) {
		this.exceptionDate = exceptionDate;
	}

	public List<SelectItem> getExceptionDayList() {
		return exceptionDayList;
	}

	public void setExceptionDayList(List<SelectItem> exceptionDayList) {
		this.exceptionDayList = exceptionDayList;
	}

	public String[] getSelectedExceptionDayList() {
		return selectedExceptionDayList;
	}

	public void setSelectedExceptionDayList(String[] selectedExceptionDayList) {
		this.selectedExceptionDayList = selectedExceptionDayList;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public String getValidFromTime() {
		return validFromTime;
	}

	public void setValidFromTime(String validFromTime) {
		this.validFromTime = validFromTime;
	}

	public String getValidToTime() {
		return validToTime;
	}

	public void setValidToTime(String validToTime) {
		this.validToTime = validToTime;
	}

	public Collection<SelectItem> getWhichOnesList() {
		return whichOnesList;
	}

	public void setWhichOnesList(Collection<SelectItem> whichOnesList) {
		this.whichOnesList = whichOnesList;
	}

	public int getHowManyTimes() {
		return howManyTimes;
	}

	public void setHowManyTimes(int howManyTimes) {
		this.howManyTimes = howManyTimes;
	}

}
