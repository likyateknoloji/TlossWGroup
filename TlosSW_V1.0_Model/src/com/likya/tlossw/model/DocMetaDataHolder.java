package com.likya.tlossw.model;

import java.util.HashMap;

import com.likyateknoloji.xmlMetaDataTypes.MetaDataDocument.MetaData;

public class DocMetaDataHolder {

	public static final int FIRST_COLUMN = 1;
	public static final int SECOND_COLUMN = 2;

	private MetaData metaData;

	private String[] currentDocs = { null, null };

	private HashMap<String, String> documentScopes = new HashMap<String, String>();

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public HashMap<String, String> getDocumentScopes() {
		return documentScopes;
	}

	public void setDocumentScopes(HashMap<String, String> documentScopes) {
		this.documentScopes = documentScopes;
	}

	public String[] getCurrentDocs() {
		return currentDocs;
	}

	public void setCurrentDocs(String[] currentDocs) {
		this.currentDocs = currentDocs;
	}


}
