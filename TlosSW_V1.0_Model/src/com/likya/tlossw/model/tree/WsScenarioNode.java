/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.tree.serkan : ScenarioNode.java
 * @author Serkan Tas
 * Tarih : 18.Nis.2010 23:46:29
 */

package com.likya.tlossw.model.tree;

import java.io.Serializable;

public class WsScenarioNode implements Serializable {

	private static final long serialVersionUID = 1676608004368003714L;

	private String id;
	private String instanceId;
	private String leafIcon;
	private String path;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getLeafIcon() {
		return leafIcon;
	}

	public void setLeafIcon(String leafIcon) {
		this.leafIcon = leafIcon;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
