/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.tree.serkan : GunlukIslerNode.java
 * @author Serkan Tas
 * Tarih : 18.Nis.2010 23:46:09
 */

package com.likya.tlossw.model.tree;

import java.io.Serializable;
import java.util.HashMap;

public class GunlukIslerNode implements Serializable {

	private static final long serialVersionUID = 2439147908698379883L;

	private HashMap<String, InstanceNode> instanceNodes = new HashMap<String, InstanceNode>();

	public HashMap<String, InstanceNode> getInstanceNodes() {
		return instanceNodes;
	}

}
