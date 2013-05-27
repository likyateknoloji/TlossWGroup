package com.likya.tlossw.model;

import java.io.Serializable;

public class FTPAccessInfoTypeClient implements Serializable {

	private static final long serialVersionUID = 1344851836456524973L;

	private String ipAddress;
	private int port;
	private String userName;
	private String password;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
