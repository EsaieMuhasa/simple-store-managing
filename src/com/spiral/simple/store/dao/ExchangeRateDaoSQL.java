/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.ExchangeRate;

/**
 * @author Esaie MUHASA
 *
 */
class ExchangeRateDaoSQL extends UtilSQL<ExchangeRate> implements ExchangeRateDao {
	
	private static final String [] TABLE_FIELDS = {"id", "currency1", "currency2", "rate", "startTime", "endTime", "recordingDate", "lastUpdateDate"};

	public ExchangeRateDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByCurrency(String currency) throws DAOException {
		return checkData("SELECT id FROM "+getViewName()+" WHERE currency1 = ? OR currency2 = ? LIMIT 1 OFFSET 0", currency, currency);
	}

	@Override
	public boolean checkByCurrencies(String currency1, String currency2) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExchangeRate[] findAvailableByCurrency(String currency) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE currency1 = ? OR currency2 = ?", currency, currency);
	}

	@Override
	public ExchangeRate[] findAvailable() throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExchangeRate findAvailableByCurrencies(String currency1, String currency2) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	ExchangeRate[] createArray(int length) {
		return new ExchangeRate [length];
	}

	@Override
	String[] getTableFields() {
		return TABLE_FIELDS;
	}

	@Override
	Object[] getOccurrenceValues(ExchangeRate entity) {
		return new Object[] {
				entity.getId(),
				entity.getCurrency1().getId(),
				entity.getCurrency2().getId(),
				entity.getRate(), 
				entity.getStartTime().getTime(),
				entity.getEndTime() != null? entity.getEndTime().getTime() : null,
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}

	@Override
	protected ExchangeRate instantiate() {
		return new ExchangeRate();
	}

	@Override
	protected String getTableName() {
		return ExchangeRate.class.getSimpleName();
	}
	
	@Override
	protected ExchangeRate mapping(ResultSet result) throws SQLException {
		ExchangeRate rate = super.mapping(result);
		rate.setCurrency1(new Currency());
		rate.setCurrency2(new Currency());
		rate.setRate(result.getDouble("rate"));
		rate.initEndTime(result.getLong("endDate"));
		rate.initSartTime(result.getLong("startTime"));
		rate.getCurrency1().setId(result.getString("currency1"));
		rate.getCurrency2().setId(result.getString("currency2"));
		return rate;
	}
	
}
