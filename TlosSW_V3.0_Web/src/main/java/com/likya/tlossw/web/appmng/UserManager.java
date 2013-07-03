package com.likya.tlossw.web.appmng;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.likya.tlossw.model.jmx.JmxAppUser;
import com.likya.tlossw.web.utils.UserInfo;

@ManagedBean(name = "userManager")
@ApplicationScoped
public class UserManager implements Serializable {

	private static final long serialVersionUID = -7948774998363534929L;

	private HashMap<Integer, Object> userList;

	@PostConstruct
	public void startUp() {
		userList = new HashMap<>();
	}

	@PreDestroy
	public void dispose() {
		userList.clear();
	}
	
	private void confirmUnicity(JmxAppUser jmxAppUser) {
		
		if(userList.containsKey(jmxAppUser.getAppUser().getId())) {
			((UserInfo) userList.get(jmxAppUser.getAppUser().getId())).getHttpSession().invalidate();
			removeUser(jmxAppUser.getAppUser().getId());
		}
		
		return;
		
	}

	public synchronized void addUser(JmxAppUser jmxAppUser) {
		
		confirmUnicity(jmxAppUser);
		
		UserInfo userInfo = new UserInfo();
		
		userInfo.setUserId(jmxAppUser.getAppUser().getId());
		
		userInfo.setJmxAppUser(jmxAppUser);
		
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		
		HttpSession currentSession = (HttpSession) externalContext.getSession(false);
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		
		userInfo.setHttpSession(currentSession);
		userInfo.setIpAddress(request.getRemoteAddr());
		userInfo.setHostName(request.getRemoteHost());
		userInfo.setUserAgent(request.getHeader("User-Agent"));
		
		userList.put(userInfo.getUserId(), userInfo);
	}
	
	public synchronized void addUser(UserInfo userInfo) {
		confirmUnicity(userInfo.getJmxAppUser());
		userList.put(userInfo.getUserId(), userInfo);
	}

	public synchronized void removeUser(UserInfo userInfo) {
		userList.remove(userInfo.getUserId());
	}

	public synchronized void removeUser(int userId) {
		userList.remove(userId);
	}
}
