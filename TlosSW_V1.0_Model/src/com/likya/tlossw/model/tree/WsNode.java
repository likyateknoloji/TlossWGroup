package com.likya.tlossw.model.tree;

import java.io.Serializable;

public class WsNode implements Serializable {

	private static final long serialVersionUID = 2297708810106906497L;

	private String labelText;
	private String leafIcon;
	private String id;
	private String path;
	private String name;

	public String getLeafIcon() {
		return leafIcon;
	}

	public void setLeafIcon(String leafIcon) {
		this.leafIcon = leafIcon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabelText() {
		if(labelText == null || labelText.equals("")) {
			labelText = name;
		}
		return labelText;// + "[" + id + "]";
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

}
