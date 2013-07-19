package com.likya.tlossw.web.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.likya.tlossw.web.TlosSWBaseBean;

public class BeanUtils {

	public final static String BATCH_PROCESS_PAGE = "/inc/definitionPanels/batchProcessJobDef.xhtml";
	public final static String WEB_SERVICE_PAGE = "/inc/definitionPanels/webServiceJobDef.xhtml";
	public final static String FTP_PAGE = "/inc/definitionPanels/ftpJobDef.xhtml";
	public final static String FILE_PROCESS_PAGE = "/inc/definitionPanels/fileProcessJobDef.xhtml";
	public final static String FILE_LISTENER_PAGE = "/inc/definitionPanels/fileListenerJobDef.xhtml";
	public final static String DB_JOBS_PAGE = "/inc/definitionPanels/dbJobDef.xhtml";
	public final static String PROCESS_NODE_PAGE = "/inc/definitionPanels/processNodeJobDef.xhtml";
	public final static String REMOTE_SHELL_PAGE = "/inc/definitionPanels/remoteJobDef.xhtml";
	public final static String SYSTEM_COMMAND_PAGE = "/inc/definitionPanels/systemCommandJobDef.xhtml";
	public final static String SHELL_SCRIPT_PAGE = "/inc/definitionPanels/shellScriptJobDef.xhtml";
	public final static String DEFAULT_DEF_PAGE = "/inc/definitionPanels/defaultJobDef.xhtml";

	public final static String SCENARIO_PAGE = "/inc/definitionPanels/scenarioDef.xhtml";

	public final static String DEFAULT_TEST_PAGE = "/inc/livePanels/testLiveJS.xhtml";

	
	public static void addMessage(ResourceBundle messages, String fieldName, FacesMessage.Severity severity, String errorMessage, String miscText) {
		errorMessage = resolveMessage(messages, errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, errorMessage, miscText));
	}

	public static void addSuccessMessage(ResourceBundle messages, String fieldName, String errorMessage, String miscText) {
		errorMessage = resolveMessage(messages, errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, errorMessage, miscText));
	}

	public static void addFailMessage(ResourceBundle messages, String fieldName, String errorMessage, String miscText) {
		errorMessage = resolveMessage(messages, errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, miscText));
	}
	
	public static void addMessage(ResourceBundle messages, String formName, String fieldName, String errorMessage, String miscText) {

		errorMessage = resolveMessage(messages, errorMessage);

		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message;

		if (miscText != null) {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", (errorMessage + " " + miscText).trim());
		} else {
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errorMessage);
		}
		// report the message.
		context.addMessage(formName + ":" + fieldName, message);
	}

	public static String resolveMessage(ResourceBundle messages, String errorMessage) {

		if (errorMessage != null) {
			try {
				errorMessage = messages.getString(errorMessage);
			}
			// eat any errors, and just use original message if there is a
			// problem
			catch (MissingResourceException e) {
				TlosSWBaseBean.getLogger().error("Missing Resource bundle, could not display message");
			} catch (NullPointerException e) {
				TlosSWBaseBean.getLogger().error("Missing Resource bundle, could not dipslay message");
			}
		} else {
			errorMessage = "";
		}

		return errorMessage;
	}
	
}
