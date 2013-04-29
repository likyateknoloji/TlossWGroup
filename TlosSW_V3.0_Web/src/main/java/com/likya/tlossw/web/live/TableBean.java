package com.likya.tlossw.web.live;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.webclient.TEJmxMpClient;

import com.likya.tlossw.web.live.LiveMBean;

@ViewScoped
public class TableBean {

	private JobProperties jobProperties;
	private LiveMBean liveMBean;
	
	TableBean() {
		liveMBean = new LiveMBean();
	}
	
	public void onNodeSelect(NodeSelectEvent event) {
		addMessage("jobTree", FacesMessage.SEVERITY_INFO, event.getTreeNode().toString() + " selected", null);
		
//		jobProperties = JobProperties.Factory.newInstance();
//		
//		BaseJobInfos baseJobInfos = BaseJobInfos.Factory.newInstance();
//		baseJobInfos.setJsName("hakan");
//		
//		JobInfos jobInfos = JobInfos.Factory.newInstance();
//		JobTypeDetails jobTypeDetails = JobTypeDetails.Factory.newInstance();
//		jobInfos.setJobTypeDetails(jobTypeDetails);
//		baseJobInfos.setJobInfos(jobInfos);
//		jobProperties.setBaseJobInfos(baseJobInfos);
		String selectedJob = event.getTreeNode().toString();
		String jobId = selectedJob.substring(selectedJob.lastIndexOf("|") + 1);
		String jobAbsolutePath = selectedJob.substring(0, selectedJob.lastIndexOf("|")-1);
		
		//sunucudan guncel job listelerini aliyor
		List<JobInfoTypeClient> newJobList = (ArrayList<JobInfoTypeClient>) TEJmxMpClient.getJobInfoTypeClientList(new JmxUser(), liveMBean.getNewjobSenaryoId(), liveMBean.isTransformToLocalTime());
		
		for(int i=0; i<newJobList.size(); i++) {
			newJobList.get(i).getAgentId();
		}

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("jobMonitoringForm");
	}
	
	public void addMessage(String fieldName, FacesMessage.Severity severity, String errorMessage, String miscText) {
		errorMessage = resolveMessage(errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, errorMessage, miscText));
	}

	private String resolveMessage(String errorMessage) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public JobProperties getJobProperties() {
		return jobProperties;
	}

	public void setJobProperties(JobProperties jobProperties) {
		this.jobProperties = jobProperties;
	}
	
}
