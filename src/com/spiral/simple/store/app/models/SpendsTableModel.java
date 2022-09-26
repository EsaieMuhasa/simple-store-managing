/**
 * 
 */
package com.spiral.simple.store.app.models;

import com.spiral.simple.store.beans.Spends;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.SpendsDao;

/**
 * @author Esaie Muhasa
 */
public class SpendsTableModel extends DBEntityTableModel<Spends> {
	private static final long serialVersionUID = 945514807108164755L;
	
	private static final String TABLE_TITLES [] = {"Rubrique", "Date", "Montant"}; 

	public SpendsTableModel() {
		super(DAOFactory.getDao(SpendsDao.class));
	}
	
	@Override
	public int getColumnCount() {
		return TABLE_TITLES.length;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column < TABLE_TITLES.length)
			return TABLE_TITLES[column];
		return super.getColumnName(column);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return data.get(rowIndex).getRubric().getLabel();
			case 1: return SpendsTableModel.DEFAULT_DATE_FORMAT.format( data.get(rowIndex).getDate());
			case 2: {
				String amount = String.format("%s %s", 
						Spends.DECIMAL_FORMAT.format(data.get(rowIndex).getAmount()),
						data.get(rowIndex).getCurrency().getShortName());
				return amount;
			}
		}
		return "";
	}

}
