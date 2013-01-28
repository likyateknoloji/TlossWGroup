package com.likya.tlossw.test;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlossw.transform.YADOM;
import com.likya.tlossw.transform.YAXPath;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.transform.TransformUtils;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

public class TestParameterManipulations {
	/*http://java.sun.com/webservices/reference/tutorials/jaxp/html/dom.html */
   @SuppressWarnings("unused")
public synchronized static void transformTest(XmlObject job) {

	  /** Genel parametreleri kullanma ornegi **/
      ArrayList<Parameter> parameterList = SpaceWideRegistry.getInstance().getParameters();

      for(int i = 0; i < parameterList.size(); i++) { 
		String paramName = parameterList.get(i).getName(); 
		String paramPreValueString = parameterList.get(i).getPreValue().getStringValue(); 
	    BigInteger paramPreValueType = parameterList.get(i).getPreValue().getType();
		String paramDesc = parameterList.get(i).getDesc(); 
	  }
      /******************************************/
      
      try {
    	  /********** XML Dosyasini org.w3c.dom.Document olarak bellege al ve XPATH sorgula ******** BASLA *********/
    	  
    	  //Dosyayi disaridan 
    	  //InputSource inputSource = new InputSource(new FileReader(file));
    	  //ile okuduk.
    	  //javax.xml.xpath.XPathExpression Object tipinde sonuc XPath ile alinir.
    	  //Object result = expr.evaluate(inputSource, XPathConstants.NODESET);
    	  
          Document p = YADOM.getDocument("xml/tlosSWParameters10.xml");
          System.out.print(" > " + p.getDoctype());
          System.out.print(" > " + p.getDocumentElement().getNodeName());
          System.out.print(" > " + p.getElementsByTagName("Parameters"));

          YADOM.dump(p);
          System.out.print(" > ");
          //TODO bu iki parametreyi job in icindeki specialParameters dan al 
          YAXPath.getXPathResult("xml/tlosSWParameters10.xml", "/par:Parameters/par:Globals/par:parameter/par:name");
          
          /********** XML Dosyasini DOM olarak oku ve XPATH sorgula *********  BIT **************/
          
          
          
          Parameter parameter = Parameter.Factory.newInstance();
  		  QName qName = Parameter.type.getOuterType().getDocumentElementName();
  		  XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

  		  String parameterXML = parameter.xmlText(xmlOptions);
          System.out.println(parameterXML.toString() ); 
          
  		/********** XML Dosyasini org.apache.xmlbeans.XmlObject olarak bellege al ve XPATH sorgula ******** BASLA *********/
          // Burada dogrudan job parametresini kullanabiliriz. Yada kullanicinin belirttigi XML i islemeye
  		  // devam ederiz.
          XmlObject xmlObjExpected = null;
  		  try {
  		    xmlObjExpected = XmlObject.Factory.parse((Node) p, xmlOptions);
		  }
		  catch(XmlException xe) {
			System.out.print(" XML i DOM dan xmlObjct e cevirmede hata olustu :" + xe.getMessage());
		  }

  		  XmlObject[] xmlObjExpectedResult = xmlObjExpected.selectPath(
  		      "declare namespace par='http://www.likyateknoloji.com/XML_parameters_types'; " +
              ".//Parameters/Globals", xmlOptions);

          // SelectPath.updateParameter(job);
          //SelectPath.updateParameter(TlosSpaceWide.getSpaceWideRegistry().getSpaceWideReference().getParameters());
          //ParserTest.SelectPath.collectNames(XmlObject.Factory.parse(p));

      } catch (Exception e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  		e.getStackTrace();
  		e.getMessage();
      }
//http://www.ibm.com/developerworks/xml/library/x-jaxp2/
   
   // an identity copy stylesheet
      /*
      final String IDENTITY_XSLT =
        "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'"
        + " version='1.0'>"
        + "<xsl:template match='/'><xsl:copy-of select='.'/>"
        + "</xsl:template></xsl:stylesheet>";
      
      Source xsltSource = new StreamSource(
    	        new StringReader(IDENTITY_XSLT));
      */
   try {
	   // Set up input documents

	   String systemID = new File("D:\\likya\\projeler\\tlos\\tlos3.0\\parameters\\job_sample.xml").toURI().toString();
	   Source inputXML = new StreamSource(
			     new File(systemID));
	// specify a system ID (a String) so the 
	// Source can resolve relative URLs
	// that are encountered in XSLT stylesheets
	   inputXML.setSystemId(systemID);

	   StreamSource inputXSL = new StreamSource(
	     new File("D:\\likya\\projeler\\tlos\\tlos3.0\\parameters\\job_sample11_xslt_1.0.xsl"));

	   // Set up output sink
	   StreamResult outputXHTML = new StreamResult(
	     new File("D:\\likya\\projeler\\tlos\\tlos3.0\\parameters\\output.html"));
	
	   try {
		    StringReader reader = new StringReader("<xml>blabla</xml>");
		    StringWriter writer = new StringWriter();
		    /* orijinal
		    TransformerFactory tFactory = TransformerFactory.newInstance();
		    Transformer transformer = tFactory.newTransformer(
		            new javax.xml.transform.stream.StreamSource("styler.xsl"));

		    transformer.transform(
		            new javax.xml.transform.stream.StreamSource(reader), 
		            new javax.xml.transform.stream.StreamResult(writer));
		    */
		    Transformer transformer = TransformUtils.getTransformer(inputXSL);
		    
			// Perform the transformation

			transformer.transform(new javax.xml.transform.stream.StreamSource(reader), 
					              new javax.xml.transform.stream.StreamResult(writer));
			   
		    String result = writer.toString();
		} catch (Exception e) {
		    e.printStackTrace();
		}

	   // Set up output sink
	   StringWriter writer = new StringWriter();
	   StreamResult outputXHTML2 = new javax.xml.transform.stream.StreamResult(writer);
	   
       //JobProperties jobProperties = JobProperties.Factory.newInstance();
       //jobProperties.set(job);
		QName qName = JobProperties.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		xmlOptions.setSaveOuter();
		String jobPropertiesXML = job.xmlText(xmlOptions);
		
		StringReader reader = new StringReader(jobPropertiesXML);
		InputSource inputSource = new InputSource();
		inputSource.setCharacterStream(reader);

		StringReader jobReader = new StringReader(job.toString());
		   
		// javax.xml.transform.stream.StreamSource
		// javax.xml.transform.stream.StreamSource@5fbc4e89
		StreamSource inputXML2 = new StreamSource(jobReader);
		// javax.xml.transform.Source
		// javax.xml.transform.stream.StreamSource@43a05220
		
		//String myJob = jobProperties.toString();


	   //SAXTransformerFactory stfactory = null;
	   // Setup a factory for transforms
	   //TransformerFactory factory = TransformerFactory.newInstance();

	// Pre-compile instructions
//	   Templates templates = factory.newTemplates(inputXSL);

	   // Get a transformer for this XSL
	   //Transformer transformer = templates.newTransformer();

	   Transformer transformer = TransformUtils.getTransformer(inputXSL);

	// Get a transformer for this XSL
	   //Transformer transformer = factory.newTransformer(inputXSL);
	   
	// Perform the transformation


	   transformer.transform(inputXML2, outputXHTML2);
	   //transformer.transform(inputXML2, outputXHTML2);
	   System.out.println(jobPropertiesXML);
	   //String xmlString = outputXHTML.getWriter().toString();
	   System.out.println("Dosyaya yazildi !!" + outputXHTML2.getWriter().toString());
	   
	 } catch (TransformerConfigurationException e) {
	   System.out.println("The underlying XSL processor " +
	     "does not support the requested features.");
	 } catch (TransformerException e) {
	   System.out.println("Error occurred obtaining " +
	     "XSL processor.");
	 }
   }
}

