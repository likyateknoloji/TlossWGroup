package com.likya.tlossw.web.definitions.calendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.apache.xmlbeans.XmlOptions;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import com.likya.tlos.model.xmlbeans.calendar.CalendarPeriodDocument.CalendarPeriod;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.calendar.DayOfWeek;
import com.likya.tlos.model.xmlbeans.calendar.ExceptionDaysDocument.ExceptionDays;
import com.likya.tlos.model.xmlbeans.calendar.NameDocument;
import com.likya.tlos.model.xmlbeans.calendar.NameDocument.Name;
import com.likya.tlos.model.xmlbeans.calendar.NameDocument.Name.ID;
import com.likya.tlos.model.xmlbeans.calendar.SpecificDaysDocument.SpecificDays;
import com.likya.tlos.model.xmlbeans.calendar.ValidFromDocument.ValidFrom;
import com.likya.tlos.model.xmlbeans.calendar.ValidToDocument.ValidTo;
import com.likya.tlos.model.xmlbeans.calendar.WhichOnesDocument.WhichOnes;
import com.likya.tlos.model.xmlbeans.common.DayDefDocument.DayDef;
import com.likya.tlos.model.xmlbeans.common.DaySpecialDocument.DaySpecial;
import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "calendarPanelMBean")
@ViewScoped
public class CalendarPanelMBean extends TlosSWBaseBean implements Serializable {

	private String selectedCalendarID;
	private String insertCheck;
	private String iCheck;

	private static final long serialVersionUID = 1L;

	private CalendarProperties calendar;

	private String calendarName;

	private Collection<SelectItem> daySpecialList = null;
	private String daySpecial;
	private Collection<SelectItem> dayDefList = null;
	private String dayDef;

	private int howManyTimes = 1;

	private Collection<SelectItem> whichOnesList;
	private String[] selectedWhichOnesList;

	private HashMap<String, String> dayList = new HashMap<String, String>();

	private Date specificDate;

	private List<SelectItem> specificDayList;
	private String[] selectedSpecificDayList;

	private Collection<SelectItem> tZList;
	private String selectedTZone;

	private Collection<SelectItem> typeOfTimeList = null;
	private String selectedTypeOfTime;

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
		fillWhichOnesList();

		selectedCalendarID = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedCalendarID"));
		insertCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("insertCheck"));
		iCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("iCheck"));

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {

			if (insertCheck.equals("update")) {
				insertButton = false;

				calendar = getDbOperations().searchCalendarByID(selectedCalendarID);

				if (calendar != null) {
					fillPanelFromCalendar();
				}

			} else {
				insertButton = true;
			}
		}
	}

	private void fillDaySpecialList() {
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

	private void fillDayDefList() {
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

	private void fillWhichOnesList() {
		whichOnesList = new ArrayList<SelectItem>();

		for (int i = 1; i <= DayOfWeek.Enum.table.lastInt(); i++) {
			String dayID = NameDocument.Name.ID.Enum.forInt(i).toString();
			String dayName = DayOfWeek.Enum.forInt(i).toString();

			SelectItem item = new SelectItem();
			item.setValue(dayID);
			item.setLabel(dayName);
			whichOnesList.add(item);

			dayList.put(dayID, dayName);
		}
	}

	private String getCalendarPropertiesXML() {
		QName qName = CalendarProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String calendarPropertiesXML = calendar.xmlText(xmlOptions);

		return calendarPropertiesXML;
	}

	public void resetCalendarAction() {
		calendarName = "";

		daySpecial = null;
		dayDef = null;
		validFrom = null;
		validTo = null;
		validFromTime = null;
		validToTime = null;
		howManyTimes = 1;

		selectedWhichOnesList = null;
		whichOnesList = new ArrayList<SelectItem>();

		specificDate = null;
		selectedSpecificDayList = null;
		specificDayList = new ArrayList<SelectItem>();

		exceptionDate = null;
		selectedExceptionDayList = null;
		exceptionDayList = new ArrayList<SelectItem>();

		setSelectedTZone(new String("Europe/Istanbul"));
		selectedTypeOfTime = new String("Actual");
		resetCalendar();
	}

	private void resetCalendar() {
		calendar = CalendarProperties.Factory.newInstance();

		ValidFrom valFrom = ValidFrom.Factory.newInstance();
		calendar.setValidFrom(valFrom);

		ValidTo valTo = ValidTo.Factory.newInstance();
		calendar.setValidTo(valTo);

		CalendarPeriod calendarPeriod = CalendarPeriod.Factory.newInstance();
		calendar.setCalendarPeriod(calendarPeriod);

		tZList = WebInputUtils.fillTZList();
		typeOfTimeList = WebInputUtils.fillTypesOfTimeList();
	}

	private void fillPanelFromCalendar() {
		fillWhichDays();
		fillSpecificDays();
		fillExceptionDays();

		calendarName = calendar.getCalendarName();

		validFrom = calendar.getValidFrom().getDate().getTime();
		validTo = calendar.getValidTo().getDate().getTime();

		// validFromTime =
		// DefinitionUtils.dateToStringTime(calendar.getValidFrom().getTime().getTime());
		// validToTime =
		// DefinitionUtils.dateToStringTime(calendar.getValidTo().getTime().getTime());

		// Calendar validFromTimeCal = calendar.getValidFrom().getTime();
		// Calendar validToTimeCal = calendar.getValidTo().getTime();
		DateTimeZone zone = DateTimeZone.forID(selectedTZone);
		LocalTime validFromTimeLT = new LocalTime(calendar.getValidFrom().getTime());
		LocalTime validToTimeLT = new LocalTime(calendar.getValidTo().getTime());

		validFromTime = validFromTimeLT.toString();
		validToTime = validToTimeLT.toString();

		// String timeInputFormat = new String("HH:mm:ss.SSSZZ");
		// String timeOutputFormat = new String("HH:mm:ss");
		// validFromTime = DefinitionUtils.calendarToStringTimeFormat(validFromTimeCal, selectedTZone, timeInputFormat, timeOutputFormat);
		// validToTime = DefinitionUtils.calendarToStringTimeFormat(validToTimeCal, selectedTZone, timeInputFormat, timeOutputFormat);

		daySpecial = calendar.getCalendarPeriod().getDaySpecial().toString();
		dayDef = calendar.getCalendarPeriod().getDayDef().toString();
		selectedTZone = calendar.getTimeZone();
		if (calendar.getTypeOfTime() != null)
			selectedTypeOfTime = calendar.getTypeOfTime().toString();
		else
			selectedTypeOfTime = new String("Broadcast");
		howManyTimes = calendar.getHowmanyTimes();

		insertButton = false;
	}

	public void fillWhichDays() {
		if (calendar.getWhichOnes() != null && calendar.getWhichOnes().getNameArray() != null && calendar.getWhichOnes().getNameArray().length > 0) {

			int length = calendar.getWhichOnes().getNameArray().length;
			selectedWhichOnesList = new String[length];

			for (int i = 0; i < length; i++) {
				selectedWhichOnesList[i] = calendar.getWhichOnes().getNameArray(i).getID() + "";
			}
		}
	}

	public void fillSpecificDays() {
		specificDayList = new ArrayList<SelectItem>();
		if (calendar.getSpecificDays() != null) {

			for (int i = 0; i < calendar.getSpecificDays().getDateArray().length; i++) {
				String dateStr = DefinitionUtils.dateToStringDate(calendar.getSpecificDays().getDateArray(i).getTime(), selectedTZone); // example 01.07.2009
				specificDayList.add(new SelectItem(dateStr, dateStr));
			}
		}
	}

	public void fillExceptionDays() {
		exceptionDayList = new ArrayList<SelectItem>();
		if (calendar.getExceptionDays() != null) {

			for (int i = 0; i < calendar.getExceptionDays().getDateArray().length; i++) {
				String dateStr = DefinitionUtils.dateToStringDate(calendar.getExceptionDays().getDateArray(i).getTime(), selectedTZone);
				exceptionDayList.add(new SelectItem(dateStr, dateStr));
			}
		}
	}

	public void updateCalendarAction(ActionEvent e) {
		fillCalendarProperties();

		if (!checkValidInterval()) {
			return;
		}

		if (getDbOperations().updateCalendar(getCalendarPropertiesXML())) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_INFO, "tlos.success.calendar.update", null);
		} else {
			addMessage("insertCalendar", FacesMessage.SEVERITY_ERROR, "tlos.error.calendar.update", null);
		}
	}

	public void insertCalendarAction(ActionEvent e) {
		fillCalendarProperties();

		if (!(checkValidInterval() && checkCalendarName() && getCalendarId())) {
			return;
		}

		if (getDbOperations().insertCalendar(getCalendarPropertiesXML())) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_INFO, "tlos.success.calendar.insert", null);
			resetCalendarAction();
		} else {
			addMessage("insertCalendar", FacesMessage.SEVERITY_ERROR, "tlos.error.calendar.insert", null);
		}
	}

	private boolean checkValidInterval() {
		boolean valid = DefinitionUtils.dateComparer(calendar.getValidTo(), calendar.getValidFrom());
		if (!valid) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.validation.calendar.timeInterval", null);
		}

		return valid;
	}

	private boolean checkCalendarName() {
		ArrayList<CalendarProperties> searchCalendarList = getDbOperations().getCalendars();
		for (CalendarProperties calendarProperties : searchCalendarList) {
			if (calendarProperties.getCalendarName().equals(calendar.getCalendarName())) {
				addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.validation.calendar.calendarExist", null);
				return false;
			}
		}
		return true;
	}

	private boolean getCalendarId() {
		int calendarId = getDbOperations().getNextId(CommonConstantDefinitions.CALENDAR_ID);
		if (calendarId < 0) {
			addMessage("insertCalendar", FacesMessage.SEVERITY_WARN, "tlos.info.calendar.db.getId", null);
			return false;
		}
		calendar.setId(calendarId);

		return true;
	}

	private void fillCalendarProperties() {
		int id = -1;

		if (calendar.getId() > 0) {
			id = calendar.getId();
		}

		resetCalendar();
		if (id > 0) {
			calendar.setId(id);
		}

		calendar.setCalendarName(calendarName);

		calendar.getCalendarPeriod().setDaySpecial(DaySpecial.Enum.forString(daySpecial));
		calendar.getCalendarPeriod().setDayDef(DayDef.Enum.forString(dayDef));

		calendar.setHowmanyTimes(new Byte(howManyTimes + ""));

		if (specificDayList.size() > 0) {
			SpecificDays specificDays = SpecificDays.Factory.newInstance();
			for (com.likya.tlos.model.xmlbeans.common.DateDocument.Date specificDay : DefinitionUtils.generateDate(specificDayList)) {
				com.likya.tlos.model.xmlbeans.common.DateDocument.Date date = specificDays.addNewDate();
				date.set(specificDay);
			}
			calendar.setSpecificDays(specificDays);
		}

		if (exceptionDayList.size() > 0) {
			ExceptionDays exceptionDays = ExceptionDays.Factory.newInstance();
			for (com.likya.tlos.model.xmlbeans.common.DateDocument.Date exceptionDay : DefinitionUtils.generateDate(exceptionDayList)) {
				com.likya.tlos.model.xmlbeans.common.DateDocument.Date date = exceptionDays.addNewDate();
				date.set(exceptionDay);
			}
			calendar.setExceptionDays(exceptionDays);
		}

		Calendar dateToXmlTimeValue = DefinitionUtils.dateToXmlTime(validFrom, validFromTime, selectedTZone);
		Calendar dateToXmlDateWithoutZoneValue = DefinitionUtils.dateToXmlDateWithoutZone(validFrom);
		Calendar dateToXmlTimeValue2 = DefinitionUtils.dateToXmlTime(validTo, validToTime, selectedTZone);
		Calendar dateToXmlDateWithoutZoneValue2 = DefinitionUtils.dateToXmlDateWithoutZone(validTo);

		calendar.getValidFrom().setTime(dateToXmlTimeValue);
		calendar.getValidFrom().setDate(dateToXmlTimeValue);
		calendar.getValidTo().setTime(dateToXmlTimeValue2);
		calendar.getValidTo().setDate(dateToXmlTimeValue2);

		Calendar calendarNow = Calendar.getInstance();
		calendar.setCreationDateTime(calendarNow);
		calendar.setTimeZone(selectedTZone);
		calendar.setTypeOfTime(TypeOfTime.Enum.forString(selectedTypeOfTime));

		if (selectedWhichOnesList.length > 0) {
			WhichOnes whichOnes = WhichOnes.Factory.newInstance();
			for (String dayID : selectedWhichOnesList) {
				Name name = whichOnes.addNewName();
				name.setStringValue(dayList.get(dayID));
				name.setID(ID.Enum.forString(dayID));
			}
			calendar.setWhichOnes(whichOnes);
		}

		calendar.setUserId(getSessionMediator().getJmxAppUser().getAppUser().getId());
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

		String date = DefinitionUtils.dateToStringDate(specificDate, selectedTZone);
		specificDayList.add(new SelectItem(date, date));

		specificDate = null;
	}

	private boolean specificDateCheck() {
		String date = DefinitionUtils.dateToStringDate(specificDate, selectedTZone);

		for (int j = 0; j < specificDayList.size(); j++) {
			if (specificDayList.get(j).getValue().equals(date)) {
				return false;
			}
		}

		return true;
	}

	public boolean exceptionDayListCheckForSpecificDate() {
		String date = DefinitionUtils.dateToStringDate(specificDate, selectedTZone);

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
		String date = DefinitionUtils.dateToStringDate(exceptionDate, selectedTZone);
		exceptionDayList.add(new SelectItem(date, date));

		exceptionDate = null;
	}

	private boolean exceptionDateCheck() {
		String date = DefinitionUtils.dateToStringDate(exceptionDate, selectedTZone);

		for (int j = 0; j < exceptionDayList.size(); j++) {
			if (exceptionDayList.get(j).getValue().equals(date)) {
				return false;
			}
		}

		return true;
	}

	private boolean specificDayListCheckForExceptionDate() {
		String date = DefinitionUtils.dateToStringDate(exceptionDate, selectedTZone);

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

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public String getSelectedTZone() {
		return selectedTZone;
	}

	public void setSelectedTZone(String selectedTZone) {
		this.selectedTZone = selectedTZone;
	}

	public Collection<SelectItem> gettZList() {
		return tZList;
	}

	public void settZList(Collection<SelectItem> tZList) {
		this.tZList = tZList;
	}

	public Collection<SelectItem> getTypeOfTimeList() {
		return typeOfTimeList;
	}

	public void setTypeOfTimeList(Collection<SelectItem> typeOfTimeList) {
		this.typeOfTimeList = typeOfTimeList;
	}

	public String getSelectedTypeOfTime() {
		return selectedTypeOfTime;
	}

	public void setSelectedTypeOfTime(String selectedTypeOfTime) {
		this.selectedTypeOfTime = selectedTypeOfTime;
	}
}
