package com.likya.tlossw.model.client.spc;

import java.io.Serializable;
import java.util.ArrayList;

public class InfoTypeClient implements Serializable {

	private static final long serialVersionUID = -1463954786388521232L;
	
	private ArrayList<JobInfoTypeClient> jobInfoTypeClient;
	private SpcLookUpTableTypeClient spcLookUpTableTypeClient;

	public SpcLookUpTableTypeClient getSpcLookUpTableTypeClient() {
		return spcLookUpTableTypeClient;
	}

	public void setSpcLookUpTableTypeClient(SpcLookUpTableTypeClient spcLookUpTableTypeClient) {
		this.spcLookUpTableTypeClient = spcLookUpTableTypeClient;
	}

	public ArrayList<JobInfoTypeClient> getJobInfoTypeClient() {
		return jobInfoTypeClient;
	}

	public void setJobInfoTypeClient(ArrayList<JobInfoTypeClient> jobInfoTypeClient) {
		this.jobInfoTypeClient = jobInfoTypeClient;
	}


}
