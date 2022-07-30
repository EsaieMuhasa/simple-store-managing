/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.CommandItem;

/**
 * @author Esaie MUHASA
 *
 */
public interface CommandItemDao extends DAOInterface<CommandItem> {
	
	/**
	 * check if unique keys are used
	 * @param commandId
	 * @param productId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByKey (String commandId, String productId) throws DAOException;
	
	/**
	 * return occurrence reference keys
	 * @param commandId
	 * @param productId
	 * @return
	 * @throws DAOException
	 */
	CommandItem findByKey (String commandId, String productId) throws DAOException;
	
	/**
	 * check when client command has items
	 * @param commandId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByCommand(String commandId) throws DAOException;
	
	/**
	 * return all items of client command
	 * @param commandId
	 * @return
	 * @throws DAOException
	 */
	CommandItem[] findByCommand (String commandId) throws DAOException;
}
