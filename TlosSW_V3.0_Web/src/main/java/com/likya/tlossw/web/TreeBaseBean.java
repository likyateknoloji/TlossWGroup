package com.likya.tlossw.web;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;

public abstract class TreeBaseBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 1056009268444752944L;
	
	private static final Logger logger = Logger.getLogger(TreeBaseBean.class);

	protected TlosProcessData getTlosProcessData(String currentDocId, String scope, int columnId){
		
		/*

		String scopeId2 = getPassedParameter().get(CommonConstantDefinitions.EXIST_SCOPEID2);
		if (scopeId2 != null) {
			getSessionMediator().setScopeId2(Boolean.valueOf(scopeId2));
		}

		getSessionMediator().setDocumentId2( CommonConstantDefinitions.EXIST_SJDATA );
		getSessionMediator().setDocumentScope( getSessionMediator().getDocumentId2(), getSessionMediator().getScopeId2() );
		
		
		long startTime = System.currentTimeMillis();

		TlosProcessData tlosProcessData = getDbOperations().getTlosDataXml( getSessionMediator().getDocumentId2(), getWebAppUser().getId(), getDocumentScope(getSessionMediator().getDocumentId2()));
		
		*/
		
		String columnScope = getPassedParameter().get(scope);
		
		getSessionMediator().getWebSpaceWideRegistery().getDocMetaDataInfo().getCurrentDocs()[columnId] = currentDocId;
		
		if (columnScope != null) {
			getSessionMediator().setDocumentScope(currentDocId, columnScope);
		}
		
		
		TlosProcessData tlosProcessData = getDbOperations().getTlosDataXml(currentDocId, getWebAppUser().getId(), getDocumentScope(currentDocId));
		
		System.out.println("Tree has been loaded !!");
		
		logger.info("Tree has been loaded !!");
		
		return tlosProcessData;
		
	}
	
}
