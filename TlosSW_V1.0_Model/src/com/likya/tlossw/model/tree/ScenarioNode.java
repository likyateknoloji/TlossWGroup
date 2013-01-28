/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.tree.serkan : ScenarioNode.java
 * @author Serkan Tas
 * Tarih : 18.Nis.2010 23:46:29
 */

package com.likya.tlossw.model.tree;

import java.io.Serializable;
import java.util.ArrayList;

import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;

public class ScenarioNode implements Serializable {

	private static final long serialVersionUID = 1676608004368003714L;

	private SpcInfoTypeClient spcInfoTypeClient;

	private ArrayList<ScenarioNode> scenarioNodes = new ArrayList<ScenarioNode>();
	private ArrayList<JobNode> jobNodes = new ArrayList<JobNode>();

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

}
