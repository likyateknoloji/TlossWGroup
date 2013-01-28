package com.likya.tlossw.web.menu;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class MenuItems implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2251435343122881787L;

	private int id = 0; // default
	private String parent;
	private String icon;
	private String name;
	private String link;

	public int getId() {
		return id;
	}

	public MenuItems() {
		this.id = this.id + 1;
		this.parent = "parent";
		this.icon = "icon";
		this.name = "name";
		this.link = "link";
	}

	public MenuItems(String parent, String icon, String name, String link) {
		super();
		this.id = this.id + 1;
		this.parent = parent;
		this.icon = icon;
		this.name = name;
		this.link = link;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	// Eclipse Generated hashCode and equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());

		// TODO buraya birsey yapmak gerekebilir.
		// result = prime * result + ((category == null) ? 0 : category.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuItems other = (MenuItems) obj;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		// TODO parent icinde yap.
		// Collection<Category> esitligi icin nasil birsey yapmak gerekir?
		/*
		 * if (category == null) {
		 * if (other.category != null)
		 * return false;
		 * } else if (!category.equals(other.category))
		 * return false;
		 */
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
}
