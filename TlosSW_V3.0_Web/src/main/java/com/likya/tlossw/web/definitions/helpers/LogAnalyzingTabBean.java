package com.likya.tlossw.web.definitions.helpers;

import java.util.Collection;

import javax.faces.model.SelectItem;

import com.likya.tlos.model.xmlbeans.common.EmailListDocument.EmailList;
import com.likya.tlos.model.xmlbeans.common.STEmail;
import com.likya.tlos.model.xmlbeans.data.ActionDocument.Action;
import com.likya.tlos.model.xmlbeans.data.CodeType;
import com.likya.tlos.model.xmlbeans.data.DirectionType;
import com.likya.tlos.model.xmlbeans.data.ElseDocument.Else;
import com.likya.tlos.model.xmlbeans.data.EventDocument.Event;
import com.likya.tlos.model.xmlbeans.data.FindWhatDocument.FindWhat;
import com.likya.tlos.model.xmlbeans.data.ForcedResultDocument.ForcedResult;
import com.likya.tlos.model.xmlbeans.data.LogAnalysisDocument.LogAnalysis;
import com.likya.tlos.model.xmlbeans.data.ModeType;
import com.likya.tlos.model.xmlbeans.data.ThenDocument.Then;
import com.likya.tlos.model.xmlbeans.state.LiveStateInfoDocument.LiveStateInfo;
import com.likya.tlos.model.xmlbeans.state.StateNameDocument.StateName;
import com.likya.tlos.model.xmlbeans.state.StatusNameDocument.StatusName;
import com.likya.tlos.model.xmlbeans.state.SubstateNameDocument.SubstateName;
import com.likya.tlossw.web.utils.ComboListUtils;

public class LogAnalyzingTabBean {
	private String regex;
	private String string;

	private boolean useLogAnalyzer = false;

	private boolean matchWord = false;
	private boolean matchCase = false;

	private boolean active = false;
	private boolean activeElse = false;

	private boolean els = false;

	private String email;
	private String emailElse;

	private String direction;

	private String code;
	private String codeElse;

	private String dependencyType;

	private String dependencyExpression;
	private String depStateName;
	private String depStatusName;
	private String depSubstateName;

	private Collection<SelectItem> jobStatusNameList;
	private Collection<SelectItem> jobSubStateNameList;
	private Collection<SelectItem> jobStateNameList;

	private String dependencyTypeElse;

	private String depStateNameElse;
	private String depStatusNameElse;
	private String depSubstateNameElse;

	private int searchType = 0;

	private int actionType = 1;

	public LogAnalysis fillLogAnalysis(LogAnalysis logAnalysis) {

		FindWhat findWhat = logAnalysis.getFindWhat();
		if (findWhat == null) {
			findWhat = FindWhat.Factory.newInstance();
		}

		// regex secilirse girilecek degeri set etme
		if (searchType == 1) {

			findWhat.setMode(ModeType.REG_EX);

			findWhat.setStringValue(regex);

		}
		// string secilirse girilecek degeri set etme
		else if (searchType == 2) {

			findWhat.setMode(ModeType.NORMAL);
			findWhat.setStringValue(string);

			// stringe ozel tum kelimeyi arama
			findWhat.setMatchWholeWordOnly(matchWord);

		}
		// Buyuk kucuk harf duyarlı
		findWhat.setMatchCase(matchCase);

		// Arama yonu
		findWhat.setDirection(DirectionType.Enum.forString(direction));
		// Action cretion
		Action action = logAnalysis.getAction();
		if (action == null) {
			action = Action.Factory.newInstance();
		}
		// Then
		Then then = action.getThen();
		if (then == null) {
			then = Then.Factory.newInstance();
		}
		// Event creation
		Event event = then.getEvent();
		if (event == null) {
			event = Event.Factory.newInstance();
		}

		event.setCode(CodeType.Enum.forString(code));
		// EmailList creation
		EmailList emailList = event.getEmailList();
		if (emailList == null) {
			emailList = EmailList.Factory.newInstance();
		}

		STEmail stEmail = emailList.addNewEmail();
		stEmail.setStringValue(email);

		event.setEmailList(emailList);
		then.setEvent(event);
		// ForcedResult creation
		ForcedResult forcedResult = then.getForcedResult();
		if (forcedResult == null) {
			forcedResult = ForcedResult.Factory.newInstance();
		}
		// liveStateInfo creation
		LiveStateInfo liveStateInfo = forcedResult.getLiveStateInfo();
		if (liveStateInfo == null) {
			liveStateInfo = LiveStateInfo.Factory.newInstance();
		}
		// liveStateInfo.setLSIDateTime();
		// if(liveStateInfo.getStateName()==null){
		// StateName stateName=StateName.Factory.newInstance();
		// liveStateInfo.getStateName();
		// if(dependencyType.equals("State")){

		liveStateInfo.setStateName(StateName.Enum.forString(depStateName));
		// }

		if (dependencyType.equals("SubState") || dependencyType.equals("Status")) {
			liveStateInfo.setSubstateName(SubstateName.Enum.forString(depSubstateName));
		}

		if (dependencyType.equals("Status")) {
			liveStateInfo.setStatusName(StatusName.Enum.forString(depStatusName));
		}

		forcedResult.setLiveStateInfo(liveStateInfo);
		forcedResult.setActive(active);
		then.setForcedResult(forcedResult);
		// then.setEvent(event);
		action.setThen(then);

		/*
		 * if(event.getContent()==null){ Content content=Content.Factory.newInstance(); content.getLogLineNumForward();
		 * 
		 * }
		 */

		if (els == true) {
			Else elses = action.getElse();
			if (elses == null) {

				elses = Else.Factory.newInstance();
				Event eventElse = elses.getEvent();
				if (eventElse == null) {

					eventElse = Event.Factory.newInstance();

					eventElse.setCode(CodeType.Enum.forString(codeElse));
				}
				EmailList emailListElse = eventElse.getEmailList();
				if (emailListElse == null) {
					emailListElse = EmailList.Factory.newInstance();

					STEmail stEmailElse = emailListElse.addNewEmail();
					stEmailElse.setStringValue(emailElse);
					eventElse.setEmailList(emailListElse);
				}
				ForcedResult forcedResultElse = elses.getForcedResult();
				if (forcedResultElse == null) {
					forcedResultElse = ForcedResult.Factory.newInstance();
					forcedResultElse.setActive(activeElse);
				}
				LiveStateInfo liveStateInfoElse = forcedResultElse.getLiveStateInfo();
				if (forcedResultElse.getActive() == true) {

					if (liveStateInfoElse == null) {
						liveStateInfoElse = LiveStateInfo.Factory.newInstance();
						// liveStateInfo.setLSIDateTime();
						// liveStateInfoElse.getStateName();
					}
					liveStateInfoElse.setStateName(StateName.Enum.forString(depStateNameElse));

					if (dependencyTypeElse.equals("SubState") || dependencyTypeElse.equals("Status")) {
						liveStateInfoElse.setStatusName(StatusName.Enum.forString(depStatusNameElse));
					}
					if (dependencyTypeElse.equals("Status")) {
						liveStateInfoElse.setSubstateName(SubstateName.Enum.forString(depSubstateNameElse));
					}

					eventElse.setEmailList(emailListElse);

				}

				forcedResultElse.setLiveStateInfo(liveStateInfoElse);
				forcedResultElse.setActive(activeElse);
				elses.setForcedResult(forcedResultElse);
			}

			elses.setEvent(event);
			action.setElse(elses);
		}

		logAnalysis.setActive(useLogAnalyzer);
		logAnalysis.setAction(action);
		logAnalysis.setFindWhat(findWhat);

		return logAnalysis;
	}

	public void fillLogAnalysisTab(LogAnalysis logAnalysis) {
		if (logAnalysis != null && logAnalysis.getActive() != false) {
			useLogAnalyzer = true;
			if (logAnalysis.getFindWhat() != null && logAnalysis.getFindWhat().getMode() != null) {
				if (logAnalysis.getFindWhat().getMode().toString() == "regEx") {
					searchType = 1;
					regex = logAnalysis.getFindWhat().getStringValue();
				} else {
					searchType = 2;
					string = logAnalysis.getFindWhat().getStringValue();
				}
			}
			if (logAnalysis.getFindWhat().getMatchCase() != false) {
				matchCase = logAnalysis.getFindWhat().getMatchCase();
			}
			if (logAnalysis.getFindWhat().getMatchWholeWordOnly() != false) {
				matchWord = logAnalysis.getFindWhat().getMatchWholeWordOnly();
			}

			if (logAnalysis.getFindWhat().getDirection() != null) {
				direction = logAnalysis.getFindWhat().getDirection().toString();
				// Then control
				if (logAnalysis.getAction().getThen() != null) {
					if (logAnalysis.getAction().getThen().getEvent().getCode().toString().equals("email")) {
						code = "email";
						email = logAnalysis.getAction().getThen().getEvent().getEmailList().getEmailArray(0);
					} else {
						code = "waitMe";
					}

					if (logAnalysis.getAction().getThen().getForcedResult().getActive() != false) {
						active = true;
					}
					LiveStateInfo liveStateInfo = logAnalysis.getAction().getThen().getForcedResult().getLiveStateInfo();
					depStateName = liveStateInfo.getStateName().toString();
					dependencyType = "State";
					if (liveStateInfo.getSubstateName() != null) {
						dependencyType = "SubState";
						depSubstateName = liveStateInfo.getSubstateName().toString();
					}
					if (liveStateInfo.getStatusName() != null) {
						dependencyType = "Status";
						depStatusName = liveStateInfo.getStatusName().toString();
					}

				}
			}
			if (logAnalysis.getAction().getElse() != null) {
				els = true;

				if (logAnalysis.getAction().getElse().getEvent().getCode().toString().equals("email")) {
					emailElse = logAnalysis.getAction().getThen().getEvent().getEmailList().getEmailArray(0);
					codeElse = "email";
				} else {
					codeElse = "waitMe";
				}
				if (logAnalysis.getAction().getElse().getForcedResult().getActive() != false) {
					activeElse = logAnalysis.getAction().getElse().getForcedResult().getActive();
				}
				if (logAnalysis.getAction().getElse().getForcedResult() != null) {
					LiveStateInfo liveStateInfoElse = logAnalysis.getAction().getElse().getForcedResult().getLiveStateInfo();
					depStateNameElse = liveStateInfoElse.getStateName().toString();
					dependencyTypeElse = "State";
					if (liveStateInfoElse.getSubstateName() != null) {
						dependencyTypeElse = "SubState";
						depSubstateNameElse = liveStateInfoElse.getSubstateName().toString();

					}
					if (liveStateInfoElse.getStatusName() != null) {
						dependencyTypeElse = "Status";
						depStatusNameElse = liveStateInfoElse.getStatusName().toString();

					}
				}
			}
		}
	}

	public boolean isUseLogAnalyzer() {
		return useLogAnalyzer;
	}

	public void setUseLogAnalyzer(boolean useLogAnalyzer) {
		this.useLogAnalyzer = useLogAnalyzer;
	}

	public int getSearchType() {
		return searchType;
	}

	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public boolean isMatchCase() {
		return matchCase;
	}

	public void setMatchCase(boolean matchCase) {
		this.matchCase = matchCase;
	}

	public boolean isEls() {
		return els;
	}

	public void setEls(boolean els) {
		this.els = els;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public boolean isMatchWord() {
		return matchWord;
	}

	public void setMatchWord(boolean matchWord) {
		this.matchWord = matchWord;
	}

	public String getDependencyType() {
		return dependencyType;
	}

	public void setDependencyType(String dependencyType) {
		this.dependencyType = dependencyType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getDependencyExpression() {
		return dependencyExpression;
	}

	public void setDependencyExpression(String dependencyExpression) {
		this.dependencyExpression = dependencyExpression;
	}

	public String getDepStateName() {
		return depStateName;
	}

	public void setDepStateName(String depStateName) {
		this.depStateName = depStateName;
	}

	public String getDepSubstateName() {
		return depSubstateName;
	}

	public void setDepSubstateName(String depSubstateName) {
		this.depSubstateName = depSubstateName;
	}

	public Collection<SelectItem> getJobStateNameList() {
		if (jobStateNameList == null) {
			jobStateNameList = ComboListUtils.constructJobStateList();
		}
		return jobStateNameList;
	}

	public Collection<SelectItem> getJobSubStateNameList() {
		if (jobSubStateNameList == null) {
			jobSubStateNameList = ComboListUtils.constructJobSubStateList();
		}
		return jobSubStateNameList;
	}

	public Collection<SelectItem> getJobStatusNameList() {
		if (jobStatusNameList == null) {
			jobStatusNameList = ComboListUtils.constructJobStatusNameList();
		}
		return jobStatusNameList;
	}

	public void setJobStatusNameList(Collection<SelectItem> jobStatusNameList) {
		this.jobStatusNameList = jobStatusNameList;
	}

	public String getDepStatusName() {
		return depStatusName;
	}

	public void setDepStatusName(String depStatusName) {
		this.depStatusName = depStatusName;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getDependencyTypeElse() {
		return dependencyTypeElse;
	}

	public void setDependencyTypeElse(String dependencyTypeElse) {
		this.dependencyTypeElse = dependencyTypeElse;
	}

	public String getEmailElse() {
		return emailElse;
	}

	public void setEmailElse(String emailElse) {
		this.emailElse = emailElse;
	}

	public boolean isActiveElse() {
		return activeElse;
	}

	public void setActiveElse(boolean activeElse) {
		this.activeElse = activeElse;
	}

	public String getCodeElse() {
		return codeElse;
	}

	public void setCodeElse(String codeElse) {
		this.codeElse = codeElse;
	}

	public String getDepStateNameElse() {
		return depStateNameElse;
	}

	public void setDepStateNameElse(String depStateNameElse) {
		this.depStateNameElse = depStateNameElse;
	}

	public String getDepStatusNameElse() {
		return depStatusNameElse;
	}

	public void setDepStatusNameElse(String depStatusNameElse) {
		this.depStatusNameElse = depStatusNameElse;
	}

	public String getDepSubstateNameElse() {
		return depSubstateNameElse;
	}

	public void setDepSubstateNameElse(String depSubstateNameElse) {
		this.depSubstateNameElse = depSubstateNameElse;
	}

}
