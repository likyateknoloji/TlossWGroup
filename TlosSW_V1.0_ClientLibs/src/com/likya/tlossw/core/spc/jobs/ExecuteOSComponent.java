package com.likya.tlossw.core.spc.jobs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.StateUtils;
import com.likya.tlossw.core.spc.helpers.StreamGrabber;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.ValidPlatforms;
import com.likya.tlossw.utils.XmlBeansTransformer;

public abstract class ExecuteOSComponent extends Job {

	transient private Process process;

	public ExecuteOSComponent(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	private static final long serialVersionUID = 7931558555995487881L;

	@Override
	public void localRun() {

	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void startNativeProcess(String jobPath, String jobCommand, Map<String, String> environmentVariables, String logClassName, Logger myLogger) throws Exception {
		startProcess(jobPath, jobCommand, environmentVariables, logClassName, myLogger, false);
		return;
	}

	public void startShellProcess(String jobPath, String jobCommand, Map<String, String> environmentVariables, String logClassName, Logger myLogger) throws Exception {
		startProcess(jobPath, jobCommand, environmentVariables, logClassName, myLogger, true);
		return;
	}

	private void startProcess(String jobPath, String jobCommand, Map<String, String> environmentVariables, String logClassName, Logger myLogger, boolean isShell) throws Exception {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		String jobKey = getJobKey();

		StringBuffer stringBufferForERROR = new StringBuffer();
		StringBuffer stringBufferForOUTPUT = new StringBuffer();

		ProcessBuilder processBuilder = null;
		
		/**
		 * @author serkan taş
		 * processBuilder.directory sets the directory of data needed for the process, 
		 * not sets exact path of process, especially on MacOs
		 */
		if(jobPath.endsWith("/")) {
			jobCommand = jobPath + jobCommand;
		} else {
			jobCommand = jobPath + "/" + jobCommand;
		}
		
		myLogger.info(" >>" + logLabel + jobKey + " Çalıştırılacak komut : " + jobCommand);
		
		if (isShell) {
			String[] cmd = ValidPlatforms.getCommand(jobCommand);
			processBuilder = new ProcessBuilder(cmd);
		} else {
			
			String realCommand = "";
			String arguments = "";
			
			int indexOfSpace = jobCommand.indexOf(" ");
			if(indexOfSpace > 0) {
				realCommand = jobCommand.substring(0, indexOfSpace).trim();
				arguments = jobCommand.substring(jobCommand.indexOf(" ")).trim();
				processBuilder = new ProcessBuilder(realCommand, arguments);
			} else {
				realCommand = jobCommand.trim();
				processBuilder = new ProcessBuilder(realCommand);
			}
		}

		processBuilder.directory(new File(jobPath));

		Map<String, String> tempEnv = new HashMap<String, String>();
				
		if (environmentVariables != null && environmentVariables.size() > 0) {
			tempEnv.putAll(environmentVariables);
		}
		
		tempEnv.putAll(XmlBeansTransformer.entryToMap(jobProperties));

		processBuilder.environment().putAll(tempEnv);;
		
		setProcess(processBuilder.start());

		Process process = getProcess();

		myLogger.debug(" >>" + logLabel + ">> " + "Sleeping 100 ms for error and output buffers to get ready...");
		Thread.sleep(100);
		myLogger.info(" >>" + logLabel + ">> " + " OK");

		errorGobbler = new StreamGrabber(process.getErrorStream(), "ERROR", stringBufferForERROR);
		errorGobbler.setName(jobKey + ".ErrorGobbler.id." + errorGobbler.getId());

		// any output?
		outputGobbler = new StreamGrabber(process.getInputStream(), "OUTPUT", stringBufferForOUTPUT);
		outputGobbler.setName(jobKey + ".OutputGobbler.id." + outputGobbler.getId());

		myLogger.info(" >>" + logLabel + " icin islemin hata ve girdi akisi baslatiliyor. " + errorGobbler.getName() + " ve " + outputGobbler.getName());

		// kick them off
		errorGobbler.start();
		outputGobbler.start();

		try {

			process.waitFor();

			int processExitValue = process.exitValue();

			myLogger.info(" >>" + logLabel + jobKey + " islemi sonlandi, islem bitis degeri : " + processExitValue);

			Status localStateCheck = null;
			StatusName.Enum statusName = null;

			if ((jobProperties.getStateInfos().getJobStatusList() != null) && (localStateCheck = StateUtils.contains(jobProperties.getStateInfos().getJobStatusList(), processExitValue)) != null) {
				statusName = localStateCheck.getStatusName();
			} else {
				Status mySubStateStatuses = StateUtils.globalContains(StateName.FINISHED, SubstateName.COMPLETED, getGlobalRegistry(), processExitValue);
				if (mySubStateStatuses != null) {
					statusName = mySubStateStatuses.getStatusName();
				} else {
					statusName = StatusName.FAILED;
				}
			}

			insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, statusName.intValue());

		} catch (InterruptedException e) {

			// Stop the process from running
			myLogger.warn(" >>" + logLabel + ">> " + logClassName + " : Job timed-out terminating " + jobProperties.getBaseJobInfos().getJsName());
			// TODO Windows process kill etmek i�in yazılan JNI kodu
			// buraya konmalı
			// Serkan Taş 13.08.2012
			process.destroy();

		}
	}

}
