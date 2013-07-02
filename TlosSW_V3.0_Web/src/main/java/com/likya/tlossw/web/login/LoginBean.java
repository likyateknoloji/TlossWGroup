package com.likya.tlossw.web.login;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.RoleDocument.Role;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.model.WebSpaceWideRegistery;
import com.likya.tlossw.model.auth.AppUser;
import com.likya.tlossw.model.jmx.JmxAppUser;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.webclient.TEJmxMpClient;

@ManagedBean(name = LoginBean.BEAN_NAME)
@ViewScoped
public class LoginBean extends LoginBase implements Serializable {

	public static final String BEAN_NAME = "loginBean";
	private static final long serialVersionUID = -5124116113489857945L;

	private static final Logger logger = Logger.getLogger(LoginBean.class);


	public static final String LOGIN_SUCCESS = "/inc/index.jsf?faces-redirect=true";
	public static final String LOGIN_FAILURE = "/login.jsf?faces-redirect=true";
	public static final String LOGIN_ENGINE_DIRECTOR = "/inc/index.jsf?faces-redirect=true";
	
	protected Person loggedUser;
	
	private String userName;
	private String userPassword;

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	
	public String login() {

		logger.info("start : MyLoginBean : login");

		String returnValue = null;

		String validated = verifyUserDB();

		WebSpaceWideRegistery webSpaceWideRegistery = getSessionMediator().getWebSpaceWideRegistery();

		
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

		setSessionLoginParam(true);
		
		return returnValue;

	}
	
	public String verifyUserDB() {

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

		Object o = dbOperations.checkUser(jmxAppUser);

		if (o instanceof JmxAppUser) {

			setSessionLoginParam(true);
			//jmxAppUser.setAppUser(((JmxAppUser) o).getAppUser());

			if (jmxAppUser.getAppUser().getResourceMapper().size() == 0) {
				logger.error("Kullanicinin Rolune Uygun Kaynak Bulunamadi ==> " + jmxAppUser.getAppUser().getRole().getRoleId());
				return LOGIN_FAILURE;
			}
			
			getSessionMediator().setJmxAppUser(jmxAppUser);

			getSessionMediator().setResourceMapper(jmxAppUser.getAppUser().getResourceMapper());
			loggedUser = Person.Factory.newInstance();
			copyAppUserToPerson(jmxAppUser.getAppUser(), loggedUser);
			appUser.setTransformToLocalTime((jmxAppUser.getAppUser()).isTransformToLocalTime());

			WebSpaceWideRegistery webSpaceWideRegistery = TEJmxMpClient.retrieveWebSpaceWideRegistery(jmxAppUser);
			getSessionMediator().setWebSpaceWideRegistery(webSpaceWideRegistery);
			
			
			return LOGIN_SUCCESS;

		}

		logger.info("setting login error message ");

		addMessage(null, FacesMessage.SEVERITY_ERROR, "Kullanıcı adı ya da şifresi hatalı !", "Kullanıcı adı ya da şifresi hatalı !");

		return LOGIN_FAILURE;

	}

	/*
	public String verifyUserJmx() {

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

			getSessionMediator().setResourceMapper(jmxAppUser.getAppUser().getResourceMapper());
			loggedUser = Person.Factory.newInstance();
			copyAppUserToPerson(jmxAppUser.getAppUser(), loggedUser);
			appUser.setTransformToLocalTime((jmxAppUser.getAppUser()).isTransformToLocalTime());

			WebSpaceWideRegistery webSpaceWideRegistery = TEJmxMpClient.retrieveWebSpaceWideRegistery(jmxConnector, jmxAppUser);
			getSessionMediator().setWebSpaceWideRegistery(webSpaceWideRegistery);
			
			return LOGIN_SUCCESS;

		}

		logger.info("setting login error message ");

		addMessage(null, FacesMessage.SEVERITY_ERROR, "Kullanıcı adı ya da şifresi hatalı !", "Kullanıcı adı ya da şifresi hatalı !");

		return LOGIN_FAILURE;

	}
	*/
	
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
	/*
	public JMXConnector getJmxConnector() {
		return jmxConnector;
	}

	public void setJmxConnector(JMXConnector jmxConnector) {
		this.jmxConnector = jmxConnector;
	}
	*/

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

}
