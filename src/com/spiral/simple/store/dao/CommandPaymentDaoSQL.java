/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.CommandPayment;

/**
 * @author Esaie Muhasa
 *
 */
public class CommandPaymentDaoSQL extends BaseCashMoneyDaoSQL<CommandPayment> implements CommandPaymentDao {

	public CommandPaymentDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByCommand(String commandId) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CommandPayment[] findByCommand(String commandId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getSoldByCommand(String commandId) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	CommandPayment[] createArray(int length) {
		return new CommandPayment[length];
	}

	@Override
	String[] getTableFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Object[] getOccurrenceValues(CommandPayment entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CommandPayment instantiate() {
		return new CommandPayment();
	}

	@Override
	protected String getTableName() {
		return CommandPayment.class.getSimpleName();
	}

}
