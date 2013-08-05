package com.likya.tlossw.web.live;

import org.primefaces.component.datatable.DataTable;

import com.likya.tlossw.model.auth.WebAppUser;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;

public interface JobManagementInterface {

	public DataTable getJobDataTable();

	public void refreshLivePanel(String scenarioPath);

	public void refreshTlosAgentPanel();

	public WebAppUser getWebAppUser();

	public JobInfoTypeClient getJobInTyCl();
}
