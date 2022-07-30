/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.Client;

/**
 * @author Esaie MUHASA
 *
 */
public interface ClientDao extends DAOInterface<Client> {
	
	/**
	 * check client owner of telephone number
	 * @param telephone
	 * @return
	 * @throws DAOException
	 */
	boolean ckeckByTelephone (String telephone) throws DAOException;
	
	/**
	 * return client owner of telephone number
	 * @param telephone
	 * @return
	 * @throws DAOException
	 */
	boolean findByTelephone (String telephone) throws DAOException;

}
