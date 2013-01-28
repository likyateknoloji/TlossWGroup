package com.likya.tlossw.core.dss;

import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceDocument.Resource;
import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceAgentListDocument.ResourceAgentList;

public class DssResult extends Result {

	/**
	 * resultCode : Eðer bu deðer >= 0 ise iþlem baþarýlýdýr, 
	 * < 0 ise iþlem baþarýsýzdýr. Her iki durumda da farklý deðerler alabilir.
	 * resultDescription : Baþarýlý veya baþarýsýz bitme durumunda açýklama 
	 * içermelidir. Baþarýlý ise baþarý ile ilgili bilgi, baþarýsýz ise 
	 * baþarýsýzlýk ile ilgili bilgi konmalýdýr. 
	 */
	
	private ResourceAgentList resourceAgentList;
	
	private Resource resource;

	public DssResult(int resultCode, String resultDescription, ResourceAgentList resourceAgentList) {
		super(resultCode, resultDescription);
		this.resourceAgentList = resourceAgentList;
		this.resource = null;
	}
	
	public DssResult(int resultCode, String resultDescription, ResourceAgentList resourceAgentList, Resource resource) {
		super(resultCode, resultDescription);
		this.resourceAgentList = resourceAgentList;
		this.resource = resource;
	}

	public DssResult(int resultCode, String resultDescription) {
		super(resultCode, resultDescription);
	}

	public ResourceAgentList getResourceAgentList() {
		return resourceAgentList;
	}

	public void setResourceAgentList(ResourceAgentList resourceAgentList) {
		this.resourceAgentList = resourceAgentList;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource myResource) {
		this.resource = myResource;
	}
	
}
