package com.likya.tlossw.model.path;

import java.util.ArrayList;

public class BasePathType {
	
	private final static String rootPath = "root";
	
	private ArrayList<String>  pathArray =  new ArrayList<String>();

	public static String getRootPath() {
		return rootPath;
	}

	public BasePathType() {
		super();
		pathArray.add(rootPath);
	}

	protected ArrayList<String> getPathArray() {
		return pathArray;
	}

	public void setPathArray(ArrayList<String> pathArray) {
		this.pathArray = pathArray;
	}
}
