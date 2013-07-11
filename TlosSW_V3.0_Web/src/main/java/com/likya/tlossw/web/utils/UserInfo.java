package com.likya.tlossw.web.utils;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import com.likya.tlossw.model.auth.WebAppUser;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -6661653607676377510L;

	private int userId;
	private HttpSession httpSession;
	private String ipAddress;
	private String hostName;
	private String userAgent;
	private WebAppUser webAppUser;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public WebAppUser getWebAppUser() {
		return webAppUser;
	}

	public void setWebAppUser(WebAppUser webAppUser) {
		this.webAppUser = webAppUser;
	}

}
