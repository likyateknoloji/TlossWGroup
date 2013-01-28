/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.tree.resource : ResourceListNode.java
 * @author Merve Ozbey
 * Tarih : 17.Sub.2012 14:37:08
 */

package com.likya.tlossw.model.tree.resource;

import java.io.Serializable;
import java.util.ArrayList;

public class ResourceListNode implements Serializable {

	private static final long serialVersionUID = 1502653134173511164L;

	//kaynak listesindeki makinelerin listesi
	private ArrayList<ResourceNode> resourceNodes = new ArrayList<ResourceNode>();

	public void setResourceNodes(ArrayList<ResourceNode> resourceNodes) {
		this.resourceNodes = resourceNodes;
	}

	public ArrayList<ResourceNode> getResourceNodes() {
		return resourceNodes;
	}
	
}
