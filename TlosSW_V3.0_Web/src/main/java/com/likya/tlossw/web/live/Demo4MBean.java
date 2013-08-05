package com.likya.tlossw.web.live;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.NodeSelectEvent;

import com.likya.tlossw.web.TlosSWBaseBean;

@ManagedBean(name = "demo4MBean")
@ViewScoped
public class Demo4MBean extends TlosSWBaseBean implements Serializable {

	private static final long serialVersionUID = -504537811128309503L;

	private String activeLivePanel = RESOURCE_PANEL;

	public final static String RESOURCELIST_PANEL = "21.xhtml";
	public final static String RESOURCE_PANEL = "22.xhtml";
	public final static String TLOSAGENT_PANEL = "23.xhtml";

	public void onNodeSelect(NodeSelectEvent event) {

		String nodeType = event.getTreeNode().getType();

		if (nodeType.equals("First")) {
			activeLivePanel = RESOURCELIST_PANEL;
		} else if (nodeType.equals("Second")) {
			activeLivePanel = RESOURCE_PANEL;
		} else if (nodeType.equals("Third")) {
			activeLivePanel = TLOSAGENT_PANEL;
		}
		
//		try {
//			FacesContext.getCurrentInstance().getExternalContext().redirect(activeLivePanel + "?faces-redirect=true");
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), "null", activeLivePanel + "?faces-redirect=true");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

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

	public String getActiveLivePanel() {
		return activeLivePanel;
	}

	public void setActiveLivePanel(String activeLivePanel) {
		this.activeLivePanel = activeLivePanel;
	}

}
