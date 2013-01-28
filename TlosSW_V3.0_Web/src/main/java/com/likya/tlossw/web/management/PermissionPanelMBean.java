package com.likya.tlossw.web.management;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.primefaces.component.datatable.DataTable;

import com.likya.tlos.model.xmlbeans.permission.PermissionsDocument.Permissions;
import com.likya.tlossw.model.auth.ResourcePermission;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.common.ManagementUtils;

@ManagedBean(name = "permissionPanelMBean")
@RequestScoped
public class PermissionPanelMBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<ResourcePermission> searchPermissionList;
	private transient DataTable searchPermissionTable;

	private List<ResourcePermission> filteredPermissions;

	@PostConstruct
	public void init() {
		searchPermissionList = getDbOperations().getPermissions();
	}

	public String getPermissionsXML(Permissions permissions) {
		QName qName = Permissions.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String permissionsXML = permissions.xmlText(xmlOptions);

		return permissionsXML;
	}

	public void savePermissionListAction(ActionEvent e) {
		Permissions permissions = Permissions.Factory.newInstance();

		for (ResourcePermission resourcePermission : searchPermissionList) {
			permissions.addNewPermission();
			permissions.setPermissionArray(permissions.sizeOfPermissionArray() - 1, ManagementUtils.ResourcePermissionToPermission(resourcePermission));
		}

		if (getDbOperations().updatePermissions(getPermissionsXML(permissions))) {
			addMessage("yetkilendirme", FacesMessage.SEVERITY_INFO, "tlos.success.permission.update", null);
		} else {
			addMessage("yetkilendirme", FacesMessage.SEVERITY_ERROR, "tlos.error.permission.update", null);
		}
	}

	public ArrayList<ResourcePermission> getSearchPermissionList() {
		return searchPermissionList;
	}

	public void setSearchPermissionList(ArrayList<ResourcePermission> searchPermissionList) {
		this.searchPermissionList = searchPermissionList;
	}

	public DataTable getSearchPermissionTable() {
		return searchPermissionTable;
	}

	public void setSearchPermissionTable(DataTable searchPermissionTable) {
		this.searchPermissionTable = searchPermissionTable;
	}

	public List<ResourcePermission> getFilteredPermissions() {
		return filteredPermissions;
	}

	public void setFilteredPermissions(List<ResourcePermission> filteredPermissions) {
		this.filteredPermissions = filteredPermissions;
	}

}
