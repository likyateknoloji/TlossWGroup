package com.likya.tlossw.core.spc.jobs.helpers;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.StateUtils;
import com.likya.tlossw.utils.GlobalRegistry;

public class JobHelper {

	public static String removeSlashAtTheEnd(JobProperties jobProperties, String jobPath, String jobCommand) {

		String pathSeperator;

		if (OSystem.INT_WINDOWS == jobProperties.getBaseJobInfos().getOSystem().intValue()) {
			pathSeperator = "\\";
		} else {
			pathSeperator = "/";
		}

		if (jobPath.endsWith(pathSeperator)) {
			jobCommand = jobPath + jobCommand;
		} else {
			jobCommand = jobPath + pathSeperator + jobCommand;
		}

		return jobCommand;
	}

	public static ProcessBuilder parsJobCmdArgs(String jobCommand) {

		ProcessBuilder processBuilder;
		
		String realCommand = "";
		String arguments = "";

		int indexOfSpace = jobCommand.indexOf(" ");
		
		if (indexOfSpace > 0) {
			realCommand = jobCommand.substring(0, indexOfSpace).trim();
			arguments = jobCommand.substring(jobCommand.indexOf(" ")).trim();
			processBuilder = new ProcessBuilder(realCommand, arguments);
		} else {
			realCommand = jobCommand.trim();
			processBuilder = new ProcessBuilder(realCommand);
		}
		
		return processBuilder;
		
	}
	
	public static StatusName.Enum searchReturnCodeInStates(GlobalRegistry globalRegistry, JobProperties jobProperties, int processExitValue, StringBuffer descStr) {
		
		Status localStateCheck = null;
		StatusName.Enum statusName = null;

		if ((jobProperties.getStateInfos().getJobStatusList() != null) && (localStateCheck = StateUtils.contains(jobProperties.getStateInfos().getJobStatusList(), processExitValue)) != null) {
			statusName = localStateCheck.getStatusName();
		} else {
			Status mySubStateStatuses = StateUtils.globalContains(StateName.FINISHED, SubstateName.COMPLETED, globalRegistry, processExitValue);
			if (mySubStateStatuses != null) {
				statusName = mySubStateStatuses.getStatusName();
			} else {
				statusName = StatusName.FAILED;
			}
		}
		
		if(StatusName.FAILED.equals(statusName)) {
			descStr.append("Fail Reason depends on ReturnCode of job through processExitValue : " + processExitValue);
		}
		
		return statusName;
	}
}
