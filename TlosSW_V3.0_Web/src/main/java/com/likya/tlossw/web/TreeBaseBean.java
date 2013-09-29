package com.likya.tlossw.web;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;
import com.likya.tlossw.model.DocMetaDataHolder;
import com.likya.tlossw.model.MetaDataType;
import com.likya.tlossw.utils.CommonConstantDefinitions;

public abstract class TreeBaseBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 1056009268444752944L;
	
	private static final Logger logger = Logger.getLogger(TreeBaseBean.class);

	protected TlosProcessData getTlosProcessData(String currentDocId){
		
		String scope;
		int columnId;
		
		String doc1Id = getPassedParameter().get("doc1Id");
		String doc2Id = getPassedParameter().get("doc2Id");
		
		if(doc1Id.equalsIgnoreCase(currentDocId)) {
			scope = CommonConstantDefinitions.FIRST_COLUMN_STR;
			columnId = DocMetaDataHolder.FIRST_COLUMN;
		} else if(doc2Id.equalsIgnoreCase(currentDocId)) {
			scope = CommonConstantDefinitions.SECOND_COLUMN_STR;
			columnId = DocMetaDataHolder.SECOND_COLUMN;
		} else {
			System.err.println("FAzladan giriyor. Arastir !!");
			return null;
		}
		
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

		//getSessionMediator().getWebSpaceWideRegistery().getDocMetaDataInfo().setCurrentDoc(currentDocId, columnId);
		getSessionMediator().getWebSpaceWideRegistery().getDocMetaDataInfo().getCurrentDocs()[columnId-1] = currentDocId;
		
		if (columnScope == null) { 
			columnScope = ""+MetaDataType.GLOBAL; // GLOBAL
		}
		getSessionMediator().setDocumentScope(currentDocId, Integer.valueOf(columnScope));
		
		TlosProcessData tlosProcessData = getDbOperations().getTlosDataXml(currentDocId, getWebAppUser().getId(), getSessionMediator().getDocumentScope(currentDocId));
		
		System.out.println(">> "+ currentDocId + " tree has been loaded !!");
		
		logger.info(">> "+ currentDocId + " tree has been loaded !!");
		
		return tlosProcessData;
		
	}
	
}
