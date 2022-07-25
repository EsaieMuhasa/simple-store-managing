/**
 * 
 */
package com.spiral.simple.store.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 *
 */
public class Command extends DBEntity {

	private static final long serialVersionUID = 5158875958000658833L;
	
	private Date date;
	private boolean delivered;
	private Client client;

	/**
	 * 
	 */
	public Command() {
		super();
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the delivered
	 */
	public boolean isDelivered() {
		return delivered;
	}

	/**
	 * @param delivered the delivered to set
	 */
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

}
