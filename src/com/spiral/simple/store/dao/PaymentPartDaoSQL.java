/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.spiral.simple.store.beans.BudgetRubric;
import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.DistributionConfigItem;
import com.spiral.simple.store.beans.helper.PaymentPart;

/**
 * @author Esaie Muhasa
 *
 */
class PaymentPartDaoSQL extends BaseCashMoneyDaoSQL<PaymentPart> implements PaymentPartDao{

	public PaymentPartDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public void delete(int requestId, String... keys) {
		throw new DAOException("Operation non pris en charge");
	}

	@Override
	public void create(int requestId, PaymentPart... t) {
		throw new DAOException("Operation non pris en charge");
	}

	@Override
	public void update(int requestId, PaymentPart... t) {
		throw new DAOException("Ooperation non pris en charge");
	}

	@Override
	public PaymentPart[] findByCommandPayment(CommandPayment item) throws DAOException {
		String sql = "SELECT * FROM "+getViewName()+" WHERE payment = ?";
		return readData(sql, item.getId());
	}

	@Override
	public PaymentPart[] findByItem(DistributionConfigItem item) throws DAOException {
		String sql = "SELECT * FROM "+getViewName()+" WHERE itemPart = ?";
		return readData(sql, item.getId());
	}

	@Override
	public PaymentPart[] findByItem(DistributionConfigItem item, Date min, Date max) throws DAOException {
		String sql = "SELECT * FROM "+getViewName()+" WHERE itemPart = ? AND date BETWEEN ? AND ?";
		return readData(sql, item.getId(), toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public PaymentPart[] findByRubric(BudgetRubric rubric, Date min, Date max) throws DAOException {
		String sql = "SELECT * FROM "+getViewName()+" WHERE rubric = ? AND date BETWEEN ? AND ?";
		return readData(sql, rubric.getId(), toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public int countByRubric(BudgetRubric rubric) throws DAOException {
		String sql = "SELECT COUNT(*) AS nombre FROM "+getViewName()+" WHERE rubric = ?";
		return countData(sql, rubric.getId());
	}

	@Override
	public int countByDefaultRubric() throws DAOException {
		String sql = "SELECT COUNT(*) AS nombre FROM "+getViewName()+" WHERE config IS NULL";
		return countData(sql);
	}

	@Override
	public PaymentPart[] findByRubric(BudgetRubric rubric) throws DAOException {
		String sql = "SELECT * FROM "+getViewName()+" WHERE rubric = ?";
		return readData(sql, rubric.getId());
	}

	@Override
	public PaymentPart[] findByRubric(BudgetRubric rubric, int limit, int offset) throws DAOException {
		String sql = "SELECT * FROM "+getViewName()+" WHERE rubric = ? LIMIT ? OFFSET ?";
		return readData(sql, rubric.getId(), limit, offset);
	}

	@Override
	public PaymentPart[] findByDefaultRubric() throws DAOException {
		String sql = "SELECT * FROM "+getViewName()+" WHERE config IS NULL";
		return readData(sql);
	}

	@Override
	public PaymentPart[] findByDefaultRubric(int limit, int offset) throws DAOException {
		String sql = "SELECT * FROM "+getViewName()+" WHERE config IS NULL LIMIT ? OFFSET ?";
		return readData(sql, limit, offset);
	}

	@Override
	public PaymentPart[] findByDefaultRubric(Date min, Date max) throws DAOException {
		String sql = "SELECT * FROM "+getViewName()+" WHERE config IS NULL AND date BETWEEN ? AND ?";
		return readData(sql, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
	}

	@Override
	public double getSumByRubric(BudgetRubric rubric, Currency currency, boolean currencyOnly) throws DAOException {
		if(rubric == null)
			return getSumByDefaultRubric(currency, currencyOnly);
		
		String sql = "SELECT SUM(part) AS somme  FROM "+getViewName()+" WHERE rubric = ? AND currency = ?";
		double sum = getSumByQuerry(sql, rubric.getId(), currency.getId());
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSumByRubric(rubric, c, true);
				if(sub > 0)
					sum += daoFactory.get(ExchangeRateDao.class).convert(sub, c, currency);
			}
		}
		return sum;
	}

	@Override
	public double getSumByRubric(BudgetRubric rubric, Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException {
		if(rubric == null)
			return getSumByDefaultRubric(min, max, currency, currencyOnly);
		
		String sql = "SELECT SUM(part) AS somme  FROM "+getViewName()+" WHERE rubric = ? AND currency = ? AND date BETWEEN ? AND ?";
		double sum = getSumByQuerry(sql, rubric.getId(), currency.getId(), toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSumByRubric(rubric, min, max, c, true);
				if(sub > 0)
					sum += daoFactory.get(ExchangeRateDao.class).convert(sub, c, currency);
			}
		}
		return sum;
	}

	@Override
	public double getSumByDefaultRubric(Currency currency, boolean currencyOnly) throws DAOException {
		String sql = "SELECT SUM(part) AS somme  FROM "+getViewName()+" WHERE currency = ? AND config IS NULL";
		double sum = getSumByQuerry(sql, currency.getId());
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSumByDefaultRubric(currency, true);
				if(sub > 0)
					sum += daoFactory.get(ExchangeRateDao.class).convert(sub, c, currency);
			}
		}
		return sum;
	}

	@Override
	public double getSumByDefaultRubric(Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException {
		String sql = "SELECT SUM(part) AS somme  FROM "+getViewName()+" WHERE config IS NULL AND currency = ? AND date BETWEEN ? AND ?";
		double sum = getSumByQuerry(sql, currency.getId(), toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		
		if(!currencyOnly) {
			Currency [] currencies = daoFactory.get(CurrencyDao.class).findAll();
			for (Currency c : currencies) {
				if(c.equals(currency))
					continue;
				
				double sub = getSumByDefaultRubric(min, max, currency, true);
				if(sub > 0)
					sum += daoFactory.get(ExchangeRateDao.class).convert(sub, c, currency);
			}
		}
		return sum;
	}
	
	@Override
	protected PaymentPart mapping(ResultSet result) throws SQLException {
		PaymentPart part = super.mapping(result);
		part.setItemPart(new DistributionConfigItem());
		part.getItemPart().setId(result.getString("itemPart"));
		part.setPercent(result.getDouble("percent"));
		part.setSource(new CommandPayment());
		part.getSource().setId(result.getString("payment"));
		return part;
	}

	@Override
	PaymentPart[] createArray(int length) {
		return new PaymentPart[length];
	}

	@Override
	String[] getTableFields() {
		throw new DAOException("operation non pris en charge");
	}

	@Override
	Object[] getOccurrenceValues(PaymentPart entity) {
		throw new DAOException("Operation non pris en charge");
	}

	@Override
	protected PaymentPart instantiate() {
		return new PaymentPart();
	}
	
	@Override
	protected boolean hasView() {
		return true;
	}
	
	@Override
	protected String getViewName() {
		return String.format("V_%s", PaymentPart.class.getSimpleName());
	}

	@Override
	protected String getTableName() {
		throw new DAOException("Aucune table physique dans la base de donnee pour l'entite "+PaymentPart.class.getSimpleName());
	}

}
