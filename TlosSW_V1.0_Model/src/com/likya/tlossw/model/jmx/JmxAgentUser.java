/*
 * TlosFaz_V2.0
 * com.likya.tlos.jmx.mp.model : JmxUser.java
 * @author Serkan Tas
 * Tarih : May 5, 2009 10:42:44 AM
 */

package com.likya.tlossw.model.jmx;

import java.io.Serializable;


public class JmxAgentUser extends JmxUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private String swAgentXML;  
	private int agentId;

	public JmxAgentUser(String swAgentXML) {
		super();
		this.swAgentXML = swAgentXML;
	}
	
	public JmxAgentUser() {
		super();
	}

	public String getSwAgentXML() {
		return swAgentXML;
	}


	public void setSwAgentXML(String swAgentXML) {
		this.swAgentXML = swAgentXML;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	
}
