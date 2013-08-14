package com.likya.tlossw.utils.xml;

import java.util.HashMap;

public class XMLNameSpaceMappings {

	public static HashMap<String, String> getXmlNameSpaceHashMap() {
		
		HashMap<String, String> xmlNameSpaceMapping = new HashMap<String, String>();
		
		xmlNameSpaceMapping.put("usr", "http://www.likyateknoloji.com/XML_user_types");
		xmlNameSpaceMapping.put("dat", "http://www.likyateknoloji.com/XML_data_types");
		xmlNameSpaceMapping.put("per", "http://www.likyateknoloji.com/XML_permission_types");
		xmlNameSpaceMapping.put("com", "http://www.likyateknoloji.com/XML_common_types");
		xmlNameSpaceMapping.put("out", "http://www.likyateknoloji.com/XML_userOutput_types");
		xmlNameSpaceMapping.put("agnt", "http://www.likyateknoloji.com/XML_agent_types");
		xmlNameSpaceMapping.put("state-types", "http://www.likyateknoloji.com/state-types");
		xmlNameSpaceMapping.put("res", "http://www.likyateknoloji.com/resource-extension-defs");
		xmlNameSpaceMapping.put("nrp", "http://www.likyateknoloji.com/XML_nrpe_results");
		xmlNameSpaceMapping.put("sla", "http://www.likyateknoloji.com/XML_SLA_types");
		xmlNameSpaceMapping.put("pp", "http://www.likyateknoloji.com/XML_PP_types");
		xmlNameSpaceMapping.put("lrns", "http://www.likyateknoloji.com/XML_SWResourceNS_types");
		xmlNameSpaceMapping.put("rns", "http://schemas.ogf.org/rns/2009/12/rns");
		xmlNameSpaceMapping.put("wsa", "http://www.w3.org/2005/08/addressing");
		xmlNameSpaceMapping.put("alm", "http://www.likyateknoloji.com/XML_alarm_types");
		xmlNameSpaceMapping.put("par", "http://www.likyateknoloji.com/XML_parameters_types");
		xmlNameSpaceMapping.put("ftp", "http://www.likyateknoloji.com/XML_ftp_adapter_types");
		xmlNameSpaceMapping.put("lstn", "http://www.likyateknoloji.com/XML_listener_types");
		xmlNameSpaceMapping.put("dbc", "http://www.likyateknoloji.com/XML_dbconnection_types");
		xmlNameSpaceMapping.put("db", "http://www.likyateknoloji.com/XML_db_job_types");
		xmlNameSpaceMapping.put("jsdl", "http://schemas.ggf.org/jsdl/2005/11/jsdl");
		xmlNameSpaceMapping.put("jsdl-posix", "http://schemas.ggf.org/jsdl/2005/11/jsdl-posix");
		xmlNameSpaceMapping.put("sweep", "http://schemas.ogf.org/jsdl/2009/03/sweep");
		xmlNameSpaceMapping.put("sweepfunc", "http://schemas.ogf.org/jsdl/2009/03/sweep/functions");
		xmlNameSpaceMapping.put("saxon", "http://saxon.sf.net/");
		xmlNameSpaceMapping.put("ws", "http://www.likyateknoloji.com/XML_web_service_types");
		xmlNameSpaceMapping.put("pn", "http://www.likyateknoloji.com/XML_process_node");
		xmlNameSpaceMapping.put("rep", "http://www.likyateknoloji.com/XML_report_types");
		
		return xmlNameSpaceMapping;
	}
	
	public static HashMap<String, String> getXmlNameSpaceHashMapXMLBeans() {
		
		HashMap<String, String> xmlNameSpaceMapping = new HashMap<String, String>();

		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_user_types", "usr");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_data_types", "dat");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_permission_types", "per");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_common_types", "com");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_userOutput_types", "out");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_agent_types", "agnt");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/state-types", "state-types");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/resource-extension-defs", "res");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_nrpe_results", "nrp");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_SLA_types", "sla");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_PP_types", "pp");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_SWResourceNS_types", "lrns");
		xmlNameSpaceMapping.put("http://schemas.ogf.org/rns/2009/12/rns", "rns");
		xmlNameSpaceMapping.put("http://www.w3.org/2005/08/addressing", "wsa");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_alarm_types", "alm");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_parameters_types", "par");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_ftp_adapter_types", "ftp");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_listener_types", "lstn");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_dbconnection_types", "dbc");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_db_job_types", "db");
		xmlNameSpaceMapping.put("http://schemas.ggf.org/jsdl/2005/11/jsdl", "jsdl");
		xmlNameSpaceMapping.put("http://schemas.ggf.org/jsdl/2005/11/jsdl-posix", "jsdl-posix");
		xmlNameSpaceMapping.put("http://schemas.ogf.org/jsdl/2009/03/sweep", "sweep");
		xmlNameSpaceMapping.put("http://schemas.ogf.org/jsdl/2009/03/sweep/functions", "sweepfunc");
		xmlNameSpaceMapping.put("http://saxon.sf.net/", "saxon");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_web_service_types", "ws");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_process_node", "pn");
		xmlNameSpaceMapping.put("http://www.likyateknoloji.com/XML_report_types", "rep");
		
		return xmlNameSpaceMapping;
	}
}
