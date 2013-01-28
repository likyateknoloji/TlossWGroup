package com.likya.tlossw.web.common;

import java.math.BigInteger;

import com.likya.tlossw.model.auth.ResourcePermission;
import com.likya.tlos.model.xmlbeans.permission.PermissionDocument.Permission;
import com.likya.tlos.model.xmlbeans.permission.ResourceDocument.Resource;
import com.likya.tlos.model.xmlbeans.permission.RolesDocument.Roles;

public class ManagementUtils {

	public static Permission ResourcePermissionToPermission(ResourcePermission resourcePermission) {
		Permission permission = Permission.Factory.newInstance();
		Resource resource = Resource.Factory.newInstance();
		Roles roles = Roles.Factory.newInstance();

		permission.setId(new BigInteger("" + resourcePermission.getId()));
		resource.setType(resourcePermission.getResourceType());
		resource.newCursor().setTextValue(resourcePermission.getResourceName());

		if (resourcePermission.getAdmin()) {
			roles.addRole("ADMIN");
		}
		if (resourcePermission.getSuperUser()) {
			roles.addRole("SUPERUSER");
		}
		if (resourcePermission.getNormalUser()) {
			roles.addRole("NORMALUSER");
		}

		permission.setResource(resource);
		permission.setRoles(roles);

		return permission;
	}

}
