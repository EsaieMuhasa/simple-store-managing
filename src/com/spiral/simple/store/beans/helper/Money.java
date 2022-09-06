/**
 * 
 */
package com.spiral.simple.store.beans.helper;

import com.spiral.simple.store.beans.Currency;

/**
 * @author Esaie Muhasa
 *
 */
public class Money {
	
	private double amount;
	private Currency currency;

	/**
	 * 
	 */
	public Money() {}

	public Money(double amount, Currency currency) {
		super();
		this.amount = amount;
		this.currency = currency;
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
	
	@Override
	public String toString() {
		return String.format("%f %s", amount, currency.getShortName());
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

}
