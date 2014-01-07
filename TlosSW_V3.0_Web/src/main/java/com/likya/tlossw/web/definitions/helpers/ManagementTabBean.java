package com.likya.tlossw.web.definitions.helpers;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.faces.model.SelectItem;

import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.XmlCursor;

import com.likya.tlos.model.xmlbeans.common.CalendarsDocument.Calendars;
import com.likya.tlos.model.xmlbeans.common.DatetimeType;
import com.likya.tlos.model.xmlbeans.common.TriggerDocument.Trigger;
import com.likya.tlos.model.xmlbeans.common.TypeOfTimeType;
import com.likya.tlos.model.xmlbeans.data.EventTypeDefDocument.EventTypeDef;
import com.likya.tlos.model.xmlbeans.data.JsPlannedTimeDocument.JsPlannedTime;
import com.likya.tlos.model.xmlbeans.data.ManagementDocument.Management;
import com.likya.tlos.model.xmlbeans.data.PeriodInfoDocument.PeriodInfo;
import com.likya.tlossw.web.definitions.JSBasePanelMBean;
import com.likya.tlossw.web.utils.DefinitionUtils;

public class ManagementTabBean {

	private JSBasePanelMBean jsBasePanelMBean;
	// time management
	private boolean useManagement = false;

	private boolean isPeriodic;
	private String trigger;
	private String eventTypeDef;

	private boolean useCalendarDef = false;
	private Collection<SelectItem> calendarNameList = null;
	private String[] calendarList;
	private Date specificDate;
	private String startDate;
	private String startTime;
	private String stopTime;

	private String jobTimeOutValue;
	//private String jobTimeOutUnit;

	private String expectedTime;
	//private String expectedTimeUnit;

	private String tolerancePercentage;
	private String minPercentage;

	private boolean defineStopTime = false;

	private String selectedTZone;

	private boolean relativeTimeOption = false;

	private String selectedTypeOfTime;

	private boolean isScenario = false;

	/* periodic job */
	private String periodTime;
	private int maxCount;
	private boolean relativeStart = true;

	public ManagementTabBean(JSBasePanelMBean jsBasePanelMBean, boolean isScenario, boolean isPeriodic) {
		super();
		this.isScenario = isScenario;
		this.jsBasePanelMBean = jsBasePanelMBean;
		this.isPeriodic = isPeriodic;
	}

	public void resetTab() {

		startDate = "";
		startTime = "";
		defineStopTime = false;
		stopTime = "";
		useCalendarDef = false;
		periodTime = "";
		maxCount = 0;
		relativeStart = true;

		selectedTZone = new String("Europe/Istanbul");
		selectedTypeOfTime = new String("Actual");
		relativeTimeOption = false;
		jobTimeOutValue = "3";
		//jobTimeOutUnit = Unit.HOURS.toString();
		expectedTime = "";
		//expectedTimeUnit = Unit.HOURS.toString();
		tolerancePercentage = "10";
		minPercentage = "10";

		isPeriodic = false;
		trigger = Trigger.TIME.toString();
		eventTypeDef = EventTypeDef.FILE.toString();

	}

	public void fillManagement(Management management) {

		Calendars calendars = Calendars.Factory.newInstance();
		
		Calendar jobStartDate = management.getTimeManagement().getJsPlannedTime().getStartTime().getDate();
		startDate = "";
		if (jobStartDate != null) {
			String dateOutputFormat = new String("dd.MM.yyyy");
			startDate = DefinitionUtils.dateToStringDate(jobStartDate.getTime(), selectedTZone, dateOutputFormat);
		}
		
		if (useCalendarDef) {
			for (int i = 0; i < calendarList.length; i++) {
					calendars.addCalendarId(new BigInteger(calendarList[i]));
			}
			
		}
		management.getTimeManagement().setCalendars(calendars);
		
		if (!startDate.isEmpty()) {
			Calendar date = Calendar.getInstance();
			date.setTime(specificDate);management.getTimeManagement().getJsPlannedTime().getStartTime().getDate();
			management.getTimeManagement().getJsPlannedTime().getStartTime().setDate(date);
		}
		management.setTrigger(Trigger.Enum.forString(trigger));

		// event tabanli bir is ise event turunu set ediyor
		if (trigger.equals(Trigger.EVENT.toString())) {
			management.setEventTypeDef(EventTypeDef.Enum.forString(eventTypeDef));
		}

		// periyodik is ise onunla ilgili alanlari dolduruyor

		// boolean isPeriodic = management.getPeriodInfo() != null ? true : false;

		if (isPeriodic) {
			if (management.getPeriodInfo() == null) {
				management.addNewPeriodInfo();
			}
			PeriodInfo periodInfo = management.getPeriodInfo();
			periodInfo.setComment("No Comment");
			// periodInfo.setStep(DefinitionUtils.dateToXmlTime(periodTime, jobBasePanelBean.getTimeManagementTabBean().getSelectedTZone()));

			String myArr[] = periodTime.split(":");
			String gDurationStr = "PT" + myArr[0] + "H" + myArr[1] + "M" + myArr[2] + "S";

			GDuration gDuration = new GDuration(gDurationStr);

			periodInfo.setStep(gDuration);
			if (maxCount > 0) {
				periodInfo.setMaxCount(BigInteger.valueOf(maxCount));
			}

			periodInfo.setRelativeStart(relativeStart);
		}

		// ekrandan starttime girildiyse onu set ediyor
		if (startTime != null && !startTime.equals("")) {

			if (management.getTimeManagement().getJsPlannedTime() == null) {
				JsPlannedTime jsPlannedTime = JsPlannedTime.Factory.newInstance();
				DatetimeType startTimeX = DatetimeType.Factory.newInstance();
				jsPlannedTime.setStartTime(startTimeX);
				management.getTimeManagement().setJsPlannedTime(jsPlannedTime);
			}

			JsPlannedTime jsPlannedTime = management.getTimeManagement().getJsPlannedTime();
			jsPlannedTime.getStartTime().setTime(DefinitionUtils.dateToXmlTime(startTime, selectedTZone));

			// ekrandan stoptime girildiyse onu set ediyor, bunu starttime
			// girildiyse kontrol ediyor cunku start time olmadan stop time
			// tanimi yapilmiyor
			if (defineStopTime) {
				DatetimeType jsStopTime = DatetimeType.Factory.newInstance();
				jsStopTime.setTime(DefinitionUtils.dateToXmlTime(stopTime, selectedTZone));

				jsPlannedTime.setStopTime(jsStopTime);

			} else if (jsPlannedTime.getStopTime() != null) {
				XmlCursor xmlCursor = jsPlannedTime.getStopTime().newCursor();
				xmlCursor.removeXml();
			}

		} else if (management.getTimeManagement().getJsPlannedTime() != null) {
			XmlCursor xmlCursor = management.getTimeManagement().getJsPlannedTime().newCursor();
			xmlCursor.removeXml();
		}
		management.getTimeManagement().setJsRelativeTimeOption(relativeTimeOption);

		if (jobTimeOutValue != null && !jobTimeOutValue.equals("")) {
			if (management.getTimeControl().getJsTimeOut() == null) {
				DatetimeType jsTimeOut = DatetimeType.Factory.newInstance();
				management.getTimeControl().setJsTimeOut(jsTimeOut);
			}
			management.getTimeControl().getJsTimeOut().setTime(DefinitionUtils.dateToXmlTime(jobTimeOutValue, "Zulu"));
		}

		if (tolerancePercentage != null && !tolerancePercentage.equals("")) {
			management.getTimeControl().setTolerancePercentage(Integer.parseInt(tolerancePercentage));
		}

		if (minPercentage != null && !minPercentage.equals("")) {
			management.getTimeControl().setMinPercentage(Integer.parseInt(minPercentage));
		}

		// expectedTime girildiyse ilgili alanlar set ediliyor
		if (expectedTime != null && !expectedTime.equals("")) {
			DatetimeType jsExpectedTime = DatetimeType.Factory.newInstance();
			jsExpectedTime.setTime(DefinitionUtils.dateToXmlTime(expectedTime, "Zulu"));

			management.getTimeControl().setExpectedTime(jsExpectedTime);

		} else if (management.getTimeControl().getExpectedTime() != null) {
			XmlCursor xmlCursor = management.getTimeControl().getExpectedTime().newCursor();
			xmlCursor.removeXml();
		}

		management.getTimeManagement().setTimeZone(selectedTZone);
		management.getTimeManagement().setTypeOfTime(TypeOfTimeType.Enum.forString(selectedTypeOfTime));

		System.out.println("nedir" + management.getTimeManagement().toString());
	}

	public void fillManagementTab(Management management) {

		if (management.getTimeManagement() != null && management.getTimeManagement().getCalendars() != null && management.getTimeManagement().getCalendars().sizeOfCalendarIdArray() > 0) {
			useCalendarDef = true;
			int sizeOfArray = management.getTimeManagement().getCalendars().sizeOfCalendarIdArray();
			calendarList = new String[sizeOfArray];
			for (int i = 0; i < sizeOfArray; i++) {
				calendarList[i] = management.getTimeManagement().getCalendars().getCalendarIdArray(i).toString();
			}
		} else {
			useCalendarDef = false;
		}

		if(management.getTrigger() != null) {
		  trigger = management.getTrigger().toString();
		} else {
			trigger = "";
		}
		isPeriodic = management.getPeriodInfo() != null ? true : false;

		if (trigger.equals(Trigger.EVENT.toString())) {
			eventTypeDef = management.getEventTypeDef().toString();
		}

		if (isPeriodic) {
			PeriodInfo periodInfo = management.getPeriodInfo();
			// String timeOutputFormat = new String("HH:mm:ss");
			periodTime = DefinitionUtils.getDurationString(periodInfo.getStep());
			// periodTime = DefinitionUtils.calendarToStringTimeFormat(periodInfo.getStep(), jobBasePanelBean.getTimeManagementTabBean().getSelectedTZone(), timeOutputFormat);
			// periodTime = periodInfo.getStep().getHour() + ":" + periodInfo.getStep().getMinute() + ":" + periodInfo.getStep().getSecond();
			if (periodInfo.getMaxCount() != null) {
				maxCount = periodInfo.getMaxCount().intValue();
			}

			relativeStart = periodInfo.getRelativeStart();
		}
		
		String timeOutputFormat = new String("HH:mm:ss.SSS");
		String timeOutputFormatHms = new String("HH:mm:ss");
		
		useManagement = false;
		if (management != null && management.getTimeManagement() != null) {
			useManagement = true;
			
			if (management.getTimeManagement().getJsPlannedTime() != null)
				if (management.getTimeManagement().getJsPlannedTime().getStartTime() != null) {

					Calendar jobStartTime = management.getTimeManagement().getJsPlannedTime().getStartTime().getTime();
					Calendar jobStartDate = management.getTimeManagement().getJsPlannedTime().getStartTime().getDate();

					// DateTimeZone zone = DateTimeZone.forID(selectedTZone);
					// DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss.SSSZZ");
					// LocalTime localStartTime = dtf.parseLocalTime(jobStartTime);
					//
					// DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");
					// String jobLocalStartTime = localStartTime.toDateTimeToday(zone).toString(formatter);

					startTime = DefinitionUtils.calendarTimeToStringTimeFormat(jobStartTime, selectedTZone, timeOutputFormat);
					
					if (jobStartDate != null) {
						String dateOutputFormat = new String("dd.MM.yyyy");
						startDate = DefinitionUtils.dateToStringDate(jobStartDate.getTime(), selectedTZone, dateOutputFormat);
					}
				}
			if (management.getTimeManagement().getJsPlannedTime().getStopTime() != null) {
				defineStopTime = true;

				Calendar jobStopTime = management.getTimeManagement().getJsPlannedTime().getStopTime().getTime();
				// LocalTime jobLocalStopTime = new LocalTime( jobStopTime);
				stopTime = DefinitionUtils.calendarTimeToStringTimeFormat(jobStopTime, selectedTZone, timeOutputFormat);
				// stopTime = jobLocalStopTime.toString();
				// LocalTime localStopTime = dtf.parseLocalTime(jobStopTime);
				//
				// String jobLocalStopTime = localStopTime.toDateTimeToday(zone).toString(formatter);

			}
			
			if (management.getTimeManagement().getJsRelativeTimeOption()) {
				relativeTimeOption = management.getTimeManagement().getJsRelativeTimeOption();
			}
			
			if (management.getTimeManagement().getTimeZone() != null)
				selectedTZone = management.getTimeManagement().getTimeZone();

			if (management.getTimeManagement().getTypeOfTime() != null)
				selectedTypeOfTime = management.getTimeManagement().getTypeOfTime().toString();
			
		}
		
		if (management != null && management.getTimeControl() != null) {

			DatetimeType jsTimeOutTime = management.getTimeControl().getJsTimeOut();
			
			if ( jsTimeOutTime.getTime() != null) {
				jobTimeOutValue = DefinitionUtils.calendarTimeToStringTimeFormat(jsTimeOutTime.getTime(), "Zulu", timeOutputFormatHms);
			}
			
			if (management.getTimeControl().getExpectedTime() != null) {
				DatetimeType jsExpectedTime = management.getTimeControl().getExpectedTime();

				if (jsExpectedTime.getTime() != null) {
					expectedTime = DefinitionUtils.calendarTimeToStringTimeFormat( jsExpectedTime.getTime(), "Zulu", timeOutputFormatHms ); ;
				}
			}

			// job taniminda bu alan yoksa degeri sifir geliyor
			if (management.getTimeControl().getTolerancePercentage() > 0) {
				tolerancePercentage = management.getTimeControl().getTolerancePercentage() + "";
			}

			// job taniminda bu alan yoksa degeri sifir geliyor
			if (management.getTimeControl().getMinPercentage() > 0) {
				minPercentage = management.getTimeControl().getMinPercentage() + "";
			}
			
			useManagement = true;
		}
	}

	public String getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(String periodTime) {
		this.periodTime = periodTime;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public boolean isRelativeStart() {
		return relativeStart;
	}

	public void setRelativeStart(boolean relativeStart) {
		this.relativeStart = relativeStart;
	}

	public boolean isUseManagement() {
		return useManagement;
	}

	public void setUseManagement(boolean useManagement) {
		this.useManagement = useManagement;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	public String getJobTimeOutValue() {
		return jobTimeOutValue;
	}

	public void setJobTimeOutValue(String jobTimeOutValue) {
		this.jobTimeOutValue = jobTimeOutValue;
	}

	public String getExpectedTime() {
		return expectedTime;
	}

	public void setExpectedTime(String expectedTime) {
		this.expectedTime = expectedTime;
	}

	public String getTolerancePercentage() {
		return tolerancePercentage;
	}

	public void setTolerancePercentage(String tolerancePercentage) {
		this.tolerancePercentage = tolerancePercentage;
	}

	public String getMinPercentage() {
		return minPercentage;
	}

	public void setMinPercentage(String minPercentage) {
		this.minPercentage = minPercentage;
	}

	public boolean isDefineStopTime() {
		return defineStopTime;
	}

	public void setDefineStopTime(boolean defineStopTime) {
		this.defineStopTime = defineStopTime;
	}

	public Collection<SelectItem> getTzList() {
		return jsBasePanelMBean.getTzList();
	}

	public String getSelectedTZone() {
		return selectedTZone;
	}

	public void setSelectedTZone(String selectedTZone) {
		this.selectedTZone = selectedTZone;
	}

	public boolean getRelativeTimeOption() {
		return relativeTimeOption;
	}

	public void setRelativeTimeOption(boolean relativeTimeOption) {
		this.relativeTimeOption = relativeTimeOption;
	}

	// public Collection<SelectItem> getRelativeTimeOptionList() {
	// return jsBasePanelMBean.getRelativeTimeOptionList();
	// }

	public String getSelectedTypeOfTime() {
		return selectedTypeOfTime;
	}

	public void setSelectedTypeOfTime(String selectedTypeOfTime) {
		this.selectedTypeOfTime = selectedTypeOfTime;
	}

	public Collection<SelectItem> getTypeOfTimeList() {
		return jsBasePanelMBean.getTypeOfTimeList();
	}

	public Collection<SelectItem> getUnitTypeList() {
		return jsBasePanelMBean.getUnitTypeList();
	}

	public boolean isScenario() {
		return isScenario;
	}

	public JSBasePanelMBean getJsBasePanelMBean() {
		return jsBasePanelMBean;
	}

	public void setJsBasePanelMBean(JSBasePanelMBean jsBasePanelMBean) {
		this.jsBasePanelMBean = jsBasePanelMBean;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public Date getSpecificDate() {
		if (!startDate.isEmpty())
			specificDate = DefinitionUtils.stringToCalendar(startDate, "dd.MM.yyyy", selectedTZone).getTime();
		return specificDate;
	}

	public void setSpecificDate(Date specificDate) {
		String date = DefinitionUtils.dateToStringDate(specificDate, selectedTZone, "dd.MM.yyyy");
		startDate = date;

		this.specificDate = specificDate;
	}

	public String getEventTypeDef() {
		return eventTypeDef;
	}

	public void setEventTypeDef(String eventTypeDef) {
		this.eventTypeDef = eventTypeDef;
	}

	public Collection<SelectItem> getEventTypeDefList() {
		return jsBasePanelMBean.getEventTypeDefList();
	}

	public Collection<SelectItem> getTriggerList() {
		return jsBasePanelMBean.getTriggerList();
	}

	public boolean isUseCalendarDef() {
		return useCalendarDef;
	}

	public void setUseCalendarDef(boolean useCalendarDef) {
		this.useCalendarDef = useCalendarDef;
	}

	public String[] getCalendarList() {
		return calendarList;
	}

	public void setCalendarList(String[] calendarList) {
		this.calendarList = calendarList;
	}

	public boolean isPeriodic() {
		return isPeriodic;
	}

	public void setPeriodic(boolean isPeriodic) {
		this.isPeriodic = isPeriodic;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public Collection<SelectItem> getCalendarNameList() {
		return calendarNameList;
	}

	public void setCalendarNameList(Collection<SelectItem> calendarNameList) {
		this.calendarNameList = calendarNameList;
	}

}
