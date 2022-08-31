/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.Stock;

/**
 * @author Esaie MUHASA
 *
 */
class StockDaoSQL extends UtilSQL<Stock> implements StockDao {
	
	private final String [] TABLE_FIELDS = { 
			"id", "quantity", "measureUnit", "description", "defaultUnitPrice", "salesCurrency",
			"buyingPrice", "buyingCurrency", "manifacturingDate", "expiryDate",
			"product", "date", "recordingDate", "lastUpdateDate" };

	public StockDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}
	
	@Override
	protected boolean hasView() {
		return true;
	}

	@Override
	public boolean checkByProduct(String key) throws DAOException {
		return check("product", key);
	}

	@Override
	public boolean checkAvailableByProduct(String key) throws DAOException {
		return checkData("SELECT * FROM "+getViewName()+" WHERE product = ? AND (quantity > used OR used IS NULL) LIMIT 1 OFFSET 0", key);
	}
	
	@Override
	public Stock findLatestByProduct (String key) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE product = ? ORDER BY recordingDate DESC LIMIT 1 OFFSET 0", key)[0];
	}

	@Override
	public int countByProduct(String key) throws DAOException {
		return countData("SELECT COUNT(*) AS nombre FROM "+getViewName()+" WHERE product = ?", key);
	}

	@Override
	public Stock[] findByProduct(String key) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE product = ?", key);
	}

	@Override
	public Stock[] findByProduct(String key, int limit, int offset) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE product = ? LIMIT ? OFFSET ?", key, limit, offset);
	}

	@Override
	public Stock[] findAvailableByProduct (String key) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE product = ? AND (quantity > used OR used IS NULL) LIMIT 1 OFFSET 0", key);
	}

	@Override
	Stock[] createArray(int length) {
		return new Stock[length];
	}

	@Override
	String[] getTableFields() {
		return TABLE_FIELDS;
	}

	@Override
	Object[] getOccurrenceValues(Stock entity) {
		return new Object[] {
				entity.getId(),
				entity.getQuantity(),
				entity.getMeasureUnit().getId(),
				entity.getDescription(),
				entity.getDefaultUnitPrice(),
				entity.getSalesCurrency().getId(),
				entity.getBuyingPrice(),
				entity.getBuyingCurrency().getId(),
				entity.getManifacturingDate() != null? entity.getManifacturingDate().getTime() : null,
				entity.getExpiryDate() != null? entity.getExpiryDate().getTime() : null,
				entity.getProduct().getId(),
				entity.getDate().getTime(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}

	@Override
	protected Stock instantiate() {
		return new Stock();
	}

	@Override
	protected String getTableName() {
		return Stock.class.getSimpleName();
	}
	
	@Override
	protected Stock mapping(ResultSet result) throws SQLException {
		Stock s = super.mapping(result);
		s.setBuyingCurrency(new Currency());
		s.setQuantity(result.getDouble("quantity"));
		s.setMeasureUnit(daoFactory.get(MeasureUnitDao.class).findById(result.getString("measureUnit")));
		s.setDescription(result.getString("description"));
		s.setDefaultUnitPrice(result.getDouble("defaultUnitPrice"));
		s.setBuyingPrice(result.getDouble("buyingPrice"));
		s.setSalesCurrency(daoFactory.get(CurrencyDao.class).findById(result.getString("salesCurrency")));
		s.setBuyingCurrency(daoFactory.get(CurrencyDao.class).findById(result.getString("buyingCurrency")));
		s.initManufacturingDate(result.getLong("manifacturingDate"));
		s.initExpiryDate(result.getLong("expiryDate"));
		s.initDate(result.getLong("date"));
		s.setProduct(daoFactory.get(ProductDao.class).findById(result.getString("product")));
		return s;
	}

}
