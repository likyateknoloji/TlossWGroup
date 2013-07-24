package com.likya.tlossw.web.menu;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.likya.tlossw.utils.CommonConstantDefinitions;

@ManagedBean
@ViewScoped
public class StaticMenuBean implements Serializable {
	
	private static final long serialVersionUID = -4808029867793610016L;
	
	private final String DEF_URLS = "/inc/definitionPanels/jobsDef.jsf?";
	private final String LIVE_JS= "/inc/livePanels/liveJobsScenarios.jsf?";

	private String globalDefsUrl;
	private String myDefsUrl;
	
	private String globalLiveJsUrl;
	private String myLiveJsUrl;
	

	@PostConstruct
	public void init() {
		globalDefsUrl = DEF_URLS + CommonConstantDefinitions.EXIST_DOCID + "=" + CommonConstantDefinitions.EXIST_GLOBALDATA;
		myDefsUrl = DEF_URLS + CommonConstantDefinitions.EXIST_DOCID + "=" + CommonConstantDefinitions.EXIST_MYDATA;
		
		globalLiveJsUrl = LIVE_JS + CommonConstantDefinitions.EXIST_DOCID + "=" + CommonConstantDefinitions.EXIST_GLOBALDATA;
		myLiveJsUrl = LIVE_JS + CommonConstantDefinitions.EXIST_DOCID + "=" + CommonConstantDefinitions.EXIST_MYDATA;
		
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




}
