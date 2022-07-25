/**
 * 
 */
package com.spiral.simple.store.beans;

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
	 * @param picture the picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}

}
