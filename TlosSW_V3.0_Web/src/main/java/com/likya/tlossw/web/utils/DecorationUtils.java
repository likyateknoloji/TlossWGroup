package com.likya.tlossw.web.utils;

import java.util.HashMap;

import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;

public class DecorationUtils {

//	private static HashMap<String, String> stateMappings = null;
	
	private static HashMap<String, String> jobIconsMappings = null;
	
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
					cssName = "ui-icon-gear";
					break;
				case SubstateName.INT_VALIDATED:
					cssName = "ui-icon-lightbulb";
					break;
				case SubstateName.INT_IDLED:
					cssName = "ui-default-state";
					break;
				case SubstateName.INT_READY:
					cssName = "ui-waiting-state";
					break;
				case SubstateName.INT_PAUSED:
					cssName = "ui-icon-info";
					break;
				case SubstateName.INT_STAGE_IN:
					cssName = "ui-icon-arrowrefresh-1-s";
					break;
				case SubstateName.INT_MIGRATING:
					cssName = "ui-icon-extlink";
					break;
				case SubstateName.INT_ON_RESOURCE:
					if (jobState.getStatusName() != null) {
						if (jobState.getStatusName().equals(StatusName.TIME_IN)) {
							cssName = "ui-working-state";
							break;
						} else if (jobState.getStatusName().equals(StatusName.TIME_OUT)) {
							cssName = "ui-timeout-state";
							break;
						}
					}
				case SubstateName.INT_HELD:
					cssName = "ui-icon-pause";
					break;
				case SubstateName.INT_STAGE_OUT:
					cssName = "ui-icon-arrowrefresh-1-n";
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
					cssName = "ui-icon-seek-next";
					break;
				case SubstateName.INT_STOPPED:
					cssName = "ui-icon-cancel";
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
		
		jobIconsMappings = new HashMap<String, String>();
		jobIconsMappings.put("WEB SERVICE",    "ui-job-icon-ws");
		jobIconsMappings.put("BATCH PROCESS",  "ui-job-icon-batch");
		jobIconsMappings.put("DB JOBS",        "ui-job-icon-database");
		jobIconsMappings.put("REMOTE SHELL",   "ui-job-icon-remote");
		jobIconsMappings.put("SYSTEM COMMAND", "ui-job-icon-syscom");
		jobIconsMappings.put("SHELL SCRIPT",   "ui-job-icon-shell-script");
		jobIconsMappings.put("FILE PROCESS",   "ui-job-icon-file-read");
		jobIconsMappings.put("FTP",            "ui-job-icon-ftp");
		jobIconsMappings.put("FILE LISTENER",  "ui-job-icon-file-listener");
		jobIconsMappings.put("PROCESS NODE",   "ui-job-icon-process-node");
		
	}

	public static HashMap<String, String> getJobIconsMappings() {
		return jobIconsMappings;
	}

	public void setJobIconsMappings(HashMap<String, String> jobIconsMappings) {
		DecorationUtils.jobIconsMappings = jobIconsMappings;
	}
}
