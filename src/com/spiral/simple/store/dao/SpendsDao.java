/**
 * 
 */
package com.spiral.simple.store.dao;

import java.util.Date;

import com.spiral.simple.store.beans.Spends;

/**
 * @author Esaie MUHASA
 *
 */
public interface SpendsDao extends CashMoneyDao<Spends> {
	
	/**
	 * check if rubric is already reference at least once
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByRubric (String rubricId) throws DAOException;
	
	/**
	 * count all operation referenced budget rubric
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	int countByRubric (String rubricId) throws DAOException;

	/**
	 * count all operations perfected at date interval and referenced budget rubric
	 * @param rubricId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	int countByRubric (String rubricId, Date min, Date max) throws DAOException;
	
	/**
	 * select all operation referenced budget rubric
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByRubric (String rubricId) throws DAOException;
	
	/**
	 * select part of operation referenced budget rubric
	 * @param rubricId
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByRubric (String rubricId, int limit, int offset) throws DAOException;
	
	/**
	 * select all operation perfected in budget rubric at date interval
	 * @param rubricId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByRubric (String rubricId, Date min, Date max) throws DAOException;
	
	/**
	 * select all part of operation execute at date interval and reference on 
	 * budget rubric
	 * @param rubricId
	 * @param min
	 * @param max
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByRubric (String rubricId, Date min, Date max, int limit, int offset) throws DAOException;
	
	/**
	 * return sum of spends at budget rubric
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	double getSoldByRubric(String rubricId) throws DAOException;
	
	/**
	 * return sum of spends at date interval for budget rubric
	 * @param rubricId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	double getSoldByRubric (String rubricId, Date min, Date max) throws DAOException;
	
	/**
	 * return operation perfected in budget rubric at date
	 * @param rubricId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default Spends[] findByRubricAt (String rubricId, Date date) throws DAOException {
		return findByRubric(rubricId, date, date);
	}
	
	/**
	 * count all operation perfected at date and referenced budget rubric
	 * @param rubricId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default int countByRubric (String rubricId, Date date) throws DAOException {
		return countByRubric(rubricId, date, date);
	}
	
	/**
	 * return sum of spends at date for budget rubric
	 * @param rubricId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default double getSoldByRubric (String rubricId, Date date) throws DAOException {
		return getSoldByRubric(rubricId, date, date);
	}
	
}
