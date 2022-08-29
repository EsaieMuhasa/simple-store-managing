/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.DistributionConfigItem;

/**
 * @author Esaie MUHASA
 *
 */
public interface DistributionConfigItemDao extends DAOInterface<DistributionConfigItem> {
	
	/**
	 * check unique key exist
	 * @param configId
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByKey(String configId, String rubricId) throws DAOException;
	
	/**
	 * return configuration repartition, for this unique key
	 * @param configId
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	DistributionConfigItem findByKey (String configId, String rubricId) throws DAOException;
	
	/**
	 * check if configuration has repartition parameters 
	 * @param configKey
	 * @return
	 * @throws DAOException
	 */
	boolean checkByConfig (String configKey) throws DAOException;
	
	/**
	 * select all repartition parameters reference configuration
	 * @param configKey
	 * @return
	 * @throws DAOException
	 */
	DistributionConfigItem[] findByConfig (String configKey) throws DAOException;
}
