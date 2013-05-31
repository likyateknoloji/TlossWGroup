package com.likya.tlossw.core.spc.jobs;

import java.util.Map;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.StateUtils;
import com.likya.tlossw.core.spc.helpers.StreamGrabber;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likya.tlossw.utils.LiveStateInfoUtils;
import com.likyateknoloji.xmlExecuteRShellTypes.ExecuteRShellParamsDocument.ExecuteRShellParams;

public abstract class ExecuteSchComponent extends Job {

	private static final long serialVersionUID = 7931558555995487881L;

	transient private Process process;
	
	public ExecuteSchComponent(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public void startSchProcess(String jobPath, String jobCommand, Map<String, String> environmentVariables, String logClassName, Logger myLogger) throws Exception {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		String jobKey = getJobKey();

		StringBuffer stringBufferForERROR = new StringBuffer();
		StringBuffer stringBufferForOUTPUT = new StringBuffer();

		JSch jsch = new JSch();
		
		ExecuteRShellParams executeRShellParams = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getExecuteRShellParams();

		String host = executeRShellParams.getIpAddress(); //"192.168.1.39";
		String user = executeRShellParams.getUserName(); // "likya";
		String password = executeRShellParams.getUserPassword(); //"likya";
		int port = executeRShellParams.getPort().intValue(); //"likya";

		jobCommand = executeRShellParams.getJobCommand(); //"/home/likya/murat/Agent/jobs/job1.sh";
		
		Session session = jsch.getSession(user, host, port);

		/*
		 * String xhost="127.0.0.1"; int xport=0; String
		 * display=JOptionPane.showInputDialog("Enter display name",
		 * xhost+":"+xport); xhost=display.substring(0, display.indexOf(':'));
		 * xport=Integer.parseInt(display.substring(display .indexOf(':')+1));
		 * session.setX11Host(xhost); session.setX11Port(xport+6000);
		 */
		
		java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
		
		session.setPassword(password);
		session.connect();

		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(jobCommand);

		// X Forwarding
		// channel.setXForwarding(true);

		channel.setInputStream(System.in);
		// channel.setInputStream(null);

		// channel.setOutputStream(System.out);

		// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
		// ((ChannelExec)channel).setErrStream(fos);
		((ChannelExec) channel).setErrStream(System.err);

		channel.connect();

		myLogger.debug(" >>" + logLabel + ">> " + "Sleeping 100 ms for error and output buffers to get ready...");
		Thread.sleep(100);
		myLogger.info(" >>" + logLabel + ">> " + " OK");

		errorGobbler = new StreamGrabber(((ChannelExec) channel).getErrStream(), "ERROR", stringBufferForERROR);
		errorGobbler.setName(jobKey + ".ErrorGobbler.id." + errorGobbler.getId());

		// any output?
		outputGobbler = new StreamGrabber(channel.getInputStream(), "OUTPUT", stringBufferForOUTPUT);
		outputGobbler.setName(jobKey + ".OutputGobbler.id." + outputGobbler.getId());

		myLogger.info(" >>" + logLabel + " icin islemin hata ve girdi akisi baslatiliyor. " + errorGobbler.getName() + " ve " + outputGobbler.getName());

		// kick them off
		errorGobbler.start();
		outputGobbler.start();

		try {

			while (true) {

				if (channel.isClosed()) {
					break;
				}
				
				Thread.sleep(1000);
			}

			channel.disconnect();
			session.disconnect();

			int processExitValue = channel.getExitStatus();

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

			LiveStateInfoUtils.insertNewLiveStateInfo(jobProperties, StateName.INT_FINISHED, SubstateName.INT_COMPLETED, statusName.intValue());

		} catch (InterruptedException e) {

			myLogger.warn(" >>" + logLabel + ">> " + logClassName + " : Job timed-out terminating " + jobProperties.getBaseJobInfos().getJsName());

			channel.disconnect();
			session.disconnect();

		}
	}

	@Override
	public void run() {

	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

}
