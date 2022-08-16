/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.MeasureUnit;

/**
 * @author Esaie MUHASA
 *
 */
public interface MeasureUnitDao  extends DAOInterface<MeasureUnit>{
	
	/**
	 * check if short name by measure unit are already used by other measure
	 * @param shortName
	 * @return
	 * @throws DAOException
	 */
	boolean checkByShortName (String shortName) throws DAOException;

	/**
	 * check if full name are already used by other occurrence
	 * @param fullName
	 * @return
	 * @throws DAOException
	 */
	boolean checkByFullName (String fullName) throws DAOException;
	
	/**
	 * check if short name are already used by other occurrence different by occurrence owner 
	 * by ID value in second parameter by this method
	 * @param shortName
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	boolean checkByShortName (String shortName, String id) throws DAOException;
	
	/**
	 * check if full name are already used by other occurrence different by occurrence owner of ID
	 * in second parameter by this method
	 * @param fullName
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	boolean checkByFullName (String fullName, String id) throws DAOException;

}
