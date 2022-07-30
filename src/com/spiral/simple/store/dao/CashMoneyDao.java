/**
 * 
 */
package com.spiral.simple.store.dao;

import java.util.Date;

import com.spiral.simple.store.beans.CashMoney;

/**
 * @author Esaie MUHASA
 *
 */
public interface CashMoneyDao <T extends CashMoney> extends DAOInterface <T> {
	
	/**
	 * check operations executed in date interval
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	boolean checkByDate (Date min, Date max) throws DAOException;
	
	/**
	 * count operations executed in date interval
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	int countByDate (Date min, Date max) throws DAOException;
	
	/**
	 * select all operation executed in date interval
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	T[] findByDate (Date min, Date max) throws DAOException;
	
	/**
	 * elect part of operation execute in date interval
	 * @param min
	 * @param max
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	T[] findByDate (Date min, Date max, int limit, int offset) throws DAOException;
	
	/**
	 * return sold at date interval
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	double getSoldByDate (Date min, Date max) throws DAOException;
	
	/**
	 * check if exist operation at date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByDate (Date date) throws DAOException {
		return checkByDate(date, date);
	}
	
	/**
	 * count operation execute  at date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default int countByDate (Date date) throws DAOException {
		return countByDate(date, date);
	}
	
	/**
	 * select all operation execute at date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default T[] findByDate (Date date) throws DAOException {
		return findByDate(date, date);
	}
	
	/**
	 * select part of operations executed at date
	 * @param date
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	default T[] findByDate (Date date, int limit, int offset) throws DAOException {
		return findByDate(date, date, limit, offset);
	}
	
	/**
	 * return sold at date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default double getSoldByDate (Date date) throws DAOException {
		return getSoldByDate(date, date);
	}

}
