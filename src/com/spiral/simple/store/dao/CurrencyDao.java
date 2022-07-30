/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.Currency;

/**
 * @author Esaie MUHASA
 *
 */
public interface CurrencyDao extends DAOInterface<Currency> {
	
	/**
	 * check when short name of currency are already
	 * @param shortName
	 * @return
	 * @throws DAOException
	 */
	boolean checkByShortName (String shortName) throws DAOException;
	
	/**
	 * check when full name of currency exist in database
	 * @param fullName
	 * @return
	 * @throws DAOException
	 */
	boolean checkByFullName (String fullName) throws DAOException;
	
}
