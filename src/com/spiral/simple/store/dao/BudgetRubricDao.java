/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.BudgetRubric;

/**
 * @author Esaie MUHASA
 */
public interface BudgetRubricDao extends DAOInterface<BudgetRubric> {
	
	/**
	 * check if label are used by other budget rubric
	 * @param label
	 * @return
	 * @throws DAOException
	 */
	boolean checkByLabel (String label) throws DAOException;
	
	/**
	 * check if label are used by other budget rubric.
	 * on checking, occurrence owner of key will be not considered 
	 * @param label
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	boolean checkByLabel (String label, String id) throws DAOException;

}
