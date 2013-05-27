package com.likya.tlossw.web.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.MethodExpressionActionListener;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

@ManagedBean
@ViewScoped
public class DynamicMenuItem extends DefaultMenuModel implements ActionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6714587323517597322L;

	protected String valueExpression;
	protected DefaultMenuModel menuItem;
	private MenuItem selectedMenuItem;
	private MenuModel model;
	protected static UIViewRoot uiViewRoot = new UIViewRoot();

	public DynamicMenuItem() {
		this.model = new DefaultMenuModel();
//		System.out.println("DynamicMenuItem Constructor ..");
	}

	public MenuModel initMenu() {

		List<MenuItems> menuitems = new ArrayList<MenuItems>();

//		System.out.println("Menu elemanlari belirleniyor ..");

		// TODO Menu DB den okunacaksa buraya DB den okuma kodu konacak. hs

		if (menuitems.isEmpty()) {

			menuitems.add(new MenuItems(null, "ui-icon-home", " ", "#"));
//			menuitems.add(new MenuItems(null, "ui-icon-document", "Tlos SW", "#"));
			menuitems.add(new MenuItems(null, "ui-icon-document", "Definitions", "#"));
//			menuitems.add(new MenuItems(null, "ui-icon-document", "View", "#"));
			menuitems.add(new MenuItems(null, "ui-icon-document", "Planning", "#"));
			menuitems.add(new MenuItems(null, "ui-icon-document", "WorkSpace", "#"));
			menuitems.add(new MenuItems(null, "ui-icon-document", "Reporting", "#"));
			menuitems.add(new MenuItems(null, "ui-icon-document", "Administration", "#"));
			menuitems.add(new MenuItems(null, "ui-icon-document", "Preferences", "#"));
			menuitems.add(new MenuItems(null, "ui-icon-document", "Help", "#"));
			
			MenuItems logoutMenuItem = new MenuItems(null, "ui-icon-document", "Logout", "#");
			logoutMenuItem.setValueExpression("#{loginBean.logout}");
			menuitems.add(logoutMenuItem);

			menuitems.add(new MenuItems(" ", "ui-icon-home", "Home", "/"));

//			menuitems.add(new MenuItems("Tlos SW", "ui-icon-document", "File", "#"));

			menuitems.add(new MenuItems("Definitions", "ui-icon-document", "Jobs & Scenarios", "/inc/definitionPanels/jobsDef.jsf"));
			menuitems.add(new MenuItems("Definitions", "ui-icon-document", "Calendars", "/inc/definitionPanels/calendarSearchPanel.jsf"));
			menuitems.add(new MenuItems("Definitions", "ui-icon-document", "Alarms", "/inc/definitionPanels/alarmSearchPanel.jsf"));
			menuitems.add(new MenuItems("Definitions", "ui-icon-document", "Agents", "/inc/definitionPanels/advancedDefinitions/agentSearchPanel.jsf"));
			menuitems.add(new MenuItems("Definitions", "ui-icon-document", "Resources", "/inc/definitionPanels/advancedDefinitions/resourceSearchPanel.jsf"));
			menuitems.add(new MenuItems("Definitions", "ui-icon-document", "Advanced D.", "#"));

			menuitems.add(new MenuItems("Advanced D.", "ui-icon-document", "SLAs", "/inc/definitionPanels/advancedDefinitions/slaSearchPanel.jsf"));
			menuitems.add(new MenuItems("Advanced D.", "ui-icon-document", "Program Provision", "/inc/definitionPanels/advancedDefinitions/programProvisionSearchPanel.jsf"));

//			menuitems.add(new MenuItems("View", "ui-icon-document", "Logs", "#"));
//
//			menuitems.add(new MenuItems("Logs", "ui-icon-document", "Jobs", "#"));
//			menuitems.add(new MenuItems("Logs", "ui-icon-document", "Scenarios", "#"));
//			menuitems.add(new MenuItems("Logs", "ui-icon-document", "Trace", "#"));
//			menuitems.add(new MenuItems("Logs", "ui-icon-document", "HeartBeat", "#"));
//
//			menuitems.add(new MenuItems("View", "ui-icon-document", "Plans", "#"));
//			menuitems.add(new MenuItems("View", "ui-icon-document", "Past Runs", "#"));
			
			menuitems.add(new MenuItems("Planning", "ui-icon-document", "Available Resources ", "#"));
			menuitems.add(new MenuItems("Planning", "ui-icon-document", "SLA Time Check ", "#"));
			menuitems.add(new MenuItems("Planning", "ui-icon-document", "SLA Hardware Check ", "#"));
			menuitems.add(new MenuItems("Planning", "ui-icon-document", "SLA Program Provision Check", "#"));
			menuitems.add(new MenuItems("Planning", "ui-icon-document", "Find Resources", "#"));
			
			menuitems.add(new MenuItems("WorkSpace", "ui-icon-document", "Jobs & Scenarios", "/inc/livePanels/liveJobsScenarios.jsf"));
			//menuitems.add(new MenuItems("WorkSpace", "ui-icon-document", "Agents ", "/inc/livePanels/liveAgents.jsf"));
			menuitems.add(new MenuItems("WorkSpace", "ui-icon-document", "Resources ", "/inc/livePanels/liveResources.jsf"));
			//menuitems.add(new MenuItems("WorkSpace", "ui-icon-document", "Alarms", "#"));
			

			
			menuitems.add(new MenuItems("Reporting", "ui-icon-document", "Jobs & Scenarios ", "/inc/reportPanels/dashboardPanel.jsf"));
			menuitems.add(new MenuItems("Reporting", "ui-icon-document", "Alarms ", "#"));
			menuitems.add(new MenuItems("Reporting", "ui-icon-document", "Documentation ", "#"));
			//menuitems.add(new MenuItems("Reporting", "ui-icon-document", "Advanced R.", "#"));

			//menuitems.add(new MenuItems("Advanced R.", "ui-icon-document", "SLA Violotions", "#"));
			//menuitems.add(new MenuItems("Advanced R.", "ui-icon-document", "Audit", "#"));

			menuitems.add(new MenuItems("Administration", "ui-icon-document", "Users", "/inc/managementPanels/userSearchPanel.jsf"));
			menuitems.add(new MenuItems("Administration", "ui-icon-document", "Permissions", "/inc/managementPanels/permissionsPanel.jsf"));
			menuitems.add(new MenuItems("Administration", "ui-icon-document", "Performance", "#"));
			menuitems.add(new MenuItems("Administration", "ui-icon-document", "BackUps", "#"));
			menuitems.add(new MenuItems("Administration", "ui-icon-document", "Audits", "#"));
			menuitems.add(new MenuItems("Administration", "ui-icon-document", "DB Connections", "/inc/managementPanels/dbConnectionSearchPanel.jsf"));
			menuitems.add(new MenuItems("Administration", "ui-icon-document", "DB Access", "/inc/managementPanels/dbAccessSearchPanel.jsf"));
			menuitems.add(new MenuItems("Administration", "ui-icon-document", "Web Service Analysis", "/inc/managementPanels/webServiceWizardPanel.jsf"));
			menuitems.add(new MenuItems("Administration", "ui-icon-document", "Web Service Access", "/inc/managementPanels/wsAccessSearchPanel.jsf"));
			menuitems.add(new MenuItems("Administration", "ui-icon-document", "FTP Access", "/inc/managementPanels/ftpAccessSearchPanel.jsf"));

			menuitems.add(new MenuItems("Preferences", "ui-icon-document", "Theme", "#"));

			menuitems.add(new MenuItems("Help", "ui-icon-document", "Welcome", "#"));

			menuitems.add(new MenuItems("Welcome", "ui-icon-document", "Overview", "#"));
			menuitems.add(new MenuItems("Welcome", "ui-icon-document", "Tutorials", "#"));
			//menuitems.add(new MenuItems("Welcome", "ui-icon-document", "Samples", "#"));
			menuitems.add(new MenuItems("Welcome", "ui-icon-document", "What's New", "#"));
			//menuitems.add(new MenuItems("Welcome", "ui-icon-document", "Workbench", "#"));

			menuitems.add(new MenuItems("Help", "ui-icon-document", "Support", "#"));
			menuitems.add(new MenuItems("Help", "ui-icon-document", "About", "#"));

		}

//		System.out.println("Menu elemanlari belirlendi !");

//		System.out.println("Menu olusturuluyor ..");

		if (addMenu(menuitems)) {
//			System.out.println("Menu olusturuldu !");
			return model;
		} else {
//			System.out.println("Menu olusturmada SORUN  !!");
			return model;
		}
	}

	public boolean addMenu(List<MenuItems> menuitems) {

		List<UIComponent> contents = getModel().getContents();
		contents.clear();
		String name = null;
		String parent = null;

		Iterator<MenuItems> i = menuitems.iterator();

		while (i.hasNext()) {

			MenuItems whoIsNext = i.next();
			name = whoIsNext.getName();
			if (whoIsNext.getParent() != null)
				parent = whoIsNext.getParent().toString();

			if (parent == null) {
				
				if(whoIsNext.getValueExpression() != null) {
					// Logout için özel
					MenuItem item = new MenuItem();
					item.setActionExpression(createMethodExpression(whoIsNext.getValueExpression()));
					item.setValue(whoIsNext.getName());
					item.setId(uiViewRoot.createUniqueId());
					contents.add(item);
				} else {
					// First submenu
					Submenu submenu = new Submenu();
					submenu.setLabel(whoIsNext.getName());
					submenu.setId(uiViewRoot.createUniqueId());
					contents.add(submenu);
				}
				// submenu.getAttributes().put("submenu", whoIsNext);
				
//				System.out.println("ParentId = NULL <" + name + "> item i <KOK> altina yerlestirildi.");
			} else {
				String parentId = searchInMenuByName(contents, parent);
				if (parentId == null) {
//					System.out.println(" YOK, bir submenu olabilir fakat su ana kadar model e gecmemis. Menu de sorun olabilir !!");
					return false;
				} else {
//					System.out.println("ParentId = " + parentId + " <" + name + "> item i <" + parent + "> altina yerlestirildi.");

					MenuItem item = new MenuItem();
					item.setValue(name);
					item.setTitle(name);
					item.setUrl(whoIsNext.getLink());
					item.setIcon(whoIsNext.getIcon());
					item.setId(uiViewRoot.createUniqueId());
					item.setAjax(false);
					item.getAttributes().put("menuitem", whoIsNext);
					
					item.addActionListener(createMethodActionListener());
					
					UIComponent submenuItem = searchInMenuById(contents, parentId);

					if (submenuItem instanceof Submenu)
						submenuItem.getChildren().add(item);
					else if (submenuItem instanceof MenuItem) {

						Submenu submenu = new Submenu();
						submenu.setLabel((String) ((MenuItem) submenuItem).getValue());
						submenu.setId(uiViewRoot.createUniqueId());
						// submenu.getAttributes().put("submenu", whoIsNext);
						submenu.getChildren().add(item);

						if (submenuItem.getParent() != null) {
							submenuItem.getParent().getChildren().add(submenu);
							submenuItem.getParent().getChildren().remove(submenuItem);
						} else {
							contents.add(submenu);
							contents.remove(submenuItem);
						}
					} else {
//						System.out.println("Undefined Menu Item Type !!");
						return false;
					}
				}
			}
		}

		return true;

	}

	// search given element in Menu Items
	public static <T> T searchInMenu(Collection<UIComponent> collection, T element) {

		for (UIComponent contents : collection) {
			if (contents.equals(element)) {
				return element;
			}
			if (contents.getClass().equals(UIComponent.class))
				if (!(boolean) ((UIComponent) contents).getChildren().isEmpty()) {
					Collection<UIComponent> subCollection = ((UIComponent) contents).getChildren();
					return searchInMenu(subCollection, element);
				}
		}
		return null;
	}

	// search given element in Menu Items
	public static <T> String searchInMenuByName(Collection<UIComponent> collection, String parentName) {

		for (UIComponent contents : collection) {
			String name = "";
			if (contents instanceof MenuItem)
				name = (String) ((MenuItem) contents).getValue();
			else if (contents instanceof Submenu)
				name = (String) ((Submenu) contents).getLabel();

			if (name.equalsIgnoreCase(parentName)) {
				return (String) contents.getId();
			}
			if (contents instanceof UIComponent)
				if (!(boolean) ((UIComponent) contents).getChildren().isEmpty()) {
					Collection<UIComponent> subCollection = ((UIComponent) contents).getChildren();
					String result = searchInMenuByName(subCollection, parentName);
					if (result != null)
						return result;
				}
		}
		return null;
	}

	// search given element in Menu Items
	public static <T> UIComponent searchInMenuById(Collection<UIComponent> collection, String nodeId) {

		for (UIComponent contents : collection) {
			String elementNodeId = "";
			if (contents instanceof MenuItem)
				elementNodeId = (String) ((MenuItem) contents).getId();
			else if (contents instanceof Submenu)
				elementNodeId = (String) ((Submenu) contents).getId();

			if (elementNodeId.equalsIgnoreCase(nodeId)) {
				return ((UIComponent) contents);
			}
			if (contents instanceof UIComponent)
				if (!(boolean) ((UIComponent) contents).getChildren().isEmpty()) {
					Collection<UIComponent> subCollection = ((UIComponent) contents).getChildren();
					UIComponent result = searchInMenuById(subCollection, nodeId);
					if (result != null)
						return result;
					// return searchInMenuById(subCollection, nodeId);
				}
		}
		return null;
	}

	// Gets called when a MenuItem is clicked. The clicked MenuItem becomes the new top level MenuItem.
	@Override
	public void processAction(ActionEvent event) throws AbortProcessingException {
		if (event.getSource().getClass() == MenuItem.class) {
			MenuItem sourceItem = (MenuItem) event.getSource();
			MenuItem chosenMenuItem = (MenuItem) sourceItem.getAttributes().get("menuitem");
			setSelectedMenuItem(chosenMenuItem);
		}
	}

	public void actionListener(ActionEvent event) {
	    addMessage("MESAJ VAR !!" + getSelectedMenuItem());
	}
	
	protected MethodExpression createMethodExpression(String elValue) {
		
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		ELContext elCtx = facesCtx.getELContext();
		ExpressionFactory expFact = facesCtx.getApplication().getExpressionFactory();
		
		return expFact.createMethodExpression(elCtx, elValue, String.class, new Class[0]);
	}
	
	protected MethodExpressionActionListener createMethodActionListener() {
	
		valueExpression = "#{dynamicMenuItem.actionListener}";
		
		MethodExpressionActionListener actionListener = null;
		Class<?> valueType = Void.TYPE;
		Class<?>[] expectedParamTypes = new Class[] { ActionEvent.class };
		
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ExpressionFactory factory = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
			MethodExpression methodExpression = factory.createMethodExpression(context.getELContext(), valueExpression, valueType, expectedParamTypes);
			actionListener = new MethodExpressionActionListener(methodExpression);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return actionListener;
	}
    
	public MenuModel getModel() {
		return model;
	}

	public void setModel(MenuModel model) {
		this.model = model;
	}

	public MenuItem getSelectedMenuItem() {
		return selectedMenuItem;
	}

	public void setSelectedMenuItem(MenuItem selectedMenuItem) {
		this.selectedMenuItem = selectedMenuItem;
	}

	public void addMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String getValueExpression() {
		return valueExpression;
	}

	public void setValueExpression(String valueExpression) {
		this.valueExpression = valueExpression;
	}
}
