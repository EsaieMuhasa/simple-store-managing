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
		return checkData("SELECT * FROM "+getViewName()+" WHERE date BETWEEN ? AND ?", 
				toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public int countByDate(Date min, Date max) throws DAOException {
		return countData("SELECT COUNT(*) AS nombre FROM "+getViewName()+" WHERE date BETWEEN ? AND ?", 
				toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public T[] findByDate(Date min, Date max) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE date BETWEEN ? AND ?", 
				toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public T[] findByDate(Date min, Date max, int limit, int offset) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE date BETWEEN ? AND ? LIMIT ? OFFSET ?", 
				toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}
	
	/**
	 * execute de la requette de selection des la somme des operations d'une collone
	 * le resultat revoyer est celle de la collone 1 dans la requette
	 * @param sqlQuerry
	 * @param params
	 * @return
	 * @throws DAOException
	 */
	protected double getSumByQuerry (String sqlQuerry, Object ...params) throws DAOException{
		double amount = 0;
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(sqlQuerry, connection, false, params);
				ResultSet result = statement.executeQuery();
				) {
			if(result.next())
				amount = result.getDouble(1);
		} catch (SQLException e) {
			throw new  DAOException("Une erreur est survenue lors du chargement des donnees: "+e.getMessage(), e);
		}		
		return amount;
	}

	@Override
	public double getSumByDate(Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException {
		String sql = "SELECT SUM(amount) AS montant FROM "+getViewName()+" WHERE currency = ? AND (date BETWEEN ? AND ?)";

		double amount = getSumByQuerry(sql, currency.getId(), 
						toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSumByDate(min, max, c, true);
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
