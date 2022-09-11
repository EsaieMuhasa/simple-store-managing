/**
 * 
 */
package com.spiral.simple.store.app.models;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.ExchangeRate;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.ExchangeRateDao;

/**
 * @author Esaie Muhasa
 *
 */
public class ExchangeRateTableModel extends DBEntityTableModel<ExchangeRate> {
	private static final long serialVersionUID = -7841645716653898384L;
	
	public static final String COLUMNS_TITLES [] = {"Intervalle de temps", "Valeur", "Equivalence"};
	
	private final ExchangeRateDao exchangeRateDao = DAOFactory.getDao(ExchangeRateDao.class);
	private Currency currency;
	private boolean availableOnly;
	
	public ExchangeRateTableModel() {
		super(DAOFactory.getDao(ExchangeRateDao.class));
		reload();
	}
	
	@Override
	public synchronized void reload() {
		if(exchangeRateDao == null)
			return;
		
		data.clear();
		ExchangeRate [] rates = null;
		if(currency == null) {//on affiche tout
			if(exchangeRateDao.countAll() == 0){
				if(availableOnly)
					rates = exchangeRateDao.findAvailable();
				else
					rates = exchangeRateDao.findAll(); 
			}
		} else {
			if (exchangeRateDao.checkByCurrency(currency.getId()))
				if(availableOnly)
					rates = exchangeRateDao.findAvailableByCurrency(currency.getId());
				else 
					rates = exchangeRateDao.findByCurrency(currency.getId());
		}
		
		if(rates != null)
			for (ExchangeRate rate : rates) 
				data.add(rate);
		
		fireTableDataChanged();
	}

	/**
	 * @return the availableOnly
	 */
	public boolean isAvailableOnly() {
		return availableOnly;
	}

	/**
	 * @param availableOnly the availableOnly to set
	 */
	public void setAvailableOnly(boolean availableOnly) {
		if(this.availableOnly == availableOnly)
			return;
		
		this.availableOnly = availableOnly;
		reload();
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		if(currency == this.currency || (currency != null && currency.equals(this.currency)))
			return;
		this.currency = currency;
		reload();
	}

	@Override
	public String getColumnName(int column) {
		if(column < COLUMNS_TITLES.length)
			return COLUMNS_TITLES[column];
		return super.getColumnName(column);
	}

	@Override
	public int getColumnCount() {
		return COLUMNS_TITLES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String value = "";
		ExchangeRate  rate = data.get(rowIndex);
		switch (columnIndex) {
			case 0: {
				value = "Du "+ExchangeRate.DATE_FORMAT.format(rate.getStartTime());
				value += " "+(rate.getEndTime() == null? " jusqu'à maintenant": " au "+ExchangeRate.DATE_FORMAT.format(rate.getEndTime()));
			}break;
			
			case 1 :{
				value = "1 " + rate.getCurrency1().getShortName();
			}break;
			
			case 2: {
				value = ExchangeRate.DECIMAL_FORMAT.format(rate.getRate())+" "+rate.getCurrency2().getShortName();
			}
		}
		return value;
	}

}
