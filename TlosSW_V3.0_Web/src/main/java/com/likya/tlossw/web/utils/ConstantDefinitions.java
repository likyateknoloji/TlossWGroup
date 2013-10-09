package com.likya.tlossw.web.utils;

public class ConstantDefinitions {

	/**
	 * Ağaç yapısında kullanılan tanımlamalar
	 */
	public static final String TREE_ROOT = "root";
	public static final String TREE_ROOTID = "-1";
	public static final String TREE_SCENARIOROOTID = "0";
	public static final String TREE_CALISANISLER = "calisanisler";
	public static final String TREE_KAYNAKLISTESI = "kaynaklistesi";
	public static final String TREE_KAYNAK = "kaynak";
	public static final String TREE_TLOSAGENT = "tlosagent";
	public static final String TREE_MONITORAGENT = "monitoragent";
	public static final String TREE_INSTANCE = "plan";
	public static final String TREE_SCENARIO = "scenario";
	public static final String TREE_JOBGROUP = "jobGroup";
	public static final String TREE_JOB = "job";
	public static final String TREE_UNKNOWN = "unknown";
	public static final String TREE_DUMMY = "empty";
	public static final Integer CACHE_TIMEOUT_VALUE = 200; //msec
	
	public static final String AGENT_NAME = "Agent";
	public static final String SERVER_NAME = "Sunucu";

	/**
	 * flex agaci sabitleri
	 */
	public static final String LIVE_TREE = "live";
	public static final String DEFINITION_TREE = "workspace";

	/**
	 * Job/Senaryo isim kontrol sabitleri
	 */
	public static final String NEW_NAME = "0"; // girilen isimde daha önce kaydedilmiş bir iş/senaryo yoksa 0
	public static final String DUPLICATE_NAME_AND_PATH = "1"; // ayni path de aynı isimde bir iş/senaryo varsa 1
	public static final String INNER_DUPLICATE_NAME = "2"; // iç senaryolarda aynı isimde bir iş/senaryo varsa 2
	public static final String OUTER_DUPLICATE_NAME = "3"; // senaryonun dışında aynı isimde bir iş/senaryo varsa 3

	/**
	 * Deployment seçenekleri
	 */
	public static final String DEPLOY_SOLSTICE = "1";
	public static final String DEPLOY_PERIOD = "2";
	public static final String DEPLOY_TIME = "3";
	public static final String DEPLOY_NOW = "4";

	/**
	 * Raporlama ekrabları sabitleri
	 */
	public static final String ZONES_REPORT = "zonesReport";
	public static final String JOB_DENSITY_REPORT = "densityReport";
	public static final String JOB_DURATION_REPORT = "durationReport";
	public static final String JOB_DISTRIBUTION_REPORT = "distributionReport";
	public static final String JOB_STATE_REPORT = "stateReport";

	/**
	 * job durum sabitleri
	 */
	public final static String STATE = "State";
	public final static String SUBSTATE = "SubState";
	public final static String STATUS = "Status";

}
