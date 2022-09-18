/**
 * 
 */
package com.spiral.simple.store.dao;

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
class PaymentPartDaoSQL extends UtilSQL<PaymentPart> implements PaymentPartDao{

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentPart[] findByDate(Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentPart[] findByDate(Date min, Date max, int limit, int offset) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentPart[] findByItem(DistributionConfigItem item) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentPart[] findByItem(DistributionConfigItem item, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentPart[] findByRubric(BudgetRubric rubric, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByRubric(BudgetRubric rubric) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int countByDefaultRubric() throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PaymentPart[] findByRubric(BudgetRubric rubric) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentPart[] findByRubric(BudgetRubric rubric, int limit, int offset) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentPart[] findByDefaultRubric() throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentPart[] findByDefaultRubric(int limit, int offset) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentPart[] findByDefaultRubric(Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getSumByRubric(BudgetRubric rubric, Currency currency, boolean currencyOnly) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSumByRubric(BudgetRubric rubric, Date min, Date max, Currency currency, boolean currencyOnly)
			throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSumByDefaultRubric(Currency currency, boolean currencyOnly) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSumByDefaultRubric(Date min, Date max, Currency currency, boolean currencyOnly)
			throws DAOException {
		// TODO Auto-generated method stub
		return 0;
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
