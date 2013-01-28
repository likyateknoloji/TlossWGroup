package com.likya.tlossw.model.client.spc;


import java.io.Serializable;
import java.util.HashMap;

public class SpcLookUpTableTypeClient implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private HashMap<String, SpcInfoTypeClient> spcInfoTypeClientList;

	public SpcLookUpTableTypeClient() {
		this.spcInfoTypeClientList = new HashMap<String, SpcInfoTypeClient>();
	}
	
	public HashMap<String, SpcInfoTypeClient> getSpcInfoTypeClientList() {
		return spcInfoTypeClientList;
	}

	public void setSpcInfoTypeClientList(HashMap<String, SpcInfoTypeClient> spcInfoTypeClientList) {
		this.spcInfoTypeClientList = spcInfoTypeClientList;
	}

}
