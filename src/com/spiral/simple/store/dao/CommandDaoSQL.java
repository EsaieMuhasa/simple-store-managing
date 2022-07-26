/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandItem;
import com.spiral.simple.store.beans.CommandPayment;

/**
 * @author Esaie Muhasa
 *
 */
class CommandDaoSQL extends UtilSQL<Command> implements CommandDao {
	
	private static final String FIELD_LABELS [] = {"id", "recordingDate", "lastUpdateDate", "date", "delivered", "client", "number"};

	public CommandDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public int getLastCommandNumber() throws DAOException {
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
	public void deliverCommand(String key, boolean delivered) throws DAOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	synchronized void create(Connection connection, int requestId, Command... t) throws DAOException, SQLException {
		int number = getLastCommandNumber();
		for (Command c : t) {
			c.setNumber(++number);
			if (c.getClient().getId() == null || c.getClient().getId().trim().isEmpty())//si le client n'existe pas dans le BDD
				((ClientDaoSQL)daoFactory.get(ClientDao.class)).create(connection, requestId, t[0].getClient());
		}
		super.create(connection, requestId, t);
		for (Command c : t) {			
			((CommandItemDaoSQL)daoFactory.get(CommandItemDao.class)).create(connection, requestId, c.getItems());
			CommandPayment [] ps = c.getPayments();
			if(ps == null || ps.length == 0)
				continue;
			for (CommandPayment p : ps)
				p.setDate(c.getDate());
			((CommandPaymentDaoSQL)daoFactory.get(CommandPaymentDao.class)).create(connection, requestId, ps);
		}
	}
	
	@Override
	synchronized void update(Connection connection, int requestId, Command... t) throws DAOException, SQLException {
		Date now = new Date();
		for (Command c : t) {
			c.setLastUpdateDate(now);
			updateInTable(connection, new String[] {"lastUpdateDate"}, new Object[] {now.getTime()}, c.getId());
			for (int i = 0; i < c.countItems(); i++) {//update items
				CommandItem item = c.getItemAt(i);
				if(item.getId() == null)
					((CommandItemDaoSQL)daoFactory.get(CommandItemDao.class)).create(connection, requestId, item);
				else
					((CommandItemDaoSQL)daoFactory.get(CommandItemDao.class)).update(connection, requestId, item);
			}
			
			if(c.countTmpDeletion() != 0) {//item a supprimer
				CommandItem [] items = c.getTmpDeletion();
				for (int i = 0; i < items.length; i++) {
					((CommandItemDaoSQL)daoFactory.get(CommandItemDao.class)).delete(connection, requestId, items[i].getId());
				}
			}
			
			//payement
			for (int i = 0; i < c.countPayements(); i++) {
				CommandPayment payment = c.getPaymentAt(i);
				if(payment.getId() != null) {
					if (payment.getAmount() == 0)
						((CommandPaymentDaoSQL)daoFactory.get(CommandPaymentDao.class)).delete(connection, requestId, payment.getId());
					else
						((CommandPaymentDaoSQL)daoFactory.get(CommandPaymentDao.class)).update(connection, requestId, payment);
				} else
					((CommandPaymentDaoSQL)daoFactory.get(CommandPaymentDao.class)).create(connection, requestId, payment);
			}
		}
	}
	
	@Override
	public void moveToTrash(String id) throws DAOException {
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
		return readData("SELECT * FROM "+getTableName()+" WHERE date BETWEEN ? AND ? ORDER BY date DESC",
				toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public Command[] findByDate(Date min, Date max, int limit, int offset) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHERE date BETWEEN ? AND ? ORDER BY number DESC LIMIT ? OFFSET ?",
				toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime(), limit, offset);
	}

	@Override
	public int countByDate(Date min, Date max) throws DAOException {
		return countData("SELECT COUNT(*) AS nombre FROM "+getTableName()+" WHERE date BETWEEN ? AND ?",
				toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public boolean checkByDate(Date min, Date max) throws DAOException {
		return checkData("SELECT * FROM "+getTableName()+" WHERE date BETWEEN ? AND ? LIMIT 1 OFFSET 0",
				toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
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
				entity.getClient().getId(),
				entity.getNumber()
		};
	}
	
	@Override
	protected Command mapping(ResultSet result) throws SQLException {
		Command c = super.mapping(result);
		c.setClient(daoFactory.get(ClientDao.class).findById(result.getString("client")));
		c.setDate(new Date(result.getLong("date")));
		c.setDelivered(result.getBoolean("delivered"));
		c.setNumber(result.getInt("number"));
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
