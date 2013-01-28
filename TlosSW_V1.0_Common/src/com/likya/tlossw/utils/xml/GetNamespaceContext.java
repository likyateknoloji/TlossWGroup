package com.likya.tlossw.utils.xml;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class GetNamespaceContext {
	
	public static NamespaceContext getNamespaceContextForXpath() {
		
    NamespaceContext ctx = new NamespaceContext() {
        public String getNamespaceURI(String prefix) {
            
            HashMap<String, String> nameSpace = XMLNameSpaceMappings.getXmlNameSpaceHashMap();
  
            return (nameSpace.get(prefix).toString().equals(null)) ? null : nameSpace.get(prefix).toString();

        }
       
        // Dummy implementation - not used!
        @SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String val) {
            return null;
        }
       
        // Dummy implemenation - not used!
        public String getPrefix(String uri) {
            return null;
        }
    };
    
    return ctx;
	}
/*	
	public static void getNamespaceContextForXpath(XPath xpath) {
		
	    NamespaceContext ctx = new NamespaceContext() {
	        public String getNamespaceURI(String prefix) {
	            
	            HashMap<String, String> nameSpace = XMLNameSpaceMappings.getXmlNameSpaceHashMap();
	  
	            return (nameSpace.get(prefix).toString().equals(null)) ? null : nameSpace.get(prefix).toString();

	        }
	       
	        // Dummy implementation - not used!
	        public Iterator getPrefixes(String val) {
	            return null;
	        }
	       
	        // Dummy implemenation - not used!
	        public String getPrefix(String uri) {
	            return null;
	        }
	    };
	    
	    xpath.setNamespaceContext(ctx);
		}
*/
}
