/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandPayment;

/**
 * @author Esaie Muhasa
 *
 */
class CommandPaymentDaoSQL extends BaseCashMoneyDaoSQL<CommandPayment> implements CommandPaymentDao {
	private static final String FEILDS_TITLES [] = {"id", "recordingDate", "lastUpdateDate", "command", "amount", "currency", "date"};

	public CommandPaymentDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByCommand(String commandId) throws DAOException {
		return checkData("SELECT * FROM "+getViewName()+" WHERE command = ? LIMIT 1", commandId);
	}

	@Override
	public CommandPayment[] findByCommand(String commandId) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE command = ?", commandId);
	}

	@Override
	CommandPayment[] createArray(int length) {
		return new CommandPayment[length];
	}

	@Override
	String[] getTableFields() {
		return FEILDS_TITLES;
	}

	@Override
	Object[] getOccurrenceValues(CommandPayment entity) {
		return new Object[] {
				entity.getId(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null,
				entity.getCommand().getId(),
				entity.getAmount(),
				entity.getCurrency().getId(),
				entity.getDate().getTime()
		};
	}
	
	@Override
	protected CommandPayment mapping(ResultSet result) throws SQLException {
		CommandPayment payment = super.mapping(result);
		payment.setCommand(new Command());
		payment.getCommand().setId(result.getString("command"));
		return payment;
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
