package com.likya.tlossw.web.live.helpers;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.likya.tlossw.model.auth.WebAppUser;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.web.live.JobManagementInterface;
import com.likya.tlossw.web.utils.LiveUtils;
import com.likya.tlossw.webclient.TEJmxMpClient;

public class LiveJobManagementBean implements Serializable {

	private static final long serialVersionUID = -6775403643497743131L;
	
	private JobManagementInterface jobManagementInterface;

	public LiveJobManagementBean(JobManagementInterface jobManagementInterface) {
		super();
		this.jobManagementInterface = jobManagementInterface;
	}

	public void pauseJobAction(ActionEvent e) {
		JobInfoTypeClient jobInfoTypeClient = getRowData();
		TEJmxMpClient.pauseJob(getWebAppUser(), LiveUtils.jobPath(jobInfoTypeClient));
		refreshLivePanel(jobInfoTypeClient.getTreePath());

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.pause");
		 */
	}

	// user based islerde kullanici ekrandan baslati sectiginde buraya geliyor
	public void startUserBasedJobAction(ActionEvent e) {
		JobInfoTypeClient jobInfoTypeClient = getRowData();
		TEJmxMpClient.startUserBasedJob(getWebAppUser(), LiveUtils.jobPath(jobInfoTypeClient));
		refreshLivePanel(jobInfoTypeClient.getTreePath());

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.start");
		 */
	}

	public void startJobAction(ActionEvent e) {
		JobInfoTypeClient jobInfoTypeClient = getRowData();
		TEJmxMpClient.startJob(getWebAppUser(), LiveUtils.jobPath(jobInfoTypeClient));
		refreshLivePanel(jobInfoTypeClient.getTreePath());

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.start");
		 */
	}

	public void retryJobAction(ActionEvent e) {
		JobInfoTypeClient jobInfoTypeClient = getRowData();
		TEJmxMpClient.retryJob(getWebAppUser(), LiveUtils.jobPath(jobInfoTypeClient));
		refreshLivePanel(jobInfoTypeClient.getTreePath());

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.retry");
		 */
	}

	public void doSuccessJobAction(ActionEvent e) {
		JobInfoTypeClient jobInfoTypeClient = getRowData();
		TEJmxMpClient.doSuccess(getWebAppUser(), LiveUtils.jobPath(jobInfoTypeClient));
		refreshLivePanel(jobInfoTypeClient.getTreePath());

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.doSuccess");
		 */
	}

	public void skipJobAction(ActionEvent e) {
		JobInfoTypeClient jobInfoTypeClient = getRowData();
		TEJmxMpClient.skipJob(getWebAppUser(), LiveUtils.jobPath(jobInfoTypeClient));
		refreshLivePanel(jobInfoTypeClient.getTreePath());

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.skip");
		 */
	}

	public void stopJobAction(ActionEvent e) {
		JobInfoTypeClient jobInfoTypeClient = getRowData();
		TEJmxMpClient.stopJob(getWebAppUser(), LiveUtils.jobPath(jobInfoTypeClient));
		refreshLivePanel(jobInfoTypeClient.getTreePath());

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.stop");
		 */
	}

	public void resumeJobAction(ActionEvent e) {
		JobInfoTypeClient jobInfoTypeClient = getRowData();
		TEJmxMpClient.resumeJob(getWebAppUser(), LiveUtils.jobPath(jobInfoTypeClient));
		refreshLivePanel(jobInfoTypeClient.getTreePath());

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.resume");
		 */
	}

	public JobInfoTypeClient getRowData() {
		return (JobInfoTypeClient) jobManagementInterface.getJobDataTable().getRowData();
	}

	public void refreshLivePanel(String scenarioPath) {
		jobManagementInterface.refreshLivePanel(scenarioPath);
	}

	public void refreshTlosAgentPanel() {
		jobManagementInterface.refreshTlosAgentPanel();
	}

	public WebAppUser getWebAppUser() {
		return jobManagementInterface.getWebAppUser();
	}

}
