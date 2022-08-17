/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.Currency;

/**
 * @author Esaie MUHASA
 *
 */
class CurrencyDaoSQL extends UtilSQL<Currency> implements CurrencyDao {
	
	private static final String[] TABLE_FIELDS = { "id", "shortName", "fullName", "symbol", "recordingDate", "lastUpdateDate" };

	public CurrencyDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByShortName(String shortName) throws DAOException {
		return check("shortName", shortName);
	}

	@Override
	public boolean checkByFullName(String fullName) throws DAOException {
		return check("fullName", fullName);
	}

	@Override
	Currency[] createArray (int length) {
		return new Currency[length];
	}

	@Override
	String[] getTableFields() {
		return TABLE_FIELDS;
	}

	@Override
	Object[] getOccurrenceValues(Currency entity) {
		return new Object[] {
				entity.getId(),
				entity.getShortName(),
				entity.getFullName(),
				entity.getSymbol(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}

	@Override
	protected Currency instantiate() {
		return new Currency();
	}

	@Override
	protected String getTableName() {
		return Currency.class.getSimpleName();
	}
	
	@Override
	protected Currency mapping(ResultSet result) throws SQLException {
		Currency c = super.mapping(result);
		c.setFullName(result.getString("fullName"));
		c.setShortName(result.getString("shortName"));
		c.setSymbol(result.getString("symbol"));
		return c;
	}

}
