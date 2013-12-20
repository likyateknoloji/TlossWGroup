package com.likya.tlossw.web.definitions.helpers;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.data.BaseJobInfosDocument.BaseJobInfos;
import com.likya.tlos.model.xmlbeans.data.JobPriorityDocument.JobPriority;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlossw.web.definitions.JobBasePanelBean;

public class BaseJobInfosTabBean implements Serializable{

	private static final long serialVersionUID = 4999462516588657534L;

	public JobBasePanelBean jobBasePanelBean;
	
	private String jobPriority;

	public BaseJobInfosTabBean(JobBasePanelBean jobBasePanelBean) {
		
		super();
		resetTab();
		this.jobBasePanelBean = jobBasePanelBean;
		
	}
	
	public void resetTab() {
		jobPriority = JobPriority.X_1.toString();
	}

	public void fillBaseInfosTab() {

		if (jobBasePanelBean.getJobProperties() != null) {

			BaseJobInfos baseJobInfos = jobBasePanelBean.getJobProperties().getBaseJobInfos();
			//jobBasePanelBean.setJsCalendar(baseJobInfos.getCalendarId() + "");
			jobBasePanelBean.setoSystem(baseJobInfos.getOSystem().toString());
			jobPriority = baseJobInfos.getJobPriority().toString();

			if (baseJobInfos.getJsIsActive()) {
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

		//baseJobInfos.setCalendarId(Integer.parseInt(jobBasePanelBean.getJsCalendar()));
		baseJobInfos.setOSystem(OSystem.Enum.forString(jobBasePanelBean.getoSystem()));
		if (jobPriority.isEmpty())
			jobPriority = JobPriority.X_3.toString(); // "3"; // default değer
		baseJobInfos.setJobPriority(JobPriority.Enum.forString(jobPriority));

		if (jobBasePanelBean.isJsActive()) {
			baseJobInfos.setJsIsActive(true);
		} else {
			baseJobInfos.setJsIsActive(false);
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

	public JobBasePanelBean getJobBasePanelBean() {
		return jobBasePanelBean;
	}

	public void setJobBasePanelBean(JobBasePanelBean jobBasePanelBean) {
		this.jobBasePanelBean = jobBasePanelBean;
	}

}
