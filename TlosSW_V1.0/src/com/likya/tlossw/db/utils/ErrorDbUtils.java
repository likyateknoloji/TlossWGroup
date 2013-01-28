package com.likya.tlossw.db.utils;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import com.likya.tlos.model.xmlbeans.error.SWErrorDocument.SWError;
import com.likya.tlossw.TlosSpaceWide;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

public class ErrorDbUtils {
	
	public static boolean insertSWError(SWError error){
		
		QName qName = SWError.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String errorXML = error.xmlText(xmlOptions);
		
		String xQueryStr = "xquery version \"1.0\";" +
					       "import module namespace lk=\"http://likya.tlos.com/\" at \"xmldb:exist://db/TLOSSW/modules/moduleErrorOperations.xquery\";" +
						   "declare namespace err = \"http://www.likyateknoloji.com/XML_error_types\";  " +
						   "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\"; " +
					       "lk:insertError("+ errorXML + ")";

		SpaceWideRegistry spaceWideRegistry = TlosSpaceWide.getSpaceWideRegistry();
		Collection collection = spaceWideRegistry.getEXistColllection();
		XPathQueryService service = null;
		try {
			service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
		} catch (XMLDBException e2) {
			e2.printStackTrace();
			return false;
		}
		
		try {
			@SuppressWarnings("unused")
			ResourceSet result = service.query(xQueryStr);
		} catch (XMLDBException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}


}
