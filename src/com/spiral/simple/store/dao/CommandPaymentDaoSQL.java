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
import com.spiral.simple.store.beans.DistributionConfig;

/**
 * @author Esaie Muhasa
 *
 */
class CommandPaymentDaoSQL extends BaseCashMoneyDaoSQL<CommandPayment> implements CommandPaymentDao {
	private static final String [] 
			FEILDS_TITLES = {"id", "recordingDate", "lastUpdateDate", "command", "amount", "currency", "date", "config", "number"},
			UPDATEBLE_FEILDS_TITLES = {"lastUpdateDate", "amount", "currency", "date", "number"};

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
	public boolean checkByConfig(String configKey) throws DAOException {
		return check("config", configKey);
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
	synchronized void create (Connection connection, int requestId, CommandPayment... t) throws DAOException, SQLException {
		int number = getLastPaymentNumber();
		for (CommandPayment payment : t){
			payment.setNumber(++number);
			//pour les elements de la commande, produits dont la configurations de repartititon des recettes est diponible
			if(payment.getConfig() == null && daoFactory.get(DistributionConfigDao.class).checkAvailable())
				payment.setConfig(daoFactory.get(DistributionConfigDao.class).findAvailable());
		}
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
	String[] getUpdatebleFields() {
		return UPDATEBLE_FEILDS_TITLES;
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
				entity.getConfig() != null? entity.getConfig().getId() : null,
				entity.getNumber()
		};
	}
	
	@Override
	Object[] getUpdatebleOccurrenceValues(CommandPayment entity) {
		return new Object[] {
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null,
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
		if(result.getString("config") != null) {
			payment.setConfig(new DistributionConfig());
			payment.getConfig().setId(result.getString("config"));
		}
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
