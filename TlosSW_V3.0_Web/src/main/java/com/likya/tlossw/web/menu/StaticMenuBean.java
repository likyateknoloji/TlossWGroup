package com.likya.tlossw.web.menu;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.likya.tlossw.model.MetaDataType;
import com.likya.tlossw.utils.CommonConstantDefinitions;

@ManagedBean
@ViewScoped
public class StaticMenuBean implements Serializable {
	
	private static final long serialVersionUID = -4808029867793610016L;
	
	private final String DEF_BASE_URL = "/inc/definitionPanels/jobsDef.jsf?";
	private final String LIVEJS_BASE_URL = "/inc/livePanels/liveJobsScenarios.jsf?";
	private final String DEPLOYMENT_BASE_URL = "/inc/definitionPanels/deploymentPage.jsf?";

	private String globalDefsUrl;
	private String myDefsUrl;
	
	private String globalLiveJsUrl;
	private String myLiveJsUrl;

	private String deploymentUrl;

	@PostConstruct
	public void init() {
		
		globalDefsUrl = DEF_BASE_URL + CommonConstantDefinitions.SECOND_COLUMN_STR + "=" + MetaDataType.GLOBAL;
		myDefsUrl = DEF_BASE_URL + CommonConstantDefinitions.SECOND_COLUMN_STR + "=" + MetaDataType.LOCAL;
		
		globalLiveJsUrl = LIVEJS_BASE_URL + CommonConstantDefinitions.VIEW_SCOPE + "=" + MetaDataType.GLOBAL;
		myLiveJsUrl = LIVEJS_BASE_URL + CommonConstantDefinitions.VIEW_SCOPE + "=" + MetaDataType.LOCAL;

		deploymentUrl = DEPLOYMENT_BASE_URL + CommonConstantDefinitions.FIRST_COLUMN_STR + "=" + MetaDataType.LOCAL + "&" + CommonConstantDefinitions.SECOND_COLUMN_STR + "=" + MetaDataType.GLOBAL;
	
	}


	public String getGlobalDefsUrl() {
		return globalDefsUrl;
	}


	public void setGlobalDefsUrl(String globalDefsUrl) {
		this.globalDefsUrl = globalDefsUrl;
	}


	public String getMyDefsUrl() {
		return myDefsUrl;
	}


	public void setMyDefsUrl(String myDefsUrl) {
		this.myDefsUrl = myDefsUrl;
	}


	public String getGlobalLiveJsUrl() {
		return globalLiveJsUrl;
	}


	public void setGlobalLiveJsUrl(String globalLiveJsUrl) {
		this.globalLiveJsUrl = globalLiveJsUrl;
	}


	public String getMyLiveJsUrl() {
		return myLiveJsUrl;
	}


	public void setMyLiveJsUrl(String myLiveJsUrl) {
		this.myLiveJsUrl = myLiveJsUrl;
	}


	public String getDeploymentUrl() {
		return deploymentUrl;
	}


	public void setDeploymentUrl(String deploymentUrl) {
		this.deploymentUrl = deploymentUrl;
	}

}
