package com.likya.tlossw.transform;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument;
import com.likya.tlos.model.xmlbeans.parameters.ParameterDocument.Parameter;
import com.likya.tlossw.utils.SpaceWideRegistry;
import com.likya.tlossw.utils.transform.TransformUtils;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;

public class ApplyXslt {
	/* http://java.sun.com/webservices/reference/tutorials/jaxp/html/dom.html */
	public synchronized static JobProperties transform(JobProperties job) {

		JobProperties jobPropertiesResult = null;

		/** Global Parametreler Job in lokal parametrelerine ekleniyor. **/
		// TODO Parametrelerde dublike kontrolu konacak !! Hakan

		Parameter parameterElement = null;

		ArrayList<Parameter> parameterList = SpaceWideRegistry.getInstance().getParameters();

		for (int i = 0; i < parameterList.size(); i++) {
//			String paramName = parameterList.get(i).getName();
//			String paramPreValueString = parameterList.get(i).getPreValue().getStringValue();
//			BigInteger paramPreValueType = parameterList.get(i).getPreValue().getType();
//			String paramDesc = parameterList.get(i).getDesc();

			QName qName = Parameter.type.getOuterType().getDocumentElementName();
			XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
			xmlOptions.setSaveOuter();
			String parameterXML = parameterList.get(i).xmlText(xmlOptions);

			parameterElement = Parameter.Factory.newInstance();

			try {
				parameterElement = ParameterDocument.Factory.parse(parameterXML, xmlOptions).getParameter();
				if (job.getLocalParameters() == null) {
					job.addNewLocalParameters();
				}
				if (job.getLocalParameters().getInParam() == null) {
					job.getLocalParameters().addNewInParam();
				}
				job.getLocalParameters().getInParam().addNewParameter().set(parameterElement);
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}

		}
		/******************************************/

		try {
			// Set up input documents

			StreamSource inputXSL = new StreamSource(new File("D:\\likya\\projeler\\tlos\\tlos3.0\\parameters\\job_sample11_xslt_1.0.xsl"));

			// Set up output sink
			// StreamResult outputXHTML2 = new StreamResult(
			// new File("D:\\likya\\projeler\\tlos\\tlos3.0\\parameters\\output.html"));

			// Set up output sink
			StringWriter writer = new StringWriter();
			StreamResult outputXHTML = new javax.xml.transform.stream.StreamResult(writer);

			QName qName = JobProperties.type.getOuterType().getDocumentElementName();
			XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
			xmlOptions.setSaveOuter();
			String jobPropertiesXML = job.xmlText(xmlOptions);

			/*
			 * StringReader reader = new StringReader(jobPropertiesXML);
			 * InputSource inputSource = new InputSource();
			 * inputSource.setCharacterStream(reader);
			 */

			// StringReader jobReader = new StringReader(job.toString());
			StringReader jobReader2 = new StringReader(jobPropertiesXML);

			// StreamSource inputXML = new StreamSource(jobReader);
			StreamSource inputXML2 = new StreamSource(jobReader2);

			// Pre-compile instructions
			// Templates templates = factory.newTemplates(inputXSL);

			// Get a transformer for this XSL
			// Transformer transformer = templates.newTransformer();

			Transformer transformer = TransformUtils.getTransformer(inputXSL);

			// Get a transformer for this XSL
			// Transformer transformer = factory.newTransformer(inputXSL);

			// Perform the transformation

			transformer.transform(inputXML2, outputXHTML);
			// transformer.transform(inputXML2, outputXHTML2);
			System.out.println("");
			System.out.println("==================== ORJINAL JOB ====================");
			System.out.println(jobPropertiesXML);
			System.out.println("=====================================================");
			// String xmlString = outputXHTML.getWriter().toString();
			System.out.println("");
			System.out.println("==================== DEGISEN JOB ====================");
			System.out.println("Dosyaya yazildi !!" + outputXHTML.getWriter().toString());
			System.out.println("=====================================================");
			System.out.println("");
			String xmlContent = (String) outputXHTML.getWriter().toString();

			// String xmlContent2 = job.xmlText(xmlOptions);
			jobPropertiesResult = JobProperties.Factory.newInstance();

			try {
				jobPropertiesResult = JobPropertiesDocument.Factory.parse(xmlContent, xmlOptions).getJobProperties();
				return jobPropertiesResult;
			} catch (XmlException e) {
				e.printStackTrace();
				return null;
			}
		} catch (TransformerConfigurationException e) {
			System.out.println("The underlying XSL processor " + "does not support the requested features.");
		} catch (TransformerException e) {
			System.out.println("Error occurred obtaining " + "XSL processor.");
		}
		return jobPropertiesResult;

	}

}
