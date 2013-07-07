/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.tree.serkan : JobNode.java
 * @author Serkan Tas
 * Tarih : 18.Nis.2010 23:46:40
 */

package com.likya.tlossw.model.tree;

import java.io.Serializable;

import com.likya.tlossw.model.client.spc.JobInfoTypeClient;

public class JobNode extends WsJobNode implements Serializable {

	private static final long serialVersionUID = 3493229044094671923L;
	
	private JobInfoTypeClient jobInfoTypeClient = new JobInfoTypeClient();
	
	public JobInfoTypeClient getJobInfoTypeClient() {
		return jobInfoTypeClient;
	}
	
}
