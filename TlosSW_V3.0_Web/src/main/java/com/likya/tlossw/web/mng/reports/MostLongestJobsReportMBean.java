package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.JobArrayDocument.JobArray;
import com.likya.tlos.model.xmlbeans.report.JobDocument.Job;
import com.likya.tlos.model.xmlbeans.report.OrderByType;
import com.likya.tlos.model.xmlbeans.report.OrderType;
import com.likya.tlos.model.xmlbeans.report.ReportParametersDocument.ReportParameters;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.db.DBOperations;

@ManagedBean(name = "mostLongestJobsReportMBean")
@ViewScoped 
public class MostLongestJobsReportMBean extends TlosSWBaseBean implements Serializable {
	
	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	
	private static final long serialVersionUID = 2570957528954820036L;
	private static final Logger logger = Logger.getLogger(MostLongestJobsReportMBean.class);
 
	private CartesianChartModel curDurationModel;
	private CartesianChartModel prevDurationModel;
 
	private int sizeOfReport;
	
	private JobArray jobsArray;
	
	@PostConstruct
	public void init() {

		logger.info("begin : init");

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String parameter_value = (String) facesContext.getExternalContext().getRequestParameterMap().get("id");

		System.out.println(parameter_value);
		
		ReportParameters reportParameters = ReportParameters.Factory.newInstance();
		
		// int derinlik, int runType, int jobId,  String refPoint, String orderType, int jobCount
		// 1, 0, 0, "true()", "xs:string(\"descending\")", 10);
		reportParameters.setIncludeNonResultedJobs(true);
		reportParameters.setIsCumulative(true);
		reportParameters.setJobId("0");
		reportParameters.setJustFirstLevel(true);
		reportParameters.setMaxNumberOfElement(BigInteger.valueOf(1));
		reportParameters.setMaxNumOfListedJobs(BigInteger.valueOf(10));
		reportParameters.setOrder(OrderType.DESCENDING);
		reportParameters.setOrderBy(OrderByType.DURATION);
		reportParameters.setRefRunIdBoolean(true);
		reportParameters.setRunId(BigInteger.valueOf(0));
		reportParameters.setScenarioId("0");
		
		QName qName = ReportParameters.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String reportParametersXML = reportParameters.xmlText(xmlOptions);
		
		curDurationModel = createCurCategoryModel(reportParametersXML);
		//prevDurationModel = createCurCategoryModel(1, -1, 0, "true()", "xs:string(\"descending\")", 10);

		logger.info("end : init");

	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());

	}
	
	public void refreshCurDurationChart() {

		ReportParameters reportParameters = ReportParameters.Factory.newInstance();
		
		// int derinlik, int runType, int jobId,  String refPoint, String orderType, int jobCount
		// 1, 0, 0, "true()", "xs:string(\"descending\")", 10);
		reportParameters.setIncludeNonResultedJobs(true);
		reportParameters.setIsCumulative(true);
		reportParameters.setJobId("1");
		reportParameters.setJustFirstLevel(true);
		reportParameters.setMaxNumberOfElement(BigInteger.valueOf(1));
		reportParameters.setMaxNumOfListedJobs(BigInteger.valueOf(10));
		reportParameters.setOrder(OrderType.DESCENDING);
		reportParameters.setOrderBy(OrderByType.DURATION);
		reportParameters.setRefRunIdBoolean(true);
		reportParameters.setRunId(BigInteger.valueOf(1));
		reportParameters.setScenarioId("0");
		
		QName qName = ReportParameters.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String reportParametersXML = reportParameters.xmlText(xmlOptions);
		
		createCurCategoryModel(reportParametersXML);
	}

	public void refreshPrevDurationChart() {
		
		ReportParameters reportParameters = ReportParameters.Factory.newInstance();
		
		// int derinlik, int runType, int jobId,  String refPoint, String orderType, int jobCount
		// 1, 0, 0, "true()", "xs:string(\"descending\")", 10);
		reportParameters.setIncludeNonResultedJobs(true);
		reportParameters.setIsCumulative(true);
		reportParameters.setJobId("1");
		reportParameters.setJustFirstLevel(true);
		reportParameters.setMaxNumberOfElement(BigInteger.valueOf(1));
		reportParameters.setMaxNumOfListedJobs(BigInteger.valueOf(10));
		reportParameters.setOrder(OrderType.DESCENDING);
		reportParameters.setOrderBy(OrderByType.DURATION);
		reportParameters.setRefRunIdBoolean(true);
		reportParameters.setRunId(BigInteger.valueOf(1));
		reportParameters.setScenarioId("0");
		
		QName qName = ReportParameters.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);

		String reportParametersXML = reportParameters.xmlText(xmlOptions);
		
		createCurCategoryModel(reportParametersXML);
	}
/**
 * 	get related Jobs with getJobsReport(
            Number of runs that dealt with for the report, 
            Run Id for which we are focusing and take a referans point, if its value is 0 then it means we are focusing the last run,
			 Job Id if we focusing just a job, or enter 0 for all job related with the second argument,
			 true() if we choose the run id as a referance point, false() otherwise
 * 
 *  get job array with requested data with getJobArray(
                 output of the getJobsReport function,
				 "ascending|descending" for ascending or descending ordered jobs based on real work time,
				 number of maksimum jobs in the array
				  Whether unfinished jobs used for stats
 * 
 *  * hs:getJobArray(hs:getJobsReport(1,-1,0, true()),"descending",10, false())
 * @return 
 */

	private CartesianChartModel createCurCategoryModel(String reportParametersXML) {

		CartesianChartModel curDurationModel = new CartesianChartModel();

		String includeNonResultedRuns = "true()";
		
		try {
			jobsArray = getDbOperations().getOverallReport(reportParametersXML);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		ChartSeries jobs = new ChartSeries();
		jobs.setLabel("Jobs");

		BigDecimal dividend = new BigDecimal(1); //new BigDecimal("60");
		  
		if(jobsArray!=null) {
		  for (Job job : jobsArray.getJobArray()) {
			BigDecimal figure = null;
			try {
			   figure = job.getBigDecimalValue().divide(dividend, 0).round(new MathContext(2, RoundingMode.HALF_UP)); //setScale(2, RoundingMode.HALF_UP);
			}
			catch(XmlValueOutOfRangeException e){
	            //do something clever with the exception
				figure = new BigDecimal(0);
	            System.out.println(e.getMessage());				
			}
			jobs.set(job.getJname(), figure);
		  }
		}
		setSizeOfReport(jobs.getData().size());
		System.out.println(jobs.getData().size() + " adet data var");
		if (getSizeOfReport()>0) {
			addMessage("Job Duration Statistics", FacesMessage.SEVERITY_INFO, "tlos.success.dbAccessDef.update", null);
		} else {
			addMessage("Job Duration Statistics", FacesMessage.SEVERITY_ERROR, "tlos.error.dbConnection.update", null);
		}
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update(":dashboardReports");
		
		curDurationModel.addSeries(jobs);
        return curDurationModel;
	}

	public CartesianChartModel getPrevDurationModel() {
		return prevDurationModel;
	}

	public void setPrevDurationModel(CartesianChartModel prevDurationModel) {
		this.prevDurationModel = prevDurationModel;
	}

	public CartesianChartModel getCurDurationModel() {
		return curDurationModel;
	}

	public void setCurDurationModel(CartesianChartModel curDurationModel) {
		this.curDurationModel = curDurationModel;
	}

	public DBOperations getDbOperations() {
		return dbOperations;
	}

	public void setDbOperations(DBOperations dbOperations) {
		this.dbOperations = dbOperations;
	}

	public JobArray getJobsArray() {
		return jobsArray;
	}

	public void setJobsArray(JobArray jobsArray) {
		this.jobsArray = jobsArray;
	}
	
	public int getSizeOfReport() {
		return sizeOfReport;
	}

	public void setSizeOfReport(int sizeOfReport) {
		this.sizeOfReport = sizeOfReport;
	} 

}