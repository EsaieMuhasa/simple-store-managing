/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.spiral.simple.store.beans.AffectedStock;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandItem;
import com.spiral.simple.store.beans.DistributionConfig;
import com.spiral.simple.store.beans.Product;

/**
 * @author Esaie Muhasa
 *
 */
class CommandItemDaoSQL extends UtilSQL<CommandItem> implements CommandItemDao {
	private static final String FIELDS_LABELS [] = {"id", "recordingDate", "lastUpdateDate", "command", "product", "config", "quantity"};
	
	public CommandItemDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByKey(String commandId, String productId) throws DAOException {
		return checkData("SELECT * FROM "+getTableName()+" WHERE command = ? AND product = ?", commandId, productId);
	}

	@Override
	public CommandItem findByKey(String commandId, String productId) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHERE command = ? AND product = ?", commandId, productId)[0];
	}

	@Override
	public boolean checkByCommand(String commandId) throws DAOException {
		return check("command", commandId);
	}

	@Override
	public CommandItem[] findByCommand(String commandId) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHERE command = ?", commandId);
	}

	@Override
	CommandItem[] createArray(int length) {
		return new CommandItem[length];
	}

	@Override
	String[] getTableFields() {
		return FIELDS_LABELS;
	}

	@Override
	Object[] getOccurrenceValues(CommandItem entity) {
		return new Object [] {
				entity.getId(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null,
				entity.getCommand().getId(),
				entity.getProduct().getId(),
				entity.getConfig().getId(),
				entity.getQuantity()
		};
	}
	
	@Override
	protected CommandItem mapping(ResultSet result) throws SQLException {
		CommandItem i = super.mapping(result);
		i.setQuantity(result.getDouble("quantity"));
		i.setCommand(new Command());
		i.getCommand().setId(result.getString("command"));
		i.setConfig(new DistributionConfig());
		i.getConfig().setId(result.getString("config"));
		i.setProduct(new Product());
		i.getProduct().setId(result.getString("product"));
		return i;
	}
	
	@Override
	synchronized void create(Connection connection, int requestId, CommandItem... t) throws DAOException, SQLException {
		super.create(connection, requestId, t);
		List<AffectedStock> stocks = new ArrayList<>();
		for (CommandItem item : t) {
			AffectedStock [] st = item.getStocks();
			if(st == null)
				continue;
			
			for (AffectedStock s : st) 
				stocks.add(s);
		}
		
		AffectedStock [] data = stocks.toArray(new AffectedStock[stocks.size()]);
		((AffectedStockDaoSQL) daoFactory.get(AffectedStockDao.class)).create(connection, requestId, data);
	}

	@Override
	protected CommandItem instantiate() {
		return new CommandItem();
	}

	@Override
	protected String getTableName() {
		return CommandItem.class.getSimpleName();
	}

}