package com.likya.tlossw.web.live;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlossw.web.appmng.TraceBean;
import com.likya.tlossw.web.common.WebConstants;

import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;

import com.likya.tlossw.model.AlarmInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcLookUpTableTypeClient;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.webclient.TEJmxMpClient;
import com.likya.tlossw.webclient.TEJmxMpDBClient;

import com.likya.tlossw.utils.date.DateUtils;

public class LiveMBean_old implements Serializable {

	private static final long serialVersionUID = 6301731898158591712L;

	private static Log log = LogFactory.getLog(LiveMBean_old.class);
	
	private List<SpcInfoTypeClient> senaryoList;
	private DataTable senaryoDataTable;

	private List<JobInfoTypeClient> jobInfoList;
	private DataTable jobDataTable;

	private SpcInfoTypeClient spcInfoTypeClient;

	private boolean systemPopup = false;
	private boolean jobLogPopup = false;

	private JobInfoTypeClient jobInTyCl;
	private JobProperties job;

	private boolean jobCommandExist;
	private String jobCommandStr;
	private boolean jobLogExist;

	private Collection<SelectItem> statuFilter;
	private Long statuFilterId;
	private Long tmpStatuFilterId = new Long(1);

	private String jobLog = "";

	private JobInfoTypeClient newJobInfoTypeClient;
	private String newjobSenaryoId;
	private String[] jobDependecyList;
	private Collection<SelectItem> senaryoSelectList;
	private List<SelectItem> senaryoJobList;
	private boolean newJobMessage = false;

	private ArrayList<JobInfoTypeClient> jobBaseReportList;
	private DataTable jobBaseReportTable;
	
	private ArrayList<AlarmInfoTypeClient> jobAlarmList;
	private DataTable jobAlarmTable;
	
	private AlarmInfoTypeClient selectedAlarm;
	private Alarm selectedAlarmHistory;
	
	private String slaName;

	private Collection<SelectItem> resourceListForJob;
	private String selectedResource;
	
	private String jobDependencyListStr;
	
	private boolean transformToLocalTime = false;
	
	public void dispose() {
		senaryoList = null;
		jobInfoList = null;
		spcInfoTypeClient = null;
		jobInTyCl = null;
		newJobInfoTypeClient = null;
	}

	public void init() {

	}

	public void getNewJobDependencyList() {
		if (newjobSenaryoId != null) {
			senaryoJobList = new ArrayList<SelectItem>();
			List<JobInfoTypeClient> newJobList = (ArrayList<JobInfoTypeClient>) TEJmxMpClient.getJobInfoTypeClientList(new JmxUser(), newjobSenaryoId, isTransformToLocalTime());
			for (JobInfoTypeClient job : newJobList) {
				senaryoJobList.add(new SelectItem(job.getJobKey(), job.getJobKey()));
			}
		}
	}

	public Collection<SelectItem> getSenaryoSelectList() {
		if (senaryoSelectList == null) {
			senaryoSelectList = new ArrayList<SelectItem>();
			for (SpcInfoTypeClient senaryo : senaryoList) {
				senaryoSelectList.add(new SelectItem(senaryo.getSpcId(), senaryo.getSpcId()));
			}
		}

		return senaryoSelectList;
	}

	public void resetNewJobPanel() {
		newJobInfoTypeClient = new JobInfoTypeClient();
		jobDependecyList = null;
		newjobSenaryoId = null;
	}

	public void filterSenaryo() {
		List<JobInfoTypeClient> jobList = new ArrayList<JobInfoTypeClient>();

		if (statuFilterId != null) {
			if (statuFilterId.intValue() == WebConstants.JOBFILTER_LIST_ALL) {
				jobList = jobInfoList;
			} else if (statuFilterId.intValue() == WebConstants.JOBFILTER_READY) {
				for (JobInfoTypeClient jobInfo : jobInfoList) {
					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.FINISHED) 
							&& jobInfo.getLiveStateInfo().getSubstateName().equals(SubstateName.READY))
						jobList.add(jobInfo);
				}
			} else if (statuFilterId.intValue() == WebConstants.JOBFILTER_WORKING) {
				for (JobInfoTypeClient jobInfo : jobInfoList) {
//					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.WORKING))
					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.RUNNING))
						jobList.add(jobInfo);
				}
			} else if (statuFilterId.intValue() == WebConstants.JOBFILTER_SUCCESSFUL) {
				for (JobInfoTypeClient jobInfo : jobInfoList) {
					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.FINISHED) 
							&& jobInfo.getLiveStateInfo().getSubstateName().equals(SubstateName.COMPLETED)
							&& jobInfo.getLiveStateInfo().getStatusName().equals(StatusName.SUCCESS))
						jobList.add(jobInfo);
				}
			} else if (statuFilterId.intValue() == WebConstants.JOBFILTER_TIME_OUT) {
				for (JobInfoTypeClient jobInfo : jobInfoList) {
					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.FINISHED) 
//							&& jobInfo.getLiveStateInfo().getSubstateName().equals(SubstateName.TIME_OUT))
							&& jobInfo.getLiveStateInfo().getSubstateName().equals(SubstateName.ON_RESOURCE)
							&& jobInfo.getLiveStateInfo().getSubstateName().equals(StatusName.TIME_OUT))
						jobList.add(jobInfo);
				}
			} else if (statuFilterId.intValue() == WebConstants.JOBFILTER_WAITING) {
				for (JobInfoTypeClient jobInfo : jobInfoList) {
//					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.WAITING))
					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.PENDING) 
							&& jobInfo.getLiveStateInfo().getStateName().equals(SubstateName.READY) 
							&& jobInfo.getLiveStateInfo().getStateName().equals(StatusName.WAITING))
						jobList.add(jobInfo);
				}
			} else if (statuFilterId.intValue() == WebConstants.JOBFILTER_PAUSED) {
				for (JobInfoTypeClient jobInfo : jobInfoList) {
					if (jobInfo.getLiveStateInfo().getSubstateName().equals(SubstateName.PAUSED))
						jobList.add(jobInfo);
				}
			} else if (statuFilterId.intValue() == WebConstants.JOBFILTER_FAILED) {
				for (JobInfoTypeClient jobInfo : jobInfoList) {
					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.FINISHED) 
							&& jobInfo.getLiveStateInfo().getSubstateName().equals(SubstateName.COMPLETED)
							&& jobInfo.getLiveStateInfo().getStatusName().equals(StatusName.FAILED))
						jobList.add(jobInfo);
				}
			} else if (statuFilterId.intValue() == WebConstants.JOBFILTER_SKIPPED) {
				for (JobInfoTypeClient jobInfo : jobInfoList) {
					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.FINISHED) 
							&& jobInfo.getLiveStateInfo().getSubstateName().equals(SubstateName.SKIPPED))
						jobList.add(jobInfo);
				}
			} else if (statuFilterId.intValue() == WebConstants.JOBFILTER_STOPPED) {
				for (JobInfoTypeClient jobInfo : jobInfoList) {
					if (jobInfo.getLiveStateInfo().getStateName().equals(StateName.FINISHED) 
							&& jobInfo.getLiveStateInfo().getSubstateName().equals(SubstateName.STOPPED))
						jobList.add(jobInfo);
				}
			} 
			
			jobInfoList = jobList;
		}

	}

	
	public void forwardPageAction() {
		String jcmdStr= getConcatenatedPathAndFileName(jobInTyCl.getJobPath().toString(), jobInTyCl.getJobCommand().toString());
		jobCommandStr = TEJmxMpClient.readFile(new JmxUser(), jcmdStr).toString();
		systemPopup = true;
	}

	public String forwardPage2() {
		return "nextpage";
	}

	public void getJobList(String scenarioId) {
		SpcInfoTypeClient spcInfoTypeClient = TEJmxMpClient.retrieveSpcInfo(new JmxUser(), scenarioId);

		spcInfoTypeClient.setSpcId(spcInfoTypeClient.getSpcId());
		spcInfoTypeClient.setNumOfActiveJobs(spcInfoTypeClient.getNumOfActiveJobs());
		spcInfoTypeClient.setNumOfJobs(spcInfoTypeClient.getNumOfJobs());
		spcInfoTypeClient.setPausable(spcInfoTypeClient.getPausable());
		spcInfoTypeClient.setResumable(spcInfoTypeClient.getResumable());
		spcInfoTypeClient.setStopable(spcInfoTypeClient.getStopable());
		spcInfoTypeClient.setStartable(spcInfoTypeClient.getStartable());

		setSpcInfoTypeClient(spcInfoTypeClient);

		jobInfoList = (ArrayList<JobInfoTypeClient>) TEJmxMpClient.getJobInfoTypeClientList(new JmxUser(), getSpcInfoTypeClient().getSpcId(), isTransformToLocalTime());
	}
	
//	public void getSenaryolar(LiveNavigationContentBean navigationBean) {
//		if (navigationBean != null && navigationBean.getScenarioFolder().isInstance) {
//			senaryoList = new ArrayList<SpcInfoTypeClient>();
//			log.info("LiveMBean : getSenaryolar JMX Begin :" + DateUtils.getCurrentTimeWithMilliseconds());
//			SpcLookUpTableTypeClient spcLookUpTableTypeClient = TEJmxMpClient.getSpsLookUpTable(new JmxUser(), navigationBean.getScenarioFolder().getInstanceId(), null);
//			log.info("LiveMBean : getSenaryolar JMX Begin :" + DateUtils.getCurrentTimeWithMilliseconds());
//			HashMap<String, SpcInfoTypeClient> spcInfoTypeClientList = spcLookUpTableTypeClient.getSpcInfoTypeClientList();
//
//			Iterator<String> keyIterator = spcInfoTypeClientList.keySet().iterator();
//
//			while (keyIterator.hasNext()) {
//				String scenarioId = keyIterator.next();
//				senaryoList.add(spcInfoTypeClientList.get(scenarioId));
//			}
//		}
//	}

	public static String getConcatenatedPathAndFileName(String path, String fileName) {
		if (path.indexOf('\\') >= 0) {
			if (path.lastIndexOf('\\') == path.length() - 1) {
				path = path.substring(0, path.length() - 1);
			}
			path = path + "\\";
		} else if (path.indexOf('/') >= 0) {
			if (path.lastIndexOf('/') == path.length() - 1) {
				path = path.substring(0, path.length() - 1);
			}
			path = path + "/";
		}

		return path + fileName;
	}
	
	public void jobInTyClSetter(String groupId, String jobId) {
		jobInTyCl = new JobInfoTypeClient();
		jobInTyCl = TEJmxMpClient.getJobInfoTypeClient(new JmxUser(), groupId, jobId, isTransformToLocalTime());
		
		//her isin komut dosyasi yok
		if(jobInTyCl.getJobPath() != null && jobInTyCl.getJobCommand() != null) {
			jobCommandExist = TEJmxMpClient.checkFile(new JmxUser(), getConcatenatedPathAndFileName(jobInTyCl.getJobPath(), jobInTyCl.getJobCommand()));
		}
		
		jobLogExist = TEJmxMpClient.checkFile(new JmxUser(), getConcatenatedPathAndFileName(jobInTyCl.getJobLogPath(), jobInTyCl.getJobLogName()));
		
		jobDependencyListStr = "";
		if(jobInTyCl.getJobDependencyList() != null && jobInTyCl.getJobDependencyList().size() > 0) {
			for(String jobName: jobInTyCl.getJobDependencyList()) {
				jobDependencyListStr += jobName + ",";
			}
			jobDependencyListStr = jobDependencyListStr.substring(0, jobDependencyListStr.length() - 1);
		}
	}

	public void fillJobReportGrid() {
//		int jobId = Integer.parseInt(jobInTyCl.getJobId());
//		job = TEJmxMpDBClient.getJobFromId(new JmxUser(), jobId);
//		String treePath = constructTreePath(navBean);
		
//		son 5 rundaki calisma listesini istiyor
		setJobBaseReportList(TEJmxMpDBClient.getJobResultList(new JmxUser(), "XXX", jobInTyCl.getJobId(), 5, isTransformToLocalTime()));
	}
	
	public void fillJobAlarmGrid() {
		setJobAlarmList(TEJmxMpDBClient.jobAlarmHistory(new JmxUser(), jobInTyCl.getJobId(), isTransformToLocalTime()));
	}
	
	/*public String generatePathForReportJob(String treePath) {
		String path = "/TlosProcessDataAll/RUN" + treePath + "/dat:jobProperties";
		return path;
	}
	
	public String constructTreePath(LiveNavigationContentBean navBean) {
		ArrayList<String> droppedBeanTreePath = new ArrayList<String>();
		DefaultMutableTreeNode treeNode = navBean.getWrapper();
		
		int threshold;
		if(treeNode.getPath()[0].toString().contains("instance")) {
			//0. dugum instance dugumu oldugu icin alinmiyor
			threshold = 0;
		} else {
			//0,1 ve 2 sirasiyla Space Wide, Gunluk Isler ve instance dugumleri oldugu icin alinmiyor
			threshold = 2;
		}
		
		for (int i = 0; i < treeNode.getPath().length; i++) {
			
			if (i > threshold) {
				droppedBeanTreePath.add(((LiveNavigationContentBean) ((DefaultMutableTreeNode) treeNode.getPath()[i]).getUserObject()).getText());
			}
		}

		String path = "/dat:TlosProcessData";

		for (int i = 0; i < droppedBeanTreePath.size(); i++) {
			
			if (!droppedBeanTreePath.get(i).equals("Serbest") && i != droppedBeanTreePath.size() - 1) {
				path = path + "/dat:scenario/dat:baseScenarioInfos[com:jsName = '" + droppedBeanTreePath.get(i) + "']/..";
			}
		}

		path = path + "/dat:jobList";

		return path;
	}*/
	
	
	// path hesaplanirkenki '.' ayracini '|' ile degistirdim, cunku job adinda '.' oldugu zaman sorun cikiyor
	public String jobPath(JobInfoTypeClient job) {
		String jobPath = new String();
		jobPath = job.getTreePath() + "|" + job.getJobKey();
		return jobPath;
	}

	public void systemPopupKapa() {
		systemPopup = false;
	}

	public void jobLogPopupOpenAction() {
		jobLog = TEJmxMpClient.readFile(new JmxUser(), getConcatenatedPathAndFileName(jobInTyCl.getJobLogPath(), jobInTyCl.getJobLogName())).toString();
		jobLogPopup = true;
	}

	public void jobLogPopUpKapat() {
		jobLogPopup = false;
	}
	
	public void startJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) jobDataTable.getRowData();
		startJob(job);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.start");
	}
	
	public void startJobActionFromJobPanel(ActionEvent e) {
		startJob(jobInTyCl);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.start");
	}
	
	public void startJob(JobInfoTypeClient job) {
		TEJmxMpClient.startJob(new JmxUser(), jobPath(job));
		getJobList(job.getTreePath());
	}
	
	//user based islerde kullanici ekrandan baslati sectiginde buraya geliyor
	public void startUserBasedJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) jobDataTable.getRowData();
		TEJmxMpClient.startUserBasedJob(new JmxUser(), jobPath(job));
		getJobList(job.getTreePath());
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.start");
	}
	
	public void stopJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) jobDataTable.getRowData();
		stopJob(job);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.stop");
	}
	
	public void stopJobActionFromJobPanel(ActionEvent e) {
		stopJob(jobInTyCl);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.stop");
	}
	
	public void stopJob(JobInfoTypeClient job) {
		TEJmxMpClient.stopJob(new JmxUser(), jobPath(job));
		getJobList(job.getTreePath());
	}

	public void retryJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) jobDataTable.getRowData();
		retryJob(job);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.retry");
	}
	
	public void retryJobActionFromJobPanel(ActionEvent e) {
		retryJob(jobInTyCl);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.retry");
	}
	
	public void retryJob(JobInfoTypeClient job) {
		TEJmxMpClient.retryJob(new JmxUser(), jobPath(job));
		getJobList(job.getTreePath());
	}

	public void doSuccessJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) jobDataTable.getRowData();
		doSuccessJob(job);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.doSuccess");
	}
	
	public void doSuccessJobActionFromJobPanel(ActionEvent e) {
		doSuccessJob(jobInTyCl);

		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.doSuccess");
	}
	
	public void doSuccessJob(JobInfoTypeClient job) {
		TEJmxMpClient.doSuccess(new JmxUser(), jobPath(job));
		getJobList(job.getTreePath());
	}

	public void skipJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) jobDataTable.getRowData();
		skipJob(job);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.skip");
	}
	
	public void skipJobActionFromJobPanel(ActionEvent e) {
		skipJob(jobInTyCl);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.skip");
	}
	
	public void skipJob(JobInfoTypeClient job) {
		TEJmxMpClient.skipJob(new JmxUser(), jobPath(job));
		getJobList(job.getTreePath());
	}

	public void pauseJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) jobDataTable.getRowData();
		pauseJob(job);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.pause");
	}
	
	public void pauseJobActionFromJobPanel(ActionEvent e) {
		pauseJob(jobInTyCl);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.pause");
	}
	
	public void pauseJob(JobInfoTypeClient job) {
		TEJmxMpClient.pauseJob(new JmxUser(), jobPath(job));
		getJobList(job.getTreePath());
	}

	public void resumeJobAction(ActionEvent e) {
		JobInfoTypeClient job = (JobInfoTypeClient) jobDataTable.getRowData();
		resumeJob(job);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + job.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.resume");
	}
	
	public void resumeJobActionFromJobPanel(ActionEvent e) {
		resumeJob(jobInTyCl);
		
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + jobInTyCl.getJobKey(), e.getComponent().getId(), 
				"tlos.trace.live.job.resume");
	}
	
	public void resumeJob(JobInfoTypeClient job) {
		TEJmxMpClient.resumeJob(new JmxUser(), jobPath(job));
		getJobList(job.getTreePath());
	}

	public void stopScenarioNormalAction(ActionEvent e) {
		TEJmxMpClient.stopScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId(), false);
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.stop.normal");
	}

	public void stopScenarioForcedAction(ActionEvent e) {
		TEJmxMpClient.stopScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId(), true);
		getJobList(getSpcInfoTypeClient().getSpcId());
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.stop.force");
	}

	public void pauseScenarioAction(ActionEvent e) {
		TEJmxMpClient.suspendScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId());
		getJobList(getSpcInfoTypeClient().getSpcId());
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.pause");
	}

	public void resumeScenarioAction(ActionEvent e) {
		TEJmxMpClient.resumeScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId());
		getJobList(getSpcInfoTypeClient().getSpcId());
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.resume");
	}

	public void startScenarioAction(ActionEvent e) {
		TEJmxMpClient.restartScenario(new JmxUser(), getSpcInfoTypeClient().getSpcId());
		getJobList(getSpcInfoTypeClient().getSpcId());
		TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + getSpcInfoTypeClient().getSpcId(), e.getComponent().getId(), 
				"tlos.trace.live.scenario.start");
	}
	
	public List<JobInfoTypeClient> getJobInfoList() {
		return jobInfoList;
	}

	public void setJobInfoList(List<JobInfoTypeClient> jobInfoList) {
		this.jobInfoList = jobInfoList;
	}

	public DataTable getJobDataTable() {
		return jobDataTable;
	}

	public void setJobDataTable(DataTable jobDataTable) {
		this.jobDataTable = jobDataTable;
	}

	public boolean isSystemPopup() {
		return systemPopup;
	}

	public void setSystemPopup(boolean systemPopup) {
		this.systemPopup = systemPopup;
	}

	public JobInfoTypeClient getJobInTyCl() {
		return jobInTyCl;
	}

	public void setJobInTyCl(JobInfoTypeClient jobInTyCl) {
		this.jobInTyCl = jobInTyCl;
	}

	public String getJobCommandStr() {
		return jobCommandStr;
	}

	public void setJobCommandStr(String jobCommandStr) {
		this.jobCommandStr = jobCommandStr;
	}

	public boolean isJobCommandExist() {
		return jobCommandExist;
	}

	public void setJobCommandExist(boolean jobCommandExist) {
		this.jobCommandExist = jobCommandExist;
	}

	public Collection<SelectItem> getStatuFilter() {
		if (statuFilter == null) {
			statuFilter = new ArrayList<SelectItem>();
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_LIST_ALL), "List All"));
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_READY), "Ready Jobs"));
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_WORKING), "Working Jobs"));
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_SUCCESSFUL), "Successful Jobs"));
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_TIME_OUT), "Time-out Jobs"));
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_WAITING), "Waiting Jobs"));
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_PAUSED), "Paused Jobs"));
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_FAILED), "Failed Jobs"));
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_SKIPPED), "Skipped Jobs"));
			statuFilter.add(new SelectItem(new Long(WebConstants.JOBFILTER_STOPPED), "Stopped Jobs"));
		}
		return statuFilter;
	}

	/*public String getJobPropertiesXML(JobProperties job) {
		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String jobPropertiesXML = job.xmlText(xmlOptions);

		return jobPropertiesXML;
	}*/
	
	public void setStatuFilter(Collection<SelectItem> statuFilter) {
		this.statuFilter = statuFilter;
	}

	public Long getStatuFilterId() {
		return statuFilterId;
	}

	public void setStatuFilterId(Long statuFilterId) {
		this.statuFilterId = statuFilterId;
		if (statuFilterId.longValue() != tmpStatuFilterId.longValue()) {
			filterSenaryo();
			tmpStatuFilterId = statuFilterId;
		}
	}

	public SpcInfoTypeClient getSpcInfoTypeClient() {
		return spcInfoTypeClient;
	}

	public void setSpcInfoTypeClient(SpcInfoTypeClient spcInfoTypeClient) {
		this.spcInfoTypeClient = spcInfoTypeClient;
	}

	public String getJobLog() {
		return jobLog;
	}

	public void setJobLog(String jobLog) {
		this.jobLog = jobLog;
	}

	public boolean isJobLogPopup() {
		return jobLogPopup;
	}

	public void setJobLogPopup(boolean jobLogPopup) {
		this.jobLogPopup = jobLogPopup;
	}

	public boolean isJobLogExist() {
		return jobLogExist;
	}

	public void setJobLogExist(boolean jobLogExist) {
		this.jobLogExist = jobLogExist;
	}

	public boolean getNewJobMessage() {
		return newJobMessage;
	}

	public void setNewJobMessage(boolean newJobMessage) {
		this.newJobMessage = newJobMessage;
	}

	public JobInfoTypeClient getNewJobInfoTypeClient() {
		if (newJobInfoTypeClient == null)
			newJobInfoTypeClient = new JobInfoTypeClient();
		return newJobInfoTypeClient;
	}

	public void setNewJobInfoTypeClient(JobInfoTypeClient newJobInfoTypeClient) {
		this.newJobInfoTypeClient = newJobInfoTypeClient;
	}

	public String getNewjobSenaryoId() {
		return newjobSenaryoId;
	}

	public void setNewjobSenaryoId(String newjobSenaryoId) {
		this.newjobSenaryoId = newjobSenaryoId;
		getNewJobDependencyList();
	}

	public String[] getJobDependecyList() {
		return jobDependecyList;
	}

	public void setJobDependecyList(String[] jobDependecyList) {
		this.jobDependecyList = jobDependecyList;
	}

	public List<SelectItem> getSenaryoJobList() {
		return senaryoJobList;
	}

	public void setSenaryoJobList(List<SelectItem> senaryoJobList) {
		this.senaryoJobList = senaryoJobList;
	}

	public DataTable getSenaryoDataTable() {
		return senaryoDataTable;
	}

	public void setSenaryoDataTable(DataTable senaryoDataTable) {
		this.senaryoDataTable = senaryoDataTable;
	}

	public List<SpcInfoTypeClient> getSenaryoList() {
		return senaryoList;
	}

	public void setSenaryoList(List<SpcInfoTypeClient> senaryoList) {
		this.senaryoList = senaryoList;
	}

	public void setJobBaseReportTable(DataTable jobBaseReportTable) {
		this.jobBaseReportTable = jobBaseReportTable;
	}

	public DataTable getJobBaseReportTable() {
		return jobBaseReportTable;
	}

	public void setJob(JobProperties job) {
		this.job = job;
	}

	public JobProperties getJob() {
		return job;
	}

	public void setJobAlarmTable(DataTable jobAlarmTable) {
		this.jobAlarmTable = jobAlarmTable;
	}

	public DataTable getJobAlarmTable() {
		return jobAlarmTable;
	}

	public void setResourceListForJob(Collection<SelectItem> resourceListForJob) {
		this.resourceListForJob = resourceListForJob;
	}

	public Collection<SelectItem> getResourceListForJob() {
		return resourceListForJob;
	}

	public void setSelectedResource(String selectedResource) {
		this.selectedResource = selectedResource;
	}

	public String getSelectedResource() {
		return selectedResource;
	}

//	public LiveManager getLiveManager() {
//		return liveManager;
//	}
//
//	public void setLiveManager(LiveManager liveManager) {
//		this.liveManager = liveManager;
//	}

	public void setJobAlarmList(ArrayList<AlarmInfoTypeClient> jobAlarmList) {
		this.jobAlarmList = jobAlarmList;
	}

	public ArrayList<AlarmInfoTypeClient> getJobAlarmList() {
		return jobAlarmList;
	}

	public void setJobBaseReportList(ArrayList<JobInfoTypeClient> jobBaseReportList) {
		this.jobBaseReportList = jobBaseReportList;
	}

	public ArrayList<JobInfoTypeClient> getJobBaseReportList() {
		return jobBaseReportList;
	}

	public void setSelectedAlarm(AlarmInfoTypeClient selectedAlarm) {
		this.selectedAlarm = selectedAlarm;
	}

	public AlarmInfoTypeClient getSelectedAlarm() {
		return selectedAlarm;
	}

	public void setSelectedAlarmHistory(Alarm selectedAlarmHistory) {
		this.selectedAlarmHistory = selectedAlarmHistory;
	}

	public Alarm getSelectedAlarmHistory() {
		return selectedAlarmHistory;
	}

	public void setSlaName(String slaName) {
		this.slaName = slaName;
	}

	public String getSlaName() {
		return slaName;
	}

	public void setJobDependencyListStr(String jobDependencyListStr) {
		this.jobDependencyListStr = jobDependencyListStr;
	}

	public String getJobDependencyListStr() {
		return jobDependencyListStr;
	}

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}


}
