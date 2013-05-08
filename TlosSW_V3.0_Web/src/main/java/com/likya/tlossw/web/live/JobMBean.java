package com.likya.tlossw.web.live;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.LiveUtils;
import com.likya.tlossw.webclient.TEJmxMpClient;
import com.likya.tlossw.webclient.TEJmxMpDBClient;

@ManagedBean(name = "jobMBean")
@ViewScoped
public class JobMBean extends TlosSWBaseBean implements Serializable  {

	private static final long serialVersionUID = -5989673026009812612L;

	private JobInfoTypeClient jobInTyCl;
	private JobProperties job;
	
	private boolean jobCommandExist;
	private String jobCommandStr;
	private boolean jobLogExist;
	
	private String jobDependencyListStr;
	
	private boolean transformToLocalTime;
	
	public void setJobInfo(String groupId, String jobId) {
		jobInTyClSetter(groupId, jobId);
		fillJobReportGrid();
		fillJobAlarmGrid();
	}
	
	public void jobInTyClSetter(String groupId, String jobId) {
		jobInTyCl = new JobInfoTypeClient();
		jobInTyCl = TEJmxMpClient.getJobInfoTypeClient(new JmxUser(), groupId, jobId, transformToLocalTime);
		
		//her isin komut dosyasi yok
		if(jobInTyCl.getJobPath() != null && jobInTyCl.getJobCommand() != null) {
			jobCommandExist = TEJmxMpClient.checkFile(new JmxUser(), LiveUtils.getConcatenatedPathAndFileName(jobInTyCl.getJobPath(), jobInTyCl.getJobCommand()));
		}
		
		jobLogExist = TEJmxMpClient.checkFile(new JmxUser(), LiveUtils.getConcatenatedPathAndFileName(jobInTyCl.getJobLogPath(), jobInTyCl.getJobLogName()));
		
		jobDependencyListStr = "";
		if(jobInTyCl.getJobDependencyList() != null && jobInTyCl.getJobDependencyList().size() > 0) {
			for(String jobName: jobInTyCl.getJobDependencyList()) {
				jobDependencyListStr += jobName + ",";
			}
			jobDependencyListStr = jobDependencyListStr.substring(0, jobDependencyListStr.length() - 1);
		}
	}
	
	public void fillJobReportGrid() {
//		son 5 rundaki calisma listesini istiyor
		//setJobBaseReportList(TEJmxMpDBClient.getJobResultList(ManagerMediator.getJmxUser(), jobInTyCl.getJobId(), 5, transformToLocalTime));
	}
	
	public void fillJobAlarmGrid() {
		//setJobAlarmList(TEJmxMpDBClient.jobAlarmHistory(ManagerMediator.getJmxUser(), jobInTyCl.getJobId(), transformToLocalTime));
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
}
