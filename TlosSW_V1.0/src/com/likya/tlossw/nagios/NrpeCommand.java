package com.likya.tlossw.nagios;

public class NrpeCommand {
	private int id;
	private OsType osType;
	private String command;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public OsType getOsType() {
		return osType;
	}
	public void setOsType(OsType osType) {
		this.osType = osType;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}

}
