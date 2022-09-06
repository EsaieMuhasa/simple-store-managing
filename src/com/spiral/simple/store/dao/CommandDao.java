/**
 * 
 */
package com.spiral.simple.store.dao;

import java.util.Date;

import com.spiral.simple.store.beans.Command;

/**
 * @author Esaie MUHASA
 *
 */
public interface CommandDao extends DAOInterface<Command> {
	
	/**
	 * return commend performed by client
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	Command [] findByClient (String key) throws DAOException;
	
	/**
	 * change delivered state by command
	 * @param key
	 * @param delivered
	 * @throws DAOException
	 */
	void deliverCommand (String key, boolean delivered) throws DAOException;
	
	/**
	 * return the slip number of  last command
	 * @return
	 * @throws DAOException
	 */
	int getLastCommandNumber () throws DAOException;
	
	/**
	 * check if client has performed commands
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	boolean checkByClient (String key) throws DAOException;

	/**
	 * return all command performed a date interval
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	Command [] findByDate (Date min, Date max) throws DAOException;
	
	/**
	 * return part of commands performed at date interval
	 * @param min
	 * @param max
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	Command [] findByDate (Date min, Date max, int limit, int offset) throws DAOException;
	
	/**
	 * count performed command at date interval
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	int countByDate (Date min, Date max) throws DAOException;
	
	/**
	 * check it exist performed command at date interval
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	boolean checkByDate (Date min, Date max) throws DAOException;
	
	/**
	 * check if exist performed command at date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByDate (Date date) throws DAOException {
		return checkByDate(date, date);
	}
	
	/**
	 * count performed command at date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default int countByDate (Date date) throws DAOException {
		return countByDate(date, date);
	}
	
	
	/**
	 * return all command performed at date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default Command [] findByDate (Date date) throws DAOException {
		return findByDate(date, date);
	}
	
	/**
	 * return part of commands performed at date
	 * @param date
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	default Command [] findByDate (Date date, int limit, int offset) throws DAOException {
		return findByDate(date, date, limit, offset);
	}
	
	/**
	 * move command to trash
	 * @param id
	 * @throws DAOException
	 */
	void moveToTrash (String id) throws DAOException;
	
}
