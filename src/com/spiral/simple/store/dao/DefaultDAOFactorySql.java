/**
 * 
 */
package com.spiral.simple.store.dao;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.spiral.simple.store.beans.DBEntity;
import com.spiral.simple.store.tools.Config;

/**
 * @author esaie
 *
 */
class DefaultDAOFactorySql implements DAOFactory {
	
	//database connection coordinates
	private final Properties databaseInfo = new Properties();
	private final String url;
	//
	
	private Map<String, String> dictionary;
	private Map<String, DAOInterface<?>> daos = new HashMap<>();

	
	public DefaultDAOFactorySql () throws DAOConfigException{
		super();
		try {
			dictionary = Config.getDictionary();
			Class.forName(dictionary.get("driverJDBC"));
		} catch (ClassNotFoundException | FileNotFoundException e) {
			throw new DAOConfigException(e.getMessage(), e);
		}
		url = dictionary.get("databaseUrl");
		databaseInfo.put("user", dictionary.get("databaseUser"));
		databaseInfo.put("password", dictionary.get("databasePassword"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DAOInterface<H>, H extends DBEntity> T find(Class<T> dao) throws DAOConfigException {
		if(!dictionary.containsKey(dao.getSimpleName()))
			throw new DAOConfigException("Aucune configuration pour l'interface "+dao.getName()+" du DAO");
		
		T in = (T) daos.get(dao.getSimpleName());
		if(in == null) {
			try {
				Class<?> c = Class.forName(dictionary.get(dao.getSimpleName()));
				Constructor<?> constructor = c.getConstructor(getClass());
				in = (T) constructor.newInstance(this);
				daos.put(dao.getSimpleName(), in);
			} catch (Exception e) {
				throw new DAOConfigException("Une erreur de fonfiguration est survenue lors de l'instanciation du DAO", e);
			}
		}
		return in;
	}
	
	/**
	 * create a new connection
	 * @return
	 * @throws SQLException
	 */
	Connection getConnection () throws SQLException {
		return DriverManager.getConnection(url, databaseInfo);
	}

}
