package com.likya.tlossw.web.management;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.common.EmailListDocument.EmailList;
import com.likya.tlos.model.xmlbeans.common.RoleDocument.Role;
import com.likya.tlos.model.xmlbeans.common.TelListDocument.TelList;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.utils.ConstantDefinitions;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "userPanelMBean")
@RequestScoped
public class UserPanelMBean extends TlosSWBaseBean implements Serializable {

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	@ManagedProperty(value = "#{param.selectedUserName}")
	private String selectedUserName;

	@ManagedProperty(value = "#{param.insertCheck}")
	private String insertCheck;

	@ManagedProperty(value = "#{param.iCheck}")
	private String iCheck;

	private static final long serialVersionUID = 1L;

	private Person person;
	private String role;
	private String userPassword2;
	private boolean transformToLocalTime;
	private String telefon;
	private String mail;
	private Collection<SelectItem> roleList;
	private boolean insertButton;
	private int personId;

	@PostConstruct
	public void init() {

		person = Person.Factory.newInstance();
		roleList = WebInputUtils.fillRoleList();

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {

			if (insertCheck.equals("update")) {
				insertButton = false;
				userPassword2 = null;
				person = Person.Factory.newInstance();
				transformToLocalTime = false;

				person = dbOperations.searchUserByUsername(selectedUserName);

				if (person != null) {
					setRole(person.getRole().toString());
					userPassword2 = person.getUserPassword();
					transformToLocalTime = person.getTransformToLocalTime();
					telefon = person.getTelList().getTelArray(0);
					mail = person.getEmailList().getEmailArray(0);
					personId = person.getId();
				}
			} else {
				insertButton = true;

			}
		}
	}

	public String getPersonXML() {
		QName qName = Person.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String personXML = person.xmlText(xmlOptions);

		return personXML;
	}

	public void updateUserAction(ActionEvent e) {

		TelList telList = TelList.Factory.newInstance();
		telList.insertTel(0, telefon);
		person.setTelList(telList);

		person.setId(personId);

		EmailList emailList = EmailList.Factory.newInstance();
		emailList.insertEmail(0, mail);
		person.setEmailList(emailList);

		person.setTransformToLocalTime(transformToLocalTime);

		if (dbOperations.updateUser(getPersonXML())) {
			addMessage("yeniKullanici", FacesMessage.SEVERITY_INFO, "tlos.success.user.update", null);
		} else {
			addMessage("yeniKullanici", FacesMessage.SEVERITY_ERROR, "tlos.error.user.update", null);
		}

	}

	public void insertUserAction(ActionEvent e) {

		TelList telList = TelList.Factory.newInstance();
		telList.addNewTel();
		telList.setTelArray(0, telefon);
		person.setTelList(telList);

		EmailList emailList = EmailList.Factory.newInstance();
		emailList.addEmail(mail);
		person.setEmailList(emailList);

		person.setTransformToLocalTime(transformToLocalTime);

		if (getUserId()) {
			Person personByUsername = dbOperations.searchUserByUsername(person.getUserName());
			if (personByUsername != null && personByUsername.getUserName().equals(person.getUserName())) {
				addSuccessMessage("yeniKullanici", "tlos.info.user.insert.existUser", "Info");
				return;
			}

			if (dbOperations.insertUser(getPersonXML())) {
				addSuccessMessage("yeniKullanici", "tlos.success.user.insert", "Info");
				resetPersonAction();
			} else {
				addFailMessage("yeniKullanici", "tlos.error.user.insert", "Error");
			}
		}

	}

	public void resetPersonAction() {
		setRole(null);
		telefon = null;
		mail = null;
		userPassword2 = null;
		person = Person.Factory.newInstance();
		// searchUserList = null;
		transformToLocalTime = false;
		roleList = WebInputUtils.fillRoleList();
	}

	public boolean getUserId() {
		int userId = dbOperations.getNextId(ConstantDefinitions.USER_ID);
		if (userId < 0) {
			addSuccessMessage("yeniKullanici", "tlos.info.user.db.getId", "Info");
			return false;
		}
		person.setId(userId);
		return true;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
		person.setRole(Role.Enum.forString(role));
	}

	public String getUserPassword2() {
		return userPassword2;
	}

	public void setUserPassword2(String userPassword2) {
		this.userPassword2 = userPassword2;
	}

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Collection<SelectItem> getRoleList() {
		return roleList;
	}

	public void setRoleList(Collection<SelectItem> roleList) {
		this.roleList = roleList;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public String getSelectedUserName() {
		return selectedUserName;
	}

	public void setSelectedUserName(String selectedUserName) {
		this.selectedUserName = selectedUserName;
	}

	public boolean isInsertButton() {
		return insertButton;
	}

	public void setInsertButton(boolean insertButton) {
		this.insertButton = insertButton;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
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

}
