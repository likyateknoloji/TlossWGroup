package com.likya.tlossw.web.definitions.helpers;

import java.util.Collection;

import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.common.AgentChoiceMethodDocument.AgentChoiceMethod;
import com.likya.tlos.model.xmlbeans.common.ChoiceType;
import com.likya.tlos.model.xmlbeans.data.AdvancedJobInfosDocument.AdvancedJobInfos;
import com.likya.tlos.model.xmlbeans.data.ResourceRequirementDocument.ResourceRequirement;
import com.likya.tlos.model.xmlbeans.sla.BirimAttribute.Birim;
import com.likya.tlos.model.xmlbeans.sla.ConditionAttribute.Condition;
import com.likya.tlos.model.xmlbeans.sla.CpuDocument.Cpu;
import com.likya.tlos.model.xmlbeans.sla.DiskDocument.Disk;
import com.likya.tlos.model.xmlbeans.sla.ForWhatAttribute.ForWhat;
import com.likya.tlos.model.xmlbeans.sla.HardwareDocument.Hardware;
import com.likya.tlos.model.xmlbeans.sla.MemDocument.Mem;
import com.likya.tlos.model.xmlbeans.sla.TimeinAttribute.Timein;
import com.likya.tlossw.web.definitions.JSBasePanelMBean;
import com.likya.tlossw.web.definitions.JobBasePanelBean;

public class AdvancedJobInfosTab {

	private boolean useResourceReq = false;

	private boolean resourceBasedDef = false;

	private String selectedResourceForHardware;

	private String agentChoiceMethod;

	private String selectedAgent;

	private String jobSLA;

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
	
	private JSBasePanelMBean jsBasePanelMBean;
	
	public AdvancedJobInfosTab(JSBasePanelMBean jsBasePanelMBean) {
		super();
		this.jsBasePanelMBean = jsBasePanelMBean;
	}

	public void resetTab() {
		jobSLA = JobBasePanelBean.NONE;
		useResourceReq = false;
		cpuValue = "0";
		cpuUnit = "%";
		memoryValue = "0";
		diskValue = "0";
	}

	public void fillAdvancedJobInfosTab(AdvancedJobInfos advancedJobInfos) {

		// sla tanimi
		if (advancedJobInfos.getSLAId() > 0) {
			jobSLA = advancedJobInfos.getSLAId() + "";
		}

		// agent secme metodu
		if (advancedJobInfos.getAgentChoiceMethod() != null) {
			setAgentChoiceMethod(advancedJobInfos.getAgentChoiceMethod().getStringValue());

			if (getAgentChoiceMethod().equals(ChoiceType.USER_MANDATORY_PREFERENCE.toString())) {
				setSelectedAgent(advancedJobInfos.getAgentChoiceMethod().getAgentId());
			}
		}

		// kaynak gereksinimi tanimi
		if (advancedJobInfos.getResourceRequirement() != null) {
			useResourceReq = true;

			Hardware hardware = advancedJobInfos.getResourceRequirement().getHardware();

			if (hardware.getEntryName() != null) {
				selectedResourceForHardware = hardware.getEntryName();
			}

			Cpu cpu = hardware.getCpu();
			cpuTimein = cpu.getTimein().toString();
			cpuCondition = cpu.getCondition().toString();
			cpuValue = cpu.getStringValue();
			cpuUnit = cpu.getBirim().toString();

			Mem mem = hardware.getMem();
			memoryPart = mem.getForWhat().toString();
			memoryCondition = mem.getCondition().toString();
			memoryValue = mem.getStringValue();
			memoryUnit = mem.getBirim().toString();

			Disk disk = hardware.getDisk();
			diskPart = disk.getForWhat().toString();
			diskCondition = disk.getCondition().toString();
			diskValue = disk.getStringValue();
			diskUnit = disk.getBirim().toString();
		}
	}
	
	public AdvancedJobInfos fillAdvancedJobInfos() {
		
		AdvancedJobInfos advancedJobInfos = AdvancedJobInfos.Factory.newInstance();

		// sla tanimi
		if (!jobSLA.equals(JobBasePanelBean.NONE)) {
			advancedJobInfos.setSLAId(Integer.valueOf(jobSLA));
		}

		AgentChoiceMethod choiceMethod = AgentChoiceMethod.Factory.newInstance();
		choiceMethod.setStringValue(getAgentChoiceMethod());

		if (getAgentChoiceMethod().equals(ChoiceType.USER_MANDATORY_PREFERENCE.toString())) {
			choiceMethod.setAgentId(getSelectedAgent());
		}
		advancedJobInfos.setAgentChoiceMethod(choiceMethod);

		// kaynak gereksinimi tanimi
		if (useResourceReq) {
			ResourceRequirement resourceRequirement;

			if (advancedJobInfos.getResourceRequirement() == null) {
				resourceRequirement = ResourceRequirement.Factory.newInstance();
			} else {
				resourceRequirement = advancedJobInfos.getResourceRequirement();
			}

			Hardware hardware;

			if (resourceRequirement.getHardware() == null) {
				hardware = Hardware.Factory.newInstance();
			} else {
				hardware = resourceRequirement.getHardware();
			}

			if (resourceBasedDef) {
				hardware.setEntryName(selectedResourceForHardware);
			}

			Cpu cpu = Cpu.Factory.newInstance();
			cpu.setTimein(Timein.Enum.forString(cpuTimein));
			cpu.setCondition(Condition.Enum.forString(cpuCondition));
			cpu.setStringValue(cpuValue);
			cpu.setBirim(Birim.Enum.forString(cpuUnit));

			Mem mem = Mem.Factory.newInstance();
			mem.setForWhat(ForWhat.Enum.forString(memoryPart));
			mem.setCondition(Condition.Enum.forString(memoryCondition));
			mem.setStringValue(memoryValue);
			mem.setBirim(Birim.Enum.forString(memoryUnit));

			Disk disk = Disk.Factory.newInstance();
			disk.setForWhat(ForWhat.Enum.forString(diskPart));
			disk.setCondition(Condition.Enum.forString(diskCondition));
			disk.setStringValue(diskValue);
			disk.setBirim(Birim.Enum.forString(diskUnit));

			hardware.setCpu(cpu);
			hardware.setMem(mem);
			hardware.setDisk(disk);

			resourceRequirement.setHardware(hardware);
			advancedJobInfos.setResourceRequirement(resourceRequirement);
		}

		return advancedJobInfos;
	}

	public boolean getAgentChoiceMethodUserMandatoryPreference() {
		return "UserMandatoryPreference".equals(agentChoiceMethod);
	}

	public boolean isUseResourceReq() {
		return useResourceReq;
	}

	public void setUseResourceReq(boolean useResourceReq) {
		this.useResourceReq = useResourceReq;
	}

	public boolean isResourceBasedDef() {
		return resourceBasedDef;
	}

	public void setResourceBasedDef(boolean resourceBasedDef) {
		this.resourceBasedDef = resourceBasedDef;
	}

	public String getSelectedResourceForHardware() {
		return selectedResourceForHardware;
	}

	public void setSelectedResourceForHardware(String selectedResourceForHardware) {
		this.selectedResourceForHardware = selectedResourceForHardware;
	}

	public String getAgentChoiceMethod() {
		return agentChoiceMethod;
	}

	public void setAgentChoiceMethod(String agentChoiceMethod) {
		this.agentChoiceMethod = agentChoiceMethod;
	}

	public Collection<SelectItem> getAgentChoiceMethodList() {
		return jsBasePanelMBean.getAgentChoiceMethodList();
	}

	public String getSelectedAgent() {
		return selectedAgent;
	}

	public void setSelectedAgent(String selectedAgent) {
		this.selectedAgent = selectedAgent;
	}

	public Collection<SelectItem> getDefinedAgentList() {
		return jsBasePanelMBean.getDefinedAgentList();
	}

	public String getJobSLA() {
		return jobSLA;
	}

	public void setJobSLA(String jobSLA) {
		this.jobSLA = jobSLA;
	}

	public Collection<SelectItem> getJsSLAList() {
		return jsBasePanelMBean.getJsSLAList();
	}

	public Collection<SelectItem> getResourceNameList() {
		return jsBasePanelMBean.getResourceNameList();
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
	}

	public String getDiskValue() {
		return diskValue;
	}

	public void setDiskValue(String diskValue) {
		this.diskValue = diskValue;
	}

	public String getDiskUnit() {
		return diskUnit;
	}

	public void setDiskUnit(String diskUnit) {
		this.diskUnit = diskUnit;
	}

}
