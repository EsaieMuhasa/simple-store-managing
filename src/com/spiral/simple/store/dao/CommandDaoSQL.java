/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.spiral.simple.store.beans.Command;

/**
 * @author Esaie Muhasa
 *
 */
class CommandDaoSQL extends UtilSQL<Command> implements CommandDao {
	
	private static final String FIELD_LABELS [] = {"id", "recordingDate", "lastUpdateDate", "date", "delivered", "client"};

	public CommandDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}
	
	@Override
	synchronized void create(Connection connection, int requestId, Command... t) throws DAOException, SQLException {
		if (t[0].getClient().getId() == null || t[0].getClient().getId().trim().isEmpty())//si le client n'existe pas dans le BDD
			((ClientDaoSQL)daoFactory.get(ClientDao.class)).create(connection, requestId, t[0].getClient());
		super.create(connection, requestId, t);
		((CommandItemDaoSQL)daoFactory.get(CommandItemDao.class)).create(connection, requestId, t[0].getItems());
	}

	@Override
	public Command[] findByClient(String key) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHERE client = ?", key);
	}

	@Override
	public boolean checkByClient(String key) throws DAOException {
		return check("client", key);
	}

	@Override
	public Command[] findByDate(Date min, Date max) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHRE date BETWEEN (?, ?) ORDER BY date DESC",
				toMinTimestampOfDay(min), toMaxTimestampOfDay(max));
	}

	@Override
	public Command[] findByDate(Date min, Date max, int limit, int offset) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHRE date BETWEEN (?, ?) ORDER BY date DESC LIMIT ? OFFSET offset",
				toMinTimestampOfDay(min), toMaxTimestampOfDay(max), limit, offset);
	}

	@Override
	public int countByDate(Date min, Date max) throws DAOException {
		return countData("SELECT COUNT(*) AS nombre FROM "+getTableName()+" WHRE date BETWEEN (?, ?)",
				toMinTimestampOfDay(min), toMaxTimestampOfDay(max));
	}

	@Override
	public boolean checkByDate(Date min, Date max) throws DAOException {
		return checkData("SELECT * FROM "+getTableName()+" WHRE date BETWEEN (?, ?) LIMIT 1 OFFSET 0",
				toMinTimestampOfDay(min), toMaxTimestampOfDay(max));
	}

	@Override
	Command[] createArray(int length) {
		return new Command[length];
	}

	@Override
	String[] getTableFields() {
		return FIELD_LABELS;
	}

	@Override
	Object[] getOccurrenceValues(Command entity) {
		return new Object[] {
				entity.getId(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null,
				entity.getDate().getTime(),
				entity.isDelivered()? 1 : 0,
				entity.getClient().getId()
		};
	}
	
	@Override
	protected Command mapping(ResultSet result) throws SQLException {
		Command c = super.mapping(result);
		c.setClient(daoFactory.get(ClientDao.class).findById(result.getString("client")));
		c.setDate(new Date(result.getLong("date")));
		c.setDelivered(result.getBoolean("delivered"));
		return c;
	}

	@Override
	protected Command instantiate() {
		return new Command();
	}

	@Override
	protected String getTableName() {
		return Command.class.getSimpleName();
	}

}
