/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.tree.serkan : TlosEnterpriseNode.java
 * @author Serkan Tas
 * Tarih : 18.Nis.2010 23:45:51
 */

package com.likya.tlossw.model.tree;

import java.io.Serializable;

public class TlosSpaceWideNode implements Serializable {
	
	private static final long serialVersionUID = -4454200145733482653L;

	private GunlukIslerNode gunlukIslerNode;
	private NavigationNode navigationNode;

	public GunlukIslerNode GunlukIslerNode() {
		return gunlukIslerNode;
	}

	public void setGunlukIslerNode(GunlukIslerNode gunlukIslerNode) {
		this.gunlukIslerNode = gunlukIslerNode;
	}

	public GunlukIslerNode getGunlukIslerNode() {
		return gunlukIslerNode;
	}

	public NavigationNode getNavigationNode() {
		return navigationNode;
	}

	public void setNavigationNode(NavigationNode navigationNode) {
		this.navigationNode = navigationNode;
	}
	
}
