/*
 * TlosFaz_V2.0
 * com.likya.tlos.utils : ParsingUtils.java
 * @author Serkan Taş
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

		if (LSIDateTime != null) {
			jobXPath = jobXPath + "and @LSIDateTime='" + LSIDateTime + "'";
		} else {
			jobXPath = jobXPath + "and not(exists(@LSIDateTime))";
		}
		jobXPath = jobXPath + "]";

		return jobXPath;
	}
	
	public static String getFunctionString(String xQueryModuleUrl, String moduleName, String functionName, String declaredNameSpaces, String moduleNamesSpace, String... params) {

		// String xQueryStr = CommonConstantDefinitions.xQueryNsHeader + CommonConstantDefinitions.hsNsUrl + spaceWideRegistry.getxQueryModuleUrl() + "/moduleTlosManagementOperations.xquery\";" + CommonConstantDefinitions.decNsCom + CommonConstantDefinitions.decNsCon + "hs:getTlosConfig()";

		StringBuffer stringBuffer = new StringBuffer();

		stringBuffer.append(CommonConstantDefinitions.xQueryNsHeader);
		stringBuffer.append(declaredNameSpaces);
		stringBuffer.append(CommonConstantDefinitions.moduleImport);
		stringBuffer.append(moduleNamesSpace);
		stringBuffer.append(xQueryModuleUrl);
		stringBuffer.append("/" + moduleName + "\";");
		stringBuffer.append(functionName);

		stringBuffer.append("(");

		for (String param : params) {
			stringBuffer.append("\"" + param + "\", ");
			System.out.println(param);
		}

		stringBuffer.append(")");

		return stringBuffer.toString();
	}

	public static String getXmlsPath(String collectionName) {
		return CommonConstantDefinitions.dbUrl + CommonConstantDefinitions.rootUrl + collectionName + CommonConstantDefinitions.xmlsPath;
	}

	public static String getXQueryModuleUrl(String collectionName) {
		return " at \"" + CommonConstantDefinitions.dbUrl + CommonConstantDefinitions.rootUrl + collectionName + CommonConstantDefinitions.modulePath;
	}

}
