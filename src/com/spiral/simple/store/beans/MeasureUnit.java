/**
 * 
 */
package com.spiral.simple.store.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class MeasureUnit extends DBEntity {
	private static final long serialVersionUID = 6073033594074811078L;

	private String shortName;
	private String fullName;

	public MeasureUnit() {
		super();
	}
	
	@Override
	public String toString() {
		return "("+shortName+") "+fullName;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
