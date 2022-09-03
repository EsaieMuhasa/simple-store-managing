/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.CommandPayment;

/**
 * @author Esaie MUHASA
 *
 */
public interface CommandPaymentDao extends CashMoneyDao<CommandPayment> {
	
	/**
	 * return the last payment number
	 * @return
	 * @throws DAOException
	 */
	int getLastPaymentNumber () throws DAOException;

	/**
	 * check if command is already paid at least once
	 * @param commandId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByCommand (String commandId) throws DAOException;
	
	/**
	 * select all payment of command
	 * @param commandId
	 * @return
	 * @throws DAOException
	 */
	CommandPayment[] findByCommand (String commandId) throws DAOException;
}
