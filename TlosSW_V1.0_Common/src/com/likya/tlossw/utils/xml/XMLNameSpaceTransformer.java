package com.likya.tlossw.utils.xml;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;

public class XMLNameSpaceTransformer {
	
	public static XmlOptions transformXML(QName qName) {
		return transformXML(qName, true);
	}
	
	public static XmlOptions transformXML(QName qName, boolean prettyPrint) {
		
		XmlOptions xmlOptions = new XmlOptions();
		
		xmlOptions.setSaveNamespacesFirst();
		xmlOptions.setUseDefaultNamespace();
		HashMap<String, String> xMLNameSpaceMappings = XMLNameSpaceMappings.getXmlNameSpaceHashMapXMLBeans();
		xmlOptions.setSaveSuggestedPrefixes(xMLNameSpaceMappings);
		xmlOptions.setSaveAggressiveNamespaces();
		
		xmlOptions.setSaveSyntheticDocumentElement(qName);
		
		if(prettyPrint) {
			xmlOptions.setSavePrettyPrint();
		}
		
		return xmlOptions;
	}
}
