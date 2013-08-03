/*
 * TlosFaz2
 * com.likya.tlos.infobus.helper : TlosMail.java
 * @author Serkan Ta�
 * Tarih : 10.Kas.2008 14:52:28
 */

package com.likya.tlossw.model.infobus.mail;

import java.util.ArrayList;

import com.likya.tlossw.model.infobus.InfoType;


public class TlosMail implements InfoType {
	
	private static final long serialVersionUID = -2640542420630063913L;
	
	private ArrayList<String> distributionList;

	public ArrayList<String> getDistributionList() {
		return distributionList;
	}

	public void setDistributionList(ArrayList<String> distributionList) {
		this.distributionList = distributionList;
	}
	
}

