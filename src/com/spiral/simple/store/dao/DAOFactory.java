/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.DBEntity;

/**
 * @author esaie
 *
 */
public interface DAOFactory {
	
	/**
	 * return instance, implementation of factory of DAO
	 * @return
	 * @throws DAOConfigException
	 */
	static DAOFactory getInstance () throws DAOConfigException {
		return DAOLoader.loadDAOFactory();
	}
	
	/**
	 * 
	 * @param <T>
	 * @param <H>
	 * @param dao
	 * @return
	 * @throws DAOConfigException
	 */
	static <T extends DAOInterface<H>, H extends DBEntity> T findDao (Class<T> dao) throws DAOConfigException {
		return getInstance().find(dao);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param <H>
	 * @param dao
	 * @return
	 */
	static <T extends DAOInterface<H>, H extends DBEntity> T getDa (Class<T> dao) {
		try {
			return getInstance().find(dao);
		} catch (DAOConfigException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * return a DAO interface of entity
	 * @param <T>
	 * @param <H>
	 * @param dao
	 * @return
	 * @throws DAOConfigException
	 */
	<T extends DAOInterface<H>, H extends DBEntity> T find (Class<T> dao) throws DAOConfigException;
	
	/**
	 * return a DAO interface of entity
	 * when error occurred in instantiation process, null value must be return
	 * @param <T>
	 * @param <H>
	 * @param dao
	 * @return
	 */
	default <T extends DAOInterface<H>, H extends DBEntity> T get (Class<T> dao) {
		try {
			return findDao(dao);
		} catch (DAOConfigException e) {
			e.printStackTrace();
		}
		return null;
	}

}
