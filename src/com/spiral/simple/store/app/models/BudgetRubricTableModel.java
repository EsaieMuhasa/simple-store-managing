/**
 * 
 */
package com.spiral.simple.store.app.models;

import com.spiral.simple.store.beans.BudgetRubric;
import com.spiral.simple.store.dao.BudgetRubricDao;
import com.spiral.simple.store.dao.DAOFactory;

/**
 * @author Esaie MUHASA
 *
 */
public class BudgetRubricTableModel extends DBEntityTableModel<BudgetRubric> {
	private static final long serialVersionUID = 5215896242712435577L;
	
	private static final String [] TITLES = {"N°", "Labele", "Description", "Date d'enregistrement"}; 
	
	public BudgetRubricTableModel() {
		super(DAOFactory.getDao(BudgetRubricDao.class));
	}

	@Override
	public int getColumnCount() {
		return TITLES.length;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column < TITLES.length)
			return TITLES[column];
		return super.getColumnName(column);
	}

	@Override
	public Object getValueAt (int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				int index = rowIndex + 1;
				return (index < 10? "0":"")+index;
			case 1:
				return data.get(rowIndex).getLabel();
			case 2:
				return data.get(rowIndex).getDescription();
			case 3:
				return DEFAULT_DATE_TIME_FORMAT.format(data.get(rowIndex).getRecordingDate());
		}
		return "";
	}

}
