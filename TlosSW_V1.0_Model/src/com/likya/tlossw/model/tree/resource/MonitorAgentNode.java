/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.tree.resource : MonitorAgentNode.java
 * @author Merve Ozbey
 * Tarih : 17.Sub.2012 14:37:08
 */

package com.likya.tlossw.model.tree.resource;

import java.io.Serializable;

import com.likya.tlossw.model.client.resource.MonitorAgentInfoTypeClient;

public class MonitorAgentNode implements Serializable {

	private static final long serialVersionUID = 3493229044094671923L;

	private String labelText;

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	private MonitorAgentInfoTypeClient monitorAgentInfoTypeClient = new MonitorAgentInfoTypeClient();

	public void setMonitorAgentInfoTypeClient(MonitorAgentInfoTypeClient monitorAgentInfoTypeClient) {
		this.monitorAgentInfoTypeClient = monitorAgentInfoTypeClient;
	}

	public MonitorAgentInfoTypeClient getMonitorAgentInfoTypeClient() {
		return monitorAgentInfoTypeClient;
	}
}
