package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.model.AlarmInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.utils.LiveUtils;
import com.likya.tlossw.webclient.TEJmxMpClient;
import com.likya.tlossw.webclient.TEJmxMpDBClient;

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
	
	private ArrayList<JobInfoTypeClient> jobBaseReportList;
	private transient DataTable jobBaseReportTable;
	
	private boolean transformToLocalTime;
	
	public void fillJobLivePanel(String groupId, String jobName) {
		setJobInfo(groupId, jobName);
		fillJobReportGrid();
		fillJobAlarmGrid();
	}
	
	public void setJobInfo(String groupId, String jobName) {
		jobInTyCl = new JobInfoTypeClient();
		jobInTyCl = TEJmxMpClient.getJobInfoTypeClient(new JmxUser(), groupId, jobName, transformToLocalTime);
		
		//her isin komut dosyasi yok
		if(jobInTyCl.getJobPath() != null && jobInTyCl.getJobCommand() != null) {
			jobCommandExist = TEJmxMpClient.checkFile(new JmxUser(), LiveUtils.getConcatenatedPathAndFileName(jobInTyCl.getJobPath(), jobInTyCl.getJobCommand()));
		}
		
		jobLogExist = TEJmxMpClient.checkFile(new JmxUser(), LiveUtils.getConcatenatedPathAndFileName(jobInTyCl.getJobLogPath(), jobInTyCl.getJobLogName()));
		
		jobDependencyListStr = "";
		if(jobInTyCl.getJobDependencyList() != null && jobInTyCl.getJobDependencyList().size() > 0) {
			for(String depJobName: jobInTyCl.getJobDependencyList()) {
				jobDependencyListStr += depJobName + ",";
			}
			jobDependencyListStr = jobDependencyListStr.substring(0, jobDependencyListStr.length() - 1);
		}
	}
	
	public void openJobCommandAction() {
		String jcmdStr = LiveUtils.getConcatenatedPathAndFileName(jobInTyCl.getJobPath().toString(), jobInTyCl.getJobCommand().toString());
		jobCommandStr = TEJmxMpClient.readFile(new JmxUser(), jcmdStr).toString();
	}
	
	public void openJobLogAction() {
		jobLog = TEJmxMpClient.readFile(new JmxUser(), LiveUtils.getConcatenatedPathAndFileName(jobInTyCl.getJobLogPath(), jobInTyCl.getJobLogName())).toString();
	}
	
	public void fillJobReportGrid() {
//		son 5 rundaki calisma listesini istiyor
		//jobBaseReportList = getDbOperations().getJobResultList(jobInTyCl.getJobId(), 5, transformToLocalTime);
	}
	
	public void fillJobAlarmGrid() {
		jobAlarmList = getDbOperations().getJobAlarmHistory(jobInTyCl.getJobId(), transformToLocalTime);
	}

	public void pauseJobAction(ActionEvent e) {
		TEJmxMpClient.pauseJob(new JmxUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.pause");*/
	}
	
	public void startJobAction(ActionEvent e) {
		TEJmxMpClient.startJob(new JmxUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.start");*/
	}
	
	//user based islerde kullanici ekrandan baslati sectiginde buraya geliyor
	public void startUserBasedJobAction(ActionEvent e) {
		TEJmxMpClient.startUserBasedJob(new JmxUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.start");*/
	}
	
	public void stopJobAction(ActionEvent e) {
		TEJmxMpClient.stopJob(new JmxUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.stop");*/
	}

	public void retryJobAction(ActionEvent e) {
		TEJmxMpClient.retryJob(new JmxUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.retry");*/
	}
	
	public void doSuccessJobAction(ActionEvent e) {
		TEJmxMpClient.doSuccess(new JmxUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();

		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.doSuccess");*/
	}

	public void skipJobAction(ActionEvent e) {
		TEJmxMpClient.skipJob(new JmxUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.skip");*/
	}
	
	public void resumeJobAction(ActionEvent e) {
		TEJmxMpClient.resumeJob(new JmxUser(), LiveUtils.jobPath(jobInTyCl));
		refreshLivePanel();
		
		/*TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.resume");*/
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

}
