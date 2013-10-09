/*
 * TlosFaz_V2.0
 * com.likya.tlos.core.cpc.model : SpcInfoType.java
 * @author Serkan Ta�
 * Tarih : Nov 27, 2008 4:20:39 PM
 */

package com.likya.tlossw.core.cpc.model;

import java.io.Serializable;

import com.likya.tlos.model.xmlbeans.data.ScenarioDocument.Scenario;
import com.likya.tlossw.core.spc.Spc;
import com.likya.tlossw.model.path.TlosSWPathType;

public class SpcInfoType implements Serializable {

	private static final long serialVersionUID = -8377199944163625988L;

	private String jsId;
	private String jsName;
	private TlosSWPathType spcId;
	private boolean concurrent;
	private String comment;

	private String userId;

	private Scenario scenario;
	
	private transient Spc spcReferance;
	private boolean scnearioListStatus = false;

	private boolean isVirgin = true;

	public Spc getSpcReferance() {
		return spcReferance;
	}

	public void setSpcReferance(Spc spcReferance) {
		this.spcReferance = spcReferance;
	}

	public boolean isScnearioListStatus() {
		return scnearioListStatus;
	}

	public void setScnearioListStatus(boolean scnearioListStatus) {
		this.scnearioListStatus = scnearioListStatus;
	}

	public boolean isVirgin() {
		return isVirgin;
	}

	public void setVirgin(boolean isVirgin) {
		this.isVirgin = isVirgin;
	}

	public String getJsName() {
		return jsName;
	}

	public void setJsName(String jsName) {
		this.jsName = jsName;
	}

	public boolean isConcurrent() {
		return concurrent;
	}

	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public String getJsId() {
		return jsId;
	}

	public void setJsId(String jsId) {
		this.jsId = jsId;
	}

	public TlosSWPathType getSpcId() {
		return spcId;
	}

	public void setSpcId(TlosSWPathType spcId) {
		this.spcId = spcId;
	}

}
