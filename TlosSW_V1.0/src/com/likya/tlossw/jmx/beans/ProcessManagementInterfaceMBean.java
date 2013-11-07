/*
 * @(#)file      ProcessManagementInterfaceMBean.java
 * @(#)author    Likya Teknoloji
 * @(#)version   1.0
 * @(#)lastedit  09/04/21
 * @(#)build     jmxremote-1_0_1_04-b58 2005.11.23_16:04:12_MET
 *
 * Copyright 2009 Likya Teknoloji  All rights reserved. Use is subject to license terms.
 */

package com.likya.tlossw.jmx.beans;

import java.util.ArrayList;

import com.likya.tlos.model.xmlbeans.swresourcenagentresults.ResourceDocument.Resource;
import com.likya.tlossw.model.TlosJmxReturnValue;
import com.likya.tlossw.model.jmx.JmxUser;


/**
 * This is the management interface explicitly defined for the
 * "SimpleStandard" standard MBean.
 *
 * The "SimpleStandard" standard MBean implements this interface
 * in order to be manageable through a JMX agent.
 *
 * The "SimpleStandardMBean" interface shows how to expose for management:
 * - a read/write attribute (named "State") through its getter and setter
 *   methods,
 * - a read-only attribute (named "NbChanges") through its getter method,
 * - an operation (named "reset").
 */
public interface ProcessManagementInterfaceMBean {

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
    
    /**
     * 
     */
    
	public void shutdown(JmxUser jmxUser);
	
	public TlosJmxReturnValue addJob(JmxUser jmxUser, String jobPropertiesXML);

	/**
	 * 
	 * @param jobPath
	 */
	public void stopJob(JmxUser jmxUser, String jobPath);

	void retryJob(JmxUser jmxUser, String jobPath);

	void doSuccess(JmxUser jmxUser, String jobPath);

	void skipJob(JmxUser jmxUser, String jobPath);

	void pauseJob(JmxUser jmxUser, String jobPath);

	void resumeJob(JmxUser jmxUser, String jobPath);

	void startJob(JmxUser jmxUser, String jobPath);
	
	void suspendScenario(JmxUser jmxUser, String scenarioId);

	void resumeScenario(JmxUser jmxUser, String scenarioId);

	void stopScenario(JmxUser jmxUser, String scenarioId, Boolean isForced);

	void restartScenario(JmxUser jmxUser, String scenarioId);
	
	public void recover(JmxUser jmxUser);
	
	public void shiftTransactionTime(JmxUser jmxUser, boolean backupReports);
	
	public void startOver(JmxUser jmxUser, boolean backupReports);
	
	public void forceCpcStart(JmxUser jmxUser);

	public void deactivateTlosAgent(JmxUser jmxUser, int agentId, Boolean isForced);
	
	public void activateTlosAgent(JmxUser jmxUser, int agentId);

	public void startUserBasedJob(JmxUser jmxUser, String jobPath);

	public ArrayList<Resource> getAvailableResourcesForJob(JmxUser jmxUser, String jobPath);

	public boolean assignAgentForJob(JmxUser jmxUser, String jobPath, String agentId);

}
