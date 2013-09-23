package com.likya.tlossw.utils.validation;

import java.util.ArrayList;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.data.JobListDocument.JobList;
import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.data.PeriodInfoDocument.PeriodInfo;

public class XMLValidations {
	
	public static void validateWithCode(JobList jobList, Logger logger) {
		validatePeriodInfo(jobList, logger);
	}
	
	private static void validatePeriodInfo(JobList jobList, Logger logger) {
		
		ArrayIterator jobListIterator = new ArrayIterator(jobList.getJobPropertiesArray());
		
		JobList validJobList = JobList.Factory.newInstance();
		
		while (jobListIterator.hasNext()) {
			
			JobProperties jobProperties = (JobProperties) (jobListIterator.next());

			PeriodInfo periodInfo = jobProperties.getBaseJobInfos().getPeriodInfo();

			if ((periodInfo != null) && (periodInfo.getMaxCount() == null || (periodInfo.getCounter().intValue() + 1) >= periodInfo.getMaxCount().intValue())) {
				
				XMLValidations.errprintln("*************************************************************");
				XMLValidations.errprintln("Validation error : periodInfo.getMaxCount() is null or period counter exceeds maxcount value, job is removed from execution queue !");
				XMLValidations.errprintln("Removed job id : " + jobProperties.getID());
				XMLValidations.errprintln("*************************************************************");
				
				continue;
			}
			
			validJobList.addNewJobProperties().set(jobProperties);
			
		}
		
		jobList.setJobPropertiesArray(validJobList.getJobPropertiesArray());
		
		return;
	}
	
	public static boolean validateWithXSDAndLog(Logger logger, XmlObject xmlObject) {
		/**
		 * @reference http://xmlbeans.apache.org/docs/1.0.4/reference/org/apache/xmlbeans/XmlObject.html#validate%28%29
		 */

		// Create an XmlOptions instance and set the error listener.
		XmlOptions validateOptions = new XmlOptions();
		ArrayList<XmlError> errorList = new ArrayList<XmlError>();
		validateOptions.setErrorListener(errorList);

		// Validate the XML.
		boolean isValid = xmlObject.validate(validateOptions);

		// If the XML isn't valid, loop through the listener's contents,
		// printing contained messages.
		
		if (!isValid) {
			
			XMLValidations.errprintln("*************************************************************");
			logger.error("*************************************************************");
			logger.error("Validating xmlObject class : " + xmlObject.getClass().getName());
			XMLValidations.errprintln("xmlObject : " + xmlObject.getClass().getName());
			logger.error("Found " + errorList.size() + " validation errors !");
			XMLValidations.errprintln("Found " + errorList.size() + " validation errors !");
			
			for (int i = 0; i < errorList.size(); i++) {
				XmlError error = (XmlError) errorList.get(i);
				
				logger.error("Validation error : " + (i + 1));
				logger.error("	> Message: " + error.getMessage());
				logger.error("	> Location of invalid XML : ");
				logger.error("		" + error.getCursorLocation().xmlText());
				logger.error("	");
				
				XMLValidations.errprintln("Validation error : " + (i + 1));
				XMLValidations.errprintln("	> Message: " + error.getMessage());
				XMLValidations.errprintln("	> Location of invalid XML : ");
				XMLValidations.errprintln("		" + error.getCursorLocation().xmlText());
				XMLValidations.errprintln("	");
			}
			
			XMLValidations.errprintln("*************************************************************");
			logger.error("*************************************************************");
		}

		return isValid;
	}
	
	public static void errprintln(String message) {
		System.err.println(message);
	}
}
