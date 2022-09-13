/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.Product;

/**
 * @author Esaie MUHASA
 *
 */
public interface ProductDao extends DAOInterface<Product> {
	
	/**
	 * execute approximative search. we select all Product for names matched by value
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	Product [] search (String value) throws DAOException;

	/**
	 * selectionne uniquement les produits qui ont des stock non vide
	 * @return
	 * @throws DAOException
	 */
	Product [] findByAvailableStock () throws DAOException;

}
