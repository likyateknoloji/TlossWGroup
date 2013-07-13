package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.likya.tlos.model.xmlbeans.alarm.SLAManagementDocument.SLAManagement;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.model.AlarmInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.utils.LiveUtils;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = "jobMBean")
@ViewScoped
public class JobMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -5989673026009812612L;

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	private JobInfoTypeClient jobInTyCl;
	private JobProperties job;

	private boolean jobCommandExist = false;
	private String jobCommandStr;

	private boolean jobLogExist = false;
	private String jobLog = "";

	private String jobDependencyListStr;

	private ArrayList<AlarmInfoTypeClient> jobAlarmList;
	private transient DataTable jobAlarmTable;

	private AlarmInfoTypeClient selectedAlarm;
	private Alarm selectedAlarmHistory;

	private String slaName;

	private ArrayList<JobInfoTypeClient> jobBaseReportList;
	private transient DataTable jobBaseReportTable;

	private JobInfoTypeClient selectedJobBaseReport;

	private boolean transformToLocalTime;

	public void fillJobLivePanel(String groupId, String jobName) {
		setJobInfo(groupId, jobName);
		fillJobReportGrid();
		fillJobAlarmGrid();
	}

	public void setJobInfo(String groupId, String jobName) {
		jobInTyCl = new JobInfoTypeClient();
		jobInTyCl = TEJmxMpClient.getJobInfoTypeClient(getWebAppUser(), groupId, jobName, transformToLocalTime);

		// her isin komut dosyasi yok
		if (jobInTyCl.getJobPath() != null && jobInTyCl.getJobCommand() != null) {
			jobCommandExist = TEJmxMpClient.checkFile(getWebAppUser(), LiveUtils.getConcatenatedPathAndFileName(jobInTyCl.getJobPath(), jobInTyCl.getJobCommand()));
		}

		jobLogExist = TEJmxMpClient.checkFile(getWebAppUser(), LiveUtils.getConcatenatedPathAndFileName(jobInTyCl.getJobLogPath(), jobInTyCl.getJobLogName()));

		jobDependencyListStr = "";
		if (jobInTyCl.getJobDependencyList() != null && jobInTyCl.getJobDependencyList().size() > 0) {
			for (String depJobName : jobInTyCl.getJobDependencyList()) {
				jobDependencyListStr += depJobName + ",";
			}
			jobDependencyListStr = jobDependencyListStr.substring(0, jobDependencyListStr.length() - 1);
		}
	}

	public void openJobCommandAction() {
		openJobCommandAction(true);
	}

	public void openJobCommandFromReportAction() {
		openJobCommandAction(false);
	}

	private void openJobCommandAction(boolean current) {
		JobInfoTypeClient jobDef;
		if (current) {
			jobDef = jobInTyCl;
		} else {
			jobDef = selectedJobBaseReport;
		}

		String jcmdStr = LiveUtils.getConcatenatedPathAndFileName(jobDef.getJobPath().toString(), jobDef.getJobCommand().toString());
		jobCommandStr = TEJmxMpClient.readFile(getWebAppUser(), jcmdStr).toString();
	}

	public void openJobLogAction() {
		openJobLogAction(true);
	}

	public void openJobLogFromReportAction() {
		openJobLogAction(false);
	}

	private void openJobLogAction(boolean current) {
		JobInfoTypeClient jobDef;
		if (current) {
			jobDef = jobInTyCl;
		} else {
			jobDef = selectedJobBaseReport;
		}

		jobLog = TEJmxMpClient.readFile(getWebAppUser(), LiveUtils.getConcatenatedPathAndFileName(jobDef.getJobLogPath(), jobDef.getJobLogName())).toString();
	}

	public void fillJobReportGrid() {
		// son 3 rundaki calisma listesini istiyor
		jobBaseReportList = getDbOperations().getJobResultList(getWebAppUser().getId(), getDocumentId(), jobInTyCl.getJobId(), 3, transformToLocalTime);
	}

	public void fillJobAlarmGrid() {
		jobAlarmList = getDbOperations().getJobAlarmHistory(jobInTyCl.getJobId(), transformToLocalTime);
	}

	// gecmis alarm listesindeki bir alarmin adini tiklayinca buraya geliyor, popup acip ayrinti bilgilerini gosteriyor
	public void openAlarmDetailAction() {
		selectedAlarm = (AlarmInfoTypeClient) jobAlarmTable.getRowData();
		selectedAlarmHistory = getDbOperations().getAlarmHistoryById(Integer.parseInt(selectedAlarm.getAlarmHistoryId()));
		job = getDbOperations().getJobFromId(getWebAppUser().getId(), getDocumentId(), jobInTyCl.getJobId());

		if (selectedAlarm.getAlarmType().equals("SLA")) {
			if (selectedAlarmHistory.getCaseManagement().getSLAManagement().equals(SLAManagement.YES)) {
				slaName = getDbOperations().searchSlaByID(job.getAdvancedJobInfos().getSLAId() + "").getName();
			}
		}
	}

	public void openReportDetailAction() {
		selectedJobBaseReport = (JobInfoTypeClient) jobBaseReportTable.getRowData();
	}

	public void pauseJobAction(ActionEvent e) {
		TEJmxMpClient.pauseJob(getWebAppUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.pause");
		 */
	}

	public void startJobAction(ActionEvent e) {
		TEJmxMpClient.startJob(getWebAppUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.start");
		 */
	}

	// user based islerde kullanici ekrandan baslati sectiginde buraya geliyor
	public void startUserBasedJobAction(ActionEvent e) {
		TEJmxMpClient.startUserBasedJob(getWebAppUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.start");
		 */
	}

	public void stopJobAction(ActionEvent e) {
		TEJmxMpClient.stopJob(getWebAppUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.stop");
		 */
	}

	public void retryJobAction(ActionEvent e) {
		TEJmxMpClient.retryJob(getWebAppUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.retry");
		 */
	}

	public void doSuccessJobAction(ActionEvent e) {
		TEJmxMpClient.doSuccess(getWebAppUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.doSuccess");
		 */
	}

	public void skipJobAction(ActionEvent e) {
		TEJmxMpClient.skipJob(getWebAppUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.skip");
		 */
	}

	public void resumeJobAction(ActionEvent e) {
		TEJmxMpClient.resumeJob(getWebAppUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();

		/*
		 * TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), "tlos.trace.live.job.resume");
		 */
	}

	private void refreshLivePanel() {
		fillJobLivePanel(jobInTyCl.getTreePath(), jobInTyCl.getJobId());

		RequestContext context = RequestContext.getCurrentInstance();
		context.update("liveForm");
	}

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}

	public JobInfoTypeClient getJobInTyCl() {
		return jobInTyCl;
	}

	public void setJobInTyCl(JobInfoTypeClient jobInTyCl) {
		this.jobInTyCl = jobInTyCl;
	}

	public JobProperties getJob() {
		return job;
	}

	public void setJob(JobProperties job) {
		this.job = job;
	}

	public boolean isJobCommandExist() {
		return jobCommandExist;
	}

	public void setJobCommandExist(boolean jobCommandExist) {
		this.jobCommandExist = jobCommandExist;
	}

	public String getJobCommandStr() {
		return jobCommandStr;
	}

	public void setJobCommandStr(String jobCommandStr) {
		this.jobCommandStr = jobCommandStr;
	}

	public boolean isJobLogExist() {
		return jobLogExist;
	}

	public void setJobLogExist(boolean jobLogExist) {
		this.jobLogExist = jobLogExist;
	}

	public String getJobDependencyListStr() {
		return jobDependencyListStr;
	}

	public void setJobDependencyListStr(String jobDependencyListStr) {
		this.jobDependencyListStr = jobDependencyListStr;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public ArrayList<AlarmInfoTypeClient> getJobAlarmList() {
		return jobAlarmList;
	}

	public void setJobAlarmList(ArrayList<AlarmInfoTypeClient> jobAlarmList) {
		this.jobAlarmList = jobAlarmList;
	}

	public DataTable getJobAlarmTable() {
		return jobAlarmTable;
	}

	public void setJobAlarmTable(DataTable jobAlarmTable) {
		this.jobAlarmTable = jobAlarmTable;
	}

	public String getJobLog() {
		return jobLog;
	}

	public void setJobLog(String jobLog) {
		this.jobLog = jobLog;
	}

	public ArrayList<JobInfoTypeClient> getJobBaseReportList() {
		return jobBaseReportList;
	}

	public void setJobBaseReportList(ArrayList<JobInfoTypeClient> jobBaseReportList) {
		this.jobBaseReportList = jobBaseReportList;
	}

	public DataTable getJobBaseReportTable() {
		return jobBaseReportTable;
	}

	public void setJobBaseReportTable(DataTable jobBaseReportTable) {
		this.jobBaseReportTable = jobBaseReportTable;
	}

	public AlarmInfoTypeClient getSelectedAlarm() {
		return selectedAlarm;
	}

	public void setSelectedAlarm(AlarmInfoTypeClient selectedAlarm) {
		this.selectedAlarm = selectedAlarm;
	}

	public Alarm getSelectedAlarmHistory() {
		return selectedAlarmHistory;
	}

	public void setSelectedAlarmHistory(Alarm selectedAlarmHistory) {
		this.selectedAlarmHistory = selectedAlarmHistory;
	}

	public String getSlaName() {
		return slaName;
	}

	public void setSlaName(String slaName) {
		this.slaName = slaName;
	}

	public JobInfoTypeClient getSelectedJobBaseReport() {
		return selectedJobBaseReport;
	}

	public void setSelectedJobBaseReport(JobInfoTypeClient selectedJobBaseReport) {
		this.selectedJobBaseReport = selectedJobBaseReport;
	}

}
