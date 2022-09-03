/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandPayment;

/**
 * @author Esaie Muhasa
 *
 */
class CommandPaymentDaoSQL extends BaseCashMoneyDaoSQL<CommandPayment> implements CommandPaymentDao {
	private static final String FEILDS_TITLES [] = {"id", "recordingDate", "lastUpdateDate", "command", "amount", "currency", "date", "number"};

	public CommandPaymentDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}
	
	@Override
	public int getLastPaymentNumber() throws DAOException {
		int number = 0;
		try (Connection connection = daoFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery("SELECT number FROM " + getTableName() + " ORDER BY number DESC LIMIT 1 OFFSET 0")) {
			if(result.next())
				number = result.getInt("number");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(),  e);
		}
		return number;
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
	synchronized void create(Connection connection, int requestId, CommandPayment... t) throws DAOException, SQLException {
		int number = getLastPaymentNumber();
		for (CommandPayment payment : t)
			payment.setNumber(++number);
		super.create(connection, requestId, t);
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
				entity.getDate().getTime(),
				entity.getNumber()
		};
	}
	
	@Override
	protected CommandPayment mapping(ResultSet result) throws SQLException {
		CommandPayment payment = super.mapping(result);
		payment.setCommand(new Command());
		payment.getCommand().setId(result.getString("command"));
		payment.setNumber(result.getInt("number"));
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
