package com.likya.tlossw.model;

public class MetaDataType {

	public final static int GLOBAL = 1;
	public final static int LOCAL = 2;

	private String documentId;
	private String documentType;

	private int scope = GLOBAL;
	
	public MetaDataType() {
	}

	public MetaDataType(String documentId, String documentType, int scope) {
		this.documentId = documentId;
		this.documentType = documentType;
		this.scope = scope;
	}
	
	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

}
