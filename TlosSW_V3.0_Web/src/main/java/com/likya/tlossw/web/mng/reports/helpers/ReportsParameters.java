package com.likya.tlossw.web.mng.reports.helpers;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.FilterByResult;
import com.likya.tlos.model.xmlbeans.report.LocalStatsDocument.LocalStats;
import com.likya.tlos.model.xmlbeans.report.OrderByType;
import com.likya.tlos.model.xmlbeans.report.OrderType;
import com.likya.tlos.model.xmlbeans.report.ReportParametersDocument.ReportParameters;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.db.DBOperations;

public class ReportsParameters implements Serializable {

	private static final long serialVersionUID = -2448886436692898113L;

	private DBOperations dbOperations;

	private ReportParameters reportParams;

	String reportParametersXML = null;
	private Calendar startDateCalendar;
	private Calendar endDateCalendar;
	private Calendar stepForDensityCalendar;
	private LiveStateInfo liveStateInfo;
	
	/* user inputs */
	private String includedJobs = FilterByResult.RESULTED.toString();
	private Boolean includePendingJobs = true;
	private String jobId = "0";
	private Boolean justFirstLevel = false;
	private BigInteger maxNumberOfElement = BigInteger.valueOf(1);;
	private Boolean refRunIdBoolean = true;
	private BigInteger runId = BigInteger.valueOf(0);
	private String scenarioId = "0";
	private String orderBy = OrderByType.DURATION.toString();
	private Boolean isCumulative = false;
	private String order = OrderType.DESCENDING.toString();
	private BigInteger maxNumOfListedJobs = new BigInteger("11");
	private Short statSampleNumber = 3;
	private Short maxNumberOfIntervals = (short) 100;
	private Boolean automaticTimeInterval = true;
	private String typeOfTime;
	private String timeZone = "Europe/Istanbul";
	
	private LocalStats statParameters = null;
	private String zuluTZone = "UTC";



	
	/* computed */

	public ReportsParameters() {
		// TODO Auto-generated constructor stub
		reportParams = ReportParameters.Factory.newInstance();

		setStartDateCalendar(com.likya.tlossw.web.utils.DefinitionUtils.stringToCalendar(new String("2013/09/19 22:00:01"), new String("yyyy/MM/dd HH:mm:ss"), timeZone));
		setEndDateCalendar(com.likya.tlossw.web.utils.DefinitionUtils.stringToCalendar(new String("2013/09/19 23:00:01"), new String("yyyy/MM/dd HH:mm:ss"), timeZone));
		setStepForDensityCalendar(com.likya.tlossw.web.utils.DefinitionUtils.dateTimeToXmlDateTime(new String("1970-01-01"), new String("00:00:30"), zuluTZone));
		
		//String timeInputFormat = new String("HH:mm:ss.SSSZZ");

		//stepForDensity = com.likya.tlossw.web.utils.DefinitionUtils.calendarToStringTimeFormat(stepForDensityCalendar, "UTC", timeInputFormat);

		typeOfTime = new String("Actual");
		
		LiveStateInfo liveStateInfo = LiveStateInfo.Factory.newInstance();

		liveStateInfo.setStateName(StateName.RUNNING);
		liveStateInfo.setSubstateName(SubstateName.ON_RESOURCE);
		liveStateInfo.setStatusName(StatusName.TIME_IN);
		
		reportParams.setLiveStateInfo(liveStateInfo);

		reportParams.setStartDateTime(startDateCalendar);
		reportParams.setEndDateTime(endDateCalendar);
		reportParams.setStepForDensity(stepForDensityCalendar);

		fillReportParameters();
	}

	private void fillReportParameters() {


		//setStepForDensityCalendar(DefinitionUtils.dateTimeToXmlDateTime(new String("1970-01-01"), stepForDensity, zuluTZone));
		
		//String timeInputFormat = new String("HH:mm:ss.SSSZZ");

		//stepForDensity = com.likya.tlossw.web.utils.DefinitionUtils.calendarToStringTimeFormat(stepForDensityCalendar, "UTC", timeInputFormat);

		LiveStateInfo liveStateInfo = LiveStateInfo.Factory.newInstance();

		typeOfTime = new String("Actual");
		//reportParams.getLiveStateInfo().getStateName()
		if(reportParams.getLiveStateInfo()!=null) {
			//if(reportParams.getLiveStateInfo().getStateName() != null) 
			liveStateInfo.setStateName(StateName.RUNNING);
			liveStateInfo.setSubstateName(SubstateName.ON_RESOURCE);
			liveStateInfo.setStatusName(StatusName.TIME_IN);	
		} else {
			liveStateInfo.setStateName(StateName.RUNNING);
			liveStateInfo.setSubstateName(SubstateName.ON_RESOURCE);
			liveStateInfo.setStatusName(StatusName.TIME_IN);			
		}

		reportParams.setLiveStateInfo(liveStateInfo);

		reportParams.setStartDateTime(startDateCalendar);
		reportParams.setEndDateTime(endDateCalendar);
		reportParams.setStepForDensity(stepForDensityCalendar);
		
		reportParams.setIncludedJobs(FilterByResult.Enum.forString(includedJobs));
		reportParams.setIncludePendingJobs(includePendingJobs);
		reportParams.setJobId(jobId);
		reportParams.setJustFirstLevel(justFirstLevel);
		reportParams.setMaxNumberOfElement(maxNumberOfElement);
		reportParams.setRefRunIdBoolean(refRunIdBoolean);
		reportParams.setRunId(runId);
		reportParams.setScenarioId(scenarioId);
		reportParams.setOrderBy(OrderByType.Enum.forString(orderBy));
		reportParams.setIsCumulative(isCumulative);
		reportParams.setOrder(OrderType.Enum.forString(order));
		reportParams.setMaxNumOfListedJobs(maxNumOfListedJobs);
		reportParams.setStatSampleNumber(statSampleNumber);
		reportParams.setMaxNumberOfIntervals(maxNumberOfIntervals);
	}

	public void setStatParameters() {

		try {
			statParameters = getDbOperations().getStatsReport(getReportParametersXML());
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

	}

	public String getReportParametersXML() {

		fillReportParameters();

		QName qName = ReportParameters.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		reportParametersXML = reportParams.xmlText(xmlOptions);

		return reportParametersXML;
	}

	public void setReportParametersXML(String reportParametersXML) {
		this.reportParametersXML = reportParametersXML;
	}

	public Boolean getIncludePendingJobs() {
		return includePendingJobs;
	}

	public void setIncludePendingJobs(Boolean includePendingJobs) {
		this.includePendingJobs = includePendingJobs;
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

	public BigInteger getMaxNumberOfElement() {
		return maxNumberOfElement;
	}

	public void setMaxNumberOfElement(BigInteger maxNumberOfElement) {
		this.maxNumberOfElement = maxNumberOfElement;
	}

	public Boolean getRefRunIdBoolean() {
		return refRunIdBoolean;
	}

	public void setRefRunIdBoolean(Boolean refRunIdBoolean) {
		this.refRunIdBoolean = refRunIdBoolean;
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

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getIncludedJobs() {
		return includedJobs;
	}

	public void setIncludedJobs(String includedJobs) {
		this.includedJobs = includedJobs;
	}

	public Short getMaxNumberOfIntervals() {
		return maxNumberOfIntervals;
	}

	public void setMaxNumberOfIntervals(Short maxNumberOfIntervals) {
		this.maxNumberOfIntervals = maxNumberOfIntervals;
	}

	public LiveStateInfo getLiveStateInfo() {
		return liveStateInfo;
	}

	public void setLiveStateInfo(LiveStateInfo liveStateInfo) {
		this.liveStateInfo = liveStateInfo;
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

	public String getTypeOfTime() {
		return typeOfTime;
	}

	public void setTypeOfTime(String typeOfTime) {
		this.typeOfTime = typeOfTime;
	}

	public Boolean getAutomaticTimeInterval() {
		return automaticTimeInterval;
	}

	public void setAutomaticTimeInterval(Boolean automaticTimeInterval) {
		this.automaticTimeInterval = automaticTimeInterval;
	}
}
