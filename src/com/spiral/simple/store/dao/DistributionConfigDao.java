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
	 * check distribution configuration of product
	 * @param productKey
	 * @return
	 * @throws DAOException
	 */
	boolean checkByProduct (String productKey) throws DAOException;
	
	/**
	 * check available configuration of product
	 * @param productKey
	 * @return
	 * @throws DAOException
	 */
	boolean checkAvailableByProduct (String productKey) throws DAOException;
	
	/**
	 * return available distribution configuration of product 
	 * @param productKey
	 * @return
	 * @throws DAOException
	 */
	DistributionConfig findAvailableByProduct (String productKey) throws DAOException;
	
	/***
	 * return all product configuration in database
	 * @param productKey
	 * @return
	 * @throws DAOException
	 */
	DistributionConfig[] findByProduct (String productKey) throws DAOException;

}
