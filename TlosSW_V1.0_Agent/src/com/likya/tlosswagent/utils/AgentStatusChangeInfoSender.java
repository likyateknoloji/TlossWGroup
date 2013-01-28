package com.likya.tlosswagent.utils;

import com.likya.tlossw.core.spc.helpers.GenericInfoSender;
import com.likya.tlossw.core.spc.model.JobRuntimeProperties;
import com.likya.tlosswagent.TlosSWAgent;
import com.likya.tlosswagent.outputqueue.OutputQueueOperations;

public class AgentStatusChangeInfoSender implements GenericInfoSender {
	
	public synchronized void sendStatusChangeInfo(String messageId, JobRuntimeProperties jobRuntimeProperties) {
		if (TlosSWAgent.getSwAgentRegistry().getOutputQueManagerRef() != null) {
			OutputQueueOperations.addLiveStateInfo(jobRuntimeProperties.getJobProperties().getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0), messageId);
		} else {
			System.out.println("AgentStatusChangeInfoSender.sendStatusChangeInfo : getSpaceWideRegistry().getOutputQueManagerRef() == null !");
		}
	}

	public void sendEndInfo(String messageId, JobRuntimeProperties jobRuntimeProperties) {
		if (TlosSWAgent.getSwAgentRegistry().getOutputQueManagerRef() != null) {
			OutputQueueOperations.addJobProperties(jobRuntimeProperties.getJobProperties(), messageId);
		} else {
			System.out.println("AgentStatusChangeInfoSender.sendEndInfo : getSpaceWideRegistry().getOutputQueManagerRef() == null !");
		}
	}
	
}
