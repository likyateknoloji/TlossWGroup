package com.likya.tlossw.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.xmlbeans.XmlCursor;

import com.likya.tlos.model.xmlbeans.common.EntryDocument.Entry;
import com.likya.tlos.model.xmlbeans.common.EnvVariablesDocument.EnvVariables;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.permission.PermissionDocument.Permission;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.useroutput.UserResourceMapDocument.UserResourceMap;
import com.likya.tlossw.model.auth.AppUser;
import com.likya.tlossw.model.auth.Resource;
import com.likya.tlossw.model.auth.ResourceMapper;
import com.likya.tlossw.model.auth.ResourcePermission;
import com.likya.tlossw.model.auth.Role;

public class XmlBeansTransformer {

	public static AppUser personToAppUser(UserResourceMap userResourceMap) {

		AppUser appUser = new AppUser();

		Person person = userResourceMap.getPerson();
		ResourceMapper resourceMapper = resourcesToResourceMapper(userResourceMap.getResources().getResourceArray(), person.getRole().toString());

		Role role = new Role(person.getRole().toString());

		appUser.setId(person.getId());
		appUser.setUsername(person.getUserName());
		appUser.setPassword(person.getUserPassword());
		appUser.setName(person.getName());
		// TODO Burada şimdilik ilkini aldık ama bakılması lazım.
		appUser.setEmail(person.getEmailList().getEmailArray(0));
		appUser.setSurname(person.getSurname());

		appUser.setRole(role);
		appUser.setResourceMapper(resourceMapper);
		appUser.setTransformToLocalTime(person.getTransformToLocalTime());

		return appUser;
	}

	public static ResourceMapper resourcesToResourceMapper(com.likya.tlos.model.xmlbeans.permission.ResourceDocument.Resource[] resources, String roleId) {

		ResourceMapper resourceMapper = new ResourceMapper();

		ArrayIterator resourceIterator = new ArrayIterator(resources);

		while (resourceIterator.hasNext()) {

			com.likya.tlos.model.xmlbeans.permission.ResourceDocument.Resource permResource = (com.likya.tlos.model.xmlbeans.permission.ResourceDocument.Resource) (resourceIterator.next());

			XmlCursor cursor = permResource.newCursor();
			cursor.toFirstContentToken();
			// System.out.println(cursor.getTextValue());

			String resourceId = cursor.getTextValue();

			/***********************************************************/
			Resource resource = new Resource();
			resource.setResourceId(resourceId);

			HashMap<String, Role> myList = new HashMap<String, Role>();
			myList.put(roleId, new Role(roleId));

			resource.setRoleList(myList);
			resourceMapper.put(resourceId, resource);
			/***********************************************************/
		}
		return resourceMapper;
	}

	public static ResourcePermission permissionsToResourcePermissions(Permission permission) {
		ResourcePermission resourcePermission = new ResourcePermission();

		resourcePermission.setId(permission.getId().longValue());
		resourcePermission.setResourceType(permission.getResource().getType());

		XmlCursor xmlCursor = permission.getResource().newCursor();
		xmlCursor.toFirstContentToken();
		resourcePermission.setResourceName(xmlCursor.getTextValue());

		resourcePermission.setAdmin(false);
		resourcePermission.setNormalUser(false);
		resourcePermission.setSuperUser(false);

		for (String role : permission.getRoles().getRoleArray()) {
			if (role.equals("ADMIN")) {
				resourcePermission.setAdmin(true);
			}
			if (role.equals("SUPERUSER")) {
				resourcePermission.setSuperUser(true);
			}
			if (role.equals("NORMALUSER")) {
				resourcePermission.setNormalUser(true);
			}
		}

		return resourcePermission;
	}

	public static Map<String, String> entryToMap(JobProperties jobProperties) {
		
		Map<String, String> envMap = new HashMap<String, String>();
		
		SpecialParameters specialParameters = jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters();
		
		if (specialParameters != null && specialParameters.getEnvVariables() != null) {
			Entry [] envVars = specialParameters.getEnvVariables().getEntryArray();
			
			for(Entry myEntry : envVars) {
				envMap.put(myEntry.getKey(), myEntry.getStringValue());
			}
		}
		
		return envMap;

	}

}
