package com.likya.tlossw.model.tree.resource;

import java.io.Serializable;

public class NavigationNode implements Serializable {

	private static final long serialVersionUID = 3739044710637786293L;

	private boolean isInstance = false;
	private boolean isScenario = false;
	private boolean isJob = false;

	private ResourceListNode instanceNode;
	private ResourceNode scenarioNode;
	private TlosAgentNode agentNode;

	public boolean isInstance() {
		return isInstance;
	}

	public void setInstance(boolean isInstance) {
		this.isInstance = isInstance;
	}

	public boolean isScenario() {
		return isScenario;
	}

	public void setScenario(boolean isScenario) {
		this.isScenario = isScenario;
	}

	public ResourceListNode getInstanceNode() {
		return instanceNode;
	}

	public void setInstanceNode(ResourceListNode instanceNode) {
		this.instanceNode = instanceNode;
	}

	public ResourceNode getScenarioNode() {
		return scenarioNode;
	}

	public void setScenarioNode(ResourceNode scenarioNode) {
		this.scenarioNode = scenarioNode;
	}

	public boolean isJob() {
		return isJob;
	}

	public void setJob(boolean isJob) {
		this.isJob = isJob;
	}

	public TlosAgentNode getJobNode() {
		return agentNode;
	}

	public void setJobNode(TlosAgentNode jobNode) {
		this.agentNode = jobNode;
	}

}
