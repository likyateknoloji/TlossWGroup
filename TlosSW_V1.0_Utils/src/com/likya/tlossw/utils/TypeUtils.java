package com.likya.tlossw.utils;

import com.likya.tlos.model.xmlbeans.data.JobPropertiesDocument.JobProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbConnectionPropertiesDocument.DbConnectionProperties;
import com.likya.tlos.model.xmlbeans.dbjob.DbJobDefinitionDocument.DbJobDefinition;
import com.likya.tlos.model.xmlbeans.fileadapter.FileAdapterPropertiesDocument.FileAdapterProperties;
import com.likya.tlos.model.xmlbeans.ftpadapter.FtpAdapterPropertiesDocument.FtpAdapterProperties;
import com.likya.tlos.model.xmlbeans.state.JsDependencyRuleDocument.JsDependencyRule;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;

public class TypeUtils {

	public static StateName.Enum resolveState(JobProperties jobProperties) {
		return jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStateName();
	}

	public static SubstateName.Enum resolveSubstate(JobProperties jobProperties) {
		return jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getSubstateName();
	}

	public static StatusName.Enum resolveStatus(JobProperties jobProperties) {
		return jobProperties.getStateInfos().getLiveStateInfos().getLiveStateInfoArray(0).getStatusName();
	}

	public static FtpAdapterProperties resolveFtpAdapterProperties(JobProperties jobProperties) {
		return jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getFtpAdapterProperties();
	}

	public static DbJobDefinition resolveDbJobDefinition(JobProperties jobProperties) {
		return jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getDbJobDefinition();
	}
	
	
	public static DbConnectionProperties resolvedbConnectionProperties(JobProperties jobProperties) {
		return jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getDbJobDefinition().getDbConnectionProperties();
	}

	public static FileAdapterProperties resolveFileAdapterProperties(JobProperties jobProperties) {
		return jobProperties.getBaseJobInfos().getJobInfos().getJobTypeDetails().getSpecialParameters().getFileAdapterProperties();
	}

	public static boolean containsNull(JsDependencyRule jsDependencyRule) {
		if (jsDependencyRule.getStateName() == null || jsDependencyRule.getSubstateName() == null || jsDependencyRule.getStatusName() == null) {
			return true;
		}
		return false;
	}

	public static boolean containsNull(LiveStateInfo liveStateInfo) {
		if (liveStateInfo.getStateName() == null || liveStateInfo.getSubstateName() == null || liveStateInfo.getStatusName() == null) {
			return true;
		}
		return false;
	}

}
