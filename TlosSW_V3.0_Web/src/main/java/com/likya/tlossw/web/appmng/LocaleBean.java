package com.likya.tlossw.web.appmng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

/**
 * <p>
 * The LocaleBean is responsible for keeping track of the current application
 * locale. The locale of the application can be changed using the a
 * selectOneMenu located in the login.xhtml. The application currently support
 * Turkish, English, French and German
 * </p>
 */

@ManagedBean(name = "localeBean")
@SessionScoped
public class LocaleBean implements Serializable {

	private static final long serialVersionUID = -6794484282234975471L;
	private List<SelectItem> availableLocales;

	private Locale currentLocale;
	private String currentLanguage;
	private static String curLanguage;

	public LocaleBean() {
		setCurrentLocale(FacesContext.getCurrentInstance().getViewRoot().getLocale());

		currentLanguage = currentLocale.getLanguage();
		curLanguage = currentLocale.getLanguage();
	}

	private void generateAvailableLocales() {
		availableLocales = new ArrayList<SelectItem>(0);

		// Add the default locale
		availableLocales.add(makeLocaleItem(FacesContext.getCurrentInstance().getApplication().getDefaultLocale()));

		// Add any other supported locales
		for (Iterator<Locale> iter = FacesContext.getCurrentInstance().getApplication().getSupportedLocales(); iter.hasNext();) {
			availableLocales.add(makeLocaleItem(iter.next()));
		}
	}

	private SelectItem makeLocaleItem(Locale toWrap) {
		if (toWrap != null) {
			return new SelectItem(toWrap.getLanguage(), toWrap.getDisplayName());
		}

		return null;
	}

	public List<SelectItem> getAvailableLocales() {
		if (availableLocales == null) {
			generateAvailableLocales();
		}

		return availableLocales;
	}

	public void setAvailableLocales(List<SelectItem> availableLocales) {
		this.availableLocales = availableLocales;
	}

	public Locale getCurrentLocale() {
		return currentLocale;
	}

	public void setCurrentLocale(Locale currentLocale) {
		this.currentLocale = currentLocale;
	}

	public String getCurrentLanguage() {
		return currentLanguage;
	}

	public void setCurrentLanguage(String currentLanguage) {
		curLanguage = currentLanguage;
		this.currentLanguage = currentLanguage;
	}

	public void applyLocale(Locale toApply) {
		setCurrentLocale(toApply);

		FacesContext.getCurrentInstance().getViewRoot().setLocale(toApply);
	}

//	public void localeChanged(ValueChangeEvent event) {
//		if (event.getNewValue() != null) {
//			applyLocale(new Locale(event.getNewValue().toString()));
//		}
//	}
	public void localeChanged(AjaxBehaviorEvent event) {
		if (event.getComponent().getAttributes() != null) {
			String currentLanguage = (String) ((UIOutput)event.getSource()).getValue();
			applyLocale(new Locale(currentLanguage));
		}
	}
	
	public static String getCurLanguage() {
		return curLanguage;
	}

}
