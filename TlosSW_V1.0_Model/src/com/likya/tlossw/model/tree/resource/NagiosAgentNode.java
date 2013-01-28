/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.tree.resource : NagiosAgentNode.java
 * @author Merve Ozbey
 * Tarih : 17.Sub.2012 14:37:08
 */

package com.likya.tlossw.model.tree.resource;

import java.io.Serializable;

import com.likya.tlossw.model.client.resource.NagiosAgentInfoTypeClient;

public class NagiosAgentNode implements Serializable {

	private static final long serialVersionUID = 3493229044094671923L;
	
	private NagiosAgentInfoTypeClient nagiosAgentInfoTypeClient = new NagiosAgentInfoTypeClient();

	public void setNagiosAgentInfoTypeClient(NagiosAgentInfoTypeClient nagiosAgentInfoTypeClient) {
		this.nagiosAgentInfoTypeClient = nagiosAgentInfoTypeClient;
	}

	public NagiosAgentInfoTypeClient getNagiosAgentInfoTypeClient() {
		return nagiosAgentInfoTypeClient;
	}
}
