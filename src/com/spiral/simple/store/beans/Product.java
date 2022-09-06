/**
 * 
 */
package com.spiral.simple.store.beans;

import java.io.File;

import javax.swing.ImageIcon;

import com.spiral.simple.store.tools.Config;

/**
 * @author Esaie MUHASA
 *
 */
public class Product extends DBEntity {
	private static final long serialVersionUID = 1764577292677181685L;
	
	/**
	 * product full name
	 */
	private String name;
	
	/**
	 * short text to describe product
	 */
	private String description;
	
	/**
	 * picture file name in data store (in soft workspace)
	 */
	private String picture;
	
	private ImageIcon image;

	/**
	 * 
	 */
	public Product() {
		super();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the picture
	 */
	public String getPicture() {
		return picture;
	}

	/**
	 * @return the image
	 */
	public ImageIcon getImage() {
		return image;
	}

	/**
	 * @param picture the picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
		File file = new File(Config.get("workspace")+picture);
		
		if(picture != null && file.exists())
			image = new ImageIcon(Config.get("workspace")+picture);
		else {
			image = new ImageIcon(Config.getIcon("fav"));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof Product) {
			Product p = (Product) obj;
			return p.getId().equals(id);
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return name;
	}

}
