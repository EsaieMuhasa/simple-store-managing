/**
 * 
 */
package com.spiral.simple.store.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class CashMoney extends DBEntity {
	private static final long serialVersionUID = 4934507349339921857L;
	
	protected double amount;
	protected Date date;

	/**
	 * 
	 */
	public CashMoney() {
		super();
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
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
	
	public void initDate(long date) {
		this.date = buildDate(date);
	}

}
