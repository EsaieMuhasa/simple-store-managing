/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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
	synchronized void create(Connection connection, int requestId, ExchangeRate... t) throws DAOException, SQLException {
		for (ExchangeRate rate : t) {
			if(!checkByCurrencies(rate.getCurrency1().getId(), rate.getCurrency2().getId())) 
				continue;
					
			ExchangeRate old = findAvailableByCurrencies(rate.getCurrency1().getId(), rate.getCurrency2().getId());
			old.setEndTime(rate.getStartTime());
			updateInTable(connection, new String[] { "endTime" }, new Object[] { old.getEndTime().getTime() },
					old.getId());
		}
		super.create(connection, requestId, t);
	}

	@Override
	public ExchangeRate findByCurrencies(String currency1, String currency2, Date time) throws DAOException {
		if(currency1.equals(currency2))
			throw new DAOException(String.format("les devises doivent etre differente: ID1: '%s' && ID2: '%s'", currency1, currency2));
		
		String sql  = "SELECT * FROM "+getTableName()+" WHERE ((currency1 = ? AND currency2 = ?) OR (currency1 = ? AND currency2 = ?)) "
				+ "AND ((startTime <= ? AND endTime >= ?) OR (startTime <= ? AND endTime IS NULL))";
		long date = time.getTime();
		return readData(sql, currency1, currency2, currency2, currency1, date, date, date)[0];
	}
	
	@Override
	public ExchangeRate[] findByCurrency(String currencyKey) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE currency1 = ? OR currency2 = ?",  currencyKey, currencyKey);
	}

	@Override
	public boolean checkByCurrency(String currency) throws DAOException {
		return checkData("SELECT id FROM "+getViewName()+" WHERE currency1 = ? OR currency2 = ? LIMIT 1 OFFSET 0", currency, currency);
	}

	@Override
	public boolean checkByCurrencies(String currency1, String currency2) throws DAOException {
		if(currency1.equals(currency2))
			throw new DAOException(String.format("le devise doivent etre difference: ID1: '%s' && ID2: '%s'", currency1, currency2));
		
		return checkData("SELECT * FROM "+getViewName()+" WHERE ((currency1 = ? AND currency2 = ?) OR (currency1 = ? AND currency2 = ?)) AND endTime IS NULL LIMIT 1", 
				 currency1, currency2, currency2, currency1);
	}

	@Override
	public ExchangeRate[] findAvailableByCurrency(String currency) throws DAOException {
		return readData("SELECT DISTINCT * FROM "+getViewName()+" WHERE (currency1 = ? OR currency2 = ?) AND endTime IS NULL", currency, currency);
	}

	@Override
	public ExchangeRate[] findAvailable() throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE endTime IS NULL");
	}

	@Override
	public ExchangeRate findAvailableByCurrencies(String currency1, String currency2) throws DAOException {
		if(currency1.equals(currency2))
			throw new DAOException(String.format("le devise doivent etre difference: ID1: '%s' && ID2: '%s'", currency1, currency2));
		
		String sql = "SELECT * FROM "+getViewName()+" WHERE ((currency1 = ? AND currency2 = ?) OR (currency1 = ? AND currency2 = ?)) AND endTime IS NULL LIMIT 1";
		return readData(sql, currency1, currency2, currency2, currency1)[0];
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
		rate.setCurrency1(DAOFactory.getDao(CurrencyDao.class).findById(result.getString("currency1")));
		rate.setCurrency2(DAOFactory.getDao(CurrencyDao.class).findById(result.getString("currency2")));
		rate.setRate(result.getDouble("rate"));
		rate.initEndTime(result.getLong("endTime"));
		rate.initSartTime(result.getLong("startTime"));
		return rate;
	}
	
}
