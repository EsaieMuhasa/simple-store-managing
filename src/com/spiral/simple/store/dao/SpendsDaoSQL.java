/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.spiral.simple.store.beans.BudgetRubric;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.Spends;

/**
 * @author Esaie Muhasa
 *
 */
class SpendsDaoSQL extends BaseCashMoneyDaoSQL<Spends> implements SpendsDao {
	private static final String [] 
			TABLE_FIELDS = {"id", "amount", "currency", "rubric", "date", "label", "recordingDate", "lastUpdateDate"},
			UPDATEBLE_TABLE_FIELDS = {"amount", "currency", "rubric", "date", "label", "lastUpdateDate"};

	public SpendsDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}
	
	@Override
	public boolean checkByDefaultRubric () throws DAOException {
		return checkData("SELECT * FROM "+getViewName()+" WHERE rubric IS NULL LIMIT 1");
	}

	@Override
	public boolean checkByRubric(String rubricId) throws DAOException {
		if(rubricId == null || rubricId.trim().isEmpty())
			return checkByDefaultRubric();
		return checkData("SELECT * FROM "+getViewName()+" WHERE rubric = ? LIMIT 1", rubricId);
	}

	@Override
	public int countByRubric(String rubricId) throws DAOException {
		return countData("SELECT COUNT(*) AS nombre FROM "+getViewName()+" WHERE rubric = ?", rubricId);
	}

	@Override
	public int countByRubric(String rubricId, Date min, Date max) throws DAOException {
		return countData("SELECT * FROM "+getViewName()+" WHERE rubric = ? AND ( date BETWEEN ? AND ? )",
				rubricId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public Spends[] findByRubric(String rubricId) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE rubric = ?", rubricId);
	}

	@Override
	public int countByDefaultRubric() throws DAOException {
		return countData("SELECT COUNT(*) FROM "+getViewName()+" WHERE rubric IS NULL");
	}

	@Override
	public Spends[] findByDefaultRubric() throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE rubric IS NULL");
	}

	@Override
	public Spends[] findByDefaultRubric(int limit, int offset) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE rubric IS NULL LIMIT ? OFFSET ?", limit, offset);
	}

	@Override
	public Spends[] findByDefaultRubric(Date min, Date max) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE rubric IS NULL AND ( date BETWEEN ? AND ? )",
				toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public Spends[] findByRubric(String rubricId, int limit, int offset) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE rubric = ? LIMIT ? OFFSET ?", rubricId, limit, offset);
	}

	@Override
	public Spends[] findByRubric(String rubricId, Date min, Date max) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE rubric =? AND (date BETWEEN ? AND ?)",
				rubricId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public Spends[] findByRubric(String rubricId, Date min, Date max, int limit, int offset) throws DAOException {
		return readData("SELECT * FROM "+getViewName()+" WHERE rubric =? AND (date BETWEEN ? AND ?) LIMIT ? OFFSET ?",
				rubricId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime(), limit, offset);
	}

	@Override
	public double getSumByRubric(String rubricId, Currency currency, boolean currencyOnly) throws DAOException {
		if(rubricId == null)
			return getSumByDefaultRubric(currency, currencyOnly);
		
		String sql = "SELECT SUM(amount) AS montant FROM "+getViewName()+" WHERE rubric = ? AND currency = ?";
		double amount = getSumByQuerry(sql, rubricId, currency.getId());
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSumByRubric(rubricId, c, true);
				if(sub > 0)
					amount += daoFactory.get(ExchangeRateDao.class).convert(sub, c, currency);
			}
		}
		
		return amount;
	}

	@Override
	public double getSumByRubric(String rubricId, Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException {
		if(rubricId == null)
			return getSumByDefaultRubric(min, max, currency, currencyOnly);
		
		String sql = "SELECT SUM(amount) AS montant FROM "+getViewName()+" WHERE rubric = ? AND currency = ? AND (date BETWEEN ? AND ?)";
		double amount = getSumByQuerry(sql, rubricId, currency.getId(), toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSumByRubric(rubricId, min, max, c, true);
				if(sub > 0)
					amount += daoFactory.get(ExchangeRateDao.class).convert(sub, c, currency);
			}
		}
		
		return amount;
	}

	@Override
	public double getSumByDefaultRubric(Currency currency, boolean currencyOnly) throws DAOException {
		String sql = "SELECT SUM(amount) AS montant FROM "+getViewName()+" WHERE currency = ? AND rubric IS NULL";
		double amount = getSumByQuerry(sql, currency.getId());
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSumByDefaultRubric(c, true);
				if(sub > 0)
					amount += daoFactory.get(ExchangeRateDao.class).convert(sub, c, currency);
			}
		}
		
		return amount;
	}

	@Override
	public double getSumByDefaultRubric(Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException {
		String sql = "SELECT SUM(amount) AS montant FROM "+getViewName()+" WHERE rubric IS NULL AND currency = ? AND (date BETWEEN ? AND ?)";
		double amount = getSumByQuerry(sql, currency.getId(), toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSumByDefaultRubric(min, max, c, true);
				if(sub > 0)
					amount += daoFactory.get(ExchangeRateDao.class).convert(sub, c, currency);
			}
		}
		
		return amount;
	}

	@Override
	Spends[] createArray(int length) {
		return new Spends[length];
	}

	@Override
	String[] getTableFields() {
		return TABLE_FIELDS;
	}
	
	@Override
	String[] getUpdatebleFields() {
		return UPDATEBLE_TABLE_FIELDS;
	}

	@Override
	Object[] getOccurrenceValues(Spends entity) {
		return new Object[] {
				entity.getId(),
				entity.getAmount(),
				entity.getCurrency().getId(),
				entity.getRubric() != null? entity.getRubric().getId() : null,
				entity.getDate().getTime(),
				entity.getLabel(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}
	
	@Override
	Object[] getUpdatebleOccurrenceValues(Spends entity) {
		return new Object[] {
				entity.getAmount(),
				entity.getCurrency().getId(),
				entity.getRubric() != null? entity.getRubric().getId() : null,
				entity.getDate().getTime(),
				entity.getLabel(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}
	
	@Override
	protected Spends mapping(ResultSet result) throws SQLException {
		Spends spends = super.mapping(result);
		spends.setLabel(result.getString("label"));
		if(result.getString("rubric") != null){
			spends.setRubric(new BudgetRubric());
			spends.getRubric().setId(result.getString("rubric"));
		}
		return spends;
	}

	@Override
	protected Spends instantiate() {
		return new Spends();
	}

	@Override
	protected String getTableName() {
		return Spends.class.getSimpleName();
	}

}
