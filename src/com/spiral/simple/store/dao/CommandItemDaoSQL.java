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

/**
 * @author Esaie Muhasa
 *
 */
class CommandItemDaoSQL extends UtilSQL<CommandItem> implements CommandItemDao {
	private static final String [] 
			FIELDS_LABELS = {"id", "recordingDate", "lastUpdateDate", "command", "product", "config", "quantity", "unitPrice", "currency"},
			UPDATEBLE_FIELDS_LABELS = {"lastUpdateDate", "quantity", "unitPrice", "currency"};
	
	public CommandItemDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}
	
	@Override
	protected boolean hasView() {
		return true;
	}

	@Override
	public boolean checkByKey(String commandId, String productId) throws DAOException {
		return checkData("SELECT * FROM "+getTableName()+" WHERE command = ? AND product = ?", commandId, productId);
	}

	@Override
	public CommandItem findByKey(String commandId, String productId) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE command = ? AND product = ?", commandId, productId)[0];
	}

	@Override
	public boolean checkByCommand(String commandId) throws DAOException {
		return check("command", commandId);
	}

	@Override
	public CommandItem[] findByCommand(String commandId) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE command = ?", commandId);
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
	String[] getUpdatebleFields() {
		return UPDATEBLE_FIELDS_LABELS;
	}

	@Override
	Object[] getOccurrenceValues(CommandItem entity) {
		return new Object [] {
				entity.getId(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null,
				entity.getCommand().getId(),
				entity.getProduct().getId(),
				entity.getQuantity(),
				entity.getUnitPrice(),
				entity.getCurrency().getId()
		};
	}
	
	@Override
	Object[] getUpdatebleOccurrenceValues(CommandItem entity) {
		return new Object [] {
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null,
				entity.getQuantity(),
				entity.getUnitPrice(),
				entity.getCurrency().getId()
		};
	}
	
	@Override
	protected CommandItem mapping(ResultSet result) throws SQLException {
		CommandItem i = super.mapping(result);
		i.setQuantity(result.getDouble("quantity"));
		i.setCommand(new Command());
		i.getCommand().setId(result.getString("command"));
		
		if(result.getString("measureUnit") != null)
			i.setMeasureUnit(daoFactory.get(MeasureUnitDao.class).findById(result.getString("measureUnit")));
		
		i.setProduct(daoFactory.get(ProductDao.class).findById(result.getString("product")));
		i.setUnitPrice(result.getDouble("unitPrice"));
		i.setCurrency(daoFactory.get(CurrencyDao.class).findById(result.getString("currency")));
		return i;
	}
	
	@Override
	synchronized void create(Connection connection, int requestId, CommandItem... t) throws DAOException, SQLException {
		List<AffectedStock> stocks = new ArrayList<>();
		for (CommandItem item : t) {
			
			AffectedStock [] st = item.getStocks();
			if(st == null)//on prevoie dans le cas ou on auras l'intension de prendre en charge les comandes sans penser a la quantite disponible en stock
				continue;
			
			for (AffectedStock s : st) 
				if(s.getQuantity() != 0.0)//on recupere uniquement le elements dont les stockes ont ete toucher
					stocks.add(s);
		}
		
		super.create(connection, requestId, t);
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
