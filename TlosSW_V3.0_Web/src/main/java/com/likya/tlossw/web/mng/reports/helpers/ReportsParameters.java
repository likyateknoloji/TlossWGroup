package com.likya.tlossw.web.mng.reports.helpers;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.common.RoleType;
import com.likya.tlos.model.xmlbeans.common.TypeOfTimeType;
import com.likya.tlos.model.xmlbeans.report.ArithmeticADocument.ArithmeticA;
import com.likya.tlos.model.xmlbeans.report.FilterByResult;
import com.likya.tlos.model.xmlbeans.report.HistoryADocument.HistoryA;
import com.likya.tlos.model.xmlbeans.report.LocalStatsDocument.LocalStats;
import com.likya.tlos.model.xmlbeans.report.OrderByType;
import com.likya.tlos.model.xmlbeans.report.OrderType;
import com.likya.tlos.model.xmlbeans.report.ReportParametersDocument.ReportParameters;
import com.likya.tlos.model.xmlbeans.report.SetADocument.SetA;
import com.likya.tlos.model.xmlbeans.report.SortingADocument.SortingA;
import com.likya.tlos.model.xmlbeans.report.StateRelatedA2Document.StateRelatedA2;
import com.likya.tlos.model.xmlbeans.report.StatisticsADocument.StatisticsA;
import com.likya.tlos.model.xmlbeans.report.TimeRelatedA1Document.TimeRelatedA1;
import com.likya.tlos.model.xmlbeans.report.TimeRelatedA2Document.TimeRelatedA2;
import com.likya.tlos.model.xmlbeans.report.UserADocument.UserA;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfosType;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.db.DBOperations;

public class ReportsParameters implements Serializable {

	private static final long serialVersionUID = -2448886436692898113L;

	private DBOperations dbOperations;

	private ReportParameters reportParams = null;

	private String reportParametersXML = null;

	ArithmeticA arithmeticA = null;
	HistoryA historyA = null;
	SetA setA = null;
	SortingA sortingA = null;
	LiveStateInfosType stateRelatedA1 = null;
	StateRelatedA2 stateRelatedA2 = null;
	StatisticsA statisticsA = null;
	TimeRelatedA1 timeRelatedA1 = null;
	TimeRelatedA2 timeRelatedA2 = null;
	UserA userA = null;

	/**** INPUTS *********/

	/* ARITHMETICA */
	private Boolean isCumulative = false;

	/* HISTORYA */
	private short numberOfRuns = 1;

	/* SETA */
	private String jobId = "0";
	private String scenarioId = "0";
	private BigInteger runId = BigInteger.valueOf(0);
	private Boolean justFirstLevel = false;
	private BigInteger maxNumOfListedJobs = new BigInteger("11");
	private String docId = "";
	private Boolean isGlobal = true;
	private Boolean countInstancesAsOne = false;

	/* SORTINGA */
	private OrderByType.Enum orderBy = OrderByType.DURATION;
	private OrderType.Enum order = OrderType.DESCENDING;

	/* STATERELATEDA1 */
	private LiveStateInfosType liveStateInfos;

	/* STATERELATEDA2 */
	private FilterByResult.Enum includedJobs = FilterByResult.FINISHED;

	/* STATISTICSA */
	private short statSampleNumber = 3;

	/* TIMERELATEDA1 */
	private Calendar startDateCalendar;
	private Calendar endDateCalendar;
	private TypeOfTimeType typeOfTime;
	private String timeZone = "Europe/Istanbul";
	private Boolean automaticTimeInterval = true;

	/* TIMERELATEDA2 */
	private Calendar stepForDensityCalendar;
	private Short maxNumberOfIntervals = (short) 100;

	/* USERA */
	private BigInteger userId = new BigInteger("-1");
	private RoleType.Enum role = RoleType.NORMALUSER;

	private LocalStats statParameters = null;
	private String zuluTZone = "UTC";

	private StateName.Enum stateName;
	private SubstateName.Enum substateName;
	private StatusName.Enum statusName;

	private String stateNameStr;
	private String substateNameStr;
	private String statusNameStr;

	private String includedJobsStr;
	private String orderStr;
	private String orderByStr;

	private String roleStr;

	/******* computed ********/

	public ReportsParameters() {

		if (reportParams == null) {
			reportParams = ReportParameters.Factory.newInstance();
			init();
		}
	}

	public void init() {

		stateNameStr = StateName.RUNNING.toString();
		substateNameStr = SubstateName.ON_RESOURCE.toString();
		statusNameStr = StatusName.TIME_IN.toString();

		includedJobsStr = FilterByResult.FINISHED.toString();

		orderByStr = OrderByType.DURATION.toString();
		orderStr = OrderType.DESCENDING.toString();

		roleStr = RoleType.NORMALUSER.toString();

		setStartDateCalendar(com.likya.tlossw.web.utils.DefinitionUtils.stringToCalendar(new String("2013/09/19 22:00:01"), new String("yyyy/MM/dd HH:mm:ss"), timeZone));
		setEndDateCalendar(com.likya.tlossw.web.utils.DefinitionUtils.stringToCalendar(new String("2013/09/19 23:00:01"), new String("yyyy/MM/dd HH:mm:ss"), timeZone));
		setStepForDensityCalendar(com.likya.tlossw.web.utils.DefinitionUtils.dateTimeToXmlDateTime(new String("1970-01-01"), new String("00:00:30"), zuluTZone));

		fillReportParameters();

	}

	private void setArithmeticA(boolean isCumulative) {
		if (arithmeticA == null) {
			arithmeticA = reportParams.addNewArithmeticA();
		}

		arithmeticA.setIsCumulative(isCumulative);
	}

	private ArithmeticA getArithmeticA() {
		ArithmeticA arithmeticA = reportParams.getArithmeticA();

		return arithmeticA;
	}

	private void setHistoryA(short numberOfRuns) {
		if (historyA == null) {
			historyA = reportParams.addNewHistoryA();
		}

		historyA.setNumberOfRuns(numberOfRuns);
	}

	private HistoryA getHistoryA() {
		HistoryA historyA = reportParams.getHistoryA();

		return historyA;
	}

	private void setSetA(String jobId, String scenarioId, BigInteger runId, Boolean justFirstLevel, BigInteger maxNumOfListedJobs, String docId, Boolean isGlobal, Boolean countInstancesAsOne) {
		if (setA == null) {
			setA = reportParams.addNewSetA();
		}

		setA.setJobId(jobId);
		setA.setScenarioId(scenarioId);
		setA.setRunId(runId);
		setA.setJustFirstLevel(justFirstLevel);
		setA.setMaxNumOfListedJobs(maxNumOfListedJobs);
		setA.setDocId(docId);
		setA.setIsGlobal(isGlobal);
		setA.setCountInstancesAsOne(countInstancesAsOne);
	}

	private SetA getSetA() {
		SetA setA = reportParams.getSetA();

		return setA;
	}

	private void setSortingA(OrderByType.Enum orderBy, OrderType.Enum order) {
		if (sortingA == null) {
			sortingA = reportParams.addNewSortingA();
		}

		sortingA.setOrderBy(orderBy);
		sortingA.setOrder(order);
	}

	private SortingA getSortingA() {
		SortingA sortingA = reportParams.getSortingA();

		return sortingA;
	}

	private void setStateRelatedA1(StateName.Enum stateName, SubstateName.Enum substateName, StatusName.Enum statusName) {
		if (stateRelatedA1 == null) {
			stateRelatedA1 = reportParams.addNewStateRelatedA1();
		}

		LiveStateInfo liveStateInfo = LiveStateInfo.Factory.newInstance();

		liveStateInfo.setStateName(stateName);
		liveStateInfo.setSubstateName(substateName);
		liveStateInfo.setStatusName(statusName);

		// TODO birden fazla state olabilir yaptim ama kontrolsuz coklama oldu. O yuzden simdilik tek elemanl olsun.
		if (stateRelatedA1.sizeOfLiveStateInfoArray() == 0) {
			stateRelatedA1.addNewLiveStateInfo().set(liveStateInfo);
		}

	}

	private LiveStateInfosType getStateRelatedA1() {
		LiveStateInfosType stateRelatedA1 = reportParams.getStateRelatedA1();

		return stateRelatedA1;
	}

	private void setStateRelatedA2(FilterByResult.Enum stateFilter) {
		if (stateRelatedA2 == null) {
			stateRelatedA2 = reportParams.addNewStateRelatedA2();
		}

		stateRelatedA2.setStateFilter(stateFilter);
	}

	private StateRelatedA2 getStateRelatedA2() {
		StateRelatedA2 stateRelatedA2 = reportParams.getStateRelatedA2();

		return stateRelatedA2;
	}

	private void setStatisticsA(short statSampleNumber) {
		if (statisticsA == null) {
			statisticsA = reportParams.addNewStatisticsA();
		}

		statisticsA.setStatSampleNumber(statSampleNumber);
	}

	private StatisticsA getStatisticsA() {
		StatisticsA statisticsA = reportParams.getStatisticsA();

		return statisticsA;
	}

	private void setTimeRelatedA1(Calendar startDateCalendar, Calendar endDateCalendar, TypeOfTimeType.Enum typeOfTime, String timeZone, Boolean automaticTimeInterval) {
		if (timeRelatedA1 == null) {
			timeRelatedA1 = reportParams.addNewTimeRelatedA1();
		}

		timeRelatedA1.setStartDateTime(startDateCalendar);
		timeRelatedA1.setEndDateTime(endDateCalendar);
		timeRelatedA1.setTypeOfTime(typeOfTime);
		timeRelatedA1.setTimeZone(timeZone);
		timeRelatedA1.setAutomaticTimeInterval(automaticTimeInterval);
	}

	private TimeRelatedA1 getTimeRelatedA1() {
		TimeRelatedA1 timeRelatedA1 = reportParams.getTimeRelatedA1();

		return timeRelatedA1;
	}

	private void setTimeRelatedA2(Calendar stepForDensityCalendar, Short maxNumberOfIntervals) {
		if (timeRelatedA2 == null) {
			timeRelatedA2 = reportParams.addNewTimeRelatedA2();
		}

		timeRelatedA2.setStepForDensity(stepForDensityCalendar);
		timeRelatedA2.setMaxNumberOfIntervals(maxNumberOfIntervals);
	}

	private TimeRelatedA2 getTimeRelatedA2() {
		TimeRelatedA2 timeRelatedA2 = reportParams.getTimeRelatedA2();

		return timeRelatedA2;
	}

	private void setUserA(BigInteger userId, RoleType.Enum role) {
		if (userA == null) {
			userA = reportParams.addNewUserA();
		}

		userA.setUserId(userId);
		userA.setRole(role);
	}

	private UserA getUserA() {
		UserA userA = reportParams.getUserA();

		return userA;
	}

	public void fillReportParameters() {

		// setStepForDensityCalendar(DefinitionUtils.dateTimeToXmlDateTime(new String("1970-01-01"), stepForDensity, zuluTZone));

		// String timeInputFormat = new String("HH:mm:ss.SSSZZ");

		// stepForDensity = com.likya.tlossw.web.utils.DefinitionUtils.calendarToStringTimeFormat(stepForDensityCalendar, "UTC", timeInputFormat);

		stateName = StateName.Enum.forString(stateNameStr);
		substateName = SubstateName.Enum.forString(substateNameStr);
		statusName = StatusName.Enum.forString(statusNameStr);

		includedJobs = FilterByResult.Enum.forString(includedJobsStr);

		orderBy = OrderByType.Enum.forString(orderByStr);
		order = OrderType.Enum.forString(orderStr);

		role = RoleType.Enum.forString(roleStr);

		// setStartDateCalendar(com.likya.tlossw.web.utils.DefinitionUtils.stringToCalendar(new String("2013/09/19 22:00:01"), new String("yyyy/MM/dd HH:mm:ss"), timeZone));
		// setEndDateCalendar(com.likya.tlossw.web.utils.DefinitionUtils.stringToCalendar(new String("2013/09/19 23:00:01"), new String("yyyy/MM/dd HH:mm:ss"), timeZone));
		// setStepForDensityCalendar(com.likya.tlossw.web.utils.DefinitionUtils.dateTimeToXmlDateTime(new String("1970-01-01"), new String("00:00:30"), zuluTZone));

		setArithmeticA(isCumulative);

		setHistoryA(numberOfRuns);

		setSetA(jobId, scenarioId, runId, justFirstLevel, maxNumOfListedJobs, docId, isGlobal, countInstancesAsOne);

		setSortingA(orderBy, order);

		setStateRelatedA1(stateName, substateName, statusName);

		setStateRelatedA2(includedJobs);

		setStatisticsA(statSampleNumber);

		setTimeRelatedA1(startDateCalendar, endDateCalendar, TypeOfTimeType.ACTUAL, timeZone, automaticTimeInterval);

		setTimeRelatedA2(stepForDensityCalendar, maxNumberOfIntervals);

		setUserA(userId, role);

	}

	public void setStatParameters() {

		try {
			statParameters = getDbOperations().getStatsReport(getReportParametersXML());
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

	}

	public String getReportParametersXML() {

		QName qName = ReportParameters.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		reportParametersXML = reportParams.xmlText(xmlOptions);

		return reportParametersXML;
	}

	public void setReportParametersXML(String reportParametersXML) {
		this.reportParametersXML = reportParametersXML;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public Boolean getJustFirstLevel() {
		return justFirstLevel;
	}

	public void setJustFirstLevel(Boolean justFirstLevel) {
		this.justFirstLevel = justFirstLevel;
	}

	public BigInteger getRunId() {
		return runId;
	}

	public void setRunId(BigInteger runId) {
		this.runId = runId;
	}

	public String getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}

	public Boolean getIsCumulative() {
		return isCumulative;
	}

	public void setIsCumulative(Boolean isCumulative) {
		this.isCumulative = isCumulative;
	}

	public BigInteger getMaxNumOfListedJobs() {
		return maxNumOfListedJobs;
	}

	public void setMaxNumOfListedJobs(BigInteger maxNumOfListedJobs) {
		this.maxNumOfListedJobs = maxNumOfListedJobs;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public LocalStats getStatParameters() {
		return statParameters;
	}

	public void setStatParameters(LocalStats statParameters) {
		this.statParameters = statParameters;
	}

	public Short getStatSampleNumber() {
		return statSampleNumber;
	}

	public void setStatSampleNumber(Short statSampleNumber) {
		this.statSampleNumber = statSampleNumber;
	}

	public Short getMaxNumberOfIntervals() {
		return maxNumberOfIntervals;
	}

	public void setMaxNumberOfIntervals(Short maxNumberOfIntervals) {
		this.maxNumberOfIntervals = maxNumberOfIntervals;
	}

	public ReportParameters getReportParams() {
		return reportParams;
	}

	public void setReportParams(ReportParameters reportParams) {
		this.reportParams = reportParams;
	}

	public Calendar getStartDateCalendar() {
		return startDateCalendar;
	}

	public void setStartDateCalendar(Calendar startDateCalendar) {
		this.startDateCalendar = startDateCalendar;
	}

	public Calendar getEndDateCalendar() {
		return endDateCalendar;
	}

	public void setEndDateCalendar(Calendar endDateCalendar) {
		this.endDateCalendar = endDateCalendar;
	}

	public Calendar getStepForDensityCalendar() {
		return stepForDensityCalendar;
	}

	public void setStepForDensityCalendar(Calendar calendar) {
		this.stepForDensityCalendar = calendar;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public Boolean getAutomaticTimeInterval() {
		return automaticTimeInterval;
	}

	public void setAutomaticTimeInterval(Boolean automaticTimeInterval) {
		this.automaticTimeInterval = automaticTimeInterval;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public Boolean getIsGlobal() {
		return isGlobal;
	}

	public void setIsGlobal(Boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	public Boolean getCountInstancesAsOne() {
		return countInstancesAsOne;
	}

	public void setCountInstancesAsOne(Boolean countInstancesAsOne) {
		this.countInstancesAsOne = countInstancesAsOne;
	}

	public LiveStateInfosType getLiveStateInfos() {
		return liveStateInfos;
	}

	public void setLiveStateInfos(LiveStateInfosType liveStateInfos) {
		this.liveStateInfos = liveStateInfos;
	}

	public short getNumberOfRuns() {
		return numberOfRuns;
	}

	public void setNumberOfRuns(short numberOfRuns) {
		this.numberOfRuns = numberOfRuns;
	}

	public TypeOfTimeType getTypeOfTime() {
		return typeOfTime;
	}

	public void setTypeOfTime(TypeOfTimeType typeOfTime) {
		this.typeOfTime = typeOfTime;
	}

	public BigInteger getUserId() {
		return userId;
	}

	public void setUserId(BigInteger userId) {
		this.userId = userId;
	}

	public RoleType.Enum getRole() {
		return role;
	}

	public void setRole(RoleType.Enum role) {
		this.role = role;
	}

	public void setOrderBy(com.likya.tlos.model.xmlbeans.report.OrderByType.Enum orderBy) {
		this.orderBy = orderBy;
	}

	public void setOrder(com.likya.tlos.model.xmlbeans.report.OrderType.Enum order) {
		this.order = order;
	}

	public void setIncludedJobs(FilterByResult.Enum includedJobs) {
		this.includedJobs = includedJobs;
	}

	public FilterByResult.Enum getIncludedJobs() {
		return includedJobs;
	}

	public OrderType.Enum getOrder() {
		return order;
	}

	public OrderByType.Enum getOrderBy() {
		return orderBy;
	}

	public StateName.Enum getStateName() {
		return stateName;
	}

	public void setStateName(StateName.Enum stateName) {
		this.stateName = stateName;
	}

	public SubstateName.Enum getSubstateName() {
		return substateName;
	}

	public void setSubstateName(SubstateName.Enum substateName) {
		this.substateName = substateName;
	}

	public StatusName.Enum getStatusName() {
		return statusName;
	}

	public void setStatusName(StatusName.Enum statusName) {
		this.statusName = statusName;
	}

	public String getStateNameStr() {
		return stateNameStr;
	}

	public void setStateNameStr(String stateNameStr) {
		this.stateNameStr = stateNameStr;
	}

	public String getSubstateNameStr() {
		return substateNameStr;
	}

	public void setSubstateNameStr(String substateNameStr) {
		this.substateNameStr = substateNameStr;
	}

	public String getStatusNameStr() {
		return statusNameStr;
	}

	public void setStatusNameStr(String statusNameStr) {
		this.statusNameStr = statusNameStr;
	}

	public String getIncludedJobsStr() {
		return includedJobsStr;
	}

	public void setIncludedJobsStr(String includedJobsStr) {
		this.includedJobsStr = includedJobsStr;
	}

	public String getOrderByStr() {
		return orderByStr;
	}

	public void setOrderByStr(String orderByStr) {
		this.orderByStr = orderByStr;
	}

	public String getOrderStr() {
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getRoleStr() {
		return roleStr;
	}

	public void setRoleStr(String roleStr) {
		this.roleStr = roleStr;
	}

}
