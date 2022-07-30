/**
 * 
 */
package com.spiral.simple.store.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 *
 */
public class ExchangeRate extends DBEntity {

	private static final long serialVersionUID = 4400736286183959920L;
	
	private Currency currency1;
	private Currency currency2;
	
	private double rate;
	
	private Date startTime;
	private Date endTime;

	/**
	 * 
	 */
	public ExchangeRate() {
		super();
	}
	
	/**
	 * check if date is in start and end time for exchange rate
	 * @param date
	 * @return
	 */
	public boolean match (Date date) {
		if (date.getTime() > startTime.getTime()) {
			if (endTime != null && endTime.getTime() < date.getTime())
				return false;
			return true;
		}
		
		return false;
	}
	
	/**
	 * utility conversion office
	 * @param value
	 * @param currency
	 * @return
	 * @throws IllegalArgumentException
	 */
	public double convert (double value, Currency currency) throws IllegalArgumentException {
		if (currency1.getId() != currency.getId() && currency2.getId() != currency.getId())
			throw new IllegalArgumentException("Impossible d'effectuer cette operation car "
					+ "la devise en parametre n'est pas pris en charge");
		
		if (value == 0 || rate == 0)
			return 0;
		
		if (currency == currency1 || currency.getId().equals(currency1.getId()))
			return value * rate;
		
		return value / rate;
	}

	/**
	 * @return the rate
	 */
	public double getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}

	/**
	 * @return the currency1
	 */
	public Currency getCurrency1() {
		return currency1;
	}

	/**
	 * @param currency1 the currency1 to set
	 */
	public void setCurrency1(Currency currency1) {
		this.currency1 = currency1;
	}

	/**
	 * @return the currency2
	 */
	public Currency getCurrency2() {
		return currency2;
	}

	/**
	 * @param currency2 the currency2 to set
	 */
	public void setCurrency2(Currency currency2) {
		this.currency2 = currency2;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public void initSartTime (long time) {
		if(time <= 1000)
			time = 0;
		
		if(time == 0)
			startTime = null;
		else
			startTime = new Date(time);
	}
	
	public void initEndTime (long time) {
		if(time <= 1000)
			time = 0;
		
		if(time == 0)
			endTime = null;
		else
			endTime = new Date(time);
	}

}
