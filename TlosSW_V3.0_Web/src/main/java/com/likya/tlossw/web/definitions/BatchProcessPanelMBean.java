package com.likya.tlossw.web.definitions;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.JobTypeDetailsDocument.JobTypeDetails;
import com.likya.tlossw.utils.FileUtils;

@ManagedBean
@ViewScoped
public class BatchProcessPanelMBean extends JobBasePanelBean implements Serializable {

	private static final Logger logger = Logger.getLogger(BatchProcessPanelMBean.class);

	private static final long serialVersionUID = 1703649221005735891L;

	private String jobPath;
	private String jobCommand;
	private StringBuffer jobFileText;
	private boolean isFileExist = false;
	
	public void dispose() {

	}

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
		if (getJobProperties() != null) {
			JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobTypeDetails();

			jobPath = jobTypeDetails.getJobPath();
			jobCommand = jobTypeDetails.getJobCommand();
		} else {
			System.out.println("getJobProperties() is NULL in fillBatchProcessProperties !!");
		}
	}

	public void fillJobPropertyDetails() {
		JobTypeDetails jobTypeDetails = getJobProperties().getBaseJobInfos().getJobTypeDetails();
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

	public StringBuffer getJobFileText() {
			jobFileText = FileUtils.readFile(jobPath + "\\" + jobCommand);
		return jobFileText;
	}

	public boolean isFileExist() {
			isFileExist = FileUtils.checkFileExist(jobPath + "\\" + jobCommand);
		return isFileExist;
	}
	public void setJobFileText(StringBuffer jobFileText) {
		this.jobFileText = jobFileText;
	}

}
