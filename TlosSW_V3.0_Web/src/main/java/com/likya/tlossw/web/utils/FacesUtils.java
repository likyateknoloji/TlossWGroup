package com.likya.tlossw.web.utils;

import javax.faces.context.FacesContext;

public class FacesUtils {

	public static String getRequestParameter(Object parameterKey) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(parameterKey);
	}
}
