/*
 * TlosFaz_V2.0
 * com.likya.tlos.jmx.mp.model : JmxUser.java
 * @author Serkan Tas
 * Tarih : May 5, 2009 10:42:44 AM
 */

package com.likya.tlossw.model.jmx;

import java.io.Serializable;

public class JmxUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private String jmxClientAuthanticationId;
	private String jmxClientAuthanticationKey;
	
	public JmxUser() {
		super();
	}

	public String getJmxClientAuthanticationId() {
		return jmxClientAuthanticationId;
	}

	public void setJmxClientAuthanticationId(String jmxClientAuthanticationId) {
		this.jmxClientAuthanticationId = jmxClientAuthanticationId;
	}

	public String getJmxClientAuthanticationKey() {
		return jmxClientAuthanticationKey;
	}

	public void setJmxClientAuthanticationKey(String jmxClientAuthanticationKey) {
		this.jmxClientAuthanticationKey = jmxClientAuthanticationKey;
	}

}
