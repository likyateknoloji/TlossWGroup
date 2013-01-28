package com.likya.tlossw.utils.xml;

/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
// This file uses 4 space indents, no tabs.

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import net.sf.saxon.om.NamespaceConstant;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.TlosProcessDataDocument.TlosProcessData;

/**
 * Very basic utility for applying the XPath API in JAXP 1.3 to an xml file and
 * printing information about the execution of the XPath object and the nodes it
 * finds. Takes 2 or 3 arguments: (1) an xml filename (2) an XPath expression to
 * apply to the file (3) (optionally) short name of the object model to be used:
 * "saxon" | "dom" | "jdom" | "xom" Examples: java ApplyXPathJAXP foo.xml / java
 * ApplyXPathJAXP foo.xml /doc/name[1]/@last
 * <p/>
 * This version modified by Michael Kay to allow testing of additional features
 * of the interface.
 * 
 * The XPath expression may use: (a) A namespace prefix f which is bound to the
 * namespace http://localhost/f (b) A variable $f:pi whose value is the Double
 * pi (c) A function f:sqrt(x) that calculates the square root of its argument x
 * 
 * @see javax.xml.xpath.XPath
 */
public class ApplyXPath {
	protected String filename = null;
	protected String xpathExpressionStr = null;
	protected static String objectModel = null;

	public synchronized static String[] queryXmlWithXPath(XmlObject xmlDoc, String xpathExpressionStr) throws Exception {

		String xmlContent = null;

		if (xmlDoc != null && (xpathExpressionStr != null) && (xpathExpressionStr.length() > 0)) {

			// Use the JAXP 1.3 XPath API to apply the xpath expression to the
			// doc.
			System.out.println("");
			System.out.println("==================== QUERYING JOB ====================");
			System.out.println(xmlDoc.toString());
			System.out.println("=====================================================");
			System.out.println("");
			System.out.println("======================== USING =======================");
			System.out.println(xpathExpressionStr);
			System.out.println("=====================================================");
			System.out.println("");

			// XPathFactory xPathFactory =
			// RemoteDBOperator.getXPathFactory("saxon", objectModel);
			// setup the xpath
			String om = "";
			String objModel = "saxon";
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

			// Get a instance of XPathFactory with ObjectModel URL parameter. If
			// no parameter
			// is mentioned then default DOM Object Model is used

			XPathFactory xpathFactory = null;
			try {
				xpathFactory = XPathFactory.newInstance(objectModel);
			} catch (XPathFactoryConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			XPath xpath = xpathFactory.newXPath();

			// source must be in a proper data structure
			Object document = null;

			QName qName = null;
			if (xmlDoc instanceof TlosProcessData) {
				qName = TlosProcessData.type.getOuterType().getDocumentElementName();
			} else if (xmlDoc instanceof JobProperties) {
				qName = JobProperties.type.getOuterType().getDocumentElementName();
			}
			XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
			xmlOptions.setSaveOuter();
			String stringXML = xmlDoc.xmlText(xmlOptions);

			if (objectModel.equals(NamespaceConstant.OBJECT_MODEL_SAXON)) {
				document = new StreamSource(new StringReader(stringXML));
			} else if (objectModel.equals(XPathConstants.DOM_OBJECT_MODEL)) {
				InputSource in = new InputSource(new StringReader(stringXML));
				// in.setSystemIdnew
				// StringReader(stringXML).toURI().toString());
				DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
				dfactory.setNamespaceAware(true);
				document = dfactory.newDocumentBuilder().parse(in);

			} else if (objectModel.equals(NamespaceConstant.OBJECT_MODEL_JDOM)) {
				InputSource in = new InputSource(new StringReader(stringXML));
				// in.setSystemId(new File(filename).toURI().toString());
				SAXBuilder builder = new SAXBuilder();
				document = builder.build(in);

			} else if (objectModel.equals(NamespaceConstant.OBJECT_MODEL_DOM4J)) {
				org.dom4j.io.SAXReader parser = new org.dom4j.io.SAXReader();
				document = parser.read(new StringReader(stringXML));

			} else if (objectModel.equals(NamespaceConstant.OBJECT_MODEL_XOM)) {
				nu.xom.Builder builder = new nu.xom.Builder();
				document = builder.build(new StringReader(stringXML));
			}

			StringReader jobReader2 = new StringReader(stringXML);

			StreamSource inputXML2 = new StreamSource(jobReader2);

			/*******************************************/

			// Declare a namespace context:
			// We map the prefixes to URIs

			NamespaceContext namespaceContext = GetNamespaceContext.getNamespaceContextForXpath();

			xpath.setNamespaceContext(namespaceContext);
			// insert xpath variable and function related code here if any

			// Now compile the expression
			XPathExpression xpathExpression = null;
			try {
				xpathExpression = xpath.compile(xpathExpressionStr);
			} catch (XPathExpressionException e) {
				System.out.println("Xpath de sorun var; " + e.toString());
			}

			// Now evaluate the expression on the document to get String result
			// String resultString =
			// xpathExpression.evaluate(inputXML2);QName(String namespaceURI,
			// String localPart, String prefix)
			try {
				// result = xpathExpression.evaluate(inputXML2,
				// XPathConstants.NODESET);
				xpathExpression.evaluate(document);
			} catch (XPathExpressionException xpee) {
				System.out.print(" Xpath i isletmede hata olustu :" + xpee.getMessage());
			}

			Object result = null;
			try {
				result = xpathExpression.evaluate(inputXML2, XPathConstants.NODESET);
			} catch (XPathExpressionException xpee) {
				System.out.print(" Xpath i isletmede hata olustu :" + xpee.getMessage());
			}

			// Serialize the found nodes to System.out.
			// In this case, the "transformer" is not actually changing
			// anything. In XSLT terminology,
			// you are using the identity transform, which means that the
			// "transformation"
			// generates a copy of the source, unchanged.

			// ////////////////////////////

			if (result != null) {
				NodeList nodes = (NodeList) result;
				String resultArray[] = new String[nodes.getLength()];
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					// //ls.add(node.getFirstChild());
					DOMSource source = new DOMSource(node);
					/*
					 * for (int i = 0; i < dizi.size(); i++) {
					 * System.out.println("sonuc " + i + " : " +
					 * dizi.get(i).getNodeName().toString()); }
					 */

					// StreamResult result2 = new StreamResult(System.out);
					Transformer transformer = TransformUtils.getTransformer(null);
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					// Set up output sink
					StringWriter writer = new StringWriter();
					StreamResult outputXHTML = new javax.xml.transform.stream.StreamResult(writer);

					try {
						transformer.transform(source, outputXHTML);
					} catch (TransformerException te) {
						System.out.println("* Transformation error");
						System.out.println(" " + te.getMessage());

						Throwable x = te;
						if (te.getException() != null)
							x = te.getException();
						x.printStackTrace();
					}

					xmlContent = (String) outputXHTML.getWriter().toString();
					resultArray[i] = xmlContent;
					outputXHTML.getWriter().close();
				}
				System.out.println("XPath result is \"" + xmlContent);
				return resultArray;
			}
			/*
			 * for (int i = 0; i < nodes.getLength(); i++) {
			 * YADOM.printXmlLoop(nodes.item(i), "", ""); }
			 */

		} else {
			System.out.println("Bad input args: " + xmlDoc + ", " + xpathExpressionStr);
			return null;
		}
		return null;
	}

	// Helper method to get the first child of an element having a given name.
	// If there is no child with the given name it returns null

	public static XdmNode getChild(XdmNode parent, QName childName) {
		XdmSequenceIterator iter = parent.axisIterator(Axis.CHILD);
		if (iter.hasNext()) {
			return (XdmNode) iter.next();
		} else {
			return null;
		}
	}

	public synchronized static boolean queryJobWithXPath(JobProperties xmlDoc, String xpathExpressionStr) throws Exception {

		// Set up output sink
		StringWriter writer = new StringWriter();
		StreamResult outputXHTML = new javax.xml.transform.stream.StreamResult(writer);

		if (xmlDoc != null && (xpathExpressionStr != null) && (xpathExpressionStr.length() > 0)) {

			QName qName = null;
			if (xmlDoc instanceof TlosProcessData) {
				qName = TlosProcessData.type.getOuterType().getDocumentElementName();
			} else if (xmlDoc instanceof JobProperties) {
				qName = JobProperties.type.getOuterType().getDocumentElementName();
			}
			XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
			xmlOptions.setSaveOuter();
			String stringXML = xmlDoc.xmlText(xmlOptions);

			StringReader jobReader2 = new StringReader(stringXML);

			StreamSource inputXML2 = new StreamSource(jobReader2);

			// Use the JAXP 1.3 XPath API to apply the xpath expression to the
			// doc.
			System.out.println("");
			System.out.println("==================== QUERYING JOB ====================");
			System.out.println(xmlDoc.toString());
			System.out.println("=====================================================");
			System.out.println("");
			System.out.println("======================== USING =======================");
			System.out.println(xpathExpressionStr);
			System.out.println("=====================================================");
			System.out.println("");

			XPathFactory xPathFactory = TransformUtils.getXPathFactory("saxon");

			XPath xpath = xPathFactory.newXPath();

			// Declare a namespace context:
			// We map the prefixes to URIs

			NamespaceContext namespaceContext = GetNamespaceContext.getNamespaceContextForXpath();

			xpath.setNamespaceContext(namespaceContext);
			/*
			 * String ddat = xpath.getNamespaceContext().getNamespaceURI("dat");
			 * String dcom = xpath.getNamespaceContext().getNamespaceURI("com");
			 * String dpar = xpath.getNamespaceContext().getNamespaceURI("par");
			 * String djsdl =
			 * xpath.getNamespaceContext().getNamespaceURI("jsdl");
			 */
			// Now compile the expression
			XPathExpression xpathExpression = null;
			try {
				xpathExpression = xpath.compile(xpathExpressionStr);
			} catch (XPathExpressionException e) {
				System.out.println("Xpath de sorun var; " + e.toString());
			}

			// Now evaluate the expression on the document to get String result
			// String resultString =
			// xpathExpression.evaluate(inputXML2);QName(String namespaceURI,
			// String localPart, String prefix)

			Object result = null;
			try {
				result = xpathExpression.evaluate(inputXML2, XPathConstants.NODESET);
			} catch (XPathExpressionException xpee) {
				System.out.print(" Xpath i isletmede hata olustu :" + xpee.getMessage());
			}
			// Serialize the found nodes to System.out.
			// In this case, the "transformer" is not actually changing
			// anything. In XSLT terminology,
			// you are using the identity transform, which means that the
			// "transformation"
			// generates a copy of the source, unchanged.

			// ////////////////////////////

			if (result != null) {
				NodeList nodes = (NodeList) result;
				if (nodes.getLength() > 0) {
					Node node = nodes.item(0);
					DOMSource source = new DOMSource(node);
					// StreamResult result2 = new StreamResult(System.out);
					Transformer transformer = TransformUtils.getTransformer(null);
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					try {
						transformer.transform(source, outputXHTML);
					} catch (TransformerException te) {
						System.out.println("* Transformation error");
						System.out.println(" " + te.getMessage());

						Throwable x = te;
						if (te.getException() != null)
							x = te.getException();
						x.printStackTrace();
					}
				}
				String xmlContent = (String) outputXHTML.getWriter().toString();
				System.out.println("XPath result is \"" + xmlContent);
				/*
				 * for (int i = 0; i < nodes.getLength(); i++) {
				 * YADOM.printXmlLoop(nodes.item(i), "", ""); }
				 */

			}
			return true;
		} else {
			System.out.println("Bad input args: " + xmlDoc + ", " + xpathExpressionStr);
			return false;
		}
	}
} // end of class applyXPath

