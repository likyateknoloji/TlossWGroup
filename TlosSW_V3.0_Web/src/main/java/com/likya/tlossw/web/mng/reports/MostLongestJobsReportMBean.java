package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.report.JobArrayDocument.JobArray;
import com.likya.tlos.model.xmlbeans.report.JobDocument.Job;
import com.likya.tlossw.web.db.DBOperations;
import com.likya.tlossw.web.mng.reports.helpers.ReportsParameters;
import com.likya.tlossw.web.utils.ConstantDefinitions;

@ManagedBean(name = "mostLongestJobsReportMBean")
@ViewScoped 
public class MostLongestJobsReportMBean extends ReportBase implements Serializable {
	
	private static final long serialVersionUID = -9017679707795179195L;

	@ManagedProperty(value = "#{dbOperations}")
	private DBOperations dbOperations;
	
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
		
		if (getReportParameters() == null) {
			setReportParameters(new ReportsParameters());
		}
		
		curDurationModel = createCurCategoryModel();
		//prevDurationModel = createCurCategoryModel(1, -1, 0, "true()", "xs:string(\"descending\")", 10);

		logger.info("end : init");

		setActiveReportPanel(ConstantDefinitions.JOB_DURATION_REPORT);
	}

	public void handleReorder(DashboardReorderEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_INFO);
		message.setSummary("Reordered: " + event.getWidgetId());
		message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());
	}
	
	public void refreshCurDurationChart() {

		createCurCategoryModel();
	}

	public void refreshPrevDurationChart() {
		
		createCurCategoryModel();
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

	private CartesianChartModel createCurCategoryModel() {

		getReportParameters().fillReportParameters();
		
		CartesianChartModel curDurationModel = new CartesianChartModel();

//		String includeNonResultedRuns = "true()";
		
		try {
			jobsArray = getDbOperations().getOverallReport( getReportParameters().getReportParametersXML() );
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		ChartSeries jobs = new ChartSeries();
		jobs.setLabel("Jobs");

		BigDecimal dividend = new BigDecimal(1); //new BigDecimal("60");
		
		int i=0;
		if(jobsArray!=null) {
		  for (Job job : jobsArray.getJobArray()) {
			i++;
			BigDecimal figure = null;
			try {
			   figure = job.getBigDecimalValue().divide(dividend, 0).round(new MathContext(2, RoundingMode.HALF_UP)); //setScale(2, RoundingMode.HALF_UP);
			}
			catch(XmlValueOutOfRangeException e){
	            //do something clever with the exception
				figure = new BigDecimal(0);
	            System.out.println(e.getMessage());				
			}
			jobs.set( i + "-" + job.getId() + " " + job.getJname(), figure);
		  }
		}
		setSizeOfReport(jobs.getData().size());
		System.out.println(jobs.getData().size() + " adet data var");
		if (getSizeOfReport()>0) {
			addSuccessMessage("Job Duration Statistics", "tlos.success.report.done", null);
			//addMessage("Job Duration Statistics", FacesMessage.SEVERITY_INFO, "tlos.success.report.done", null);
		} else {
			addFailMessage("Job Duration Statistics", "tlos.error.report.done", null);;
			//addMessage("Job Duration Statistics", FacesMessage.SEVERITY_ERROR, "tlos.error.report.done", null);
		}
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update(":dashboardReports");
		
		curDurationModel.addSeries(jobs);
        return curDurationModel;
	}

	public void refreshReport(ActionEvent actionEvent) {
		curDurationModel = createCurCategoryModel();
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