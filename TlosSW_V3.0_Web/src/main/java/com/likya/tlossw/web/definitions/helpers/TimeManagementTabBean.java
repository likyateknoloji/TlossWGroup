package com.likya.tlossw.web.definitions.helpers;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.apache.xmlbeans.XmlCursor;

import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlos.model.xmlbeans.common.UnitDocument.Unit;
import com.likya.tlos.model.xmlbeans.data.ExpectedTimeDocument.ExpectedTime;
import com.likya.tlos.model.xmlbeans.data.JsPlannedTimeDocument.JsPlannedTime;
import com.likya.tlos.model.xmlbeans.data.JsRelativeTimeOptionDocument.JsRelativeTimeOption;
import com.likya.tlos.model.xmlbeans.data.JsTimeOutDocument.JsTimeOut;
import com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime;
import com.likya.tlos.model.xmlbeans.data.StopTimeDocument.StopTime;
import com.likya.tlos.model.xmlbeans.data.TimeManagementDocument.TimeManagement;
import com.likya.tlossw.web.definitions.JSBasePanelMBean;
import com.likya.tlossw.web.utils.DefinitionUtils;

public class TimeManagementTabBean {

	private JSBasePanelMBean jsBasePanelMBean;
	// time management
	private boolean useTimeManagement = false;

	private String startTime;
	private String stopTime;

	private String jobTimeOutValue;
	private String jobTimeOutUnit;

	private String expectedTime;
	private String expectedTimeUnit;

	private String tolerancePercentage;
	private String minPercentage;

	private boolean defineStopTime = false;

	private String selectedTZone;

	private String relativeTimeOption;

	private String selectedTypeOfTime;

	private boolean isScenario = false;

	public TimeManagementTabBean(JSBasePanelMBean jsBasePanelMBean, boolean isScenario) {
		super();
		this.isScenario = isScenario;
		this.jsBasePanelMBean = jsBasePanelMBean;
	}

	public void resetTab() {

		startTime = "";
		defineStopTime = false;
		stopTime = "";
		// gmt = 0;
		// dst = false;
		selectedTZone = new String("Europe/Istanbul");
		selectedTypeOfTime = new String("Actual");
		relativeTimeOption = JsRelativeTimeOption.NO.toString();
		jobTimeOutValue = "3";
		jobTimeOutUnit = Unit.HOURS.toString();
		expectedTime = "";
		expectedTimeUnit = Unit.HOURS.toString();
		tolerancePercentage = "10";
		minPercentage = "10";

	}
	
	public void fillTimeManagement(TimeManagement timeManagement) {

		// ekrandan starttime girildiyse onu set ediyor
		if (startTime != null && !startTime.equals("")) {

			if (timeManagement.getJsPlannedTime() == null) {
				JsPlannedTime jsPlannedTime = JsPlannedTime.Factory.newInstance();
				StartTime startTime = StartTime.Factory.newInstance();
				jsPlannedTime.setStartTime(startTime);
				timeManagement.setJsPlannedTime(jsPlannedTime);
			}

			JsPlannedTime jsPlannedTime = timeManagement.getJsPlannedTime();
			jsPlannedTime.getStartTime().setTime(DefinitionUtils.dateToXmlTime(startTime, selectedTZone));

			// ekrandan stoptime girildiyse onu set ediyor, bunu starttime
			// girildiyse kontrol ediyor cunku start time olmadan stop time
			// tanimi yapilmiyor
			if (defineStopTime) {
				StopTime jsStopTime = StopTime.Factory.newInstance();
				jsStopTime.setTime(DefinitionUtils.dateToXmlTime(stopTime, selectedTZone));

				jsPlannedTime.setStopTime(jsStopTime);

			} else if (jsPlannedTime.getStopTime() != null) {
				XmlCursor xmlCursor = jsPlannedTime.getStopTime().newCursor();
				xmlCursor.removeXml();
			}

		} else if (timeManagement.getJsPlannedTime() != null) {
			XmlCursor xmlCursor = timeManagement.getJsPlannedTime().newCursor();
			xmlCursor.removeXml();
		}

		timeManagement.setJsRelativeTimeOption(JsRelativeTimeOption.Enum.forString(relativeTimeOption));

		if (jobTimeOutValue != null && !jobTimeOutValue.equals("")) {
			if (timeManagement.getJsTimeOut() == null) {
				JsTimeOut jsTimeOut = JsTimeOut.Factory.newInstance();
				timeManagement.setJsTimeOut(jsTimeOut);
			}
			timeManagement.getJsTimeOut().setValueInteger(new BigInteger(jobTimeOutValue));
			timeManagement.getJsTimeOut().setUnit(Unit.Enum.forString(jobTimeOutUnit));
		}

		if (tolerancePercentage != null && !tolerancePercentage.equals("")) {
			timeManagement.setTolerancePercentage(Integer.parseInt(tolerancePercentage));
		}

		if (minPercentage != null && !minPercentage.equals("")) {
			timeManagement.setMinPercentage(Integer.parseInt(minPercentage));
		}

		// expectedTime girildiyse ilgili alanlar set ediliyor
		if (expectedTime != null && !expectedTime.equals("")) {
			ExpectedTime jsExpectedTime = ExpectedTime.Factory.newInstance();
			jsExpectedTime.setValueInteger(new BigInteger(expectedTime));
			jsExpectedTime.setUnit(Unit.Enum.forString(expectedTimeUnit));

			timeManagement.setExpectedTime(jsExpectedTime);

		} else if (timeManagement.getExpectedTime() != null) {
			XmlCursor xmlCursor = timeManagement.getExpectedTime().newCursor();
			xmlCursor.removeXml();
		}

		timeManagement.setTimeZone(selectedTZone);
		timeManagement.setTypeOfTime(TypeOfTime.Enum.forString(selectedTypeOfTime));

		System.out.println("nedir" + timeManagement.toString());
	}
	
	public void fillTimeManagementTab(TimeManagement timeManagement) {

		if (timeManagement != null && timeManagement.getJsTimeOut() != null) {
			useTimeManagement = true;

			if (timeManagement.getJsPlannedTime() != null && timeManagement.getJsPlannedTime().getStartTime() != null) {
				Calendar jobStartTime = timeManagement.getJsPlannedTime().getStartTime().getTime();

				// DateTimeZone zone = DateTimeZone.forID(selectedTZone);
				// DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss.SSSZZ");
				// LocalTime localStartTime = dtf.parseLocalTime(jobStartTime);
				//
				// DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");
				// String jobLocalStartTime = localStartTime.toDateTimeToday(zone).toString(formatter);

				String timeOutputFormat = new String("HH:mm:ss.SSS");
				startTime = DefinitionUtils.calendarToStringTimeFormat(jobStartTime, selectedTZone, timeOutputFormat);
				// startTime = jobLocalTime.toString();

				if (timeManagement.getJsPlannedTime().getStopTime() != null) {
					defineStopTime = true;

					Calendar jobStopTime = timeManagement.getJsPlannedTime().getStopTime().getTime();
					// LocalTime jobLocalStopTime = new LocalTime( jobStopTime);
					stopTime = DefinitionUtils.calendarToStringTimeFormat(jobStopTime, selectedTZone, timeOutputFormat);
					// stopTime = jobLocalStopTime.toString();
					// LocalTime localStopTime = dtf.parseLocalTime(jobStopTime);
					//
					// String jobLocalStopTime = localStopTime.toDateTimeToday(zone).toString(formatter);

				}
			}

			if (timeManagement.getJsRelativeTimeOption() != null) {
				relativeTimeOption = timeManagement.getJsRelativeTimeOption().toString();
			}

			JsTimeOut timeOut = timeManagement.getJsTimeOut();

			if (timeOut.getValueInteger() != null) {
				jobTimeOutValue = timeOut.getValueInteger() + "";
			}
			if (timeOut.getUnit() != null) {
				jobTimeOutUnit = timeOut.getUnit().toString();
			}

			if (timeManagement.getExpectedTime() != null) {
				ExpectedTime jobExpectedTime = timeManagement.getExpectedTime();

				if (jobExpectedTime.getValueInteger() != null) {
					expectedTime = jobExpectedTime.getValueInteger() + "";
				}
				if (jobExpectedTime.getUnit() != null) {
					expectedTimeUnit = jobExpectedTime.getUnit().toString();
				}
			}

			// job taniminda bu alan yoksa degeri sifir geliyor
			if (timeManagement.getTolerancePercentage() > 0) {
				tolerancePercentage = timeManagement.getTolerancePercentage() + "";
			}

			// job taniminda bu alan yoksa degeri sifir geliyor
			if (timeManagement.getMinPercentage() > 0) {
				minPercentage = timeManagement.getMinPercentage() + "";
			}

			if (timeManagement.getTimeZone() != null)
				selectedTZone = timeManagement.getTimeZone();

			if (timeManagement.getTypeOfTime() != null)
				selectedTypeOfTime = timeManagement.getTypeOfTime().toString();

		} else {
			useTimeManagement = false;
		}
	}
	
	public boolean isUseTimeManagement() {
		return useTimeManagement;
	}

	public void setUseTimeManagement(boolean useTimeManagement) {
		this.useTimeManagement = useTimeManagement;
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

	public String getJobTimeOutUnit() {
		return jobTimeOutUnit;
	}

	public void setJobTimeOutUnit(String jobTimeOutUnit) {
		this.jobTimeOutUnit = jobTimeOutUnit;
	}

	public String getExpectedTime() {
		return expectedTime;
	}

	public void setExpectedTime(String expectedTime) {
		this.expectedTime = expectedTime;
	}

	public String getExpectedTimeUnit() {
		return expectedTimeUnit;
	}

	public void setExpectedTimeUnit(String expectedTimeUnit) {
		this.expectedTimeUnit = expectedTimeUnit;
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

	public String getRelativeTimeOption() {
		return relativeTimeOption;
	}

	public void setRelativeTimeOption(String relativeTimeOption) {
		this.relativeTimeOption = relativeTimeOption;
	}

	public Collection<SelectItem> getRelativeTimeOptionList() {
		return jsBasePanelMBean.getRelativeTimeOptionList();
	}

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

}
