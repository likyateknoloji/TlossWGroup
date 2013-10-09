package com.likya.tlossw.web;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;

public abstract class TreeBaseBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 1056009268444752944L;
	
	private static final Logger logger = Logger.getLogger(TreeBaseBean.class);

	
	protected TlosProcessData getTlosProcessData(String docId, Integer scope){
		
		TlosProcessData tlosProcessData = getDbOperations().getTlosDataXml( docId, getWebAppUser().getId(), scope);
		
		System.out.println(">> "+ docId + " tree has been loaded !!");
		
		logger.info(">> "+ docId + " tree has been loaded !!");
		
		return tlosProcessData;
		
	}
	
}
