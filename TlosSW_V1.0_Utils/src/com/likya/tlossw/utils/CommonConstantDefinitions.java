package com.likya.tlossw.utils;


public class CommonConstantDefinitions {

	// public static final String dbUrl = "xmldb:exist:";
	
	// public static final String rootUrl = "//db/";
	// public static final String dbHost = "/";
	
	public static final String modulePath = "/modules/";
	public static final String xmlsPath = "/xmls/";
	
	/**
	 * eXist'teki xml dosyalari
	 */
	public static final String DAILY_SCENARIOS_DATA = "tlosSWDailyScenarios10.xml";
	public static final String JOB_TEMPLATES_DATA = "tlosSWJobTemplates10.xml";
	public static final String JOB_DEFINITION_DATA = "tlosSWData10.xml";
	public static final String PLAN_DEFINITION_DATA = "tlosSWDailyPlan10.xml";
	public static final String CALENDAR_DEFINITION_DATA = "tlosSWCalendar10.xml";
	public static final String SEQUENCE_DATA = "tlosSWSequenceData10.xml";
	public static final String AGENT_DATA = "tlosSWAgents10.xml";
	public static final String ALARM_DATA = "tlosSWAlarm10.xml";
	public static final String ALARM_HISTORY_DATA = "tlosSWAlarmHistory10.xml";
	public static final String DB_CONNECTION_PROFILES_DATA = "tlosSWDBConnectionProfiles10.xml";
	public static final String DB_CONNECTIONS_DATA = "tlosSWDBConnections10.xml";
	public static final String USER_DATA = "tlosSWUser10.xml";
	public static final String ERROR_DATA = "tlosSWErrors10.xml";
	public static final String NRPE_DATA = "tlosSWNrpeData10.xml";
	public static final String NRPE_RESULT_DATA = "tlosSWNrpeResult_v_1_0.xml";
	public static final String PROGRAM_PROVISIONING_DATA = "tlosSWProgramProvisioning10.xml";
	public static final String REPORTS_DATA = "tlosSWReports10.xml";
	public static final String RESOURCES_DATA = "tlosSWResources10.xml";
	public static final String FILE_LISTENER_DATA = "tlosSWSJFileListener10.xml";
	public static final String FTP_DATA = "tlosSWSJFTPConnections10.xml";
	public static final String WEB_SERVICES_DATA = "tlosSWSJWebServices10.xml";
	public static final String SLA_DATA = "tlosSWSLAs10.xml";
	public static final String TRACE_DATA = "tlosSWTrace10.xml";
	public static final String WEB_SERVICE_ACCESS_DATA = "tlosSWWebServiceAccessProfiles10.xml";
	public static final String PERMISSION_DATA = "tlosSWPermission10.xml";
	public static final String CONFIG_DATA = "tlosSWConfig10.xml";

	public static final String PARAMETER_DATA = "http://www.likyateknoloji.com/XML_parameters_types";
	public static final String META_DATA = "metaData.xml";

	/**
	 * Exist arabirimi tanımları
	 */

	public static final String xQueryNsHeader = "xquery version \"1.0\";";
	public static final String moduleImport =  "import module namespace ";
			
	public static final String hsNsUrl = "hs=\"http://hs.tlos.com/\"";
	public static final String sqNsUrl = "sq=\"http://sq.tlos.com/\"";
	public static final String dbNsUrl = "db=\"http://db.tlos.com/\"";
	public static final String lkNsUrl = "lk=\"http://likya.tlos.com/\"";
	public static final String rscNsUrl = "rsc=\"http://rsc.tlos.com/\"";
	public static final String ksNsUrl = "ks=\"http://ks.tlos.com/\"";
	public static final String wsoNsUrl = "wso=\"http://wso.tlos.com/\"";
	public static final String fcNsUrl = "fc=\"http://fc.tlos.com/\"";
	public static final String dssNsUrl = "dss=\"http://tlos.dss.com/\"";
	public static final String metaNsUrl = "dss=\"http://meta.tlos.com/\"";
	public static final String densityNsUrl = "density=\"http://density.tlos.com/\"";

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
	public static final String decNsPer = "declare namespace per = \"http://www.likyateknoloji.com/XML_permission_types\";";
	public static final String decNsFo = "declare namespace fo = \"http://www.w3.org/1999/XSL/Format\";";
	public static final String decNsXslfo = "declare namespace xslfo = \"http://exist-db.org/xquery/xslfo\";";
	public static final String decNsMeta = "declare namespace  meta = \"http://www.likyateknoloji.com/XML_metaData_types\";";

	/**
	 * Validasyon testlerinde kullanılan tanımlamalar.
	 */
	public static final String FTP_CONNECTION_ERROR = "ftpConnectionError";
	public static final String FTP_LOGIN_ERROR = "ftpLoginError";
	public static final String FTP_SUCCESSFUL = "ftpSuccessful";

	/**
	 * eXist'teki sequenceData.xml'den sorgu icin kullanilan sabitler
	 */
	public static final String CALENDAR_ID = "calendarId";
	public static final String DBCONNECTION_ID = "dbConnectionId";
	public static final String DBUSER_ID = "dbUserId";
	public static final String WSDEFINITION_ID = "wsDefinitionId";
	public static final String JOB_ID = "jobId";
	public static final String USER_ID = "userId";
	public static final String SCENARIO_ID = "scenarioId";
	public static final String WSUSERPROFILE_ID = "wsUserProfileId";
	public static final String FTPCONNECTION_ID = "ftpConnectionId";
	public static final String ERROR_ID = "errorId";
}
