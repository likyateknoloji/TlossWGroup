package com.likya.tlossw.core.spc.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.ParamList;
import com.likya.tlossw.core.spc.jobs.helpers.JobHelper;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.ValidPlatforms;
import com.likya.tlossw.utils.XmlBeansTransformer;

public abstract class ExecuteOSComponent extends ExecuteComponent {

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

		jobCommand = JobHelper.removeSlashAtTheEnd(jobProperties, jobPath, jobCommand);

		myLogger.info(" >>" + logLabel + jobKey + " Çalıştırılacak komut : " + jobCommand);

		if (isShell) {
			String[] cmd = ValidPlatforms.getCommand(jobCommand);
			processBuilder = new ProcessBuilder(cmd);
		} else {
			processBuilder = JobHelper.parsJobCmdArgs(jobCommand);
		}

		/**
		 * @author serkan taş
		 *         processBuilder.directory sets the directory of data needed for the process,
		 *         not sets exact path of process, especially on MacOs
		 */
		
		processBuilder.directory(new File(jobPath));

		Map<String, String> tempEnv = new HashMap<String, String>();

		if (environmentVariables != null && environmentVariables.size() > 0) {
			tempEnv.putAll(environmentVariables);
		}

		tempEnv.putAll(XmlBeansTransformer.entryToMap(jobProperties));

		processBuilder.environment().putAll(tempEnv);

		setProcess(processBuilder.start());

		Process process = getProcess();

		initGrabbers(jobKey, myLogger, stringBufferForERROR, stringBufferForOUTPUT);

		try {

			process.waitFor();

			int processExitValue = process.exitValue();

			myLogger.info(" >>" + logLabel + jobKey + " islemi sonlandi, islem bitis degeri : " + processExitValue);

			StringBuffer descStr = new StringBuffer();
			
			StatusName.Enum statusName = JobHelper.searchReturnCodeInStates(getGlobalRegistry(), jobProperties, processExitValue, descStr);

			updateDescStr(descStr, stringBufferForOUTPUT, stringBufferForERROR);
			
			processParameters(new ArrayList<ParamList>(), stringBufferForOUTPUT, stringBufferForERROR);

			writetErrorLogFromOutputs(myLogger, logClassName, stringBufferForOUTPUT, stringBufferForERROR);
			
			insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, statusName.intValue(), descStr.toString());

		} catch (InterruptedException e) {

			// Stop the process from running
			myLogger.warn(" >>" + logLabel + ">> " + logClassName + " : Job timed-out terminating " + jobProperties.getBaseJobInfos().getJsName());
			/**
			* // TODO Windows process kill etmek için yazılan JNI kodu
			* // buraya konmalı
			* // Serkan Taş 13.08.2012
			* 
			* TL'de uygulama yapıldı ancak sadece windows için.
			* unix için de bir şeyler yapmalı
			* 
			* @author serkan taş
			* 29.10.2013
			*/
			process.destroy();

		}
	}

}
