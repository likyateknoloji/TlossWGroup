package com.likya.tlossw.model;

import java.io.Serializable;
import java.util.HashMap;

import com.likyateknoloji.xmlMetaDataTypes.MetaDataDocument.MetaData;

public class DocMetaDataHolder implements Serializable {

	private static final long serialVersionUID = 534978859176228488L;

	public static final int FIRST_COLUMN = 1;
	public static final int SECOND_COLUMN = 2;

	private MetaData metaData;

	private String[] currentDocs = { null, null };

	private HashMap<String, Integer> documentScopes = new HashMap<String, Integer>();

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public HashMap<String, Integer> getDocumentScopes() {
		return documentScopes;
	}

	public void setDocumentScopes(HashMap<String, Integer> documentScopes) {
		this.documentScopes = documentScopes;
	}

	public String[] getCurrentDocs() {
		return currentDocs;
	}

	public void setCurrentDocs(String[] currentDocs) {
		this.currentDocs = currentDocs;
	}

//	public String getCurrentDoc(Integer index) {
//		return currentDocs[index];
//	}
//	
//	public void setCurrentDoc(String currentDoc, Integer index) {
//		if(index.equals(1) || index.equals(2)) {
//			this.currentDocs[index-1] = currentDoc;
//		} else {
//			this.currentDocs[0] = currentDoc;
//		}
//	}
	
}
