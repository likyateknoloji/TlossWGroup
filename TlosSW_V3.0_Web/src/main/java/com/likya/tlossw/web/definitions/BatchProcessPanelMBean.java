package com.likya.tlossw.web.definitions;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;

@ManagedBean
@ViewScoped
public class BatchProcessPanelMBean extends JobBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(BatchProcessPanelMBean.class);

	private static final long serialVersionUID = 1703649221005735891L;

	private String jobPath;
	private String jobCommand;

	private boolean skip;

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

	public String onFlowProcess(FlowEvent event) {
		if (skip) {
			skip = false;
			return CONFIRM;

		} else {
			String newStep = event.getNewStep();

			return newStep;
		}
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

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
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
