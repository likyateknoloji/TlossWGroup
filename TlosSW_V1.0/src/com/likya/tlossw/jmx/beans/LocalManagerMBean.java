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
public interface LocalManagerMBean {

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
    
	public void shutdown(String str);
	
	public void shiftTransitionTime(boolean backupReports);
	
	public void startOver(boolean backupReports);
	
	public void simulateGunDonumu();
	// public void redeploy(String str);

}
