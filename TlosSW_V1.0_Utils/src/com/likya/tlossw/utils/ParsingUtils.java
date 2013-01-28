/*
 * TlosFaz_V2.0
 * com.likya.tlos.utils : ParsingUtils.java
 * @author Serkan Taþ
 * Tarih : 02.Nis.2010 17:13:53
 */

package com.likya.tlossw.utils;

import java.util.StringTokenizer;

import com.likya.tlossw.model.engine.EngineeConstants;


public class ParsingUtils {
	
	public static String getJobXPath(String jobURI) {
		jobURI = jobURI.replaceFirst("root.", "");
		String XPath = "/TlosProcessDataAll";
		StringTokenizer xPathToken = new StringTokenizer(jobURI, ".");

		int i = 1;
		while (xPathToken.hasMoreTokens()) {
			String token = xPathToken.nextToken();
			if (i == 1) {
				XPath = XPath + "/RUN[@id='" + token + "']/dat:TlosProcessData";
			} else {
				if (!token.equals(EngineeConstants.LONELY_JOBS)) {
					XPath = XPath + "/dat:scenario[@ID='" + token + "']";
				}
			}
			i = i + 1;
		}
		XPath = XPath + "/dat:jobList";

		return XPath;
	}

	public static String getConcatenatedPathAndFileName(String path, String fileName) {
		if (path.indexOf('\\') >= 0) {
			if (path.lastIndexOf('\\') == path.length() - 1) {
				path = path.substring(0, path.length() - 1);
			}
			path = path + "\\";
		} else if (path.indexOf('/') >= 0) {
			if (path.lastIndexOf('/') == path.length() - 1) {
				path = path.substring(0, path.length() - 1);
			}
			path = path + "/";
		}

		return path + fileName;
	}
	
	public static String getJobXFullPath(String jobURI, String jobId, String agentId, String LSIDateTime) {
		String jobXPath = getJobXPath(jobURI);
		
		jobXPath = jobXPath + "/dat:jobProperties[@ID='" + jobId + "'" + " and @agentId='" + agentId + "' ";
		
		if ( LSIDateTime != null) {
			jobXPath = jobXPath + "and @LSIDateTime='" + LSIDateTime + "'";
		}
		else {
			jobXPath = jobXPath + "and not(exists(@LSIDateTime))";
		}
		jobXPath = jobXPath + "]";
		
		return jobXPath;
	}


}
