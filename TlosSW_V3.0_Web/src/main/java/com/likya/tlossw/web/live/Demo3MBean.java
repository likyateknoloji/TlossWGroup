package com.likya.tlossw.web.live;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.NodeSelectEvent;

import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "demo3MBean")
@ViewScoped
public class Demo3MBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -504537811128309503L;

	private String activePanel = FIRST_PANEL;

	public final static String FIRST_PANEL = "first.xhtml";
	public final static String SECOND_PANEL = "second.xhtml";
	public final static String THIRD_PANEL = "third.xhtml";

	public void onNodeSelect(NodeSelectEvent event) {

		String nodeType = event.getTreeNode().getType();
		if (nodeType.equals("First")) {

			activePanel = FIRST_PANEL;
			
		} else if (nodeType.equals("Second")) {

			activePanel = SECOND_PANEL;
			
		} else if (nodeType.equals("Third")) {

			activePanel = THIRD_PANEL;
		}
		
	}
	
	public void printMe1(ActionEvent e) {
		System.out.println("Me 1");
	}

	public void printMe2(ActionEvent e) {
		System.out.println("Me 2");
	}
	
	public void printMe3(ActionEvent e) {
		System.out.println("Me 3");
	}

	public String getActivePanel() {
		return activePanel;
	}

	public void setActivePanel(String activePanel) {
		this.activePanel = activePanel;
	}

}
