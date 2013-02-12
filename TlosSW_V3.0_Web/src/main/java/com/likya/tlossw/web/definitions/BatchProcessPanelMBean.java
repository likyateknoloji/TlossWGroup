package com.likya.tlossw.web.definitions;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;

@ManagedBean
@ViewScoped
public class BatchProcessPanelMBean extends JobBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(BatchProcessPanelMBean.class);

	private static final long serialVersionUID = 1703649221005735891L;

	private String jobPath;
	private String jobCommand;

	public void dispose() {

	}

	@PostConstruct
	public void init() {
		initJobPanel();

		jobPath = "";
		jobCommand = "";
	}

	public void fillTabs() {
		fillJobPanel();
		fillBatchProcessProperties();
	}

	private void fillBatchProcessProperties() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();

		jobPath = jobTypeDetails.getJobPath();
		jobCommand = jobTypeDetails.getJobCommand();
	}

	public void insertJobAction(ActionEvent e) {
		fillJobProperties();

		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobInfos().getJobTypeDetails();
		jobTypeDetails.setJobCommand(jobCommand);
		jobTypeDetails.setJobPath(jobPath);

		insertJobDefinition();
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
