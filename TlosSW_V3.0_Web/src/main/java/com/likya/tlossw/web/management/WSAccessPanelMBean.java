package com.likya.tlossw.web.management;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.common.ActiveDocument.Active;
import com.likya.tlos.model.xmlbeans.common.RoleDocument.Role;
import com.likya.tlos.model.xmlbeans.common.UserIdDocument.UserId;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.webservice.AllowedRolesDocument.AllowedRoles;
import com.likya.tlos.model.xmlbeans.webservice.AllowedUsersDocument.AllowedUsers;
import com.likya.tlos.model.xmlbeans.webservice.UserAccessProfileDocument.UserAccessProfile;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;
import com.likya.tlossw.model.WSAccessInfoTypeClient;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "wsAccessPanelMBean")
@ViewScoped
public class WSAccessPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 3068992050142317053L;

	private String selectedWSAccessID;
	private String insertCheck;
	private String iCheck;

	private UserAccessProfile userAccessProfile;
	private WSAccessInfoTypeClient wsAccessInfoTypeClient;

	private Collection<SelectItem> webServiceDefinitionList = null;
	private String webServiceDefinition;

	private String userType;

	private Collection<SelectItem> roleList = null;
	private String[] selectedRoleList;

	private Collection<SelectItem> userList = null;
	private String[] selectedUserList;

	private String active;

	private boolean insertButton;

	public void dispose() {
		resetUserAccessProfileAction();
		userAccessProfile = null;
	}

	@PostConstruct
	public void init() {
		resetUserAccessProfileAction();

		selectedWSAccessID = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedWSAccessID"));
		insertCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("insertCheck"));
		iCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("iCheck"));

		userAccessProfile = UserAccessProfile.Factory.newInstance();

		ArrayList<Person> dbUserList = getDbOperations().getUsers();
		userList = WebInputUtils.fillUserList(dbUserList);

		roleList = WebInputUtils.fillRoleList();
		
		ArrayList<WebServiceDefinition> webServiceList = getDbOperations().getWebServiceListForActiveUser(getWebAppUser().getId());
		webServiceDefinitionList = WebInputUtils.fillWebServiceDefinitionList(webServiceList);

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {

			if (insertCheck.equals("update")) {

				insertButton = false;

				userAccessProfile = getDbOperations().searchWSAccessByID(selectedWSAccessID);

				if (userAccessProfile != null) {
					webServiceDefinition = userAccessProfile.getWebServiceID() + "";
					active = userAccessProfile.getActive().toString();

					if (userAccessProfile.getAllowedUsers() != null && userAccessProfile.getAllowedUsers().getUserIdArray() != null && userAccessProfile.getAllowedUsers().sizeOfUserIdArray() > 0) {
						userType = "User";

						int length = userAccessProfile.getAllowedUsers().sizeOfUserIdArray();
						setSelectedUserList(new String[length]);

						for (int i = 0; i < length; i++) {
							getSelectedUserList()[i] = userAccessProfile.getAllowedUsers().getUserIdArray(i) + "";
						}

					} else {
						userType = "Role";

						int length = userAccessProfile.getAllowedRoles().sizeOfRoleArray();
						setSelectedRoleList(new String[length]);

						for (int i = 0; i < length; i++) {
							getSelectedRoleList()[i] = userAccessProfile.getAllowedRoles().getRoleArray(i) + "";
						}
					}
				}

			} else {
				insertButton = true;
			}
		}
	}

	public void resetUserAccessProfileAction() {
		userAccessProfile = UserAccessProfile.Factory.newInstance();

		active = "";
		webServiceDefinition = "";
		userType = "";
		selectedRoleList = null;
		selectedUserList = null;
	}

	public String getWSAccessProfileXML() {
		QName qName = UserAccessProfile.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String userAccessProfileXML = userAccessProfile.xmlText(xmlOptions);

		return userAccessProfileXML;
	}

	public void updateWSAccessAction(ActionEvent e) {
		fillUserAccessProperties();

		if (getDbOperations().updateWSAccessProfile(getWSAccessProfileXML())) {
			addMessage("insertWSAccessAction", FacesMessage.SEVERITY_INFO, "tlos.success.wsAccessDef.update", null);
		} else {
			addMessage("insertWSAccessAction", FacesMessage.SEVERITY_INFO, "tlos.error.wsAccessDefinition.update", null);
		}

		// TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "id=" + userAccessProfile.getID() + "insertWSAccessAction", e.getComponent().getId(), resolveMessage("tlos.trace.wsAccess.update.desc").toString());
	}

	public void insertWSAccessAction(ActionEvent e) {
		fillUserAccessProperties();

		if (setAccessProfileID()) {
			if (getDbOperations().insertWSAccessProfile(getWSAccessProfileXML())) {
				addMessage("insertWSAccessAction", FacesMessage.SEVERITY_INFO, "tlos.success.wsAccessDef.insert", null);
				resetUserAccessProfileAction();
			} else {
				addMessage("insertWSAccessAction", FacesMessage.SEVERITY_INFO, "tlos.error.wsAccessDefinition.insert", null);
			}

			// TraceBean.traceData(Thread.currentThread().getStackTrace()[1], "insertWSAccessAction", e.getComponent().getId(), resolveMessage("tlos.trace.wsAccess.insert.desc").toString());
		}
	}

	private void fillUserAccessProperties() {
		if (!webServiceDefinition.equals("")) {
			userAccessProfile.setWebServiceID(new BigInteger(webServiceDefinition));
		}

		if (!active.equals("")) {
			userAccessProfile.setActive(Active.Enum.forString(active));
		} else {
			userAccessProfile.setActive(null);
		}

		if (userType.equals("User")) {
			fillAllowedUserList();
		} else if (userType.equals("Role")) {
			fillAllowedRoleList();
		}
	}

	public void fillAllowedUserList() {
		if (selectedUserList != null && selectedUserList.length > 0) {
			AllowedUsers allowedUsers = AllowedUsers.Factory.newInstance();

			for (int i = 0; i < selectedUserList.length; i++) {
				String selectedId = selectedUserList[i];

				if (!selectedId.equals("")) {
					UserId userId = allowedUsers.addNewUserId();
					userId.setStringValue(selectedId);
				}
			}
			userAccessProfile.setAllowedUsers(allowedUsers);

		} else {

			if (userAccessProfile.getAllowedUsers() != null) {
				XmlCursor xmlCursor = userAccessProfile.getAllowedUsers().newCursor();
				xmlCursor.removeXml();
			}
		}

		if (userAccessProfile.getAllowedRoles() != null) {
			XmlCursor xmlCursor = userAccessProfile.getAllowedRoles().newCursor();
			xmlCursor.removeXml();
		}
	}

	public void fillAllowedRoleList() {
		if (selectedRoleList != null && selectedRoleList.length > 0) {
			AllowedRoles allowedRoles = AllowedRoles.Factory.newInstance();

			for (int i = 0; i < selectedRoleList.length; i++) {
				String selectedRole = selectedRoleList[i];

				if (!selectedRole.equals("")) {
					Role role = allowedRoles.addNewRole();
					role.setStringValue(selectedRole);
				}
			}
			userAccessProfile.setAllowedRoles(allowedRoles);

		} else {

			if (userAccessProfile.getAllowedRoles() != null) {
				XmlCursor xmlCursor = userAccessProfile.getAllowedRoles().newCursor();
				xmlCursor.removeXml();
			}
		}

		if (userAccessProfile.getAllowedUsers() != null) {
			XmlCursor xmlCursor = userAccessProfile.getAllowedUsers().newCursor();
			xmlCursor.removeXml();
		}
	}

	// veri tabaninda kayitli siradaki id degerini set ediyor
	public boolean setAccessProfileID() {
		int id = getDbOperations().getNextId(CommonConstantDefinitions.WSUSERPROFILE_ID);

		if (id < 0) {
			addMessage("insertWSAccessAction", FacesMessage.SEVERITY_INFO, "tlos.error.wsAccessDefinition.getId", null);
			return false;
		}
		userAccessProfile.setID(new BigInteger(id + ""));

		return true;
	}

	public boolean isInsertButton() {
		return insertButton;
	}

	public void setInsertButton(boolean insertButton) {
		this.insertButton = insertButton;
	}

	public String getInsertCheck() {
		return insertCheck;
	}

	public void setInsertCheck(String insertCheck) {
		this.insertCheck = insertCheck;
	}

	public String getiCheck() {
		return iCheck;
	}

	public void setiCheck(String iCheck) {
		this.iCheck = iCheck;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getSelectedWSAccessID() {
		return selectedWSAccessID;
	}

	public void setSelectedWSAccessID(String selectedWSAccessID) {
		this.selectedWSAccessID = selectedWSAccessID;
	}

	public UserAccessProfile getUserAccessProfile() {
		return userAccessProfile;
	}

	public void setUserAccessProfile(UserAccessProfile userAccessProfile) {
		this.userAccessProfile = userAccessProfile;
	}

	public WSAccessInfoTypeClient getWsAccessInfoTypeClient() {
		return wsAccessInfoTypeClient;
	}

	public void setWsAccessInfoTypeClient(WSAccessInfoTypeClient wsAccessInfoTypeClient) {
		this.wsAccessInfoTypeClient = wsAccessInfoTypeClient;
	}

	public Collection<SelectItem> getWebServiceDefinitionList() {
		return webServiceDefinitionList;
	}

	public void setWebServiceDefinitionList(Collection<SelectItem> webServiceDefinitionList) {
		this.webServiceDefinitionList = webServiceDefinitionList;
	}

	public String getWebServiceDefinition() {
		return webServiceDefinition;
	}

	public void setWebServiceDefinition(String webServiceDefinition) {
		this.webServiceDefinition = webServiceDefinition;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Collection<SelectItem> getRoleList() {
		return roleList;
	}

	public void setRoleList(Collection<SelectItem> roleList) {
		this.roleList = roleList;
	}

	public String[] getSelectedRoleList() {
		return selectedRoleList;
	}

	public void setSelectedRoleList(String[] selectedRoleList) {
		this.selectedRoleList = selectedRoleList;
	}

	public Collection<SelectItem> getUserList() {
		return userList;
	}

	public void setUserList(Collection<SelectItem> userList) {
		this.userList = userList;
	}

	public String[] getSelectedUserList() {
		return selectedUserList;
	}

	public void setSelectedUserList(String[] selectedUserList) {
		this.selectedUserList = selectedUserList;
	}

}
