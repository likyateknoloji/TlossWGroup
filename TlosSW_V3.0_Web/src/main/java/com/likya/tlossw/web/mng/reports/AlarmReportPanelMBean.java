package com.likya.tlossw.web.mng.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.alarmhistory.AlarmReportDocument.AlarmReport;
import com.likya.tlos.model.xmlbeans.data.JsRealTimeDocument.JsRealTime;
import com.likya.tlos.model.xmlbeans.data.StartTimeDocument.StartTime;
import com.likya.tlos.model.xmlbeans.data.StopTimeDocument.StopTime;
import com.likya.tlossw.model.DocMetaDataHolder;
import com.likya.tlossw.web.mng.alarm.AlarmBaseBean;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.WebAlarmUtils;

@ManagedBean(name = "alarmReportPanelMBean")
@ViewScoped
public class AlarmReportPanelMBean extends AlarmBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(AlarmReportPanelMBean.class);

	private static final long serialVersionUID = -7436267818850177642L;
 
	private ArrayList<AlarmReport> alarmReportList;
	private List<SWAgent> filteredReportAlarms;
	private JsRealTime jsRealTime;
	private transient DataTable alarmReportTable;
	
	@PostConstruct
	public void init() {
		
		logger.info("begin : init");
		
		String docId = getDocId( DocMetaDataHolder.SECOND_COLUMN );
		
		try {
			setAlarmUserList(WebAlarmUtils.fillAlarmUserList(getDbOperations().getUsers()));
			setAlarmNameList(WebAlarmUtils.fillAlarmNameList(getDbOperations().getAlarms()));
			setAlarmRoleList(WebAlarmUtils.fillAlarmRoleList(getDbOperations().getUsers()));
			setAlarmJobNameList(WebAlarmUtils.fillJobsNameList(getDbOperations().getJobList( docId, getWebAppUser().getId(), getSessionMediator().getDocumentScope(docId), 5)));
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		logger.info("end : init");

	}

	public void generateAlarmReportAction(ActionEvent e) {
 
		Calendar cal;

 

		if (getAlarmReportStartDate() == null) {
			cal = Calendar.getInstance();

			if (cal.get(Calendar.MONTH) == 0)
				cal.set(Calendar.MONTH, 11);
			else
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);

			if (cal.get(Calendar.DAY_OF_MONTH) > cal.getActualMaximum(Calendar.DAY_OF_MONTH))
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

			setAlarmReportStartDate(cal.getTime());
		}

		if (getAlarmReportFinishDate() == null) {
			cal = Calendar.getInstance();
			setAlarmReportFinishDate(cal.getTime());
		}

			jsRealTime = JsRealTime.Factory.newInstance();
			StartTime startTime = StartTime.Factory.newInstance();
			startTime.setDate(DefinitionUtils.dateToXmlDate(getAlarmReportStartDate()));

			StopTime stopTime = StopTime.Factory.newInstance();
			stopTime.setDate(DefinitionUtils.dateToXmlDate(getAlarmReportFinishDate()));

			jsRealTime.setStartTime(startTime);
			jsRealTime.setStopTime(stopTime);

		// iki tarih araliginda job'in performansi
		try {
			alarmReportList = getDbOperations().getAlarmReportList(jsRealTime.getStartTime().getDate().toString(), jsRealTime.getStopTime().getDate().toString(), getAlarmLevel(), getAlarmName(), getAlarmUser());
		} catch (XMLDBException e1) {
			e1.printStackTrace();
		}
 
	}
	
	public void resetAlarmReportAction() {
		
		setAlarmNameList(null);
		setAlarmUserList(null);
		setAlarmJobNameList(null);
		setAlarmLevel(null);
		setAlarmReportStartDate(null);
		setAlarmReportFinishDate(null);
		setAlarmUser(null);
		setAlarmName(null);
		setAlarmReportJob(null);
		
		String docId = getDocId( DocMetaDataHolder.SECOND_COLUMN );
		
		try {
			setAlarmUserList(WebAlarmUtils.fillAlarmUserList(getDbOperations().getUsers()));
			setAlarmNameList(WebAlarmUtils.fillAlarmNameList(getDbOperations().getAlarms()));
			setAlarmRoleList(WebAlarmUtils.fillAlarmRoleList(getDbOperations().getUsers()));
			setAlarmJobNameList(WebAlarmUtils.fillJobsNameList(getDbOperations().getJobList( docId, getWebAppUser().getId(), getSessionMediator().getDocumentScope(docId), 5)));
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		
	}
	
	 
	public List<SWAgent> getFilteredReportAlarms() {
		return filteredReportAlarms;
	}

	public void setFilteredReportAlarms(List<SWAgent> filteredReportAlarms) {
		this.filteredReportAlarms = filteredReportAlarms;
	}

	public ArrayList<AlarmReport> getAlarmReportList() {
		return alarmReportList;
	}

	public void setAlarmReportList(ArrayList<AlarmReport> alarmReportList) {
		this.alarmReportList = alarmReportList;
	}

	public JsRealTime getJsRealTime() {
		return jsRealTime;
	}

	public void setJsRealTime(JsRealTime jsRealTime) {
		this.jsRealTime = jsRealTime;
	}

	public DataTable getAlarmReportTable() {
		return alarmReportTable;
	}

	public void setAlarmReportTable(DataTable alarmReportTable) {
		this.alarmReportTable = alarmReportTable;
	}

 

}
