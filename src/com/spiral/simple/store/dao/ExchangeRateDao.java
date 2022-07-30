/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.ExchangeRate;

/**
 * @author Esaie MUHASA
 *
 */
public interface ExchangeRateDao extends DAOInterface<ExchangeRate> {
	
	/**
	 * convert value form valueCurrency to currency conversion
	 * @param value
	 * @param valueCurrency
	 * @param conversion
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 */
	default double convert (double value, Currency valueCurrency, Currency conversion) throws DAOException, IllegalArgumentException{
		return findAvailableByCurrencies(valueCurrency.getId(), conversion.getId()).convert(value, valueCurrency);
	}
	
	/**
	 * check if currency has exchange rate in database 
	 * @param currency
	 * @return
	 * @throws DAOException
	 */
	boolean checkByCurrency (String currency) throws DAOException;
	
	/**
	 * check if currencies in method parameters has exchange rate
	 * @param currency1
	 * @param currency2
	 * @return
	 * @throws DAOException
	 */
	boolean checkByCurrencies (String currency1, String currency2) throws DAOException;
	
	/**
	 * return all exchange rate available for currency
	 * @param currency
	 * @return
	 * @throws DAOException
	 */
	ExchangeRate[] findAvailableByCurrency (String currency) throws DAOException;
	
	/**
	 * select all available exchange rate
	 * @return
	 * @throws DAOException
	 */
	ExchangeRate[] findAvailable () throws DAOException;
	
	/**
	 * return exchange rate available for currencies keys in
	 * method parameters
	 * @param currency1
	 * @param currency2
	 * @return
	 * @throws DAOException
	 */
	ExchangeRate findAvailableByCurrencies (String currency1, String currency2) throws DAOException;
}
