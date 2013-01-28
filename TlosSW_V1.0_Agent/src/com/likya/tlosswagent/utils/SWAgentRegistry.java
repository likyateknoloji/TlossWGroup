package com.likya.tlosswagent.utils;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.agentconfig.AgentConfigInfoDocument.AgentConfigInfo;
import com.likya.tlos.model.xmlbeans.state.GlobalStateDefinitionDocument.GlobalStateDefinition;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.utils.InfoBus;
import com.likya.tlosswagent.TlosSWAgent;
import com.likya.tlosswagent.core.cpc.Cpc;
import com.likya.tlosswagent.core.cpc.HeartBeater;
import com.likya.tlosswagent.core.model.AgentGlobalRegistry;
import com.likya.tlosswagent.outputqueue.OutputQueueManager;
import com.likya.tlosswagent.taskqueue.TaskQueueManager;

public class SWAgentRegistry implements AgentGlobalRegistry {
	
	private static final long serialVersionUID = -2082531379728678167L;

	public static boolean TEST = false;
	
	private TlosSWAgent swAgentReference;
	
	private ResourceBundle applicationResources;
	
	private static Logger sWAgentLogger;
	
	private Cpc cpcReference;
	
	private GlobalStateDefinition globalStateDefinition;
	
	private TaskQueueManager taskQueManagerRef;
	
	private OutputQueueManager outputQueueManagerRef;
	
	private AgentConfigInfo agentConfigInfo;
	
	private JmxAgentUser jmxAgentUser; 
	
	private HeartBeater heartBeaterRef;
	
	
	public SWAgentRegistry(Logger sWAgentLogger) {
		super();
		SWAgentRegistry.sWAgentLogger = sWAgentLogger;
	}
	
	public TlosSWAgent getSwAgentReference() {
		return swAgentReference;
	}

	public void setSwAgentReference(TlosSWAgent swAgentReference) {
		this.swAgentReference = swAgentReference;
	}

	public ResourceBundle getApplicationResources() {
		return applicationResources;
	}

	public void setApplicationResources(ResourceBundle applicationResources) {
		this.applicationResources = applicationResources;
	}

	public static Logger getsWAgentLogger() {
		return sWAgentLogger;
	}

	public static void setsWAgentLogger(Logger sWAgentLogger) {
		SWAgentRegistry.sWAgentLogger = sWAgentLogger;
	}

	public Cpc getCpcReference() {
		return cpcReference;
	}

	public void setCpcReference(Cpc cpcReference) {
		this.cpcReference = cpcReference;
	}
	
	public GlobalStateDefinition getGlobalStateDefinition() {
		return globalStateDefinition;
	}

	public void setGlobalStateDefinition(GlobalStateDefinition globalStateDefinition) {
		this.globalStateDefinition = globalStateDefinition;
	}

	public TaskQueueManager getTaskQueManagerRef() {
		return taskQueManagerRef;
	}

	public void setTaskQueManagerRef(TaskQueueManager taskQueManagerRef) {
		this.taskQueManagerRef = taskQueManagerRef;
	}
	
	public OutputQueueManager getOutputQueManagerRef() {
		return outputQueueManagerRef;
	}

	public void setOutputQueManagerRef(OutputQueueManager outputQueueManagerRef) {
		this.outputQueueManagerRef = outputQueueManagerRef;
	}

	public AgentConfigInfo getAgentConfigInfo() {
		return agentConfigInfo;
	}

	public void setAgentConfigInfo(AgentConfigInfo agentConfigInfo) {
		this.agentConfigInfo = agentConfigInfo;
	}

	public HeartBeater getHeartBeaterRef() {
		return heartBeaterRef;
	}

	public void setHeartBeaterRef(HeartBeater heartBeaterRef) {
		this.heartBeaterRef = heartBeaterRef;
	}

	@Override
	public InfoBus getInfoBus() {
		// TODO Auto-generated method stub
		return null;
	}

	public JmxAgentUser getJmxAgentUser() {
		return jmxAgentUser;
	}

	public void setJmxAgentUser(JmxAgentUser jmxAgentUser) {
		this.jmxAgentUser = jmxAgentUser;
	}
	
}
