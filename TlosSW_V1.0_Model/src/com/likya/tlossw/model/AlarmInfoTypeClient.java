package com.likya.tlossw.model;

import java.io.Serializable;

public class AlarmInfoTypeClient implements Serializable {

	private static final long serialVersionUID = 7189814018388602017L;
	
	private String alarmId;
	private String alarmHistoryId;
	private String alarmName;
	private String description;
	private String alarmType;
	private String creationDate;
	private String resourceName;
	private String level;
	private String subscriber;
	private String warnBy;
	
	public String getAlarmId() {
		return alarmId;
	}
	
	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}
	
	public String getAlarmName() {
		return alarmName;
	}
	
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getResourceName() {
		return resourceName;
	}
	
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
	public String getSubscriber() {
		return subscriber;
	}
	
	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setAlarmHistoryId(String alarmHistoryId) {
		this.alarmHistoryId = alarmHistoryId;
	}

	public String getAlarmHistoryId() {
		return alarmHistoryId;
	}

	public void setWarnBy(String warnBy) {
		this.warnBy = warnBy;
	}

	public String getWarnBy() {
		return warnBy;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getAlarmType() {
		return alarmType;
	}

	
}