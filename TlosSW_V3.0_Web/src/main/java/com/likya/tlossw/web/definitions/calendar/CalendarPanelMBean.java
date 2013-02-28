package com.likya.tlossw.web.definitions.calendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.calendar.CalendarPeriodDocument.CalendarPeriod;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.calendar.DayOfWeek;
import com.likya.tlos.model.xmlbeans.calendar.ExceptionDaysDocument.ExceptionDays;
import com.likya.tlos.model.xmlbeans.calendar.NameDocument;
import com.likya.tlos.model.xmlbeans.calendar.NameDocument.Name;
import com.likya.tlos.model.xmlbeans.calendar.SpecificDaysDocument.SpecificDays;
import com.likya.tlos.model.xmlbeans.calendar.ValidFromDocument.ValidFrom;
import com.likya.tlos.model.xmlbeans.calendar.ValidToDocument.ValidTo;
import com.likya.tlos.model.xmlbeans.calendar.WhichOnesDocument.WhichOnes;
import com.likya.tlos.model.xmlbeans.common.DayDefDocument.DayDef;
import com.likya.tlos.model.xmlbeans.common.DaySpecialDocument.DaySpecial;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.ConstantDefinitions;
import com.likya.tlossw.web.utils.DefinitionUtils;

@ManagedBean(name = "calendarPanelMBean")
@ViewScoped
public class CalendarPanelMBean extends TlosSWBaseBean implements Serializable {

	private String selectedCalendarID;
	private String insertCheck;
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
	
	private HashMap<String, Name> nameList;

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

	private int gmt;
	private boolean dst;

	private boolean insertButton;

	@PostConstruct
	public void init() {
		resetCalendarAction();

		fillDaySpecialList();
		fillDayDefList();
		fillWhichNameList();

		selectedCalendarID = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedCalendarID"));
		insertCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("insertCheck"));
		iCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("iCheck"));

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
		daySpecial = null;
		dayDef = null;
		validFrom = null;
		validTo = null;
		validFromTime = null;
		validToTime = null;
		howManyTimes = 1;

		selectedWhichOnesList = null;
		whichOnesList = new ArrayList<SelectItem>();
		nameList = new HashMap<String, Name>();
		
		specificDate = null;
		selectedSpecificDayList = null;
		specificDayList = new ArrayList<SelectItem>();

		exceptionDate = null;
		selectedExceptionDayList = null;
		exceptionDayList = new ArrayList<SelectItem>();

		gmt = 2;
		dst = false;

		calendar = CalendarProperties.Factory.newInstance();

		ValidFrom valFrom = ValidFrom.Factory.newInstance();
		calendar.setValidFrom(valFrom);

		ValidTo valTo = ValidTo.Factory.newInstance();
		calendar.setValidTo(valTo);

		CalendarPeriod calendarPeriod = CalendarPeriod.Factory.newInstance();
		calendar.setCalendarPeriod(calendarPeriod);
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

		if (!(checkCalendarName() && getCalendarId())) {
			return;
		}

		if (getDbOperations().insertCalendar(getCalendarPropertiesXML())) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_INFO, "tlos.success.calendar.insert", null);
			resetCalendarAction();
		} else {
			addMessage("insertCalendar", FacesMessage.SEVERITY_ERROR, "tlos.error.calendar.insert", null);
		}
	}

	private boolean checkCalendarName() {
		ArrayList<CalendarProperties> searchCalendarList = getDbOperations().getCalendars();
		for (CalendarProperties calendar : searchCalendarList) {
			if (calendar.getCalendarName().equals(calendar.getCalendarName())) {
				addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.validation.calendar.calendarExist", null);
				return false;
			}
		}
		return true;
	}

	private boolean getCalendarId() {
		int calendarId = getDbOperations().getNextId(ConstantDefinitions.CALENDAR_ID);
		if (calendarId < 0) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.info.calendar.db.getId", null);
			return false;
		}
		calendar.setId(calendarId);

		return true;
	}

	private void fillCalendarProperties() {
		SpecificDays specDays = SpecificDays.Factory.newInstance();
		int i = 0;
		for (com.likya.tlos.model.xmlbeans.common.DateDocument.Date sDay : DefinitionUtils.generateDate(specificDayList)) {
			specDays.addNewDate();
			specDays.setDateArray(i, sDay.getCalendarValue());
			i = i + 1;
		}
		calendar.setSpecificDays(specDays);
		if (calendar.getSpecificDays().getDateArray().length == 0) {
			XmlCursor xmlCursor = calendar.getSpecificDays().newCursor();
			xmlCursor.removeXml();
		}

		ExceptionDays exceptDays = ExceptionDays.Factory.newInstance();
		int j = 0;
		for (com.likya.tlos.model.xmlbeans.common.DateDocument.Date eDay : DefinitionUtils.generateDate(exceptionDayList)) {
			exceptDays.addNewDate();
			exceptDays.setDateArray(j, eDay.getCalendarValue());
			j = j + 1;
		}
		calendar.setExceptionDays(exceptDays);
		if (calendar.getExceptionDays().getDateArray().length == 0) {
			XmlCursor xmlCursor = calendar.getExceptionDays().newCursor();
			xmlCursor.removeXml();
		}

		if (validFromTime != null) {
			calendar.getValidFrom().setTime(DefinitionUtils.dateToXmlTime(validFromTime));
		}

		if (validFrom != null) {
			calendar.getValidFrom().setDate(DefinitionUtils.dateToXmlDate(validFrom));
		}

		if (validToTime != null) {
			calendar.getValidTo().setTime(DefinitionUtils.dateToXmlTime(validToTime));
		}

		if (validTo != null) {
			calendar.getValidTo().setDate(DefinitionUtils.dateToXmlDate(validTo));
		}

		WhichOnes whichOnes = WhichOnes.Factory.newInstance();
		int k = 0;
		for (Name name : nameList.values()) {
			whichOnes.addNewName();
			whichOnes.setNameArray(k, name);
			k = k + 1;
		}
		calendar.setWhichOnes(whichOnes);
		if (calendar.getWhichOnes().getNameArray().length == 0) {
			XmlCursor xmlCursor = calendar.getWhichOnes().newCursor();
			xmlCursor.removeXml();
		}

		// TODO ekranlara login sayfasi eklendikten sonra userId kismina login
		// olan kullanicinin id degeri set edilecek
		calendar.setUserId(1);
	}

	public void addSpecificDayAction() {
		if (specificDate == null) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.info.calendar.specificDay.select", null);
			return;

		} else if (!specificDateCheck()) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.info.calendar.specificDay.duplicate", null);
			return;

		} else if (!exceptionDayListCheckForSpecificDate()) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.info.calendar.exceptionDay.duplicate", null);
			return;
		}

		String date = DefinitionUtils.dateToStringDate(specificDate);
		specificDayList.add(new SelectItem(date, date));

		specificDate = null;
	}

	private boolean specificDateCheck() {
		String date = DefinitionUtils.dateToStringDate(specificDate);

		for (int j = 0; j < specificDayList.size(); j++) {
			if (specificDayList.get(j).getValue().equals(date)) {
				return false;
			}
		}

		return true;
	}

	public boolean exceptionDayListCheckForSpecificDate() {
		String date = DefinitionUtils.dateToStringDate(specificDate);

		for (int j = 0; j < exceptionDayList.size(); j++) {
			if (exceptionDayList.get(j).getValue().equals(date)) {
				return false;
			}
		}

		return true;
	}

	public void deleteSpecificDaysAction() {
		for (int i = 0; i < selectedSpecificDayList.length; i++) {
			for (int j = 0; j < specificDayList.size(); j++) {
				if (specificDayList.get(j).getValue().equals(selectedSpecificDayList[i])) {
					specificDayList.remove(j);
					j = specificDayList.size();
				}
			}
		}
	}

	public void addExceptionDayAction() {
		if (exceptionDate == null) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.info.calendar.exceptionDay.select", null);
			return;

		} else if (!exceptionDateCheck()) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.info.calendar.exceptionDay.duplicate", null);
			return;

		} else if (!specificDayListCheckForExceptionDate()) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.info.calendar.specificDay.duplicate", null);
			return;
		}

		String date = DefinitionUtils.dateToStringDate(exceptionDate);
		exceptionDayList.add(new SelectItem(date, date));

		exceptionDate = null;
	}

	private boolean exceptionDateCheck() {
		String date = DefinitionUtils.dateToStringDate(exceptionDate);

		for (int j = 0; j < exceptionDayList.size(); j++) {
			if (exceptionDayList.get(j).getValue().equals(date)) {
				return false;
			}
		}

		return true;
	}

	private boolean specificDayListCheckForExceptionDate() {
		String date = DefinitionUtils.dateToStringDate(exceptionDate);

		for (int j = 0; j < specificDayList.size(); j++) {
			if (specificDayList.get(j).getValue().equals(date)) {
				return false;
			}
		}

		return true;
	}

	public void deleteExceptionDaysAction() {
		for (int i = 0; i < selectedExceptionDayList.length; i++) {
			for (int j = 0; j < exceptionDayList.size(); j++) {
				if (exceptionDayList.get(j).getValue().equals(selectedExceptionDayList[i])) {
					exceptionDayList.remove(j);
					j = exceptionDayList.size();
				}
			}
		}
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

	public int getGmt() {
		return gmt;
	}

	public void setGmt(int gmt) {
		this.gmt = gmt;
	}

	public boolean isDst() {
		return dst;
	}

	public void setDst(boolean dst) {
		this.dst = dst;
	}

	public HashMap<String, Name> getNameList() {
		return nameList;
	}

	public void setNameList(HashMap<String, Name> nameList) {
		this.nameList = nameList;
	}

}
