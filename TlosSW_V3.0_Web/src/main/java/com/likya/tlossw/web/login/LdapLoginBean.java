package com.likya.tlossw.web.login;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.management.remote.JMXConnector;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.log4j.Logger;

import com.likya.tlos.model.xmlbeans.common.RoleType;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.model.auth.WebAppUser;
import com.likya.tlossw.web.appmng.SessionMediator;
import com.likya.tlossw.web.utils.LdapClient;

@ManagedBean(name = LdapLoginBean.BEAN_NAME)
@ViewScoped
public class LdapLoginBean extends LoginBase implements Serializable {

	private static final long serialVersionUID = 2692803198850093936L;

	private DirContext dirContext = null;

	public static final String BEAN_NAME = "ldapLoginBean";

	private static final Logger logger = Logger.getLogger(LdapLoginBean.class);

	public static final String LOGIN_SUCCESS = "loginSuccess";
	public static final String LOGIN_FAILURE = "loginFailure";
	public static final String LOGIN_ENGINE_DIRECTOR = "loginEngineDirector";

	protected Person loggedUser;

	private String userName;
	private String userPassword;

	@ManagedProperty(value = "#{sessionMediator}")
	public SessionMediator sessionMediator;

	@ManagedProperty(value = "#{jmxConnectionHolder.jmxConnector}")
	public JMXConnector jmxConnector;

	public String login() {

		logger.info("start : MyLdapLoginBean : login");

		String returnValue;

		String validated = verifyUserBean();

		// WebSpaceWideRegistery webSpaceWideRegistery = sessionMediator.getWebSpaceWideRegistery();

		if (LOGIN_FAILURE.equals(validated)) {
			returnValue = LOGIN_FAILURE;
		} else if (LOGIN_SUCCESS.equals(validated)) {
			addMessage("loginForm", "loadingMessage", "tlos.login.status", null);
			returnValue = LOGIN_SUCCESS;
		} else {
			addMessage("loginForm", "errorMessage", "invalid Mode", null);
			returnValue = LOGIN_FAILURE;
		}

		logger.info("end : Registered LdapLoginBean : login");

		return returnValue;

	}

	public String verifyUserBean() {

		// ManagerMediator mm = (ManagerMediator)
		// FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("managerMediator");
		// ServletContext webAppContext = (ServletContext)
		// FacesContext.getCurrentInstance().getExternalContext().getContext();
		// JmxUser jmxUserApp = (JmxUser) webAppContext.getAttribute("JmxUser");

		String host = "Localhost";
		String port = "10389";
		String rootdn = "cn=carol,ou=Users,dc=likya,dc=com";
		// String username = "carol";
		String password = "hakan123";

		// String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
		// String MY_HOST = "ldap://Localhost:10389";
		String MGR_DN = "cn=carol,ou=Users,dc=likya,dc=com";
		// String MGR_PW = "hakan123";

		String identifier = "carol";

		String MY_SEARCHBASE = "";
		String MY_FILTER = "(&(cn=" + identifier + "))";
		// String MY_ATTRS[] = { "cn", "mail" };

		LdapClient ldapClient = new LdapClient();
		try {
			ldapClient.connect(host, port, rootdn, MGR_DN, password);

			if (ldapClient.getDirContext() != null) {
				if (ldapClient.searchTest(MY_SEARCHBASE, MY_FILTER)) {
					System.out.println("BASARILI");
					ldapClient.disconnect();
					return LOGIN_SUCCESS;
				} else {
					System.out.println("BASARISIZ !");
				}
			}
		} catch (NamingException e) {
			System.out.println("LDAP authentication could NOT Initialized. " + e.getMessage());
			System.err.println(e);
		}

		logger.info("setting login error message ");

		addMessage(null, FacesMessage.SEVERITY_ERROR, "Kullan�c� adi ya da sifresi hatali !", "Kullanici adi ya da sifresi hatali !");

		return LOGIN_FAILURE;

	}

	public static void copyAppUserToPerson(WebAppUser appUser, Person person) {
		person.setId(appUser.getId());
		person.setName(appUser.getName());
		person.setSurname(appUser.getSurname());
		person.setRole(RoleType.Enum.forString(appUser.getRole().getRoleId()));
		person.setUserPassword(appUser.getPassword());
		person.setUserName(appUser.getUsername());
		person.setTransformToLocalTime(appUser.isTransformToLocalTime());
	}

	/**
	 * Disconnect from the server.
	 */
	public void disconnect() {
		try {
			if (dirContext != null) {
				dirContext.close();
				dirContext = null;
			}
		} catch (NamingException e) {
			logger.error("Ldap client - ", e);
		}
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
