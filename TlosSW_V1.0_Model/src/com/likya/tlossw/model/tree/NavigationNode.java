package com.likya.tlossw.model.tree;

import java.io.Serializable;

public class NavigationNode implements Serializable {

	private static final long serialVersionUID = 3739044710637786293L;

	private boolean isInstance = false;
	private boolean isScenario = false;
	private boolean isJob = false;

	private InstanceNode instanceNode;
	private ScenarioNode scenarioNode;
	private JobNode jobNode;

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

	public InstanceNode getInstanceNode() {
		return instanceNode;
	}

	public void setInstanceNode(InstanceNode instanceNode) {
		this.instanceNode = instanceNode;
	}

	public ScenarioNode getScenarioNode() {
		return scenarioNode;
	}

	public void setScenarioNode(ScenarioNode scenarioNode) {
		this.scenarioNode = scenarioNode;
	}

	public boolean isJob() {
		return isJob;
	}

	public void setJob(boolean isJob) {
		this.isJob = isJob;
	}

	public JobNode getJobNode() {
		return jobNode;
	}

	public void setJobNode(JobNode jobNode) {
		this.jobNode = jobNode;
	}

}
