package com.likya.tlossw.db.utils;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.error.SWErrorDocument.SWError;
import com.likya.tlossw.utils.CommonConstantDefinitions;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

public class ErrorDbUtils extends DBBase {
	
	public static boolean insertSWError(SWError error){
				
		boolean returnValue = false;
		
		QName qName = SWError.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		
		String errorXML = error.xmlText(xmlOptions);
		
		String xQueryStr = localFunctionConstructor("moduleErrorOperations.xquery", "lk:insertError", CommonConstantDefinitions.lkNsUrl, errorXML);
				
		SpaceWideRegistry.getGlobalLogger().debug(xQueryStr);

		ArrayList<Object> objectList = moduleGeneric(xQueryStr);

		for(Object currentObject : objectList) {
			returnValue = ((Boolean.parseBoolean(currentObject.toString())));
		}

		return returnValue;
	}
}
