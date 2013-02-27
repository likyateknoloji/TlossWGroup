package com.likya.tlossw.utils.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceReader {
	
	private static final String resourcePath = "resources.";
	private static final String resourceName = "TlosSWTextBundle";
	
	public static ResourceBundle getResourceBundle(String language, String country) {
		
		Locale currentLocale;
		
		ResourceBundle resourceBundle;

		currentLocale = new Locale(language, country);

		Locale.setDefault(currentLocale);
		
		resourceBundle = ResourceBundle.getBundle("resources.MessagesBundle", currentLocale);

		return resourceBundle;
		
	}

	public static ResourceBundle getResourceBundle(Locale thisLocale) {

		ResourceBundle resourceBundle = ResourceBundle.getBundle(resourcePath + resourceName, thisLocale);
	
		return resourceBundle;
	
	}

	public static ResourceBundle getResourceBundle() {

		/**
		 * @author serkan taþ
		 * 25.02.2013
		 * Aþaðýdaki linke binaen deðiþtirildi.
		 * http://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle
		 */

		// Locale thisLocale = Locale.getDefault();
		// 	ResourceBundle resourceBundle = ResourceBundle.getBundle(resourcePath + resourceName, thisLocale);
		
		ResourceBundle resourceBundle = ResourceBundle.getBundle(resourcePath + resourceName, new UTF8Control());

		return resourceBundle;
	
	}

	public static String getResourceName() {
		return resourceName;
	}

	public static String getResourcePath() {
		return resourcePath;
	}
}
