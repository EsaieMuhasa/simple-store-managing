/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.DistributionConfig;

/**
 * @author Esaie MUHASA
 *
 */
public interface DistributionConfigDao extends DAOInterface<DistributionConfig> {
	
	/**
	 * this method plays a versatile role. 
	 * He can create a new configuration, or modify the existing configuration.
	 * it takes into account elements of the configuration and it is able to know which element, add / modify or delete
	 * It run in new thread
	 * @param requestId
	 * @param config
	 */
	void toggle (int requestId, DistributionConfig config);
	
	/**
	 * verifie s'il y a une configuation falide
	 * @return
	 * @throws DAOException
	 */
	boolean checkAvailable () throws DAOException;
	
	/**
	 * renvoie la configuration valide
	 * @return
	 * @throws DAOException
	 */
	DistributionConfig findAvailable () throws DAOException;

}
