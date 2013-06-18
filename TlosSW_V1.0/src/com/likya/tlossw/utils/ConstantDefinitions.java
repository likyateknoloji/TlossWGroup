package com.likya.tlossw.utils;

public class ConstantDefinitions {

	/**
	 * Agent yapısında kullanılan tanımlamalar.
	 */

	public static final String AGENT_ON_SERVER = "server";

	/**
	 * Web arabiriminde kullanılan tanımlamalar.
	 */

	public static final String FTP_CONNECTION_ERROR = "ftpConnectionError";
	public static final String FTP_LOGIN_ERROR = "ftpLoginError";
	public static final String FTP_SUCCESSFUL = "ftpSuccessful";

	
	/**
	 * Exist arabirimi tanımları
	 */
	
	public static final String xQueryNsHeader =  "xquery version \"1.0\";  import module namespace ";
	
	public static final String hsNsUrl = "hs=\"http://hs.tlos.com/\"";
	public static final String sqNsUrl = "sq=\"http://sq.tlos.com/\"";
	public static final String dbNsUrl = "db=\"http://db.tlos.com/\"";
	public static final String lkNsUrl = "lk=\"http://likya.tlos.com/\"";
	public static final String rscNsUrl = "rsc=\"http://rsc.tlos.com/\"";
	public static final String ksNsUrl = "ks=\"http://ks.tlos.com/\"";
	public static final String wsoNsUrl = "wso=\"http://wso.tlos.com/\"";
	public static final String fcNsUrl = "fc=\"http://fc.tlos.com/\"";
	public static final String dssNsUrl = "dss=\"http://tlos.dss.com/\"";

	public static final String decNsRes = "declare namespace res = \"http://www.likyateknoloji.com/resource-extension-defs\";";
	public static final String decNsDbc = "declare namespace dbc = \"http://www.likyateknoloji.com/XML_dbconnection_types\";";
	public static final String decNsCom = "declare namespace com = \"http://www.likyateknoloji.com/XML_common_types\";";
	public static final String decNsDat = "declare namespace dat = \"http://www.likyateknoloji.com/XML_data_types\";";
	public static final String decNsFtp = "declare namespace ftp = \"http://www.likyateknoloji.com/XML_ftp_adapter_types\";";
	public static final String decNsSt = "declare namespace state-types = \"http://www.likyateknoloji.com/state-types\";";
	public static final String decNsWs = "declare namespace ws = \"http://www.likyateknoloji.com/XML_web_service_types\";";
	public static final String decNsRep = "declare namespace rep = \"http://www.likyateknoloji.com/XML_report_types\";";
	public static final String decNsAgnt = "declare namespace agnt = \"http://www.likyateknoloji.com/XML_agent_types\";";
	public static final String decNsCon = "declare namespace con = \"http://www.likyateknoloji.com/XML_config_types\";";
	public static final String decNsCal = "declare namespace cal  = \"http://www.likyateknoloji.com/XML_calendar_types\";";
	public static final String decNsXsi = "declare namespace xsi  = \"http://www.w3.org/2001/XMLSchema-instance\";";
	public static final String decNsFn = "declare namespace fn   = \"http://www.w3.org/2005/xpath-functions\";";
	public static final String decNsLrns = "declare namespace lrns   = \"www.likyateknoloji.com/XML_SWResourceNS_types\";";
	public static final String decNsNrp = "declare namespace nrp   = \"www.likyateknoloji.com/XML_nrpe_types\";";
	public static final String decNsErr = "declare namespace err = \"http://www.likyateknoloji.com/XML_error_types\";";

	public static final String dbUrl = "xmldb:exist:";
	public static final String rootUrl = "//db/";
	// TODO merve: collection ismi config dosyasından alınınca "TLOSSW" kısmı ona göre düzenlenecek
	public static String xQueryModuleUrl = " at \"" + ConstantDefinitions.dbUrl + ConstantDefinitions.rootUrl + "TLOSSW" + "/modules";
}
