package com.likya.tlossw.web.appmng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.likya.tlossw.model.auth.WebAppUser;
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

	private boolean confirmUnicity(WebAppUser webAppUser, HttpSession currentSession) {

		if (userList.containsKey(webAppUser.getId())) {

			HttpSession userSession = ((UserInfo) userList.get(webAppUser.getId())).getHttpSession();

			if (currentSession != null && !currentSession.equals(userSession)) {

				try {
					userSession.invalidate();
				} catch (IllegalStateException ise) {
					// it's invalid
				}

				removeUser(webAppUser.getId());

				return true;
			}

			return false;
		}

		return true;

	}

	public synchronized void addUser(WebAppUser webAppUser) {

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

		HttpSession currentSession = (HttpSession) externalContext.getSession(false);

		if (!confirmUnicity(webAppUser, currentSession)) {
			return;
		}

		UserInfo userInfo = new UserInfo();

		userInfo.setUserId(webAppUser.getId());

		userInfo.setWebAppUser(webAppUser);

		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

		userInfo.setHttpSession(currentSession);
		userInfo.setIpAddress(request.getRemoteAddr());
		userInfo.setHostName(request.getRemoteHost());
		userInfo.setUserAgent(request.getHeader("User-Agent"));

		userList.put(userInfo.getUserId(), userInfo);

		cleanupSessionQueue();
	}

	/**
	 * Timeout süresi dolan sessionların kullanıcılarını kullanıcı listesinden çıkartıyor.
	 */
	private void cleanupSessionQueue() {
		ArrayList<Integer> timeoutList = new ArrayList<Integer>();
		for (int userId : userList.keySet()) {
			try {
				@SuppressWarnings("unused")
				long time = ((UserInfo) userList.get(userId)).getHttpSession().getCreationTime();
			} catch (IllegalStateException ise) {
				timeoutList.add(userId);
			}
		}

		for (int userId : timeoutList) {
			userList.remove(userId);
		}
	}

	public synchronized void removeUser(UserInfo userInfo) {
		userList.remove(userInfo.getUserId());
	}

	public synchronized void removeUser(int userId) {
		userList.remove(userId);
	}
}
