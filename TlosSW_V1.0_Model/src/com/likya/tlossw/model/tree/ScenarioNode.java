/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.tree.serkan : ScenarioNode.java
 * @author Serkan Tas
 * Tarih : 18.Nis.2010 23:46:29
 */

package com.likya.tlossw.model.tree;

import java.util.ArrayList;

import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;

public class ScenarioNode extends WsScenarioNode {

	private static final long serialVersionUID = 2001950248328974205L;

	private SpcInfoTypeClient spcInfoTypeClient;

	private ArrayList<ScenarioNode> scenarioNodes = new ArrayList<ScenarioNode>();
	private ArrayList<JobNode> jobNodes = new ArrayList<JobNode>();

	public ScenarioNode() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public ScenarioNode(ScenarioNode scenarioNode) {
		super();
		this.setId(scenarioNode.getId());
		this.setLabelText(scenarioNode.getLabelText());
		this.setLeafIcon(scenarioNode.getLeafIcon());
		this.setName(scenarioNode.getName());
		this.setPath(scenarioNode.getPath());
		this.setScenarioNodes(scenarioNode.getScenarioNodes());
		this.setSpcInfoTypeClient(scenarioNode.getSpcInfoTypeClient());
	}
	
	public ArrayList<ScenarioNode> getScenarioNodes() {
		return scenarioNodes;
	}

	public ArrayList<JobNode> getJobNodes() {
		return jobNodes;
	}

	public SpcInfoTypeClient getSpcInfoTypeClient() {
		return spcInfoTypeClient;
	}

	public void setSpcInfoTypeClient(SpcInfoTypeClient spcInfoTypeClient) {
		this.spcInfoTypeClient = spcInfoTypeClient;
	}

	public void setScenarioNodes(ArrayList<ScenarioNode> scenarioNodes) {
		this.scenarioNodes = scenarioNodes;
	}

}
