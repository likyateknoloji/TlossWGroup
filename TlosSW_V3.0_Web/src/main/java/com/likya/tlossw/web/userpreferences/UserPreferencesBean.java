package com.likya.tlossw.web.userpreferences;

/*
 * Copyright 2009-2011 Prime Technology.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "userPreferencesBean")
@SessionScoped
public class UserPreferencesBean implements Serializable {
       
    /**
	 *  Kullanicinin kendi secimleri ile calisabilmesi icin
	 */
	private static final long serialVersionUID = 8989360323299317309L;

	private Map<String, String> themes;
	
	private Map<String, String> jobStateIconCssPaths;
	
	private Map<String, String> jobStateColorCssPaths;
	
	private Map<String, String> jobIconCssPaths;
   
    private List<Theme> advancedThemes;
   
    private String theme;
   
	private String jobStateIconCssPath = null;
	
	private String jobStateColorCssPath  = null;
	
	private String jobIconCssPath  = null;
	
    private UserPreferences up;

    @PostConstruct
    public void init() {
    	
//        FacesContext.getCurrentInstance().addMessage(null, 
//                new FacesMessage("Test Message from @PostConstruct"));
        
    	if (up == null) up = new UserPreferences();
    	
        theme = up.getTheme();
        jobStateIconCssPath = up.getJobStateIconCssPath();
        jobStateColorCssPath = up.getJobStateColorCssPath();
        jobIconCssPath = up.getJobIconCssPath();

        jobStateIconCssPaths = new TreeMap<String, String>();
        jobStateIconCssPaths.put("Default", "job-state-icon-default");
        jobStateIconCssPaths.put("Flags", "job-state-icon-flags");
        jobStateIconCssPaths.put("Geo Animations", "job-state-icon-geo-animated");
        jobStateIconCssPaths.put("Whirling Balls", "job-state-icon-ball-animated");
        
        jobStateColorCssPaths = new TreeMap<String, String>();
        jobStateColorCssPaths.put("Default", "job-state-color-default");
        jobStateColorCssPaths.put("Set1", "job-state-color-set1");
        
        jobIconCssPaths = new TreeMap<String, String>();
        jobIconCssPaths.put("Default", "job-icon-default");
        jobIconCssPaths.put("Set1", "job-icon-set1");

        
        advancedThemes = new ArrayList<Theme>();
        advancedThemes.add(new Theme("afterdark", "afterdark.png"));
        advancedThemes.add(new Theme("afternoon", "afternoon.png"));
        //advancedThemes.add(new Theme("afterwork", "afterwork.png"));
        //advancedThemes.add(new Theme("aristo", "aristo.png"));
        // advancedThemes.add(new Theme("black-tie", "black-tie.png"));
        advancedThemes.add(new Theme("blitzer", "blitzer.png"));
        advancedThemes.add(new Theme("bluesky", "bluesky.png"));
        //advancedThemes.add(new Theme("bootstrap", "bootstrap.png"));
        //advancedThemes.add(new Theme("casablanca", "casablanca.png"));
        //advancedThemes.add(new Theme("cruze", "cruze.png"));
        advancedThemes.add(new Theme("cupertino", "cupertino.png"));    
        //advancedThemes.add(new Theme("dark-hive", "dark-hive.png"));
        //advancedThemes.add(new Theme("dot-luv", "dot-luv.png"));
        //advancedThemes.add(new Theme("eggplant", "eggplant.png"));
        advancedThemes.add(new Theme("excite-bike", "excite-bike.png"));
        //advancedThemes.add(new Theme("flick", "flick.png"));
        advancedThemes.add(new Theme("glass-x", "glass-x.png"));
        advancedThemes.add(new Theme("home", "home.png"));
        //advancedThemes.add(new Theme("hot-sneaks", "hot-sneaks.png"));
        advancedThemes.add(new Theme("humanity", "humanity.png"));
        advancedThemes.add(new Theme("le-frog", "le-frog.png"));
        //advancedThemes.add(new Theme("midnight", "midnight.png"));
        //advancedThemes.add(new Theme("mint-choc", "mint-choc.png"));
        //advancedThemes.add(new Theme("overcast", "overcast.png"));
        //advancedThemes.add(new Theme("pepper-grinder", "pepper-grinder.png"));
        advancedThemes.add(new Theme("redmond", "redmond.png"));
        //advancedThemes.add(new Theme("rocket", "rocket.png"));
        //advancedThemes.add(new Theme("sam", "sam.png"));
        //advancedThemes.add(new Theme("smoothness", "smoothness.png"));
        //advancedThemes.add(new Theme("south-street", "south-street.png"));
        //advancedThemes.add(new Theme("start", "start.png"));
        advancedThemes.add(new Theme("sunny", "sunny.png"));
        advancedThemes.add(new Theme("swanky-purse", "swanky-purse.png"));
        advancedThemes.add(new Theme("trontastic", "trontastic.png"));
        //advancedThemes.add(new Theme("ui-darkness", "ui-darkness.png"));
        //advancedThemes.add(new Theme("ui-lightness", "ui-lightness.png"));
        //advancedThemes.add(new Theme("vader", "vader.png"));
       
        themes = new TreeMap<String, String>();
        themes.put("Afterdark", "afterdark");
        themes.put("Afternoon", "afternoon");
        //themes.put("Afterwork", "afterwork");
        //themes.put("Aristo", "aristo");
        //themes.put("Black-Tie", "black-tie");
        themes.put("Blitzer", "blitzer");
        themes.put("Bluesky", "bluesky");
        //themes.put("Bootstrap", "bootstrap");
        //themes.put("Casablanca", "casablanca");
        themes.put("Cupertino", "cupertino");
        //themes.put("Cruze", "cruze");
        //themes.put("Dark-Hive", "dark-hive");
        //themes.put("Dot-Luv", "dot-luv");
        //themes.put("Eggplant", "eggplant");
        themes.put("Excite-Bike", "excite-bike");
        //themes.put("Flick", "flick");
        themes.put("Glass-X", "glass-x");
        themes.put("Home", "home");
        //themes.put("Hot-Sneaks", "hot-sneaks");
        themes.put("Humanity", "humanity");
        themes.put("Le-Frog", "le-frog");
        //themes.put("Likya Cupertino", "likya-cupertino");
        //themes.put("Merve", "merve");
        //themes.put("Midnight", "midnight");
        //themes.put("Mint-Choc", "mint-choc");
        //themes.put("Overcast", "overcast");
        //themes.put("Pepper-Grinder", "pepper-grinder");
        themes.put("Redmond", "redmond");
        //themes.put("Rocket", "rocket");
        //themes.put("Sam", "sam");
        //themes.put("Smoothness", "smoothness");
        //themes.put("South-Street", "south-street");
        //themes.put("Start", "start");
        themes.put("Sunny", "sunny");
        themes.put("Swanky-Purse", "swanky-purse");
        themes.put("Trontastic", "trontastic");
        //themes.put("UI-Darkness", "ui-darkness");
        //themes.put("UI-Lightness", "ui-lightness");
        //themes.put("Vader", "vader");
    }
   
    public void setUp(UserPreferences up) {
        this.up = up;
    }
   
    public Map<String, String> getThemes() {
        return themes;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public void saveTheme() {
        up.setTheme(theme);
    }

    public List<Theme> getAdvancedThemes() {
        return advancedThemes;
    }

	public String getJobStateIconCssPath() {
		return jobStateIconCssPath;
	}

	public void setJobStateIconCssPath(String jobStateIconCssPath) {
		this.jobStateIconCssPath = jobStateIconCssPath;
	}

    public void saveJobStateIconCssPath() {
        up.setJobStateIconCssPath(jobStateIconCssPath);
    }
    
	public String getJobStateColorCssPath() {
		return jobStateColorCssPath;
	}

	public void setJobStateColorCssPath(String jobStateColorCssPath) {
		this.jobStateColorCssPath = jobStateColorCssPath;
	}

    public void saveJobStateColorCssPath() {
        up.setJobStateColorCssPath(jobStateColorCssPath);
    }
    
	public String getJobIconCssPath() {
		return jobIconCssPath;
	}

	public void setJobIconCssPath(String jobIconCssPath) {
		this.jobIconCssPath = jobIconCssPath;
	}
	
    public void saveJobIconCssPath() {
        up.setJobIconCssPath(jobIconCssPath);
    }
    
    public void displayPrefereneces() {  
        FacesMessage msg = new FacesMessage("Selected", "Theme:" + theme + ", jobStateIconCssPath: " + jobStateIconCssPath);  
  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }

	public Map<String, String> getJobStateIconCssPaths() {
		return jobStateIconCssPaths;
	}

	public Map<String, String> getJobStateColorCssPaths() {
		return jobStateColorCssPaths;
	}

	public Map<String, String> getJobIconCssPaths() {
		return jobIconCssPaths;
	}  
}
