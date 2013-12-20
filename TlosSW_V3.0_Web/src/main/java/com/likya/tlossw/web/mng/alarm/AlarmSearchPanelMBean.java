package com.likya.tlossw.web.mng.alarm;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.xmldb.api.base.XMLDBException;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.alarm.AlarmTypeDocument.AlarmType;
import com.likya.tlos.model.xmlbeans.alarm.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.alarm.SubscriberDocument.Subscriber;
import com.likya.tlos.model.xmlbeans.alarm.SubscriptionTypeDocument.SubscriptionType;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.WebAlarmUtils;

@ManagedBean(name = "alarmSearchPanelMBean")
@ViewScoped
public class AlarmSearchPanelMBean extends AlarmBaseBean implements Serializable {

	private static final Logger logger = Logger.getLogger(AlarmSearchPanelMBean.class);

	private static final long serialVersionUID = -7436267818850177642L;

	private List<SWAgent> filteredAlarms;

	@PostConstruct
	public void init() {
		
		logger.info("begin : init");
		
		setPassedParameters();
		
		setAlarmType(AlarmType.JOB.toString());
		setUserType(SubscriptionType.USER.toString());

		setAlarmUserList(WebAlarmUtils.fillAlarmUserList(getDbOperations().getUsers()));
		setAlarmNameList(WebAlarmUtils.fillAlarmNameList(getDbOperations().getAlarms()));

		logger.info("end : init");

	}

	public void searchAlarmAction(ActionEvent e) {

		setAlarm(DefinitionUtils.getAlarmInstance(getStartDate(), getEndDate()));

		Subscriber subscriber = Subscriber.Factory.newInstance();
		Person apers = Person.Factory.newInstance();

		if (getAlarmUser() != null && !getAlarmUser().equals("")) {
			apers.setId(new BigInteger(getAlarmUser()));
		} else {
			apers.setId(new BigInteger("-1"));
		}
		
		subscriber.addNewPerson();
		subscriber.setPerson(apers);
		getAlarm().setSubscriber(subscriber);

		if (getAlarmName() != null && !getAlarmName().equals("")) {
			getAlarm().setName(getAlarmName());
		} else {
			getAlarm().setName(null);
		}

		try {
			setSearchAlarmList(getDbOperations().searchAlarm(getAlarmXML()));
		} catch (XMLDBException e1) {
			e1.printStackTrace();
		}
		if (getSearchAlarmList() == null || getSearchAlarmList().size() == 0) {
			addMessage("searchAlarm", FacesMessage.SEVERITY_ERROR, "tlos.info.search.noRecord", null);
		}
	}

	public void deleteAlarmAction(ActionEvent e) {
		// setAlarm((Alarm) getSearchAlarmTable().getRowData());
		setAlarm(getSelectedRow());

		if (getDbOperations().deleteAlarm(getAlarmXML())) {
			getSearchAlarmList().remove(getAlarm());
			addMessage("searchAlarm", FacesMessage.SEVERITY_INFO, "tlos.success.alarm.delete", null);
		} else {
			addMessage("searchAlarm", FacesMessage.SEVERITY_ERROR, "tlos.success.alarm.delete", null);
		}
	}
	
	public void resetAlarmAction() {
		setSearchAlarmList(null);
		setAlarmName(null);
		setAlarmUserList(null);
		setStartDate(null);
		setEndDate(null);
		setAlarmUserList(WebAlarmUtils.fillAlarmUserList(getDbOperations().getUsers()));
		setAlarmUser("");
	}
	
	public String updateAlarm() {
		setAlarm((Alarm) getSearchAlarmTable().getRowData());
		return "alarmPanel.jsf"; //?selectedAlarmName=" + getAlarm().getName() + "&insertCheck=update&faces-redirect=true";
	}

	public List<SWAgent> getFilteredAlarms() {
		return filteredAlarms;
	}

	public void setFilteredAlarms(List<SWAgent> filteredAlarms) {
		this.filteredAlarms = filteredAlarms;
	}

}
