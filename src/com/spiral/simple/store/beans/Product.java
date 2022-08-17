/**
 * 
 */
package com.spiral.simple.store.beans;

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
		if(picture != null)
			image = new ImageIcon(Config.get("workspace")+picture);
		else 
			image = null;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
