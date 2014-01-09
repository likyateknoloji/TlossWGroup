/*
 * Copyright 2009 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.likya.tlossw.web.userpreferences;

import java.io.Serializable;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class UserPreferences implements Serializable {

	private static final long serialVersionUID = -2150653771700666896L;

	private String theme = null;

	private String jobStateIconCssPath = null;
	
	private String jobStateColorCssPath  = null;
	
	private String jobIconCssPath  = null;
	
	private boolean transformToLocalTime  = true;
	
	
	public UserPreferences() {
		theme = new String("cupertino");
		jobStateIconCssPath = new String("job-state-icon-flags");
		jobStateColorCssPath = new String("job-state-color-default");
		jobIconCssPath = new String("job-icon-set1");
		transformToLocalTime = true;
	}
	
	public UserPreferences(String theme, String jobStateIconCssPath,
			String jobStateColorCssPath, String jobIconCssPath, boolean transformToLocalTime) {
		this.theme = theme;
		this.jobStateIconCssPath = jobStateIconCssPath;
		this.jobStateColorCssPath = jobStateColorCssPath;
		this.jobIconCssPath = jobIconCssPath;
		this.transformToLocalTime = transformToLocalTime;
	}

	public String getTheme() {
		
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if (params.containsKey("theme")) {
			theme = params.get("theme");
		}

		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getJobStateIconCssPath() {
		
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if (params.containsKey("jobStateIconCssPath")) {
			jobStateIconCssPath = params.get("jobStateIconCssPath");
		}

		return jobStateIconCssPath;
	}

	public void setJobStateIconCssPath(String jobStateIconCssPath) {
		this.jobStateIconCssPath = jobStateIconCssPath;
	}

	public String getJobStateColorCssPath() {
		
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if (params.containsKey("jobStateColorCssPath")) {
			jobStateColorCssPath = params.get("jobStateColorCssPath");
		}

		return jobStateColorCssPath;
	}

	public void setJobStateColorCssPath(String jobStateColorCssPath) {
		this.jobStateColorCssPath = jobStateColorCssPath;
	}

	public String getJobIconCssPath() {
		
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if (params.containsKey("jobIconCssPath")) {
			jobIconCssPath = params.get("jobIconCssPath");
		}

		return jobIconCssPath;
	}

	public void setJobIconCssPath(String jobIconCssPath) {
		this.jobIconCssPath = jobIconCssPath;
	}

	public boolean isTransformToLocalTime() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if (params.containsKey("transformToLocalTime")) {
			transformToLocalTime = Boolean.getBoolean(params.get("transformToLocalTime"));
		}
		return transformToLocalTime;
	}

	public void setTransformToLocalTime(boolean transformToLocalTime) {
		this.transformToLocalTime = transformToLocalTime;
	}
}
