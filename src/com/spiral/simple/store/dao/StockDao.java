/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.Stock;

/**
 * @author Esaie MUHASA
 *
 */
public interface StockDao extends DAOInterface<Stock> {
	
	/**
	 * check stock of product
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	boolean checkByProduct (String key) throws DAOException;
	
	/**
	 * check only available stocks of product
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	boolean checkAvailableByProduct (String key) throws DAOException;
	
	/**
	 * count stocks reference product, in database
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	int countByProduct (String key) throws DAOException;
	
	/**
	 * return array of stocks reference product
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	Stock [] findByProduct (String key) throws DAOException;
	
	/**
	 * return the latest stock saved, referenced product key
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	Stock findLatestByProduct (String key) throws DAOException;
	
	/**
	 * return interval of stocks data reference on product
	 * @param key
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	Stock [] findByProduct (String key, int limit, int offset) throws DAOException;

	/**
	 * return array of stocks reference product
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	Stock [] findAvailableByProduct (String key) throws DAOException;
	
	/**
	 * renvoie le nombre des stock qui ne sont pas vide
	 * @return
	 * @throws DAOException
	 */
	int countAvailable() throws DAOException;
	
	/**
	 * verifie s'il ya  aumoin un stock non vide
	 * @return
	 * @throws DAOException
	 */
	default boolean checkAvailable () throws DAOException {
		return checkAvailable(0);
	}
	
	/**
	 * verifie s'il y a aumoin un stock non vide, apres X stock
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	boolean checkAvailable (int offset) throws DAOException;
	
	/**
	 * renvoie tout les stock non-vide
	 * @return
	 * @throws DAOException
	 */
	Stock [] findAvailable() throws DAOException;
	
	/**
	 * renvoie une partie des stock non vide
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	Stock [] findAvailable(int limit, int offset) throws DAOException;
	
}
