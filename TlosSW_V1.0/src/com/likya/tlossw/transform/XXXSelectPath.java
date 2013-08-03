/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.likya.tlossw.transform;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlRuntimeException;

import com.likya.tlos.model.xmlbeans.common.LocalParametersDocument.LocalParameters;
import com.likya.tlos.model.xmlbeans.common.SpecialParametersDocument.SpecialParameters;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

/**
 * This class demonstrates how to use the selectPath method to execute XPath
 * expressions. Compare the code here with the code in the ExecQuery class. That
 * class uses the execQuery method to execute XQuery expressions.
 * <p/>
 * You can call the selectPath method from either an XmlObject or XmlCursor
 * instance. Calling from XmlObject returns an XmlObject array. Calling from
 * XmlCursor returns void, and you use methods of the cursor to navigate among
 * returned "selections".
 */
public class XXXSelectPath {
	// Declare a namespace corresponding to the namespace declared in the XML
	// instance. The string here will be used as part of the XPath expression to
	// ensure that the query finds namespace-qualified elements in the XML.
	final static String m_namespaceDeclaration = "declare namespace par='http://www.likyateknoloji.com/XML_parameters_types';" +
			"declare namespace dat='http://www.likyateknoloji.com/XML_data_types';" +
			"declare namespace com='http://www.likyateknoloji.com/XML_common_types';" +
			"declare namespace fn='http://www.w3.org/2005/xpath-functions';";

	/**
	 * Prints the XML bound to <em>empDoc</em>, uses XPath to retrieve elements
	 * containing work phone numbers, changes the numbers to another number,
	 * then prints the XML again to display the changes.
	 * 
	 * This method demonstrates the following characteristics of the selectPath
	 * method:
	 * 
	 * - it supports expressions that include predicates - the XML it returns is
	 * the XML queried against -- not a copy, as with results returned via
	 * execQuery methods and XQuery. Changes to this XML update the XML queried
	 * against. - selectPath called from an XMLBean type (instead of a cursor)
	 * returns an array of results (if any). These results can be cast to a
	 * matching type generated from schema.
	 * 
	 * @param empDoc
	 *            The incoming XML.
	 * @return <code>true</code> if the XPath expression returned results;
	 *         otherwise, <code>false</code>.
	 */
	public static boolean updateParameter(XmlObject empDoc) throws XmlException {
		boolean hasResults = false;
//	      ArrayList<Parameter> parameterList = SpaceWideRegistry.getInstance().getParameters();
	      
		// Print the XML received.
		System.out.println("XML as received by updateParameter method: \n\n" + empDoc.toString());

		// Create a variable with the query expression.
		//String pathExpression = m_namespaceDeclaration + "./com:localParameters";
		
		String pathExpression = m_namespaceDeclaration + "./dat:baseJobInfos/dat:jobInfos/com:jobTypeDetails/com:specialParameters";
		
		//String queryExpression =
		//	    "declare namespace xq='http://xmlbeans.apache.org/samples/xquery/employees';" +
		//	    "$this/xq:employees/xq:employee/xq:phone[contains(., '(206)')]";
		
//    	  for(int i = 0; i < parameterList.size(); i++) { 
//    	    String paramName = parameterList.get(i).getName(); 
//    	    String paramPreValueString = parameterList.get(i).getPreValue().getStringValue(); 
//			BigInteger paramPreValueType = parameterList.get(i).getPreValue().getType();
//    	    String paramDesc = parameterList.get(i).getDesc(); 
//		  }
    	  
        JobProperties jobProperties = JobProperties.Factory.newInstance();
        jobProperties.set(empDoc);
		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		xmlOptions.setSaveOuter();
//		String jobPropertiesXML = jobProperties.xmlText(xmlOptions);
		
//		String myJob = empDoc.toString();

		
		// Execute the query.
		XmlObject[] results;
	try {	
		results = jobProperties.selectPath(pathExpression, xmlOptions);
	} catch (XmlRuntimeException e) {
		// TODO Auto-generated catch block
		results = null;
		e.getStackTrace();
		e.getMessage();
	}
		if (results != null && results.length > 0) {
			hasResults = true;
			
			XmlObject result = null;
			if(results[0] instanceof LocalParameters) {
				result = (LocalParameters) results[0];
				qName = LocalParameters.type.getOuterType().getDocumentElementName();
				xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
				xmlOptions.setSaveOuter();
//				String localParametersXML = result.xmlText(xmlOptions);				
			} else if(results[0] instanceof SpecialParameters) {
				result = (SpecialParameters) results[0];
				qName = SpecialParameters.type.getOuterType().getDocumentElementName();
				xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
				xmlOptions.setSaveOuter();
//				String specialParametersXML = result.xmlText(xmlOptions);				
			} else { 
				System.out.println("XPath den hatali tip dondu !!");
				
			}

			
			// <phone> elements returned from the expression will conform to the
			// schema, so bind them to the appropriate XMLBeans type generated
			// from the schema.
			System.out.println(result.toString());
			
			//XmlObject[] phones = null;

			//
			// (Parameter[]) results[0];
			// Change all the work phone numbers to the same number.
			//for (int i = 0; i < phones.length; i++) {
			//	((Parameter) phones[i]).setDesc("vay be");
			//}
			// Print the XML with updates.
			//System.out.println("\nXML as updated by updateWorkPhone method (each work \n" + "phone number has been changed to the same number): \n\n" + empDoc.toString() + "\n");
		}
		return hasResults;
	}

	/**
	 * Uses the XPath text() function to get values from <name> elements in
	 * received XML, then collects those values as the value of a <names>
	 * element created here.
	 * <p/>
	 * Demonstrates the following characteristics of the selectPath method:
	 * <p/>
	 * - It supports expressions that include XPath function calls. - selectPath
	 * called from an XmlCursor instance (instead of an XMLBeans type) places
	 * results (if any) into the cursor's selection set.
	 * 
	 * @param empDoc
	 *            The incoming XML.
	 * @return <code>true</code> if the XPath expression returned results;
	 *         otherwise, <code>false</code>.
	 */
	public static boolean collectNames(XmlObject empDoc) {
		boolean hasResults = false;

		// Create a cursor with which to execute query expressions. The cursor
		// is inserted at the very beginning of the incoming XML, then moved to
		// the first element's START token.
		XmlCursor pathCursor = empDoc.newCursor();
		pathCursor.toNextToken();

		// Execute the path expression, qualifying it with the namespace
		// declaration.

		String xPathCommand = m_namespaceDeclaration;

		// + "$this//par:Parameters/par:Globals/par:parameter/par:name/text()");
		try {
			pathCursor.selectPath(xPathCommand);
		} catch (XmlRuntimeException e) {
			System.out.print("HATA : XPath komutu hatali !!" + e.getError() + " ++ " + e.getMessage() + " xx " + e.getCause());
			return false;
		}

		System.out.println("Kac kayit = " + pathCursor.getSelectionCount());
		// If there are results, then go ahead and do stuff.
		if (pathCursor.getSelectionCount() > 0) {
			hasResults = true;

			// Create a new <names> element into which names from the XML
			// will be copied. Note that this element is in the default
			// namespace; it's not part of the schema.
			XmlObject namesElement = null;
			try {
				namesElement = XmlObject.Factory.parse("<names/>");
			} catch (XmlException e) {
				e.printStackTrace();
			}

			// Add a cursor the new element and put it between its START and END
			// tokens, where new values can be inserted.
			XmlCursor namesCursor = namesElement.newCursor();
			namesCursor.toFirstContentToken();
			namesCursor.toEndToken();

			// Loop through the selections, appending the incoming <name>
			// element's
			// value to the new <name> element's value. (Of course, this could
			// have
			// been done with a StringBuffer, but that wouldn't show the cursor
			// in
			// use.)
			while (pathCursor.toNextSelection()) {
				namesCursor.insertChars(pathCursor.getTextValue());
				if (pathCursor.hasNextSelection()) {
					namesCursor.insertChars(", ");
				}
			}
			// Dispose of the cursors now that they're not needed.
			pathCursor.dispose();
			namesCursor.dispose();

			// Print the new element.
			System.out.println("\nNames collected by collectNames method: \n\n" + namesElement + "\n");
		}
		return hasResults;
	}
}
