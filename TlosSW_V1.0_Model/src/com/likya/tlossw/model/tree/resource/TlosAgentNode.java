/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.tree.resource : TlosAgentNode.java
 * @author Merve Ozbey
 * Tarih : 17.Sub.2012 14:37:08
 */

package com.likya.tlossw.model.tree.resource;

import java.io.Serializable;

import com.likya.tlossw.model.client.resource.TlosAgentInfoTypeClient;

public class TlosAgentNode implements Serializable {

	private static final long serialVersionUID = 3493229044094671923L;
	
	private TlosAgentInfoTypeClient tlosAgentInfoTypeClient = new TlosAgentInfoTypeClient();

	public void setTlosAgentInfoTypeClient(TlosAgentInfoTypeClient tlosAgentInfoTypeClient) {
		this.tlosAgentInfoTypeClient = tlosAgentInfoTypeClient;
	}

	public TlosAgentInfoTypeClient getTlosAgentInfoTypeClient() {
		return tlosAgentInfoTypeClient;
	}
	
}
