package com.likya.tlossw.core.spc.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.likya.tlos.model.xmlbeans.data.DependencyListDocument.DependencyList;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.model.path.TlosSWPathType;
import com.likya.tlossw.utils.RunTimeUtils;
import com.likya.tlossw.utils.date.DateUtils;

public class JobRuntimeProperties implements Serializable {

	private static final long serialVersionUID = 1L;

	private TlosSWPathType treePath;
	
	private JobProperties jobProperties;
	
	private DbProperties dbProperties;
	
	private DbConnectionProfile dbConnectionProfile;
	
	private FtpProperties ftpProperties;
	
	int processReturnCode;

	public Calendar plannedExecutionDate = null;
	private Date realExecutionDate = null;
	public Calendar completionDate = null;
	public String workDuration = "-";


	@Override
	public String toString() {

		String dumpString = "";
		dumpString += "*Görev Adı : " + jobProperties.getBaseJobInfos().getJsName() + " * ";
		dumpString += "[Görev Komutu:" + jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommand() + "][";
		dumpString += "[Görev Tipi:" + jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getJobCommandType() + "][";
		dumpString += "[Görev Program Adı:" + jobProperties.getBaseJobInfos().getJobLogPath() + "][";
		dumpString += "[Bağımlılık Listesi:";
		DependencyList jobDependencyList = jobProperties.getDependencyList();
		if(jobDependencyList == null) {
			dumpString += "yok";	
		} else {
			dumpString += jobProperties.getDependencyList().toString();
		}
		dumpString += "][";
		dumpString += "[Çıkış Kodu:" + getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getReturnCode() + "][";
		
		// TODO Burada şimdilik listenin ilk elemanını alıyoruz ama xsd üzerinde konuşmak gerekecek.
		dumpString += "[Başlangıç Zamanı:" + jobProperties.getTimeManagement().getJsPlannedTime().getStartTime().toString() + "][";
		dumpString += "[Zaman Aşımı:" + jobProperties.getTimeManagement().getJsTimeOut() + "][";

		return dumpString;
	}

	public String getWorkDuration() {
		
		if(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName().equals(StateName.RUNNING)) {
			workDuration = DateUtils.getUnFormattedDuration(getRealExecutionDate());
		} 
		
		return workDuration == null ? "-" : workDuration;
	}

	public void setWorkDuration(String workDuration) {
		this.workDuration = workDuration;
	}

	public int getProcessReturnCode() {
		return processReturnCode;
	}

	public void setProcessReturnCode(int processReturnCode) {
		this.processReturnCode = processReturnCode;
	}

	public JobProperties getJobProperties() {
		return jobProperties;
	}

	public void setJobProperties(JobProperties jobProperties) {
		this.jobProperties = jobProperties;
	}
	
	public boolean isRetriable() {
		if(( RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName(), StatusName.FAILED )
				|| RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName(), SubstateName.STOPPED ) 
				|| ((getJobProperties().getDependencyList() != null) && (getJobProperties().getDependencyList().getItemArray().length == 0) 
						&& RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName(), StatusName.SUCCESS)))) {
			return true;
			//TODO ((getJobProperties().getDependencyList() != null) && (getJobProperties().getDependencyList().getItemArray().length == 0) acikla !!
		} 
		return false;
	}
	
	public boolean isSuccessable() {
		return isRetriable();
	}
	
	public boolean isSkippable() {
		return isRetriable();
	}
	
	public boolean isStopable() {
		if(RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName(), StateName.RUNNING) ||
				(RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName(), StateName.PENDING) &&
			     RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName(), SubstateName.READY)) ||
			     (RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName(), StateName.PENDING) &&
					     RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName(), SubstateName.PAUSED))
						) {
			return true;
		} 
		return false;
	}
	
	public boolean isPausable() {
		if(RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName(), StateName.PENDING) && 
				RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName(), SubstateName.READY)) {
			return true;
		} 
		return false;
	}
	
	public boolean isResumable() {
		if(RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName(), SubstateName.PAUSED)) {
			return true;
		} 
		return false;
	}
	
	public boolean isHoldable() {
		if(RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName(), StateName.RUNNING) && 
				RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName(), SubstateName.ON_RESOURCE)) {
			return true;
		} 
		return false;
	}
	
	public boolean isReleasable() {
		if(RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName(), StateName.RUNNING) && 
				RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName(), SubstateName.HELD)) {
			return true;
		} 
		return false;
	}

	public boolean isMigratable() {
		if(RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName(), StateName.RUNNING) && 
				RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName(), SubstateName.ON_RESOURCE)) {
			return true;
		} 
		return false;
	}
	
	public boolean isStartable() {
		if(RunTimeUtils.equal(getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName(), SubstateName.IDLED) && 
				((getJobProperties().getDependencyList() == null) || (getJobProperties().getDependencyList().getItemArray().length == 0))) {
			return true;
		} 
		return false;
	}

	public Date getRealExecutionDate() {
		return realExecutionDate;
	}

	public void setRealExecutionDate(Date realExecutionDate) {
		this.realExecutionDate = realExecutionDate;
	}

	public Calendar getPlannedExecutionDate() {
		return plannedExecutionDate;
	}

	public void setPlannedExecutionDate(Calendar plannedExecutionDate) {
		this.plannedExecutionDate = plannedExecutionDate;
	}

	public Calendar getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Calendar completionDate) {
		this.completionDate = completionDate;
	}

	public DbProperties getDbProperties() {
		return dbProperties;
	}

	public void setDbProperties(DbProperties dbProperties) {
		this.dbProperties = dbProperties;
	}

	public FtpProperties getFtpProperties() {
		return ftpProperties;
	}

	public void setFtpProperties(FtpProperties ftpProperties) {
		this.ftpProperties = ftpProperties;
	}

	public void setDbConnectionProfile(DbConnectionProfile dbConnectionProfile) {
		this.dbConnectionProfile = dbConnectionProfile;
	}

	public DbConnectionProfile getDbConnectionProfile() {
		return dbConnectionProfile;
	}

	public TlosSWPathType getTreePath() {
		return treePath;
	}

	public void setTreePath(TlosSWPathType treePath) {
		this.treePath = treePath;
	}

}
