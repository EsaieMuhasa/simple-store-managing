/**
 * 
 */
package com.spiral.simple.store.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class Client extends DBEntity {
	private static final long serialVersionUID = -3793340954368507357L;
	
	private String names;
	private String telephone;

	/**
	 * 
	 */
	public Client() {
		super();
	}

	/**
	 * @return the names
	 */
	public String getNames() {
		return names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(String names) {
		this.names = names;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	@Override
	public String toString() {
		return names;
	}

}
