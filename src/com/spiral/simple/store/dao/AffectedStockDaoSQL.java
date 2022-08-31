/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.AffectedStock;
import com.spiral.simple.store.beans.CommandItem;
import com.spiral.simple.store.beans.Stock;

/**
 * @author Esaie Muhasa
 *
 */
class AffectedStockDaoSQL extends UtilSQL<AffectedStock> implements AffectedStockDao {

	private static final String[] FIELD_LABELS = {"id", "recordingDate", "lastUpdateDate", "item", "stock", "quantity"};

	public AffectedStockDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByKey(String stockId, String itemId) throws DAOException {
		return checkData("SELECT * FROM "+getTableName()+"  WHERE stock = ? AND item = ? LIMIT 1 OFFSET 0", stockId, itemId);
	}

	@Override
	public AffectedStock findByKey(String stockId, String itemId) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+"  WHERE stock = ? AND item = ?", stockId, itemId)[0];
	}

	@Override
	public boolean ckeckByStock(String stockId) throws DAOException {
		return check("stock", stockId);
	}

	@Override
	public boolean ckeckByCommandItem(String itemId) throws DAOException {
		return check("item", itemId);
	}

	@Override
	public AffectedStock[] findByStock(String stockId) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+"  WHERE stock = ?", stockId);
	}

	@Override
	public AffectedStock[] findByCommandItem(String itemId) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+"  WHERE item = ?", itemId);
	}

	@Override
	AffectedStock[] createArray(int length) {
		return new AffectedStock[length];
	}

	@Override
	String[] getTableFields() {
		return FIELD_LABELS;
	}

	@Override
	Object[] getOccurrenceValues(AffectedStock entity) {
		return new Object[] {
				entity.getId(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null?  entity.getLastUpdateDate().getTime() : null,
				entity.getItem().getId(),
				entity.getStock().getId(),
				entity.getQuantity()
		};
	}
	
	@Override
	protected AffectedStock mapping(ResultSet result) throws SQLException {
		AffectedStock a= super.mapping(result);
		a.setQuantity(result.getDouble("quantity"));
		a.setStock(new Stock());
		a.getStock().setId(result.getString("stock"));
		a.setItem(new CommandItem());
		a.getItem().setId(result.getString("item"));
		return a;
	}

	@Override
	protected AffectedStock instantiate() {
		return new AffectedStock();
	}

	@Override
	protected String getTableName() {
		return AffectedStock.class.getSimpleName();
	}

}
