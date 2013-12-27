package com.likya.tlossw.utils.xml;

import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import net.sf.saxon.om.NamespaceConstant;

import com.likya.tlossw.exceptions.TransformCodeCreateException;

public class TransformUtils {

	protected static String objectModel = null;
	
	public static Transformer getTransformer(String transformXsl) throws TransformCodeCreateException, TransformerConfigurationException {
		
		

		// setup the xslt transformer
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		//net.sf.saxon.TransformerFactoryImpl impl = new net.sf.saxon.TransformerFactoryImpl();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		

			StreamSource streamSource = getStreamSource(transformXsl);
			
			if (streamSource != null) {
				transformer = tFactory.newTransformer(streamSource);
			} else {
				transformer = tFactory.newTransformer();
			}
			//return impl.newTransformer(streamSource);
			return transformer;

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
		System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
		System.setProperty("javax.xml.xpath.XPathFactory:" + XPathConstants.DOM_OBJECT_MODEL, "net.sf.saxon.xpath.XPathFactoryImpl");
		System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_JDOM, "net.sf.saxon.xpath.XPathFactoryImpl");
		System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_XOM, "net.sf.saxon.xpath.XPathFactoryImpl");
		System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_DOM4J, "net.sf.saxon.xpath.XPathFactoryImpl");

		XPathFactory xpf = null;
		try {
			xpf = XPathFactory.newInstance(objectModel);
		} catch (XPathFactoryConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return xpf;
	}

	public static StreamSource getStreamSource(String transformCode) throws TransformCodeCreateException {

		StringReader xslReader = transformCode != null ? new StringReader(transformCode) : null;

		StreamSource streamSource = xslReader != null ? new StreamSource(xslReader) : null;

		return streamSource;
	}

}
