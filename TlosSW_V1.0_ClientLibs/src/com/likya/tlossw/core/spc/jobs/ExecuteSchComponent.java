package com.likya.tlossw.core.spc.jobs;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.core.spc.helpers.ParamList;
import com.likya.tlossw.core.spc.jobs.helpers.JobHelper;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;
import com.likyateknoloji.xmlExecuteRShellTypes.ExecuteRShellParamsDocument.ExecuteRShellParams;

public abstract class ExecuteSchComponent extends ExecuteComponent {

	private static final long serialVersionUID = 7931558555995487881L;

	public ExecuteSchComponent(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	public void startSchProcess(String jobPath, String jobCommand, Map<String, String> environmentVariables, String logClassName, Logger myLogger) throws Exception {

		JobProperties jobProperties = getJobRuntimeProperties().getJobProperties();
		String jobKey = getJobKey();

		StringBuffer stringBufferForERROR = new StringBuffer();
		StringBuffer stringBufferForOUTPUT = new StringBuffer();

		JSch jsch = new JSch();

		ExecuteRShellParams executeRShellParams = jobProperties.getBaseJobInfos().getJobTypeDetails().getSpecialParameters().getExecuteRShellParams();

		String host = executeRShellParams.getIpAddress(); // "192.168.1.39";
		String user = executeRShellParams.getUserName(); // "likya";
		String password = executeRShellParams.getUserPassword(); // "likya";
		int port = executeRShellParams.getPort(); // "22";
		String fileSeperator = executeRShellParams.getFileSeperator();

		jobCommand = jobPath + fileSeperator + jobCommand; // "/home/likya/murat/Agent/jobs/job1.sh";

		Session session = jsch.getSession(user, host, port);

		/*
		 * String xhost="127.0.0.1"; int xport=0; 
		 * String display=JOptionPane.showInputDialog("Enter display name", xhost+":"+xport); 
		 * xhost=display.substring(0, display.indexOf(':')); 
		 * xport=Integer.parseInt(display.substring(display .indexOf(':')+1)); 
		 * session.setX11Host(xhost); 
		 * session.setX11Port(xport+6000);
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

		initGrabbers(channel, jobKey, myLogger, stringBufferForERROR, stringBufferForOUTPUT);

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
			
			StringBuffer descStr = new StringBuffer();

			StatusName.Enum statusName = JobHelper.searchReturnCodeInStates(getGlobalRegistry(), jobProperties, processExitValue, descStr);
			
			updateDescStr(descStr, stringBufferForOUTPUT, stringBufferForERROR);
			
			processParameters(new ArrayList<ParamList>(), stringBufferForOUTPUT, stringBufferForERROR);

			writetErrorLogFromOutputs(myLogger, logClassName, stringBufferForOUTPUT, stringBufferForERROR);
			
			insertNewLiveStateInfo(StateName.INT_FINISHED, SubstateName.INT_COMPLETED, statusName.intValue());

		} catch (InterruptedException e) {

			myLogger.warn(" >>" + logLabel + ">> " + logClassName + " : Job timed-out terminating " + jobProperties.getBaseJobInfos().getJsName());

			channel.disconnect();
			session.disconnect();

		}
	}

	@Override
	public void localRun() {

	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

}
