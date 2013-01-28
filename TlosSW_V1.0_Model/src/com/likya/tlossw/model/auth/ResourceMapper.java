/*
 * TlosFaz_V2.0_Model
 * com.likya.tlos.model.auth : RoleMap.java
 * @author Serkan Tas
 * Tarih : Jul 20, 2009 11:25:33 AM
 */

package com.likya.tlossw.model.auth;

import java.io.Serializable;
import java.util.HashMap;

public class ResourceMapper extends HashMap<String, Resource> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static ResourceMapper generateTestData() {
		
		ResourceMapper resourceMapper = new ResourceMapper();
		
		/***********************************************************/
		String resId = "AdminView";
		Resource resource = new Resource();
		resource.setResourceId(resId);
		
		HashMap<String, Role> myList = new HashMap<String, Role>();
		myList.put("admin", new Role("admin"));
		
		resource.setRoleList(myList);
		resourceMapper.put(resId, resource);
		/***********************************************************/

		resId = "ReportView";
		resource = new Resource();
		resource.setResourceId(resId);
		
		myList = new HashMap<String, Role>();
		myList.put("admin", new Role("admin"));
		myList.put("superuser", new Role("superuser"));
		
		resource.setRoleList(myList);
		resourceMapper.put(resId, resource);
		/***********************************************************/
		
		resId = "PlanView";
		resource = new Resource();
		resource.setResourceId(resId);
		
		myList = new HashMap<String, Role>();
		myList.put("admin", new Role("admin"));
		myList.put("superuser", new Role("superuser"));
		
		resource.setRoleList(myList);
		resourceMapper.put(resId, resource);
		/***********************************************************/

		resId = "ProcessView";
		resource = new Resource();
		resource.setResourceId(resId);
		
		myList = new HashMap<String, Role>();
		myList.put("admin", new Role("admin"));
		myList.put("superuser", new Role("superuser"));
		myList.put("normaluser", new Role("normaluser"));
		
		resource.setRoleList(myList);
		resourceMapper.put(resId, resource);
		/***********************************************************/
		
		resId = "SenaryoViewPanel";
		resource = new Resource();
		resource.setResourceId(resId);
		
		myList = new HashMap<String, Role>();
		myList.put("admin", new Role("admin"));
		
		resource.setRoleList(myList);
		resourceMapper.put(resId, resource);
		/***********************************************************/

		resId = "ControlPanelManager";
		resource = new Resource();
		resource.setResourceId(resId);
		
		myList = new HashMap<String, Role>();
		myList.put("admin", new Role("admin"));
		
		resource.setRoleList(myList);
		resourceMapper.put(resId, resource);
		/***********************************************************/

		return  resourceMapper;
	}
}
