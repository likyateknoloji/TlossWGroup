package com.likya.tlossw.web.utils;

import java.util.HashMap;

import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;

public class DecorationUtils {

//	private static HashMap<String, String> stateMappings = null;
	
	private static HashMap<Integer, String> jobIconsMappings = null;
	
	/**
	 * yeni state yapisina gore duzenleme yaptim, onceden burada olmayan substatelere varolan ikonlardan koydum gecici olarak.
	 * burada ihtiyac olmayan substateler varsa kaldirilabilir. merve
	 * http://jquery-ui.googlecode.com/svn/tags/1.6rc5/tests/static/icons.html
	 * @param jobState
	 * @return
	 */
	
	public static String jobStateCSSMapper(LiveStateInfo jobState) {
		String cssName = null;

		if (jobState == null) {
			cssName = "ui-default-state";
		} else {
			if (jobState.getSubstateName() != null) {
				switch (jobState.getSubstateName().intValue()) {
				case SubstateName.INT_CREATED:
					if (jobState.getStatusName() != null) {
						if (jobState.getStatusName().equals(StatusName.DEVELOPMENT)) {
							cssName = "ui-development-state";
							break;
						} else if (jobState.getStatusName().equals(StatusName.TEST)) {
							cssName = "ui-test-state";
							break;
						} else if (jobState.getStatusName().equals(StatusName.REQUEST)) {
							cssName = "ui-request-state";
							break;
						} else if (jobState.getStatusName().equals(StatusName.DEPLOYED)) {
							cssName = "ui-deployed-state";
							break;
						}
					}
				case SubstateName.INT_DEACTIVATED:
					cssName = "ui-deactivated-state";
					break;
				case SubstateName.INT_VALIDATED:
					cssName = "ui-validated-state";
					break;
				case SubstateName.INT_IDLED:
					cssName = "ui-default-state";
					break;
				case SubstateName.INT_READY:
					if (jobState.getStatusName() != null) {
						if (jobState.getStatusName().equals(StatusName.LOOKFOR_RESOURCE)) {
							cssName = "ui-look4resource-state";
							break;
						} else if (jobState.getStatusName().equals(StatusName.USER_CHOOSE_RESOURCE)) {
							cssName = "ui-user-choose-resource-state";
							break;
						} else if (jobState.getStatusName().equals(StatusName.USER_WAITING)) {
							cssName = "ui-user-waiting-state";
							break;
						} else if (jobState.getStatusName().equals(StatusName.WAITING)) {
							cssName = "ui-waiting-state";
							break;
						} else if (jobState.getStatusName().equals(StatusName.TRANSFERING)) {
							cssName = "ui-transferring-state";
							break;
						}
					}
				case SubstateName.INT_PAUSED:
					cssName = "ui-paused-state";
					break;
				case SubstateName.INT_STAGE_IN:
					cssName = "ui-stage-in-state";
					break;
				case SubstateName.INT_MIGRATING:
					cssName = "ui-migrating-state";
					break;
				case SubstateName.INT_ON_RESOURCE:
					if (jobState.getStatusName() != null) {
						if (jobState.getStatusName().equals(StatusName.TIME_IN)) {
							cssName = "ui-time-in-state";
							break;
						} else if (jobState.getStatusName().equals(StatusName.TIME_OUT)) {
							cssName = "ui-time-out-state";
							break;
						}
					}
				case SubstateName.INT_HELD:
					cssName = "ui-held-state";
					break;
				case SubstateName.INT_STAGE_OUT:
					cssName = "ui-stage-out-state";
					break;
				case SubstateName.INT_COMPLETED:
					if ((jobState.getStateName() != null && jobState.getStateName().equals(StateName.FINISHED)) && (jobState.getStatusName() != null && jobState.getStatusName().equals(StatusName.SUCCESS))) {
						cssName = "ui-success-state";
						break;
					} else if ((jobState.getStateName() != null && jobState.getStateName().equals(StateName.FINISHED)) && (jobState.getStatusName() != null && jobState.getStatusName().equals(StatusName.FAILED))) {
						cssName = "ui-failed-state";
						break;
					}
				case SubstateName.INT_SKIPPED:
					cssName = "ui-skipped-state";
					break;
				case SubstateName.INT_STOPPED:
					cssName = "ui-stopped-state";
					break;
				}
			}
		}
		return cssName;
	}
	
	public static String jobStateColorMappings(LiveStateInfo jobState) {
		
		return jobStateCSSMapper(jobState) + "-color";
		
	}
	
	public static String jobStateIconMappings(LiveStateInfo jobState) {
		
		return jobStateCSSMapper(jobState) + "-icon";
		
	}
	
	public static void jobCssSetter() {
		
		jobIconsMappings = new HashMap<Integer, String>();
		jobIconsMappings.put(1, "ui-job-icon-shell-script");  //SHELL SCRIPT
		jobIconsMappings.put(2, "ui-job-icon-remote");        //REMOTE_SHELL
		
		jobIconsMappings.put(6, "ui-job-icon-syscom");        //SYSTEM COMMAND
		jobIconsMappings.put(7, "ui-job-icon-ftp");           //FTP
		jobIconsMappings.put(8, "ui-job-icon-file-read");     //FILE PROCESS
		jobIconsMappings.put(9, "ui-job-icon-file-listener"); //FILE LISTENER
		jobIconsMappings.put(10,"ui-job-icon-batch");         //BATCH PROCESS
		jobIconsMappings.put(11,"ui-job-icon-process-node");  //PROCESS NODE
		jobIconsMappings.put(12,"ui-job-icon-database");      //DB JOBS
		jobIconsMappings.put(13,"ui-job-icon-ws");            //WEB SERVICE
	}

	public static HashMap<Integer, String> getJobIconsMappings() {
		return jobIconsMappings;
	}

	public void setJobIconsMappings(HashMap<Integer, String> jobIconsMappings) {
		DecorationUtils.jobIconsMappings = jobIconsMappings;
	}
}
