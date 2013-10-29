package com.likya.tlossw.core.spc.jobs;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.likya.tlossw.core.spc.helpers.StreamGrabber;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlossw.utils.GlobalRegistry;

public abstract class ExecuteComponent extends Job {

	private static final long serialVersionUID = 7931558555995487881L;

	transient protected Process process;

	public ExecuteComponent(GlobalRegistry globalRegistry, Logger globalLogger, JobRuntimeProperties jobRuntimeProperties) {
		super(globalRegistry, globalLogger, jobRuntimeProperties);
	}

	protected void initGrabbers(String jobKey, Logger myLogger, StringBuffer stringBufferForERROR, StringBuffer stringBufferForOUTPUT) throws InterruptedException {

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
	}

	protected void initGrabbers(Channel channel, String jobKey, Logger myLogger, StringBuffer stringBufferForERROR, StringBuffer stringBufferForOUTPUT) throws InterruptedException, IOException {
		
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
	}

	protected void updateDescStr(StringBuffer descStr, StringBuffer stringBufferForOUTPUT, StringBuffer stringBufferForERROR) {

		if (!"".equals(stringBufferForOUTPUT)) {
			descStr.append("OUTPUT : " + stringBufferForOUTPUT);
		}

		if (!"".equals(stringBufferForERROR)) {
			descStr.append("\nERROR : " + stringBufferForERROR);
		}

		return;
	}

}
