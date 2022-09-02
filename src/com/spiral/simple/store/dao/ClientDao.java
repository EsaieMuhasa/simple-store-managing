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
	boolean checkByTelephone (String telephone) throws DAOException;
	
	/**
	 * return client owner of telephone number
	 * @param telephone
	 * @return
	 * @throws DAOException
	 */
	Client findByTelephone (String telephone) throws DAOException;

}
