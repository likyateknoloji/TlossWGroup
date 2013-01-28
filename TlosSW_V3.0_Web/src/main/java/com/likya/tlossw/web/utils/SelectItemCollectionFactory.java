package com.likya.tlossw.web.utils;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.PropertyUtils;

public class SelectItemCollectionFactory {

	/**
	 *  Örnek kullaným 
	 */
     //	setAlarmUserList(SelectItemCollectionFactory.getCollection(getDbOperations().getUsers(), "id", "userName"));
	
public static Collection<SelectItem> getCollection(ArrayList<?> objectList, String valueBindingName, String labelBindingName) {
		
		Collection<SelectItem> retObjectList = new ArrayList<SelectItem>();
		
		try {

			SelectItem item = new SelectItem();
			
			for (Object tmpObject : objectList) {
				item = new SelectItem();
				
				Object value = PropertyUtils.getProperty(tmpObject, valueBindingName);
				Object label = PropertyUtils.getProperty(tmpObject, labelBindingName);
				
				item.setValue(value + "");
				item.setLabel(label + "");
				
				retObjectList.add(item);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return retObjectList;
	}
}
