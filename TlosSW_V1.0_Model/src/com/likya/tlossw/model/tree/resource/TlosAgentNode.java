/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.tree.resource : TlosAgentNode.java
 * @author Merve Ozbey
 * Tarih : 17.Sub.2012 14:37:08
 */

package com.likya.tlossw.model.tree.resource;

import java.io.Serializable;
import java.util.ArrayList;

import com.likya.tlossw.model.client.resource.TlosAgentInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;

public class TlosAgentNode implements Serializable {

	private static final long serialVersionUID = 3493229044094671923L;
	
	private String labelText;
	
	private ArrayList<JobInfoTypeClient> jobInfoTypeClientList = new ArrayList<JobInfoTypeClient>();
	
	public ArrayList<JobInfoTypeClient> getJobInfoTypeClientList() {
		return jobInfoTypeClientList;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	private TlosAgentInfoTypeClient tlosAgentInfoTypeClient = new TlosAgentInfoTypeClient();

	public void setTlosAgentInfoTypeClient(TlosAgentInfoTypeClient tlosAgentInfoTypeClient) {
		this.tlosAgentInfoTypeClient = tlosAgentInfoTypeClient;
	}

	public TlosAgentInfoTypeClient getTlosAgentInfoTypeClient() {
		return tlosAgentInfoTypeClient;
	}
	
}
