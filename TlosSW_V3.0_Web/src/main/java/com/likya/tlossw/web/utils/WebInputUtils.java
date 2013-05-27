package com.likya.tlossw.web.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import javax.faces.model.SelectItem;

import org.joda.time.DateTimeZone;
import org.ogf.schemas.rns.x2009.x12.rns.RNSEntryType;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.alarm.AlarmDocument.Alarm;
import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.common.AgentChoiceMethodDocument;
import com.likya.tlos.model.xmlbeans.common.EventTypeDefDocument.EventTypeDef;
import com.likya.tlos.model.xmlbeans.common.JobBaseTypeDocument.JobBaseType;
import com.likya.tlos.model.xmlbeans.common.JobTypeDefDocument.JobTypeDef;
import com.likya.tlos.model.xmlbeans.common.RoleDocument.Role;
import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlos.model.xmlbeans.common.UnitDocument.Unit;
import com.likya.tlos.model.xmlbeans.data.JsRelativeTimeOptionDocument.JsRelativeTimeOption;
import com.likya.tlos.model.xmlbeans.data.OSystemDocument.OSystem;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlos.model.xmlbeans.dbconnections.DbPropertiesDocument.DbProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbJobTypeDocument.DbJobType;
import com.likya.tlos.model.xmlbeans.fileadapter.BinaryFileDetailOptions;
import com.likya.tlos.model.xmlbeans.fileadapter.TextFileDetailOptions;
import com.likya.tlos.model.xmlbeans.ftpadapter.AdapterTypeDocument.AdapterType;
import com.likya.tlos.model.xmlbeans.ftpadapter.FileModificationTimeDocument.FileModificationTime;
import com.likya.tlos.model.xmlbeans.ftpadapter.AuthenticationTypeDocument;
import com.likya.tlos.model.xmlbeans.ftpadapter.FileTypeDocument;
import com.likya.tlos.model.xmlbeans.ftpadapter.TransportProviderDocument;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpPropertiesDocument.FtpProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.OperationTypeDocument.OperationType;
import com.likya.tlos.model.xmlbeans.ftpadapter.ProcessedFilesOperationTypeDocument.ProcessedFilesOperationType;
import com.likya.tlos.model.xmlbeans.listener.PollingTypeDocument.PollingType;
import com.likya.tlos.model.xmlbeans.processnode.ProcessDocument.Process.Source;
import com.likya.tlos.model.xmlbeans.sla.SLADocument.SLA;
import com.likya.tlos.model.xmlbeans.state.ReturnCodeDocument.ReturnCode;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.Status;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlos.model.xmlbeans.user.PersonDocument.Person;
import com.likya.tlos.model.xmlbeans.webservice.WebServiceDefinitionDocument.WebServiceDefinition;

public class WebInputUtils {


	
	public static Collection<SelectItem> fillTZList() {
		Collection<SelectItem> tZList = null;
		//Collection<SelectItem> tZList2 = null;
		String day = null;
		
		Set<String> availId2 = DateTimeZone.getAvailableIDs();
		tZList = new ArrayList<SelectItem>();
		// checking available Ids

	      Iterator<String> itr = availId2.iterator();
	      
	      while(itr.hasNext()) {
	         Object element = itr.next();
	         SelectItem item2 = new SelectItem();
				day = element.toString();
				item2.setValue(day);
				item2.setLabel(day);
				tZList.add(item2);
	         //System.out.print(element + " ");
	      }
	      
//		tZList = new ArrayList<SelectItem>();
//		// getting available Ids
//		String[] availId = TimeZone.getAvailableIDs();
//
//		// checking available Ids
//		System.out.println("Available Ids are: ");
//		for (int i = 0; i < availId.length; i++) {
//			//System.out.println(availId[i]);
//			SelectItem item = new SelectItem();
//			day = availId[i];
//			item.setValue(day);
//			item.setLabel(day);
//			tZList.add(item);
//		}
		return tZList;
	}
	
	public static Collection<SelectItem> fillTypesOfTimeList() {
		String tot = null;

		Collection<SelectItem> typeOfTimeList = new ArrayList<SelectItem>();

		for (int i = 0; i < TypeOfTime.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			tot = TypeOfTime.Enum.table.forInt(i + 1).toString();
			item.setValue(tot);
			item.setLabel(tot);
			typeOfTimeList.add(item);
		}
		return typeOfTimeList;
	}
	
	public static Collection<SelectItem> fillCalendarList(ArrayList<CalendarProperties> calendarList) {
		Collection<SelectItem> jsCalendarList = new ArrayList<SelectItem>();

		for (CalendarProperties calendar : calendarList) {
			SelectItem item = new SelectItem();
			item.setValue(calendar.getId() + "");
			item.setLabel(calendar.getCalendarName());
			jsCalendarList.add(item);
		}

		return jsCalendarList;
	}
	
	public static Collection<SelectItem> fillAlarmList(ArrayList<Alarm> alarmList) {
		Collection<SelectItem> jsAlarmList = new ArrayList<SelectItem>();

		for (Alarm alarm : alarmList) {
			SelectItem item = new SelectItem();
			item.setValue(alarm.getID() + "");
			item.setLabel(alarm.getName() + ": " + alarm.getDesc());
			item.setDescription(alarm.getDesc());
			jsAlarmList.add(item);
		}

		return jsAlarmList;
	}

	public static Collection<SelectItem> fillAgentList(ArrayList<SWAgent> agentList) {
		Collection<SelectItem> jsAgentList = new ArrayList<SelectItem>();

		for (SWAgent agent : agentList) {
			SelectItem item = new SelectItem();
			item.setValue(agent.getId() + "");
			item.setLabel(agent.getResource().getStringValue() + "." + agent.getId());
			jsAgentList.add(item);
		}

		return jsAgentList;
	}

	public static Collection<SelectItem> fillSLAList(ArrayList<SLA> slaList) {
		Collection<SelectItem> jsSlaList = new ArrayList<SelectItem>();

		for (SLA sla : slaList) {
			SelectItem item = new SelectItem();
			item.setValue(sla.getID() + "");
			item.setLabel(sla.getName() + ": " + sla.getDesc());
			jsSlaList.add(item);
		}

		return jsSlaList;
	}

	public static Collection<SelectItem> fillResourceNameList(ArrayList<RNSEntryType> resources) {
		Collection<SelectItem> resourceNameList = new ArrayList<SelectItem>();

		for (RNSEntryType resource : resources) {
			SelectItem item = new SelectItem();
			item.setValue(resource.getEntryName());
			item.setLabel(resource.getEntryName());
			resourceNameList.add(item);
		}

		return resourceNameList;
	}

	public static Collection<SelectItem> fillWebServiceDefinitionList(ArrayList<WebServiceDefinition> wsList) {
		Collection<SelectItem> webServiceList = new ArrayList<SelectItem>();

		for (WebServiceDefinition webService : wsList) {
			SelectItem item = new SelectItem();
			item.setValue(webService.getID() + "");
			item.setLabel(webService.getServiceName() + "." + webService.getID());
			webServiceList.add(item);
		}

		return webServiceList;
	}

	public static Collection<SelectItem> fillOSystemList() {
		String oSystem = null;

		Collection<SelectItem> oSystemList = new ArrayList<SelectItem>();

		for (int i = 0; i < OSystem.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			oSystem = OSystem.Enum.table.forInt(i + 1).toString();
			item.setValue(oSystem);
			item.setLabel(oSystem);
			oSystemList.add(item);
		}

		return oSystemList;
	}

	public static Collection<SelectItem> fillEventTypeDefList() {
		String eventTypeDef = null;

		Collection<SelectItem> eventTypeDefList = new ArrayList<SelectItem>();

		for (int i = 0; i < EventTypeDef.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			eventTypeDef = EventTypeDef.Enum.table.forInt(i + 1).toString();
			item.setValue(eventTypeDef);
			item.setLabel(eventTypeDef);
			eventTypeDefList.add(item);
		}

		return eventTypeDefList;
	}

	public static Collection<SelectItem> fillJobBaseTypeList() {
		String jobBaseType = null;

		Collection<SelectItem> jobBaseTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < JobBaseType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			jobBaseType = JobBaseType.Enum.table.forInt(i + 1).toString();
			item.setValue(jobBaseType);
			item.setLabel(jobBaseType);
			jobBaseTypeList.add(item);
		}

		return jobBaseTypeList;
	}

	public static Collection<SelectItem> fillJobTypeDefList() {
		String jobTypeDef = null;

		Collection<SelectItem> jobTypeDefList = new ArrayList<SelectItem>();

		for (int i = 0; i < JobTypeDef.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			jobTypeDef = JobTypeDef.Enum.table.forInt(i + 1).toString();
			item.setValue(jobTypeDef);
			item.setLabel(jobTypeDef);
			jobTypeDefList.add(item);
		}

		return jobTypeDefList;
	}

	public static Collection<SelectItem> fillAgentChoiceMethodList() {
		String choiceMethod = null;

		Collection<SelectItem> agentChoiceMethodList = new ArrayList<SelectItem>();

		for (int i = 0; i < AgentChoiceMethodDocument.AgentChoiceMethod.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			choiceMethod = AgentChoiceMethodDocument.AgentChoiceMethod.Enum.forInt(i + 1).toString();
			item.setValue(choiceMethod);
			item.setLabel(choiceMethod);
			agentChoiceMethodList.add(item);
		}

		return agentChoiceMethodList;
	}

	public static Collection<SelectItem> fillRelativeTimeOptionList() {
		String relativeTimeOption = null;

		Collection<SelectItem> relativeTimeOptionList = new ArrayList<SelectItem>();

		for (int i = 0; i < JsRelativeTimeOption.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			relativeTimeOption = JsRelativeTimeOption.Enum.forInt(i + 1).toString();
			item.setValue(relativeTimeOption);
			item.setLabel(relativeTimeOption);
			relativeTimeOptionList.add(item);
		}

		return relativeTimeOptionList;
	}

	public static Collection<SelectItem> fillUnitTypeList() {
		String unitType = null;

		Collection<SelectItem> unitTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < Unit.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			unitType = Unit.Enum.forInt(i + 1).toString();
			item.setValue(unitType);
			item.setLabel(unitType);
			unitTypeList.add(item);
		}

		return unitTypeList;
	}

	public static Collection<SelectItem> fillJobStateList() {
		String stateName = null;

		Collection<SelectItem> jobStateNameList = new ArrayList<SelectItem>();

		for (int i = 0; i < StateName.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			stateName = StateName.Enum.table.forInt(i + 1).toString();
			item.setValue(stateName);
			item.setLabel(stateName);
			jobStateNameList.add(item);
		}

		return jobStateNameList;
	}

	public static Collection<SelectItem> fillJobSubstateList() {
		String substateName = null;

		Collection<SelectItem> jobSubstateNameList = new ArrayList<SelectItem>();

		for (int i = 0; i < SubstateName.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			substateName = SubstateName.Enum.table.forInt(i + 1).toString();
			item.setValue(substateName);
			item.setLabel(substateName);
			jobSubstateNameList.add(item);
		}

		return jobSubstateNameList;
	}

	public static Collection<SelectItem> fillJobStatusList() {
		String statusName = null;

		Collection<SelectItem> jobStatusNameList = new ArrayList<SelectItem>();

		for (int i = 0; i < StatusName.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			statusName = StatusName.Enum.table.forInt(i + 1).toString();
			item.setValue(statusName);
			item.setLabel(statusName);
			jobStatusNameList.add(item);
		}

		return jobStatusNameList;
	}

	public static ReturnCode cloneReturnCode(ReturnCode returnCode) {
		ReturnCode tmpReturnCode = ReturnCode.Factory.newInstance();
		tmpReturnCode.setCode(returnCode.getCode());
		tmpReturnCode.setDesc(returnCode.getDesc());
		tmpReturnCode.setCdId(returnCode.getCdId());

		return tmpReturnCode;
	}

	public static Status cloneJobStatus(Status jobStatus) {
		Status tmpJobStatus = Status.Factory.newInstance();
		tmpJobStatus.setStatusName(jobStatus.getStatusName());
		tmpJobStatus.setDesc(jobStatus.getDesc());
		tmpJobStatus.setReturnCodeListArray(jobStatus.getReturnCodeListArray());
		tmpJobStatus.setStsId(jobStatus.getStsId());

		return tmpJobStatus;
	}

	public static Collection<SelectItem> fillAdapterTypeList() {
		String adapterType = null;

		Collection<SelectItem> adapterTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < AdapterType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			adapterType = AdapterType.Enum.table.forInt(i + 1).toString();
			item.setValue(adapterType);
			item.setLabel(adapterType);
			adapterTypeList.add(item);
		}

		return adapterTypeList;
	}

	public static Collection<SelectItem> fillFileAdapterTypeList() {
		String fileAdapterType = null;

		Collection<SelectItem> fileAdapterTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < com.likya.tlos.model.xmlbeans.fileadapter.AdapterTypeDocument.AdapterType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			fileAdapterType = com.likya.tlos.model.xmlbeans.fileadapter.AdapterTypeDocument.AdapterType.Enum.table.forInt(i + 1).toString();
			item.setValue(fileAdapterType);
			item.setLabel(fileAdapterType);
			fileAdapterTypeList.add(item);
		}

		return fileAdapterTypeList;
	}

	public static Collection<SelectItem> fillTextFileDetailOptions() {
		String textFileDetail = null;

		Collection<SelectItem> textFileDetailList = new ArrayList<SelectItem>();

		for (int i = 0; i < TextFileDetailOptions.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			textFileDetail = TextFileDetailOptions.Enum.table.forInt(i + 1).toString();
			item.setValue(textFileDetail);
			item.setLabel(textFileDetail);
			textFileDetailList.add(item);
		}

		return textFileDetailList;
	}

	public static Collection<SelectItem> fillBinaryFileDetailOptions() {
		String binaryFileDetail = null;

		Collection<SelectItem> binaryFileDetailList = new ArrayList<SelectItem>();

		for (int i = 0; i < BinaryFileDetailOptions.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			binaryFileDetail = BinaryFileDetailOptions.Enum.table.forInt(i + 1).toString();
			item.setValue(binaryFileDetail);
			item.setLabel(binaryFileDetail);
			binaryFileDetailList.add(item);
		}

		return binaryFileDetailList;
	}

	public static Collection<SelectItem> fillOperationTypeList() {
		String operationType = null;

		Collection<SelectItem> operationTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < OperationType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			operationType = OperationType.Enum.table.forInt(i + 1).toString();
			item.setValue(operationType);
			item.setLabel(operationType);
			operationTypeList.add(item);
		}

		return operationTypeList;
	}

	public static Collection<SelectItem> fillProcessedFilesOperationTypeList() {
		String operationType = null;

		Collection<SelectItem> operationTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < ProcessedFilesOperationType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			operationType = ProcessedFilesOperationType.Enum.table.forInt(i + 1).toString();
			item.setValue(operationType);
			item.setLabel(operationType);
			operationTypeList.add(item);
		}

		return operationTypeList;
	}

	public static Collection<SelectItem> fillFileTypeList() {
		String fileType = null;

		Collection<SelectItem> fileTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < FileTypeDocument.FileType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			fileType = FileTypeDocument.FileType.Enum.forInt(i + 1).toString();
			item.setValue(fileType);
			item.setLabel(fileType);
			fileTypeList.add(item);
		}

		return fileTypeList;
	}

	public static Collection<SelectItem> fillFileModificationTimeList() {
		String fileModificationTime = null;

		Collection<SelectItem> fileModificationTimeList = new ArrayList<SelectItem>();

		for (int i = 0; i < FileModificationTime.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			fileModificationTime = FileModificationTime.Enum.forInt(i + 1).toString();
			item.setValue(fileModificationTime);
			item.setLabel(fileModificationTime);
			fileModificationTimeList.add(item);
		}

		return fileModificationTimeList;
	}

	public static Collection<SelectItem> fillFtpConnectionDefinitionList(ArrayList<FtpProperties> ftpPropertiesList) {
		Collection<SelectItem> definitionList = new ArrayList<SelectItem>();

		for (FtpProperties ftpProperties : ftpPropertiesList) {
			SelectItem item = new SelectItem();
			item.setValue(ftpProperties.getId() + "");
			item.setLabel(ftpProperties.getConnection().getConnName() + "." + ftpProperties.getId());
			definitionList.add(item);
		}

		return definitionList;
	}

	public static Collection<SelectItem> fillTextFileOperationTypeList() {
		String fileOperationType = null;

		Collection<SelectItem> fileOperationTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < com.likya.tlos.model.xmlbeans.fileadapter.OperationTypeDocument.OperationType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			fileOperationType = com.likya.tlos.model.xmlbeans.fileadapter.OperationTypeDocument.OperationType.Enum.table.forInt(i + 1).toString();
			item.setValue(fileOperationType);
			item.setLabel(fileOperationType);
			fileOperationTypeList.add(item);
		}

		return fileOperationTypeList;
	}

	public static Collection<SelectItem> fillBinaryFileOperationTypeList() {
		String fileOperationType = null;

		Collection<SelectItem> fileOperationTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < com.likya.tlos.model.xmlbeans.fileadapter.OperationTypeDocument.OperationType.Enum.table.lastInt(); i++) {
			fileOperationType = com.likya.tlos.model.xmlbeans.fileadapter.OperationTypeDocument.OperationType.Enum.table.forInt(i + 1).toString();

			// Update Record, Insert Record, Delete Record islemleri sadece
			// text file islemlerinde var
			if (!fileOperationType.contains("Record")) {
				SelectItem item = new SelectItem();

				item.setValue(fileOperationType);
				item.setLabel(fileOperationType);
				fileOperationTypeList.add(item);
			}
		}

		return fileOperationTypeList;
	}

	public static Collection<SelectItem> fillPollingTypeList() {
		String pollingType = null;

		Collection<SelectItem> pollingTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < PollingType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			pollingType = PollingType.Enum.forInt(i + 1).toString();
			item.setValue(pollingType);
			item.setLabel(pollingType);
			pollingTypeList.add(item);
		}

		return pollingTypeList;
	}

	public static Collection<SelectItem> fillDBJobTypeList() {
		String dbJobType = null;

		Collection<SelectItem> dbJobTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < DbJobType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			dbJobType = DbJobType.Enum.table.forInt(i + 1).toString();
			item.setValue(dbJobType);
			item.setLabel(dbJobType);
			dbJobTypeList.add(item);
		}

		return dbJobTypeList;
	}

	public static Collection<SelectItem> fillDbDefinitionList(ArrayList<DbConnectionProfile> dbAccessProfileList, ArrayList<DbProperties> dbList) {
		Collection<SelectItem> definitionList = new ArrayList<SelectItem>();

		for (DbConnectionProfile dbProfiles : dbAccessProfileList) {
			String id = dbProfiles.getDbDefinitionId() + "";

			for (DbProperties dbProperties : dbList) {
				String dbId = dbProperties.getID() + "";

				if (dbId.equals(id)) {
					SelectItem item = new SelectItem();
					item.setValue(dbProfiles.getID());
					item.setLabel(dbProperties.getConnectionName() + "." + dbProfiles.getUserName());
					definitionList.add(item);

					break;
				}
			}
		}

		return definitionList;
	}

	public static Collection<SelectItem> fillSourceTypeList() {
		String sourceType = null;

		Collection<SelectItem> sourceTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < Source.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			sourceType = Source.Enum.forInt(i + 1).toString();
			item.setValue(sourceType);
			item.setLabel(sourceType);
			sourceTypeList.add(item);
		}

		return sourceTypeList;
	}

	public static Collection<SelectItem> fillUserList(ArrayList<Person> dbUserList) {
		Collection<SelectItem> userList = new ArrayList<SelectItem>();

		for (Person person : dbUserList) {
			SelectItem item = new SelectItem();
			item.setValue(person.getId() + "");
			item.setLabel(person.getUserName());
			userList.add(item);
		}

		return userList;
	}

	public static Collection<SelectItem> fillRoleList() {
		String roleValue = null;
		Collection<SelectItem> roleList = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();

		for (int i = 0; i < Role.Enum.table.lastInt(); i++) {
			item = new SelectItem();
			roleValue = Role.Enum.forInt(i + 1).toString();
			item.setValue(roleValue);
			item.setLabel(roleValue);
			roleList.add(item);
		}

		return roleList;
	}

	public static Collection<SelectItem> fillSftpAuthenticationTypeList() {
		String authenticationType = null;
		Collection<SelectItem> sftpAuthenticationTypeList = new ArrayList<SelectItem>();

		for (int i = 0; i < AuthenticationTypeDocument.AuthenticationType.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			authenticationType = AuthenticationTypeDocument.AuthenticationType.Enum.forInt(i + 1).toString();
			item.setValue(authenticationType);
			item.setLabel(authenticationType);
			sftpAuthenticationTypeList.add(item);
		}

		return sftpAuthenticationTypeList;
	}

	public static Collection<SelectItem> fillSftpTransportProviderList() {
		String transportProvider = null;
		Collection<SelectItem> sftpTransportProviderList = new ArrayList<SelectItem>();

		for (int i = 0; i < TransportProviderDocument.TransportProvider.Enum.table.lastInt(); i++) {
			SelectItem item = new SelectItem();
			transportProvider = TransportProviderDocument.TransportProvider.Enum.forInt(i + 1).toString();
			item.setValue(transportProvider);
			item.setLabel(transportProvider);
			sftpTransportProviderList.add(item);
		}

		return sftpTransportProviderList;
	}
}
