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
	protected Currency currency;

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
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
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
	
	
	/**
	 * utility method to execute addition operation
	 * @param payment
	 */
	public void sum (CashMoney money) {
		if(!currency.equals(money.getCurrency()))
			throw new RuntimeException("Impossible de d'effectuer cette operation car les devises sont differente");
		
		amount += money.amount;
	}
	
	/**
	 * utility method to execute substraction
	 * @param money
	 */
	public void substract (CashMoney money) {
		if(!currency.equals(money.getCurrency()))
			throw new RuntimeException("Impossible de d'effectuer cette operation car les devises sont differente");
		
		amount -= money.amount;
	}

}
