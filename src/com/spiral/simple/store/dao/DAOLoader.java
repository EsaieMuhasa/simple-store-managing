/**
 * 
 */
package com.spiral.simple.store.dao;

import java.lang.reflect.Constructor;

import com.spiral.simple.store.tools.Config;

/**
 * @author esaie
 *
 */
final class DAOLoader {
	
	private static DAOFactory daoFactory;

	/**
	 * 
	 */
	private DAOLoader () {}
	
	/**
	 * utility to instantiate implementation of factory of DAO
	 * (the singleton pattern are used)
	 * @return
	 * @throws DAOConfigException
	 */
	public static DAOFactory loadDAOFactory () throws DAOConfigException {
		if(daoFactory == null) {
			try {
				Class<?> cl = Class.forName(Config.get("daoFactory"));
				Constructor<?> c = cl.getConstructor();
				daoFactory = (DAOFactory) c.newInstance();
			} catch (Exception e) {
				throw new DAOConfigException(e.getMessage(), e);
			}
		}		
		return daoFactory;
	}

}
