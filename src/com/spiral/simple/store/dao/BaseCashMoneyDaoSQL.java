/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.spiral.simple.store.beans.CashMoney;
import com.spiral.simple.store.beans.Currency;

/**
 * @author Esaie Muhasa
 *
 */
abstract class BaseCashMoneyDaoSQL <T extends CashMoney> extends UtilSQL<T> implements CashMoneyDao<T> {

	public BaseCashMoneyDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByDate(Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int countByDate(Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public T[] findByDate(Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T[] findByDate(Date min, Date max, int limit, int offset) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getSoldByDate(Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException {
		String sql = "SELECT SUM(amount) AS montant FROM "+getViewName()+" WHERE currency = ? AND (date BETWEEN ? AND ?)";

		double amount = 0;
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(sql, connection, false, currency.getId(), 
						toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
				ResultSet result = statement.executeQuery();
				) {
			if(result.next())
				amount = result.getDouble("montant");
		} catch (SQLException e) {
			throw new  DAOException("Une erreur est survenue lors du chargement des donnees: "+e.getMessage(), e);
		}
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSoldByDate(min, max, c, true);
				if(sub > 0)
					amount += daoFactory.get(ExchangeRateDao.class).convert(sub, c, currency);
			}
		}
		
		return amount;
	}

	@Override
	protected T mapping(ResultSet result) throws SQLException {
		T t = super.mapping(result);
		t.initDate(result.getLong("date"));
		t.setAmount(result.getDouble("amount"));
		t.setCurrency(daoFactory.get(CurrencyDao.class).findById(result.getString("currency")));
		return t;
	}

}
