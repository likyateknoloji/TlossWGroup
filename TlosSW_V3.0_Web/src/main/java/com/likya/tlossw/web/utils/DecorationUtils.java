package com.likya.tlossw.web.utils;

import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;

public class DecorationUtils {

	
	/**
	 * yeni state yapisina gore duzenleme yaptim, onceden burada olmayan substatelere varolan ikonlardan koydum gecici olarak.
	 * burada ihtiyac olmayan substateler varsa kaldirilabilir. merve
	 * http://jquery-ui.googlecode.com/svn/tags/1.6rc5/tests/static/icons.html
	 * @param jobState
	 * @return
	 */
	
	public static String jobImageSetter(LiveStateInfo jobState) {
		String imagePath = null;

		if (jobState == null) {
			imagePath = "ui-icon-help";
		} else {
			if (jobState.getSubstateName() != null) {
				switch (jobState.getSubstateName().intValue()) {
				case SubstateName.INT_CREATED:
					imagePath = "ui-icon-gear";
					break;
				case SubstateName.INT_VALIDATED:
					imagePath = "ui-icon-lightbulb";
					break;
				case SubstateName.INT_IDLED:
					imagePath = "ui-icon-power";
					break;
				case SubstateName.INT_READY:
					imagePath = "ui-icon-clock";
					break;
				case SubstateName.INT_PAUSED:
					imagePath = "ui-icon-info";
					break;
				case SubstateName.INT_STAGE_IN:
					imagePath = "ui-icon-arrowrefresh-1-s";
					break;
				case SubstateName.INT_MIGRATING:
					imagePath = "ui-icon-extlink";
					break;
				case SubstateName.INT_ON_RESOURCE:
					if (jobState.getStatusName() != null) {
						if (jobState.getStatusName().equals(StatusName.TIME_IN)) {
							imagePath = "ui-icon-play";
							break;
						} else if (jobState.getStatusName().equals(StatusName.TIME_OUT)) {
							imagePath = "ui-icon-notice";
							break;
						}
					}
				case SubstateName.INT_HELD:
					imagePath = "ui-icon-pause";
					break;
				case SubstateName.INT_STAGE_OUT:
					imagePath = "ui-icon-arrowrefresh-1-n";
					break;
				case SubstateName.INT_COMPLETED:
					if ((jobState.getStateName() != null && jobState.getStateName().equals(StateName.FINISHED)) && (jobState.getStatusName() != null && jobState.getStatusName().equals(StatusName.SUCCESS))) {
						imagePath = "ui-icon-check";
						break;
					} else if ((jobState.getStateName() != null && jobState.getStateName().equals(StateName.FINISHED)) && (jobState.getStatusName() != null && jobState.getStatusName().equals(StatusName.FAILED))) {
						imagePath = "ui-icon-alert";
						break;
					}
				case SubstateName.INT_SKIPPED:
					imagePath = "ui-icon-seek-next";
					break;
				case SubstateName.INT_STOPPED:
					imagePath = "ui-icon-cancel";
					break;
				}
			}
		}
		return imagePath;
	}
}
