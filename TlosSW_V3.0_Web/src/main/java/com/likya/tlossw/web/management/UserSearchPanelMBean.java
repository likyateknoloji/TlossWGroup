package com.likya.tlossw.web.management;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.common.RoleDocument.Role;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "userSearchPanelMBean")
@ViewScoped
public class UserSearchPanelMBean extends TlosSWBaseBean implements Serializable {

	// private static Logger logger = Logger.getLogger(UserMBean.class);

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;

	private static final long serialVersionUID = -7436267818850177642L;

	private Collection<SelectItem> roleList;
	private String role = null;
	private String telefon;
	private String mail;
	private Person person;
	private ArrayList<Person> searchUserList;

	private List<Person> filteredUsers;

	private String userPassword2;
	private boolean transformToLocalTime = false;

	private transient DataTable searchUserTable;
	private Person selectedRow;

	public void dispose() {
		person = null;
		roleList = null;
	}

	@PostConstruct
	public void init() {
		person = Person.Factory.newInstance();
		roleList = WebInputUtils.fillRoleList();
		// resetPersonAction();
	}

	public void resetPersonAction() {
		role = null;
		person = Person.Factory.newInstance();
		roleList = WebInputUtils.fillRoleList();
		searchUserList = null;
	}

	public String getPersonXML() {
		QName qName = Person.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String personXML = person.xmlText(xmlOptions);

		return personXML;
	}

	public void searchUserAction(ActionEvent e) {
		if (!role.equals("")) {
			person.setRole(Role.Enum.forString(role));
		} else {
			person.setRole(null);
		}
		
		searchUserList = dbOperations.searchUser(getPersonXML());

		if (searchUserList == null || searchUserList.size() == 0) {
			addMessage("searchUser", FacesMessage.SEVERITY_INFO, "tlos.info.search.noRecord", "");
		}
	}

	public void editUserAction(ActionEvent e) {
		person = (Person) searchUserTable.getRowData();
		telefon = person.getTelList().getTelArray(0);
		mail = person.getEmailList().getEmailArray(0);
		role = person.getRole().toString();
		userPassword2 = person.getUserPassword();
		transformToLocalTime = person.getTransformToLocalTime();

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("userPanel.xhtml");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public void deleteUserAction(ActionEvent e) {
		// person = (Person) searchUserTable.getRowData();
		person = selectedRow;
		
		role = person.getRole().toString();

		if (checkDeleteUser()) {
			if (dbOperations.deleteUser(getPersonXML())) {
				searchUserList.remove(person);
				person = Person.Factory.newInstance();
				addMessage("searchUser", FacesMessage.SEVERITY_INFO, "tlos.success.user.delete", null);
			} else {
				addMessage("searchUser", FacesMessage.SEVERITY_ERROR, "tlos.error.user.delete", null);
			}
		}

	}

	public boolean checkDeleteUser() {
		// TODO login kullanıcının id değerine göre kontrol edilmeli, 1'e göre değil.
		if (person.getId() == 1) {
			addMessage("searchUser", FacesMessage.SEVERITY_ERROR, "tlos.error.user.delete.defaultUser", null);
			return false;
		}

		return true;
	}

	public Collection<SelectItem> getRoleList() {
		return roleList;
	}

	public void setRoleList(Collection<SelectItem> roleList) {
		this.roleList = roleList;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public ArrayList<Person> getSearchUserList() {
		return searchUserList;
	}

	public void setSearchUserList(ArrayList<Person> searchUserList) {
		this.searchUserList = searchUserList;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUserPassword2() {
		return userPassword2;
	}

	public void setUserPassword2(String userPassword2) {
		this.userPassword2 = userPassword2;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}

	public boolean isTransformToLocalTime() {
		return transformToLocalTime;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public DataTable getSearchUserTable() {
		return searchUserTable;
	}

	public void setSearchUserTable(DataTable searchUserTable) {
		this.searchUserTable = searchUserTable;
	}

	public List<Person> getFilteredUsers() {
		return filteredUsers;
	}

	public void setFilteredUsers(List<Person> filteredUsers) {
		this.filteredUsers = filteredUsers;
	}

	public Person getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(Person selectedRow) {
		this.selectedRow = selectedRow;
	}

}
