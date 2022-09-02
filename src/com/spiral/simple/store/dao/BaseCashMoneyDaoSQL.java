/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.spiral.simple.store.beans.CashMoney;

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
	public double getSoldByDate(Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
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
