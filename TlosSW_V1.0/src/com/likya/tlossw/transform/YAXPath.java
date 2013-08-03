package com.likya.tlossw.transform;

import java.io.FileReader;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//http://www.java2s.com/Tutorial/Java/0440__XML/XPathevaluatescheduleseriesId.htm
//http://blog.davber.com/2006/09/17/xpath-with-namespaces-in-java/
//http://www.ibm.com/developerworks/library/x-javaxpathapi/index.html
//http://xml.apache.org/xalan-j/xpath_apis.html
public class YAXPath {

	public class MyVariableResolver implements XPathVariableResolver {
		public Object resolveVariable(QName var) {
			if (var == null)
				throw new NullPointerException("The variable name cannot be null");

			if (var.equals(new QName("x"))) /*
											 * veya qName
											 * .getLocalPart().equals("x")
											 */
				return new Integer(2);
			else if (var.equals(new QName("y")))
				return new Integer(3);
			else
				return null;
		}
	}

	/**
	 * The XPathFunctionResolver implementation is used to evaluate
	 * the extension function "ext:myAdditionFunction(2, 3)".
	 */

	public class MyFunctionResolver implements XPathFunctionResolver {
		/**
		 * This method returns a customized XPathFunction implementation
		 * for the extension function "ext:myAdditionFunction(2, 3)".
		 */
		public XPathFunction resolveFunction(QName fname, int arity) {
			if (fname == null)
				throw new NullPointerException("The function name cannot be null.");

			// We only recognize one function, i.e. ex:addFunc().
			if (fname.equals(new QName("http://ext.com", "myAdditionFunction", "ext")))
				/**
				 * Return a customized implementation of XPathFunction. We need
				 * to implement the evaluate(List) method.
				 */
				return new XPathFunction() {
					/**
					 * The actual implementation of the extension function.
					 * Just cast two arguments to Double and add them together.
					 */
					@SuppressWarnings("rawtypes")
					public Object evaluate(java.util.List args) {
						if (args.size() == 2) {
							Double arg1 = (Double) args.get(0);
							Double arg2 = (Double) args.get(1);
							return new Double(arg1.doubleValue() + arg2.doubleValue());
						} else
							return null;
					}
				};
			else
				return null;
		}
	}

	public class MyNamespaceContext implements NamespaceContext {
		public String getNamespaceURI(String prefix) {
			String uri;
			if (prefix.equals("par"))
				uri = "http://www.likyateknoloji.com/XML_parameters_types";
			else if (prefix.equals("par2"))
				uri = "http://www.likyateknoloji.com/XML_parameters_types2";
			else
				uri = XMLConstants.DEFAULT_NS_PREFIX;
			return uri;

		}

		public String getPrefix(String namespace) {
			if (namespace.equals("http://www.likyateknoloji.com/XML_parameters_types"))
				return "par";
			else if (namespace.equals("http://www.likyateknoloji.com/XML_parameters_types2"))
				return "par2";
			else
				return null;
		}

		// Dummy implemenation - not used!
		@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String namespace) {
			return null;
		}
	}

	public synchronized static void getXPathResult(String file, String xpathCommand) throws Exception {

		// We map the prefixes to URIs

		NamespaceContext ctx = new NamespaceContext() {
			public String getNamespaceURI(String prefix) {
				String uri;
				if (prefix.equals("par"))
					uri = "http://www.likyateknoloji.com/XML_parameters_types";
				else if (prefix.equals("par2"))
					uri = "http://www.likyateknoloji.com/XML_parameters_types2";
				else
					uri = null;
				return uri;
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

		/****** evaluating an XPath expression. *******/

		// 1. Instantiate an XPathFactory.
		XPathFactory factory = XPathFactory.newInstance();

		// 2. Use the XPathFactory to create a new XPath object
		XPath xPath = factory.newXPath();

		xPath.setNamespaceContext(ctx);

		InputSource inputSource = new InputSource(new FileReader(file));

		String xpathStr = xpathCommand;

		/*
		 * Calisti.
		 * String result = xPath.evaluate(xpathStr, inputSource);
		 * System.out.println("XPath result is \"" + result + " \"");
		 */

		// 3. Compile an XPath string into an XPathExpression
		XPathExpression expr = null;
		try {
			expr = xPath.compile(xpathStr);
		} catch (XPathExpressionException xpee) {
			System.out.print(" Xpath i derlemede hata olustu :" + xpee.getMessage());
		}
		// 4. Evaluate the XPath expression on an input document
		Object result = null;
		try {
			result = expr.evaluate(inputSource, XPathConstants.NODESET);
		} catch (XPathExpressionException xpee) {
			System.out.print(" Xpath i isletmede hata olustu :" + xpee.getMessage());
		}
		System.out.println("XPath result is \"");

		if (result != null) {
			NodeList nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); i++) {
				YADOM.dumpLoop(nodes.item(i), "");
			}
		}

	}

	public synchronized static void getXPathResult(XmlObject job, String xpathCommand) throws Exception {

		// We map the prefixes to URIs

		NamespaceContext ctx = new NamespaceContext() {
			public String getNamespaceURI(String prefix) {
				String uri;
				if (prefix.equals("par"))
					uri = "http://www.likyateknoloji.com/XML_parameters_types";
				else if (prefix.equals("par2"))
					uri = "http://www.likyateknoloji.com/XML_parameters_types2";
				else
					uri = null;
				return uri;
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

		/****** evaluating an XPath expression. *******/

		// 1. Instantiate an XPathFactory.
		XPathFactory factory = XPathFactory.newInstance();

		// 2. Use the XPathFactory to create a new XPath object
		XPath xPath = factory.newXPath();

		xPath.setNamespaceContext(ctx);

		// InputSource inputSource = new InputSource(new FileReader(file));

		String xpathStr = xpathCommand;

		/*
		 * Calisti.
		 * String result = xPath.evaluate(xpathStr, inputSource);
		 * System.out.println("XPath result is \"" + result + " \"");
		 */

		// 3. Compile an XPath string into an XPathExpression
		XPathExpression expr = xPath.compile(xpathStr);

		// 4. Evaluate the XPath expression on an input document
		Object result = expr.evaluate(job, XPathConstants.NODESET);
		System.out.println("XPath result is \"");

		if (result != null) {
			NodeList nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); i++) {
				YADOM.dumpLoop(nodes.item(i), "");
			}
		}

	}

	/**
	 * Perform a transformation using a compiled stylesheet (a Templates object)
	 * 
	 * @param sourceID
	 *            file name of the source file
	 * @param xslID
	 *            file name of the stylesheet file
	 */
	public void exampleUseTemplatesHandler(String sourceID, String xslID) throws Exception {

		TransformerFactory tfactory = TransformerFactory.newInstance();

		// Does this factory support SAX features?
		if (tfactory.getFeature(SAXSource.FEATURE)) {

			// If so, we can safely cast.
			SAXTransformerFactory stfactory = ((SAXTransformerFactory) tfactory);

			// Create a Templates ContentHandler to handle parsing of the
			// stylesheet.
			javax.xml.transform.sax.TemplatesHandler templatesHandler = stfactory.newTemplatesHandler();

			// Create an XMLReader and set its features.
			// XMLReader reader = makeXMLReader();
			// reader.setFeature("http://xml.org/sax/features/namespaces", true);
			// reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);

			// Create a XMLFilter that modifies the stylesheet
			// XMLFilter filter = new ModifyStylesheetFilter();
			// filter.setParent(reader);

			// filter.setContentHandler(templatesHandler);

			// Parse the stylesheet.
			// filter.parse(new InputSource(xslID));

			// Get the Templates object (generated during the parsing of the stylesheet)
			// from the TemplatesHandler.
			Templates templates = templatesHandler.getTemplates();

			// do the transformation
			templates.newTransformer().transform(new StreamSource(sourceID), new StreamResult(System.out));

		} else {
			System.out.println("Factory doesn't implement SAXTransformerFactory");
		}

	}
}