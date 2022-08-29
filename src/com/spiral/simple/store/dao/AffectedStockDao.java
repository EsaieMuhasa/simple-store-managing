/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.AffectedStock;

/**
 * @author Esaie MUHASA
 *
 */
public interface AffectedStockDao extends DAOInterface<AffectedStock> {
	
	/**
	 * check existence of unique key in database
	 * @param stockId
	 * @param itemId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByKey (String stockId, String itemId) throws DAOException;
	
	/**
	 * return occurrence owner of this unique key
	 * @param stockId
	 * @param itemId
	 * @return
	 * @throws DAOException
	 */
	AffectedStock findByKey (String stockId, String itemId) throws DAOException;
	
	/**
	 * check if stock is already used at least once
	 * @param stockId
	 * @return
	 * @throws DAOException
	 */
	boolean ckeckByStock (String stockId) throws DAOException;
	
	/**
	 * check if command item is already referenced at least once
	 * @param itemId
	 * @return
	 * @throws DAOException
	 */
	boolean ckeckByCommandItem (String itemId) throws DAOException;
	
	/**
	 * return all operations must affect stock quantity
	 * @param stockId
	 * @return
	 * @throws DAOException
	 */
	AffectedStock[] findByStock (String stockId) throws DAOException;
	
	/**
	 * return all operation references command item 
	 * @param itemId
	 * @return
	 * @throws DAOException
	 */
	AffectedStock[] findByCommandItem (String itemId) throws DAOException;

}
