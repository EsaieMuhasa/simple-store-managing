/**
 * 
 */
package com.spiral.simple.store.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class Currency extends DBEntity {

	private static final long serialVersionUID = -5094391469130817815L;
	
	private String shortName;
	private String fullName;
	private String symbol;

	/** */
	public Currency() {
		super();
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

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", shortName, fullName);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if (obj != null && obj instanceof Currency) {
			Currency c = (Currency) obj;
			if(c.id == null || c.id.trim().isEmpty() || id == null || id.trim().isEmpty())
				return false;
			return c.getId().equals(id);
		}
		return super.equals(obj);
	}

}
