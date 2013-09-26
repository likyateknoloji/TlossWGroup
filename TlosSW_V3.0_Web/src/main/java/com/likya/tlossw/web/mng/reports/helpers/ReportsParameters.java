package com.likya.tlossw.web.mng.reports.helpers;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

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

	/**
	 * 
	 */
	private static final long serialVersionUID = -2448886436692898113L;

	private DBOperations dbOperations;
	
	String reportParametersXML = null;
	
	/* user inputs */
	private Boolean includeNonResultedJobs;
	private String jobId;
	private Boolean justFirstLevel;
	private BigInteger maxNumberOfElement;
	private Boolean refRunIdBoolean;
	private BigInteger runId;
	private String scenarioId;
	private String orderBy;
	private Boolean isCumulative;
	private String order;
	private BigInteger maxNumOfListedJobs;
	private Short statSampleNumber;
	private LocalStats statParameters = null;
	private String includedJobs;
	private Short maxNumberOfIntervals;
	private Date startDate;
	private String startTime;
	private Date endDate;
	private String endTime;
	private LiveStateInfo liveStateInfo;
	private String stepForDensity;
	
	/* computed */

	
	public ReportsParameters() {
		// TODO Auto-generated constructor stub
		ReportParameters reportParameters = ReportParameters.Factory.newInstance();
		
		includeNonResultedJobs = true;
		jobId = "0";
		justFirstLevel = false;
		maxNumberOfElement = BigInteger.valueOf(1);
		refRunIdBoolean = true;
		runId = new BigInteger("0");
		scenarioId = "0";
		orderBy = OrderByType.DURATION.toString();
		isCumulative = false;
		order = OrderType.DESCENDING.toString();
		maxNumOfListedJobs = new BigInteger("11");
		statSampleNumber = 3;
		
		QName qName = ReportParameters.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		Calendar startDate = com.likya.tlossw.web.utils.DefinitionUtils.stringToCalendar(new String("2013/09/19 22:00:01"), new String("yyyy/MM/dd HH:mm:ss"), "Europe/Istanbul");
		Calendar endDate = com.likya.tlossw.web.utils.DefinitionUtils.stringToCalendar(new String("2013/09/19 23:00:01"), new String("yyyy/MM/dd HH:mm:ss"),"Europe/Istanbul");

		Calendar stepForDensity = com.likya.tlossw.web.utils.DefinitionUtils.intervalCalendar(new String("1970-01-01T00:00:30"), new String("yyyy-MM-dd'T'HH:mm:ss"), "GMT-0:00");
	      
		LiveStateInfo liveStateInfo = LiveStateInfo.Factory.newInstance();
		
		liveStateInfo.setStateName(StateName.RUNNING);
		liveStateInfo.setSubstateName(SubstateName.ON_RESOURCE);
		liveStateInfo.setStatusName(StatusName.TIME_IN);
		reportParameters.setLiveStateInfo(liveStateInfo);
		
		reportParameters.setIncludedJobs(FilterByResult.RESULTED);
		reportParameters.setIncludeNonResultedJobs(true);
		reportParameters.setStartDateTime(startDate);
		reportParameters.setEndDateTime(endDate);
		reportParameters.setStepForDensity(stepForDensity);
		reportParameters.setJobId(jobId);
		reportParameters.setJustFirstLevel(justFirstLevel);
		reportParameters.setMaxNumberOfElement(maxNumberOfElement);
		reportParameters.setRefRunIdBoolean(refRunIdBoolean);
		reportParameters.setRunId(runId);
		reportParameters.setScenarioId(scenarioId);
		reportParameters.setOrderBy(OrderByType.Enum.forString(orderBy));
		reportParameters.setIsCumulative(isCumulative);
		reportParameters.setOrder(OrderType.Enum.forString(order));
		reportParameters.setMaxNumOfListedJobs(maxNumOfListedJobs);
		reportParameters.setStatSampleNumber(statSampleNumber);
		reportParameters.setMaxNumberOfIntervals((short) 100);
		
		reportParametersXML = reportParameters.xmlText(xmlOptions);
	}


	public void setStatParameters() {
	
		try {
			statParameters = getDbOperations().getStatsReport( getReportParametersXML() );
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

	}
	
	public String getReportParametersXML() {
		return reportParametersXML;
	}

	public void setReportParametersXML(String reportParametersXML) {
		this.reportParametersXML = reportParametersXML;
	}

	public Boolean getIncludeNonResultedJobs() {
		return includeNonResultedJobs;
	}

	public void setIncludeNonResultedJobs(Boolean includeNonResultedJobs) {
		this.includeNonResultedJobs = includeNonResultedJobs;
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


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public LiveStateInfo getLiveStateInfo() {
		return liveStateInfo;
	}


	public void setLiveStateInfo(LiveStateInfo liveStateInfo) {
		this.liveStateInfo = liveStateInfo;
	}


	public String getStepForDensity() {
		return stepForDensity;
	}


	public void setStepForDensity(String stepForDensity) {
		this.stepForDensity = stepForDensity;
	}

}
