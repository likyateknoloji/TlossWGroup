/*
 * TlosSW_V1.0_Model
 * com.likya.tlossw.model.tree.resource : TlosLiveResourceNode.java
 * @author Merve Ozbey
 * Tarih : 17.Sub.2012 14:37:08
 */

package com.likya.tlossw.model.tree.resource;

import java.io.Serializable;

public class TlosSWResourceNode implements Serializable {
	
	private static final long serialVersionUID = -4454200145733482653L;

	private ResourceListNode resourceListNode;

	public void setResourceListNode(ResourceListNode resourceListNode) {
		this.resourceListNode = resourceListNode;
	}

	public ResourceListNode getResourceListNode() {
		return resourceListNode;
	}

}
