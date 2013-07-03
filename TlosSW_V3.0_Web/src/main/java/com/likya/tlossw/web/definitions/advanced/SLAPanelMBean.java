package com.likya.tlossw.web.definitions.advanced;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlOptions;

import com.likya.tlos.model.xmlbeans.calendar.CalendarPropertiesDocument.CalendarProperties;
import com.likya.tlos.model.xmlbeans.common.TypeOfTimeDocument.TypeOfTime;
import com.likya.tlos.model.xmlbeans.sla.BirimAttribute.Birim;
import com.likya.tlos.model.xmlbeans.sla.ConditionAttribute.Condition;
import com.likya.tlos.model.xmlbeans.sla.CpuDocument.Cpu;
import com.likya.tlos.model.xmlbeans.sla.DiskDocument.Disk;
import com.likya.tlos.model.xmlbeans.sla.ForWhatAttribute.ForWhat;
import com.likya.tlos.model.xmlbeans.sla.HardwareDocument.Hardware;
import com.likya.tlos.model.xmlbeans.sla.JobsInStatusDocument.JobsInStatus;
import com.likya.tlos.model.xmlbeans.sla.MaxTimeInQueueDocument.MaxTimeInQueue;
import com.likya.tlos.model.xmlbeans.sla.MaxTimeToResolveDocument.MaxTimeToResolve;
import com.likya.tlos.model.xmlbeans.sla.MemDocument.Mem;
import com.likya.tlos.model.xmlbeans.sla.NumberOfJobsDocument.NumberOfJobs;
import com.likya.tlos.model.xmlbeans.sla.ProgramType;
import com.likya.tlos.model.xmlbeans.sla.QueueFrameDocument.QueueFrame;
import com.likya.tlos.model.xmlbeans.sla.RIntervalDocument.RInterval;
import com.likya.tlos.model.xmlbeans.sla.ResolveIncidentDocument.ResolveIncident;
import com.likya.tlos.model.xmlbeans.sla.ResourceDocument.Resource;
import com.likya.tlos.model.xmlbeans.sla.ResourcePoolDocument.ResourcePool;
import com.likya.tlos.model.xmlbeans.sla.ResourceReqDocument.ResourceReq;
import com.likya.tlos.model.xmlbeans.sla.SIntervalDocument.SInterval;
import com.likya.tlos.model.xmlbeans.sla.SLADocument.SLA;
import com.likya.tlos.model.xmlbeans.sla.SoftwareDocument.Software;
import com.likya.tlos.model.xmlbeans.sla.StateAttribute.State;
import com.likya.tlos.model.xmlbeans.sla.TimeinAttribute.Timein;
import com.likya.tlossw.utils.xml.XMLNameSpaceTransformer;
import com.likya.tlossw.web.TlosSWBaseBean;
import com.likya.tlossw.web.utils.DefinitionUtils;
import com.likya.tlossw.web.utils.WebInputUtils;

@ManagedBean(name = "slaPanelMBean")
@ViewScoped
public class SLAPanelMBean extends TlosSWBaseBean implements Serializable {

	private String selectedSlaID;

	private String insertCheck;

	private String iCheck;

	private static final long serialVersionUID = 1L;

	private SLA sla;

	private String selectedTZone = "Europe/Istanbul";

	private Collection<SelectItem> tZList;

	private Collection<SelectItem> typeOfTimeList;
	private String selectedTypeOfTime;

	private Date startDate;
	private String startTime;
	private Date endDate;
	private String endTime;

	private Collection<SelectItem> calendarList = null;

	private String numberOfJobs;
	private String jobStatusName;
	private Collection<SelectItem> jobStatusNameList = null;

	private String maxTimeInQueue;
	private String maxTimeInQueueUnit;

	private String maxTimeToResolve;
	private String maxTimeToResolveUnit;

	private String sIntervalStartTime;
	private String sIntervalStopTime;
	private String rIntervalStartTime;
	private String rIntervalStopTime;

	private Collection<SelectItem> resourceNameList = null;
	private String[] selectedResourceList;
	private String selectedResourceForHardware;

	private boolean resourceBasedDef = false;

	private String cpuTimein;
	private String cpuCondition;
	private String cpuValue;
	private String cpuUnit;

	private String memoryPart;
	private String memoryCondition;
	private String memoryValue;
	private String memoryUnit;

	private String diskPart;
	private String diskCondition;
	private String diskValue;
	private String diskUnit;

	private Collection<SelectItem> softwareNameList = null;
	private String[] selectedSoftwareList;

	private boolean insertButton;

	@PostConstruct
	public void init() {
		selectedSlaID = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedSlaID"));
		insertCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("insertCheck"));
		iCheck = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("iCheck"));

		resetSlaAction();

		fillJobStatusList();
		fillCalendarList();
		fillSoftwareNameList();

		setResourceNameList(WebInputUtils.fillResourceNameList(getDbOperations().getResources()));

		setTZList(WebInputUtils.fillTZList());
		setTypeOfTimeList(WebInputUtils.fillTypesOfTimeList());

		if (iCheck != null && iCheck.equals("insert"))
			insertButton = true;

		if (insertCheck != null) {

			if (insertCheck.equals("update")) {
				insertButton = false;

				sla = getDbOperations().searchSlaByID(selectedSlaID);

				if (sla != null) {
					fillPanelFromSla();
				}

			} else {
				insertButton = true;
			}
		}
	}

	public void fillJobStatusList() {
		String statusName = null;
		Collection<SelectItem> jobStatusNameList = new ArrayList<SelectItem>();
		SelectItem item;

		for (int i = 0; i < State.Enum.table.lastInt(); i++) {
			item = new SelectItem();
			statusName = State.Enum.table.forInt(i + 1).toString();
			item.setValue(statusName);
			item.setLabel(statusName);
			jobStatusNameList.add(item);
		}
		setJobStatusNameList(jobStatusNameList);
	}

	public void fillCalendarList() {
		Collection<SelectItem> calendarList = new ArrayList<SelectItem>();
		SelectItem item;

		for (CalendarProperties calendar : getDbOperations().getCalendars()) {
			item = new SelectItem();
			item.setValue(calendar.getId() + "");
			item.setLabel(calendar.getCalendarName());
			calendarList.add(item);
		}
		setCalendarList(calendarList);
	}

	public void fillSoftwareNameList() {
		Collection<SelectItem> softwareList = new ArrayList<SelectItem>();
		SelectItem item;

		for (String software : getDbOperations().getSoftwareList()) {
			item = new SelectItem();
			item.setValue(software);
			item.setLabel(software);
			softwareList.add(item);
		}
		setSoftwareNameList(softwareList);
	}

	public String getSlaXML() {
		QName qName = SLA.type.getOuterType().getDocumentElementName();
		XmlOptions xmlOptions = XMLNameSpaceTransformer.transformXML(qName);
		String slaXML = sla.xmlText(xmlOptions);

		return slaXML;
	}

	public void resetSlaAction() {
		sla = SLA.Factory.newInstance();

		sla.setPriority(new BigInteger("1"));
		sla.setCalendarId(new BigInteger("1"));

		QueueFrame queueFrame = QueueFrame.Factory.newInstance();
		MaxTimeInQueue mTInQueue = MaxTimeInQueue.Factory.newInstance();
		queueFrame.setMaxTimeInQueue(mTInQueue);
		sla.setQueueFrame(queueFrame);

		ResolveIncident resolveIncident = ResolveIncident.Factory.newInstance();
		MaxTimeToResolve mTToResolve = MaxTimeToResolve.Factory.newInstance();
		resolveIncident.setMaxTimeToResolve(mTToResolve);
		sla.setResolveIncident(resolveIncident);

		JobsInStatus jobsInStatus = JobsInStatus.Factory.newInstance();
		NumberOfJobs numOfJobs = NumberOfJobs.Factory.newInstance();
		jobsInStatus.setNumberOfJobs(numOfJobs);
		sla.setJobsInStatus(jobsInStatus);

		SInterval sInterval = SInterval.Factory.newInstance();
		sla.setSInterval(sInterval);

		RInterval rInterval = RInterval.Factory.newInstance();
		sla.setRInterval(rInterval);

		ResourcePool resourcePool = ResourcePool.Factory.newInstance();
		sla.setResourcePool(resourcePool);

		ResourceReq resourceReq = ResourceReq.Factory.newInstance();

		Hardware hardware = Hardware.Factory.newInstance();
		Mem mem = Mem.Factory.newInstance();
		Cpu cpu = Cpu.Factory.newInstance();
		Disk disk = Disk.Factory.newInstance();
		hardware.setCpu(cpu);
		hardware.setMem(mem);
		hardware.setDisk(disk);
		resourceReq.setHardware(hardware);

		Software software = Software.Factory.newInstance();
		resourceReq.setSoftware(software);

		sla.setResourceReq(resourceReq);

		startDate = null;
		startTime = "00:00:00";
		endDate = null;
		endTime = "23:59:59";

		numberOfJobs = null;
		jobStatusName = State.RUNNING.toString();

		maxTimeInQueue = "1";
		maxTimeInQueueUnit = Birim.MIN.toString();

		maxTimeToResolve = "1";
		maxTimeToResolveUnit = Birim.MIN.toString();

		sIntervalStartTime = null;
		sIntervalStopTime = null;
		rIntervalStartTime = null;
		rIntervalStopTime = null;

		selectedResourceList = null;

		cpuValue = "0";
		cpuUnit = "%";
		memoryValue = "0";
		diskValue = "0";

		selectedSoftwareList = null;

		setSelectedTZone(new String("Europe/Istanbul"));
		selectedTypeOfTime = new String("Actual");
	}

	public void insertSlaAction(ActionEvent e) {
		fillSlaProperties();

		Calendar creationDate = Calendar.getInstance();
		sla.setCreationDate(creationDate);

		if (getDbOperations().insertSla(getSlaXML())) {
			addMessage("insertSLA", FacesMessage.SEVERITY_INFO, "tlos.success.sla.insert", null);
			resetSlaAction();
		} else {
			addMessage("insertSLA", FacesMessage.SEVERITY_ERROR, "tlos.error.calendar.insert", null);
		}
	}

	public void updateSlaAction(ActionEvent e) {
		fillSlaProperties();

		if (getDbOperations().updateSla(getSlaXML())) {
			addMessage("insertSLA", FacesMessage.SEVERITY_INFO, "tlos.success.sla.update", null);
		} else {
			addMessage("insertSLA", FacesMessage.SEVERITY_ERROR, "tlos.error.sla.update", null);
		}
	}

	private void fillSlaProperties() {
		sla.setStartDate(DefinitionUtils.dateTimeToXmlDateTime(startDate, startTime, selectedTZone));
		sla.setEndDate(DefinitionUtils.dateTimeToXmlDateTime(endDate, endTime, selectedTZone));

		sla.getQueueFrame().getMaxTimeInQueue().setStringValue(maxTimeInQueue);
		sla.getQueueFrame().getMaxTimeInQueue().setBirim(Birim.Enum.forString(maxTimeInQueueUnit));

		sla.getResolveIncident().getMaxTimeToResolve().setStringValue(maxTimeToResolve);
		sla.getResolveIncident().getMaxTimeToResolve().setBirim(Birim.Enum.forString(maxTimeToResolveUnit));

		try {
			BigInteger val = new BigInteger(numberOfJobs);
			sla.getJobsInStatus().getNumberOfJobs().setBigIntegerValue(val);
		} catch (NumberFormatException e) {
			addMessage("insertSLA", FacesMessage.SEVERITY_ERROR, "tlos.validation.sla.numberOfJobs.valid", null);
			return;
		}
		sla.getJobsInStatus().getNumberOfJobs().setState(State.Enum.forString(jobStatusName));

		sla.getSInterval().setStartTime(DefinitionUtils.dateToXmlTime(sIntervalStartTime, selectedTZone));
		sla.getSInterval().setStopTime(DefinitionUtils.dateToXmlTime(sIntervalStopTime, selectedTZone));
		sla.getRInterval().setStartTime(DefinitionUtils.dateToXmlTime(rIntervalStartTime, selectedTZone));
		sla.getRInterval().setStopTime(DefinitionUtils.dateToXmlTime(rIntervalStopTime, selectedTZone));

		sla.setTimeZone(selectedTZone);
		sla.setTypeOfTime(TypeOfTime.Enum.forString(selectedTypeOfTime));

		// makine listesindekileri sla tanimindaki resourcePool kismina set
		// ediyor
		ResourcePool resourcePool = ResourcePool.Factory.newInstance();
		if (selectedResourceList != null) {
			for (int i = 0; i < selectedResourceList.length; i++) {
				Resource resource = Resource.Factory.newInstance();
				resource.setStringValue(selectedResourceList[i].toString());

				resourcePool.addNewResource();
				resourcePool.setResourceArray(i, resource);
			}
		}
		sla.setResourcePool(resourcePool);

		if (resourceBasedDef) {
			sla.getResourceReq().getHardware().setEntryName(selectedResourceForHardware);
		}

		sla.getResourceReq().getHardware().getCpu().setTimein(Timein.Enum.forString(cpuTimein));
		sla.getResourceReq().getHardware().getCpu().setCondition(Condition.Enum.forString(cpuCondition));
		sla.getResourceReq().getHardware().getCpu().setStringValue(cpuValue);
		sla.getResourceReq().getHardware().getCpu().setBirim(Birim.Enum.forString(cpuUnit));

		sla.getResourceReq().getHardware().getMem().setForWhat(ForWhat.Enum.forString(memoryPart));
		sla.getResourceReq().getHardware().getMem().setCondition(Condition.Enum.forString(memoryCondition));
		sla.getResourceReq().getHardware().getMem().setStringValue(memoryValue);
		sla.getResourceReq().getHardware().getMem().setBirim(Birim.Enum.forString(memoryUnit));

		sla.getResourceReq().getHardware().getDisk().setForWhat(ForWhat.Enum.forString(diskPart));
		sla.getResourceReq().getHardware().getDisk().setCondition(Condition.Enum.forString(diskCondition));
		sla.getResourceReq().getHardware().getDisk().setStringValue(diskValue);
		sla.getResourceReq().getHardware().getDisk().setBirim(Birim.Enum.forString(diskUnit));

		// yazilim listesindekileri sla tanimindaki ResourceReq/software kismina
		// set ediyor
		Software software = Software.Factory.newInstance();
		if (selectedSoftwareList != null) {
			for (int i = 0; i < selectedSoftwareList.length; i++) {
				ProgramType program = ProgramType.Factory.newInstance();
				program.setStringValue(selectedSoftwareList[i].toString());

				software.addNewProgram();
				software.setProgramArray(i, program);
			}
		}
		sla.getResourceReq().setSoftware(software);

		sla.setUserId(getSessionMediator().getJmxAppUser().getAppUser().getId());
	}

	private void fillPanelFromSla() {
		// startDate = sla.getStartDate().getTime();
		// endDate = sla.getEndDate().getTime();

		startDate = DefinitionUtils.dateToDate(sla.getStartDate().getTime(), selectedTZone);
		endDate = DefinitionUtils.dateToDate(sla.getEndDate().getTime(), selectedTZone);

		String timeOutputFormat = new String("HH:mm:ss");

		startTime = DefinitionUtils.calendarToStringTimeFormat(sla.getStartDate(), selectedTZone, timeOutputFormat);
		endTime = DefinitionUtils.calendarToStringTimeFormat(sla.getEndDate(), selectedTZone, timeOutputFormat);

		// startTime = DefinitionUtils.dateToStringTime(sla.getStartDate().getTime());
		// endTime = DefinitionUtils.dateToStringTime(sla.getEndDate().getTime());

		maxTimeInQueue = sla.getQueueFrame().getMaxTimeInQueue().getStringValue();
		maxTimeInQueueUnit = sla.getQueueFrame().getMaxTimeInQueue().getBirim().toString();

		maxTimeToResolve = sla.getResolveIncident().getMaxTimeToResolve().getStringValue();
		maxTimeToResolveUnit = sla.getResolveIncident().getMaxTimeToResolve().getBirim().toString();

		numberOfJobs = sla.getJobsInStatus().getNumberOfJobs().getStringValue();
		jobStatusName = sla.getJobsInStatus().getNumberOfJobs().getState().toString();

		sIntervalStartTime = DefinitionUtils.calendarToStringTimeFormat(sla.getSInterval().getStartTime(), selectedTZone, timeOutputFormat);
		sIntervalStopTime = DefinitionUtils.calendarToStringTimeFormat(sla.getSInterval().getStopTime(), selectedTZone, timeOutputFormat);
		rIntervalStartTime = DefinitionUtils.calendarToStringTimeFormat(sla.getRInterval().getStartTime(), selectedTZone, timeOutputFormat);
		rIntervalStopTime = DefinitionUtils.calendarToStringTimeFormat(sla.getRInterval().getStopTime(), selectedTZone, timeOutputFormat);

		selectedTZone = sla.getTimeZone();
		if (sla.getTypeOfTime() != null)
			selectedTypeOfTime = sla.getTypeOfTime().toString();
		else
			selectedTypeOfTime = new String("Broadcast");

		// sIntervalStartTime = DefinitionUtils.dateToStringTime(sla.getSInterval().getStartTime().getTime());
		// sIntervalStopTime = DefinitionUtils.dateToStringTime(sla.getSInterval().getStopTime().getTime());
		// rIntervalStartTime = DefinitionUtils.dateToStringTime(sla.getRInterval().getStartTime().getTime());
		// rIntervalStopTime = DefinitionUtils.dateToStringTime(sla.getRInterval().getStopTime().getTime());

		cpuTimein = sla.getResourceReq().getHardware().getCpu().getTimein().toString();
		cpuUnit = sla.getResourceReq().getHardware().getCpu().getBirim().toString();
		cpuCondition = sla.getResourceReq().getHardware().getCpu().getCondition().toString();
		cpuValue = sla.getResourceReq().getHardware().getCpu().getStringValue();

		diskPart = sla.getResourceReq().getHardware().getDisk().getForWhat().toString();
		diskUnit = sla.getResourceReq().getHardware().getDisk().getBirim().toString();
		diskCondition = sla.getResourceReq().getHardware().getDisk().getCondition().toString();
		diskValue = sla.getResourceReq().getHardware().getDisk().getStringValue();

		memoryPart = sla.getResourceReq().getHardware().getMem().getForWhat().toString();
		memoryUnit = sla.getResourceReq().getHardware().getMem().getBirim().toString();
		memoryCondition = sla.getResourceReq().getHardware().getMem().getCondition().toString();
		memoryValue = sla.getResourceReq().getHardware().getMem().getStringValue();

		if (sla.getResourceReq().getHardware().getEntryName() != null && !sla.getResourceReq().getHardware().getEntryName().equals("")) {
			resourceBasedDef = true;
			selectedResourceForHardware = sla.getResourceReq().getHardware().getEntryName();
		}

		fillResourcePool();
		fillSoftwareList();
	}

	private void fillResourcePool() {

		if (sla.getResourcePool().getResourceArray() != null) {

			selectedResourceList = new String[sla.getResourcePool().getResourceArray().length];

			for (int i = 0; i < sla.getResourcePool().getResourceArray().length; i++) {
				selectedResourceList[i] = sla.getResourcePool().getResourceArray(i).getStringValue();
			}
		}
	}

	private void fillSoftwareList() {
		if (sla.getResourceReq().getSoftware().getProgramArray() != null) {

			selectedSoftwareList = new String[sla.getResourceReq().getSoftware().getProgramArray().length];

			for (int i = 0; i < sla.getResourceReq().getSoftware().getProgramArray().length; i++) {
				selectedSoftwareList[i] = sla.getResourceReq().getSoftware().getProgramArray(i).getStringValue();
			}
		}
	}

	public boolean isInsertButton() {
		return insertButton;
	}

	public void setInsertButton(boolean insertButton) {
		this.insertButton = insertButton;
	}

	public String getInsertCheck() {
		return insertCheck;
	}

	public void setInsertCheck(String insertCheck) {
		this.insertCheck = insertCheck;
	}

	public String getiCheck() {
		return iCheck;
	}

	public void setiCheck(String iCheck) {
		this.iCheck = iCheck;
	}

	public String getSelectedSlaID() {
		return selectedSlaID;
	}

	public void setSelectedSlaID(String selectedSlaID) {
		this.selectedSlaID = selectedSlaID;
	}

	public SLA getSla() {
		return sla;
	}

	public void setSla(SLA sla) {
		this.sla = sla;
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

	public String getsIntervalStartTime() {
		return sIntervalStartTime;
	}

	public void setsIntervalStartTime(String sIntervalStartTime) {
		this.sIntervalStartTime = sIntervalStartTime;
	}

	public String getsIntervalStopTime() {
		return sIntervalStopTime;
	}

	public void setsIntervalStopTime(String sIntervalStopTime) {
		this.sIntervalStopTime = sIntervalStopTime;
	}

	public String getrIntervalStartTime() {
		return rIntervalStartTime;
	}

	public void setrIntervalStartTime(String rIntervalStartTime) {
		this.rIntervalStartTime = rIntervalStartTime;
	}

	public String getrIntervalStopTime() {
		return rIntervalStopTime;
	}

	public void setrIntervalStopTime(String rIntervalStopTime) {
		this.rIntervalStopTime = rIntervalStopTime;
	}

	public Collection<SelectItem> getCalendarList() {
		return calendarList;
	}

	public void setCalendarList(Collection<SelectItem> calendarList) {
		this.calendarList = calendarList;
	}

	public String getNumberOfJobs() {
		return numberOfJobs;
	}

	public void setNumberOfJobs(String numberOfJobs) {
		this.numberOfJobs = numberOfJobs;
	}

	public String getJobStatusName() {
		return jobStatusName;
	}

	public void setJobStatusName(String jobStatusName) {
		this.jobStatusName = jobStatusName;
	}

	public Collection<SelectItem> getJobStatusNameList() {
		return jobStatusNameList;
	}

	public void setJobStatusNameList(Collection<SelectItem> jobStatusNameList) {
		this.jobStatusNameList = jobStatusNameList;
	}

	public String getMaxTimeInQueue() {
		return maxTimeInQueue;
	}

	public void setMaxTimeInQueue(String maxTimeInQueue) {
		this.maxTimeInQueue = maxTimeInQueue;
	}

	public String getMaxTimeInQueueUnit() {
		return maxTimeInQueueUnit;
	}

	public void setMaxTimeInQueueUnit(String maxTimeInQueueUnit) {
		this.maxTimeInQueueUnit = maxTimeInQueueUnit;
	}

	public String getMaxTimeToResolve() {
		return maxTimeToResolve;
	}

	public void setMaxTimeToResolve(String maxTimeToResolve) {
		this.maxTimeToResolve = maxTimeToResolve;
	}

	public String getMaxTimeToResolveUnit() {
		return maxTimeToResolveUnit;
	}

	public void setMaxTimeToResolveUnit(String maxTimeToResolveUnit) {
		this.maxTimeToResolveUnit = maxTimeToResolveUnit;
	}

	public Collection<SelectItem> getResourceNameList() {
		return resourceNameList;
	}

	public void setResourceNameList(Collection<SelectItem> resourceNameList) {
		this.resourceNameList = resourceNameList;
	}

	public String[] getSelectedResourceList() {
		return selectedResourceList;
	}

	public void setSelectedResourceList(String[] selectedResourceList) {
		this.selectedResourceList = selectedResourceList;
	}

	public String getSelectedResourceForHardware() {
		return selectedResourceForHardware;
	}

	public void setSelectedResourceForHardware(String selectedResourceForHardware) {
		this.selectedResourceForHardware = selectedResourceForHardware;
	}

	public boolean isResourceBasedDef() {
		return resourceBasedDef;
	}

	public void setResourceBasedDef(boolean resourceBasedDef) {
		this.resourceBasedDef = resourceBasedDef;
	}

	public String getCpuTimein() {
		return cpuTimein;
	}

	public void setCpuTimein(String cpuTimein) {
		this.cpuTimein = cpuTimein;
	}

	public String getCpuCondition() {
		return cpuCondition;
	}

	public void setCpuCondition(String cpuCondition) {
		this.cpuCondition = cpuCondition;
	}

	public String getCpuValue() {
		return cpuValue;
	}

	public void setCpuValue(String cpuValue) {
		this.cpuValue = cpuValue;
	}

	public String getCpuUnit() {
		return cpuUnit;
	}

	public void setCpuUnit(String cpuUnit) {
		this.cpuUnit = cpuUnit;
	}

	public String getMemoryPart() {
		return memoryPart;
	}

	public void setMemoryPart(String memoryPart) {
		this.memoryPart = memoryPart;
	}

	public String getMemoryCondition() {
		return memoryCondition;
	}

	public void setMemoryCondition(String memoryCondition) {
		this.memoryCondition = memoryCondition;
	}

	public String getMemoryValue() {
		return memoryValue;
	}

	public void setMemoryValue(String memoryValue) {
		this.memoryValue = memoryValue;
	}

	public String getMemoryUnit() {
		return memoryUnit;
	}

	public void setMemoryUnit(String memoryUnit) {
		this.memoryUnit = memoryUnit;
	}

	public String getDiskPart() {
		return diskPart;
	}

	public void setDiskPart(String diskPart) {
		this.diskPart = diskPart;
	}

	public String getDiskCondition() {
		return diskCondition;
	}

	public void setDiskCondition(String diskCondition) {
		this.diskCondition = diskCondition;
		sla.getResourceReq().getHardware().getDisk().setCondition(Condition.Enum.forString(diskCondition));
	}

	public String getDiskValue() {
		return diskValue;
	}

	public void setDiskValue(String diskValue) {
		this.diskValue = diskValue;
		sla.getResourceReq().getHardware().getDisk().setStringValue(diskValue);
	}

	public String getDiskUnit() {
		return diskUnit;
	}

	public void setDiskUnit(String diskUnit) {
		this.diskUnit = diskUnit;
		sla.getResourceReq().getHardware().getDisk().setBirim(Birim.Enum.forString(diskUnit));
	}

	public Collection<SelectItem> getSoftwareNameList() {
		return softwareNameList;
	}

	public void setSoftwareNameList(Collection<SelectItem> softwareNameList) {
		this.softwareNameList = softwareNameList;
	}

	public String[] getSelectedSoftwareList() {
		return selectedSoftwareList;
	}

	public void setSelectedSoftwareList(String[] selectedSoftwareList) {
		this.selectedSoftwareList = selectedSoftwareList;
	}

	public String getSelectedTZone() {
		return selectedTZone;
	}

	public void setSelectedTZone(String selectedTZone) {
		this.selectedTZone = selectedTZone;
	}

	public Collection<SelectItem> getTZList() {
		return tZList;
	}

	public void setTZList(Collection<SelectItem> tZList) {
		this.tZList = tZList;
	}

	public Collection<SelectItem> getTypeOfTimeList() {
		return typeOfTimeList;
	}

	public void setTypeOfTimeList(Collection<SelectItem> typeOfTimeList) {
		this.typeOfTimeList = typeOfTimeList;
	}

	public String getSelectedTypeOfTime() {
		return selectedTypeOfTime;
	}

	public void setSelectedTypeOfTime(String selectedTypeOfTime) {
		this.selectedTypeOfTime = selectedTypeOfTime;
	}

}