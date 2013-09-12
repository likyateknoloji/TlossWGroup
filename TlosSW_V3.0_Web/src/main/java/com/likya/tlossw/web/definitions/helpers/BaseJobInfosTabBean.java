package com.likya.tlossw.web.definitions.helpers;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;

import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.common.EventTypeDefDocument.EventTypeDef;
import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.common.JobTypeDefDocument.JobTypeDef;
import com.likya.tlos.model.xmlbeans.data.BaseJobInfosDocument.BaseJobInfos;
import com.likya.tlos.model.xmlbeans.data.JobInfosDocument.JobInfos;
import com.likya.tlos.model.xmlbeans.data.JobPriorityDocument.JobPriority;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.JsIsActiveDocument.JsIsActive;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.data.PeriodInfoDocument.PeriodInfo;
import com.likya.tlossw.web.definitions.JobBasePanelBean;
import com.likya.tlossw.web.utils.DefinitionUtils;

public class BaseJobInfosTabBean implements Serializable{

	private static final long serialVersionUID = 4999462516588657534L;

	public JobBasePanelBean jobBasePanelBean;
	
	private String jobPriority;

	/* periodic job */
	private String periodTime;
	private int maxCount;

	private String jobBaseType;

	private String jobTypeDef;

	private String eventTypeDef;

	public BaseJobInfosTabBean(JobBasePanelBean jobBasePanelBean, String jobBaseType) {
		
		super();
		resetTab();
		this.jobBasePanelBean = jobBasePanelBean;
		this.jobBaseType = jobBaseType;
		
	}
	
	public void resetTab() {
		jobPriority = JobPriority.X_1.toString();
		jobBaseType = JobBaseType.NON_PERIODIC.toString();
		periodTime = "";
		maxCount = 0;
		jobTypeDef = JobTypeDef.TIME_BASED.toString();
		eventTypeDef = EventTypeDef.FILE.toString();
	}

	public void fillBaseInfosTab() {

		if (jobBasePanelBean.getJobProperties() != null) {

			BaseJobInfos baseJobInfos = jobBasePanelBean.getJobProperties().getBaseJobInfos();
			jobBasePanelBean.setJsCalendar(baseJobInfos.getCalendarId() + "");
			jobBasePanelBean.setoSystem(baseJobInfos.getOSystem().toString());
			jobPriority = baseJobInfos.getJobPriority().toString();
			jobTypeDef = baseJobInfos.getJobInfos().getJobTypeDef().toString();
			jobBaseType = baseJobInfos.getJobInfos().getJobBaseType().toString();

			if (jobBaseType.equals(JobBaseType.PERIODIC.toString()) && baseJobInfos.getPeriodInfo() != null) {
				PeriodInfo periodInfo = baseJobInfos.getPeriodInfo();
				String timeOutputFormat = new String("HH:mm:ss");
				periodTime = DefinitionUtils.calendarToStringTimeFormat(periodInfo.getStep(), jobBasePanelBean.getTimeManagementTabBean().getSelectedTZone(), timeOutputFormat);

				if (periodInfo.getMaxCount() != null) {
					maxCount = periodInfo.getMaxCount().intValue();
				}
			}

			if (jobTypeDef.equals(JobTypeDef.EVENT_BASED.toString())) {
				eventTypeDef = baseJobInfos.getJobInfos().getJobTypeDetails().getEventTypeDef().toString();
			}

			if (baseJobInfos.getJsIsActive().equals(JsIsActive.YES)) {
				jobBasePanelBean.setJsActive(true);
			} else {
				jobBasePanelBean.setJsActive(false);
			}

		} else {
			System.out.println("jobProperties is NULL in fillBaseInfosTab !!");
		}
	}

	public void fillBaseJobInfos() {
		
		BaseJobInfos baseJobInfos = jobBasePanelBean.getJobProperties().getBaseJobInfos();

		baseJobInfos.setCalendarId(Integer.parseInt(jobBasePanelBean.getJsCalendar()));
		baseJobInfos.setOSystem(OSystem.Enum.forString(jobBasePanelBean.getoSystem()));
		if (jobPriority.isEmpty())
			jobPriority = JobPriority.X_3.toString(); // "3"; // default değer
		baseJobInfos.setJobPriority(JobPriority.Enum.forString(jobPriority));

		if (jobBasePanelBean.isJsActive()) {
			baseJobInfos.setJsIsActive(JsIsActive.YES);
		} else {
			baseJobInfos.setJsIsActive(JsIsActive.NO);
		}

		JobInfos jobInfos = baseJobInfos.getJobInfos();
		jobInfos.setJobBaseType(JobBaseType.Enum.forString(jobBaseType));

		// periyodik is ise onunla ilgili alanlari dolduruyor
		if (jobBaseType.equals(JobBaseType.PERIODIC.toString())) {
			if(baseJobInfos.getPeriodInfo() == null) {
				baseJobInfos.addNewPeriodInfo();
			}
			PeriodInfo periodInfo = baseJobInfos.getPeriodInfo();
			periodInfo.setComment("No Comment");
			periodInfo.setStep(DefinitionUtils.dateToXmlTime(periodTime, jobBasePanelBean.getTimeManagementTabBean().getSelectedTZone()));

			if (maxCount > 0) {
				periodInfo.setMaxCount(BigInteger.valueOf(maxCount));
			}
		}

		jobInfos.setJobTypeDef(JobTypeDef.Enum.forString(jobTypeDef));

		// event tabanli bir is ise event turunu set ediyor
		if (jobTypeDef.equals(JobTypeDef.EVENT_BASED.toString())) {
			jobInfos.getJobTypeDetails().setEventTypeDef(EventTypeDef.Enum.forString(eventTypeDef));
		}

		baseJobInfos.setUserId(jobBasePanelBean.getWebAppUser().getId());
	}
	
	public JobProperties getJobProperties() {
		return jobBasePanelBean.getJobProperties();
	}
	
	public String getJsCalendar() {
		return jobBasePanelBean.getJsCalendar();
	}
	
	public void setJsCalendar(String jsCalendar) {
		jobBasePanelBean.setJsCalendar(jsCalendar);
	}

	public Collection<SelectItem> getJsCalendarList() {
		return jobBasePanelBean.getJsCalendarList();
	}

	public void setJsCalendarList(Collection<SelectItem> jsCalendarList) {
		jobBasePanelBean.setJsCalendarList(jsCalendarList);
	}

	public String getoSystem() {
		return jobBasePanelBean.getoSystem();
	}

	public void setoSystem(String oSystem) {
		jobBasePanelBean.setoSystem(oSystem);
	}

	public Collection<SelectItem> getoSystemList() {
		return jobBasePanelBean.getoSystemList();
	}

	public String getJobPriority() {
		return jobPriority;
	}

	public void setJobPriority(String jobPriority) {
		this.jobPriority = jobPriority;
	}

	public String getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(String periodTime) {
		this.periodTime = periodTime;
	}

	public Collection<SelectItem> getJobBaseTypeList() {
		return jobBasePanelBean.getJobBaseTypeList();
	}

	public String getJobBaseType() {
		return jobBaseType;
	}

	public void setJobBaseType(String jobBaseType) {
		this.jobBaseType = jobBaseType;
	}

	public Collection<SelectItem> getJobTypeDefList() {
		return jobBasePanelBean.getJobTypeDefList();
	}

	public String getJobTypeDef() {
		return jobTypeDef;
	}

	public void setJobTypeDef(String jobTypeDef) {
		this.jobTypeDef = jobTypeDef;
	}

	public Collection<SelectItem> getEventTypeDefList() {
		return jobBasePanelBean.getEventTypeDefList();
	}

	public String getEventTypeDef() {
		return eventTypeDef;
	}

	public void setEventTypeDef(String eventTypeDef) {
		this.eventTypeDef = eventTypeDef;
	}

	public JobBasePanelBean getJobBasePanelBean() {
		return jobBasePanelBean;
	}

	public void setJobBasePanelBean(JobBasePanelBean jobBasePanelBean) {
		this.jobBasePanelBean = jobBasePanelBean;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

}
