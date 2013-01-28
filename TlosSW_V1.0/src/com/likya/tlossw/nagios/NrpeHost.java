package com.likya.tlossw.nagios;

public class NrpeHost {
	
	private String id;
	private String ipAddress;
	private int port;
	private OsType os;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public OsType getOs() {
		return os;
	}
	public void setOs(OsType os) {
		this.os = os;
	}
	
}
