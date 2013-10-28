/*
 * @(#)file      SimpleStandardMBean.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.1
 * @(#)lastedit  03/04/22
 * @(#)build     jmxremote-1_0_1_04-b58 2005.11.23_16:04:12_MET
 *
 * Copyright 2005 Sun Microsystems, Inc.  All rights reserved. Use is subject to license terms.
 */

package com.likya.tlossw.jmx.beans;

import java.util.ArrayList;

import com.likya.tlos.model.xmlbeans.agent.SWAgentDocument.SWAgent;
import com.likya.tlos.model.xmlbeans.dbconnections.DbConnectionProfileDocument.DbConnectionProfile;
import com.likya.tlossw.model.WebSpaceWideRegistery;
import com.likya.tlossw.model.client.resource.ResourceInfoTypeClient;
import com.likya.tlossw.model.client.resource.TlosAgentInfoTypeClient;
import com.likya.tlossw.model.client.spc.JobInfoTypeClient;
import com.likya.tlossw.model.client.spc.SpcInfoTypeClient;
import com.likya.tlossw.model.client.spc.TreeInfoType;
import com.likya.tlossw.model.jmx.JmxAgentUser;
import com.likya.tlossw.model.jmx.JmxUser;
import com.likya.tlossw.model.tree.resource.TlosSWResourceNode;

/**
 * This is the management interface explicitly defined for the
 * "SimpleStandard" standard MBean.
 * 
 * The "SimpleStandard" standard MBean implements this interface
 * in order to be manageable through a JMX agent.
 * 
 * The "SimpleStandardMBean" interface shows how to expose for management:
 * - a read/write attribute (named "State") through its getter and setter
 * methods,
 * - a read-only attribute (named "NbChanges") through its getter method,
 * - an operation (named "reset").
 */
public interface ProcessInfoProviderMBean {

	/**
	 * Getter: set the "State" attribute of the "SimpleStandard" standard
	 * MBean.
	 * 
	 * @return the current value of the "State" attribute.
	 */
	public String getState();

	/**
	 * Setter: set the "State" attribute of the "SimpleStandard" standard
	 * MBean.
	 * 
	 * @param <VAR>s</VAR> the new value of the "State" attribute.
	 */
	public void setState(String s);

	/**
	 * Getter: get the "NbChanges" attribute of the "SimpleStandard" standard
	 * MBean.
	 * 
	 * @return the current value of the "NbChanges" attribute.
	 */
	public int getNbChanges();

	/**
	 * Operation: reset to their initial values the "State" and "NbChanges"
	 * attributes of the "SimpleStandard" standard MBean.
	 */
	public void reset();

	public Object retrieveGlobalStates(JmxAgentUser jmxAgentUser);

	public Object runningJobs();

	public JobInfoTypeClient retrieveJobDetails(JmxUser jmxUser, String groupId, String jobId, Boolean transformToLocalTime);

	public ArrayList<String> retrieveViewFiles(JmxUser jmxUser);

	public TreeInfoType retrieveTreeInfo(JmxUser jmxUser, String runId, ArrayList<String> scenariodIdList);

	public boolean retrieveWaitConfirmOfGUI(JmxUser jmxUser);

	public ArrayList<String> retrievePlanIds(JmxUser jmxUser);

	public String retrieveMaxRunId(JmxUser jmxUser);

	public SpcInfoTypeClient retrieveSpcInfo(JmxUser jmxUser, String treePath);

//	public ScenarioNode getLiveTreeInfo(JmxUser jmxUser, ScenarioNode scenarioNode);

	public WebSpaceWideRegistery retrieveSpaceWideRegistery(JmxUser jmxUser);

	public TlosSWResourceNode getLiveResourceTreeInfo(JmxUser jmxUser, TlosSWResourceNode tlosSpaceWideNode);

	public TlosAgentInfoTypeClient retrieveTlosAgentInfo(JmxUser jmxUser, int tlosAgentId);

	public ArrayList<JobInfoTypeClient> getAgentsJobList(JmxUser jmxUser, int tlosAgentId, Boolean transformToLocalTime);

	public ArrayList<SWAgent> getAgentList(JmxUser jmxUser);

	public boolean runningInstanceExists(JmxUser jmxUser);

	public boolean testDBConnection(JmxUser jmxUser, DbConnectionProfile dbConnectionProfile);

	public ArrayList<ResourceInfoTypeClient> getResourceInfoTypeClientList(JmxUser jmxUser);

	public ArrayList<TlosAgentInfoTypeClient> getTlosAgentInfoTypeClientList(JmxUser jmxUser, String resourceName);
}
