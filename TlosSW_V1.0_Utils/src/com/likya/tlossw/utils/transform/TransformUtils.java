package com.likya.tlossw.utils.transform;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlossw.utils.CommonConstantDefinitions;

import net.sf.saxon.om.NamespaceConstant;

public class TransformUtils {

	protected static String objectModel = null;
	
	public static Transformer getTransformer(StreamSource streamSource) {
		// setup the xslt transformer
	    System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		
	    //net.sf.saxon.TransformerFactoryImpl impl = new net.sf.saxon.TransformerFactoryImpl();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			if(streamSource != null) {
			transformer =  tFactory.newTransformer(streamSource);
			} else {
				transformer =  tFactory.newTransformer();
			}
			//return impl.newTransformer(streamSource);
            return transformer;
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static XPathFactory getXPathFactory(String objModel) {
		// setup the xpath
    	String om = "";
    	
        if (objModel.isEmpty()) {
            om = "saxon";
        } else {
        	om = objModel;
        }
        
        if (om.equals("saxon")) {
            objectModel = NamespaceConstant.OBJECT_MODEL_SAXON;
        } else if (om.equals("dom")) {
            objectModel = XPathConstants.DOM_OBJECT_MODEL;
        } else if (om.equals("jdom")) {
            objectModel = NamespaceConstant.OBJECT_MODEL_JDOM;
        } else if (om.equals("dom4j")) {
            objectModel = NamespaceConstant.OBJECT_MODEL_DOM4J;
        } else if (om.equals("xom")) {
            objectModel = NamespaceConstant.OBJECT_MODEL_XOM;
        } else {
            System.err.println("Unknown object model " + objModel);
            return null;
        }
        
        // Following is specific to Saxon: should be in a properties file
        System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON,
                "net.sf.saxon.xpath.XPathFactoryImpl");
        System.setProperty("javax.xml.xpath.XPathFactory:"+XPathConstants.DOM_OBJECT_MODEL,
                "net.sf.saxon.xpath.XPathFactoryImpl");
        System.setProperty("javax.xml.xpath.XPathFactory:"+ NamespaceConstant.OBJECT_MODEL_JDOM,
                "net.sf.saxon.xpath.XPathFactoryImpl");
        System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_XOM,
                "net.sf.saxon.xpath.XPathFactoryImpl");
        System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_DOM4J,
                "net.sf.saxon.xpath.XPathFactoryImpl");

		XPathFactory xpf = null;
		try {
			xpf = XPathFactory.newInstance(objectModel);
		} catch (XPathFactoryConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return xpf;
	}

	public static String typeSelector(Parameter parameter) {
		
		
		int paramType = parameter.getPreValue().getType().intValue();
		
		Object retValue = "";
		
		switch (paramType) {
		case CommonConstantDefinitions.INTEGER:
			retValue = parameter.getValueInteger();

			break;
		case CommonConstantDefinitions.STRING:
			retValue = parameter.getValueString();

			break;
		case CommonConstantDefinitions.DATE:
			retValue = parameter.getValueDate();

			break;
		case CommonConstantDefinitions.TIME:
			retValue = parameter.getValueTime();

			break;
		case CommonConstantDefinitions.DATETIME:
			retValue = parameter.getValueDateTime();

			break;
		case CommonConstantDefinitions.XPATH:
			retValue = parameter.getValueXPATH();

			break;

		default:
			break;
		}
		
		return retValue == null ? null : retValue.toString();
	}
	
	public static String toXSString(int intData) {
		return toXSString("" + intData);
	}

	public static String toXSString(String stringData) {
		return "xs:string(\"" + stringData + "\")";
	}
}
