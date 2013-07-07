/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.tree.serkan : JobNode.java
 * @author Serkan Tas
 * Tarih : 18.Nis.2010 23:46:40
 */

package com.likya.tlossw.model.tree;

import com.likya.tlossw.model.client.spc.JobInfoTypeClient;

public class JobNode extends WsJobNode {

	private static final long serialVersionUID = 134596769724656392L;

	private JobInfoTypeClient jobInfoTypeClient = new JobInfoTypeClient();
	
	public JobInfoTypeClient getJobInfoTypeClient() {
		return jobInfoTypeClient;
	}
	
}
