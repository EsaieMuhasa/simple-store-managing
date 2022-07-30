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
	
	/**
	 * return sum of amounts paid for client command
	 * @param commandId
	 * @return
	 * @throws DAOException
	 */
	double getSoldByCommand (String commandId) throws DAOException;
}
