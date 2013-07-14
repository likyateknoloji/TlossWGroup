package com.likya.tlossw.web.definitions;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;

@ManagedBean
@ViewScoped
public class SystemCommandPanelMBean extends JobBasePanelBean implements Serializable {

	private static final Logger logger = Logger.getLogger(SystemCommandPanelMBean.class);

	private static final long serialVersionUID = -344116824165137597L;

	private String jobPath;
	private String jobCommand;

	public void dispose() {

	}

	@PostConstruct
	public void init() {
		initJobPanel();
	}

	public void fillTabs() {
		fillJobPanel();

		jobPath = "";
		jobCommand = "";

		fillSystemCommandProperties();
	}

	private void fillSystemCommandProperties() {
		if (getJobProperties() != null) {
			JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();

			jobPath = jobTypeDetails.getJobPath();
			jobCommand = jobTypeDetails.getJobCommand();

		} else {
			System.out.println("getJobProperties() is NULL in fillSystemCommandProperties !!");
		}
	}

	public void insertJsAction() {
		if (validateTimeManagement()) {
			fillJobProperties();
			fillJobPropertyDetails();

			insertJobDefinition();
		}
	}

	public void updateJsAction() {
		fillJobProperties();
		fillJobPropertyDetails();

		updateJobDefinition();
	}

	public void fillJobPropertyDetails() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
		jobTypeDetails.setJobCommand(jobCommand);
		jobTypeDetails.setJobPath(jobPath);
	}

	public static Logger getLogger() {
		return logger;
	}

	public String getJobPath() {
		return jobPath;
	}

	public void setJobPath(String jobPath) {
		this.jobPath = jobPath;
	}

	public String getJobCommand() {
		return jobCommand;
	}

	public void setJobCommand(String jobCommand) {
		this.jobCommand = jobCommand;
	}

}
