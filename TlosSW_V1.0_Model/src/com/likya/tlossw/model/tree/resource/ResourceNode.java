/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.tree.resource : ResourceNode.java
 * @author Merve Ozbey
 * Tarih : 17.Sub.2012 14:37:08
 */

package com.likya.tlossw.model.tree.resource;

import java.io.Serializable;
import java.util.HashMap;

import com.likya.tlossw.model.client.resource.ResourceInfoTypeClient;

public class ResourceNode implements Serializable {

	private static final long serialVersionUID = 1676608004368003714L;

	//makinenin kendi parametreleri
	private ResourceInfoTypeClient resourceInfoTypeClient = new ResourceInfoTypeClient();
	
	//o makinedeki tlos ve monitor agentlar (tlos agent birden fazla olabilir)
	private HashMap<Integer, TlosAgentNode> tlosAgentNodes = new HashMap<Integer, TlosAgentNode>();
	private MonitorAgentNode monitorAgentNode = new MonitorAgentNode();

	public void setTlosAgentNodes(HashMap<Integer, TlosAgentNode> tlosAgentNodes) {
		this.tlosAgentNodes = tlosAgentNodes;
	}

	public HashMap<Integer, TlosAgentNode> getTlosAgentNodes() {
		return tlosAgentNodes;
	}

	public void setMonitorAgentNode(MonitorAgentNode monitorAgentNode) {
		this.monitorAgentNode = monitorAgentNode;
	}

	public MonitorAgentNode getMonitorAgentNode() {
		return monitorAgentNode;
	}

	public void setResourceInfoTypeClient(ResourceInfoTypeClient resourceInfoTypeClient) {
		this.resourceInfoTypeClient = resourceInfoTypeClient;
	}

	public ResourceInfoTypeClient getResourceInfoTypeClient() {
		return resourceInfoTypeClient;
	}
}
