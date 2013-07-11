/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model : AppUser.java
 * @author Serkan Tas
 * Tarih : Jul 20, 2009 11:20:16 AM
 */

package com.likya.tlossw.model.auth;

import com.likya.tlossw.model.jmx.JmxUser;


public class WebAppUser extends JmxUser {

	private static final long serialVersionUID = 1L;

	private String name;
	private String surname;
	private String username;
	private String password;
	private String email;
	private String lastlogin;
	private boolean transformToLocalTime;

	private ResourceMapper resourceMapper;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastlogin() {
		return lastlogin;
	}

	public void setLastlogin(String lastlogin) {
		this.lastlogin = lastlogin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public ResourceMapper getResourceMapper() {
		return resourceMapper;
	}

	public void setResourceMapper(ResourceMapper resourceMapper) {
		this.resourceMapper = resourceMapper;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}
}
