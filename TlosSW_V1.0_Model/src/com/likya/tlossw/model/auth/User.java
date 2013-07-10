package com.likya.tlossw.model.auth;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -5429269806185912759L;
	
	private int id;
	private Role role;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
