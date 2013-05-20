package com.likya.tlossw.core.spc.helpers;

public class ParamList {
	String paramName;
	String type; // Integer, String, etc
	String kind; // Variable, InMemeory, File, etc
	Object paramRef;

	public ParamList(String paramName, String type, String kind, Object paramRef) {
		super();
		this.paramName = paramName;
		this.type = type;
		this.kind = kind;
		this.paramRef = paramRef;
	}

	public String getParamName() {
		return paramName;
	}

	public String getType() {
		return type;
	}

	public String getKind() {
		return kind;
	}

	public Object getParamRef() {
		return paramRef;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLocation(String kind) {
		this.kind = kind;
	}

	public void setParamRef(Object paramRef) {
		this.paramRef = paramRef;
	}

	@Override
	public String toString() {
		return "ParamList [paramName=" + paramName + ", type=" + type + ", kind=" + kind + ", paramRef=" + paramRef + "]";
	}

}
