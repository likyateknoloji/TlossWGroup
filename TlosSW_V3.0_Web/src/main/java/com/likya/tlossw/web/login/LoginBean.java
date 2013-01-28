package com.likya.tlossw.web.login;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.management.remote.JMXConnector;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.RoleDocument.Role;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.model.WebSpaceWideRegistery;
import com.likya.tlossw.model.auth.AppUser;
import com.likya.tlossw.model.jmx.JmxAppUser;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.appmng.SessionMediator;
import com.likya.tlossw.webclient.TEJmxMpClient;
import com.likya.tlossw.webclient.TEJmxMpDBClient;

@ManagedBean(name = LoginBean.BEAN_NAME)
@ViewScoped
public class LoginBean extends TlosSWBaseBean implements Serializable {

	public static final String BEAN_NAME = "loginBean";
	private static final long serialVersionUID = -5124116113489857945L;

	private static final Logger logger = Logger.getLogger(LoginBean.class);


	public static final String LOGIN_SUCCESS = "loginSuccess";
	public static final String LOGIN_FAILURE = "loginFailure";
	public static final String LOGIN_ENGINE_DIRECTOR = "loginEngineDirector";
	
	protected Person loggedUser;
	
	private String userName;
	private String userPassword;

	@ManagedProperty(value = "#{sessionMediator}")
	public SessionMediator sessionMediator;
	
	public SessionMediator getSessionMediator() {
		return sessionMediator;
	}

	public void setSessionMediator(SessionMediator sessionMediator) {
		this.sessionMediator = sessionMediator;
	}

	@ManagedProperty(value = "#{jmxConnectionHolder.jmxConnector}")
	public JMXConnector jmxConnector;

	
	public String login() {

		logger.info("start : MyLoginBean : login");

		String returnValue;

		String validated = verifyUserBean();

		WebSpaceWideRegistery webSpaceWideRegistery = sessionMediator.getWebSpaceWideRegistery();

		if (LOGIN_FAILURE.equals(validated)) {
			returnValue = LOGIN_FAILURE;
		} else if (webSpaceWideRegistery.getWaitConfirmOfGUI() && loggedUser.getRole() != Role.ADMIN) {
			addMessage("loginForm", "loadingMessage", "tlos.info.engine.start.authorization", null);
			returnValue = LOGIN_FAILURE;
		} else if (webSpaceWideRegistery.getWaitConfirmOfGUI() && loggedUser.getRole() == Role.ADMIN) {
			addMessage("loginForm", "loadingMessage", "tlos.info.engine.start.waitMode", null);
			returnValue = LOGIN_ENGINE_DIRECTOR;
		} else if (!webSpaceWideRegistery.getWaitConfirmOfGUI() && LOGIN_SUCCESS.equals(validated)) {
			addMessage("loginForm", "loadingMessage", "tlos.login.status", null);
			returnValue = LOGIN_SUCCESS;
		} else {
			addMessage("loginForm", "errorMessage", "invalid Mode", null);
			returnValue = LOGIN_FAILURE;
		}

		logger.info("end : RegisteredLoginBean : login");

		return returnValue;

	}
	
	public String verifyUserBean() {

		// ManagerMediator mm = (ManagerMediator)
		// FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("managerMediator");
		// ServletContext webAppContext = (ServletContext)
		// FacesContext.getCurrentInstance().getExternalContext().getContext();
		// JmxUser jmxUserApp = (JmxUser) webAppContext.getAttribute("JmxUser");
		
		JmxAppUser jmxAppUser = new JmxAppUser();
		AppUser appUser = new AppUser();
		appUser.setUsername(userName);
		appUser.setPassword(userPassword);

		jmxAppUser.setAppUser(appUser);

		Object o = TEJmxMpDBClient.checkUser(jmxConnector, jmxAppUser);

		if (o instanceof JmxUser) {

			setSessionLoginParam(true);
			jmxAppUser.setAppUser(((JmxAppUser) o).getAppUser());

			if (jmxAppUser.getAppUser().getResourceMapper().size() == 0) {

				logger.error("Kullanicinin Rolune Uygun Kaynak Bulunamadi ==> " + jmxAppUser.getAppUser().getRole().getRoleId());

				return LOGIN_FAILURE;

			}

			sessionMediator.setResourceMapper(jmxAppUser.getAppUser().getResourceMapper());
			loggedUser = Person.Factory.newInstance();
			copyAppUserToPerson(jmxAppUser.getAppUser(), loggedUser);
			appUser.setTransformToLocalTime((jmxAppUser.getAppUser()).isTransformToLocalTime());

			WebSpaceWideRegistery webSpaceWideRegistery = TEJmxMpClient.retrieveWebSpaceWideRegistery(jmxConnector, jmxAppUser);
			sessionMediator.setWebSpaceWideRegistery(webSpaceWideRegistery);
			
			return LOGIN_SUCCESS;

		}

		logger.info("setting login error message ");

		addMessage(null, FacesMessage.SEVERITY_ERROR, "Kullanýcý adý ya da þifresi hatalý !", "Kullanýcý adý ya da þifresi hatalý !");

		return LOGIN_FAILURE;

	}
	
	private void setSessionLoginParam(boolean isLoggedIn) {
		HttpSession httpSession = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if (facesContext != null) {
			httpSession = (HttpSession) facesContext.getExternalContext().getSession(false);
		}

		if (httpSession != null) {
			httpSession.setAttribute("LoggedIn", Boolean.toString(isLoggedIn));
		}
	}

	public static void copyAppUserToPerson(AppUser appUser, Person person) {
		person.setId(appUser.getId());
		person.setName(appUser.getName());
		person.setSurname(appUser.getSurname());
		person.setRole(Role.Enum.forString(appUser.getRole().getRoleId()));
		person.setUserPassword(appUser.getPassword());
		person.setUserName(appUser.getUsername());
		person.setTransformToLocalTime(appUser.isTransformToLocalTime());
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public JMXConnector getJmxConnector() {
		return jmxConnector;
	}

	public void setJmxConnector(JMXConnector jmxConnector) {
		this.jmxConnector = jmxConnector;
	}

}
