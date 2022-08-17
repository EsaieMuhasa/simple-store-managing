/**
 * 
 */
package com.spiral.simple.store.app.models;

import com.spiral.simple.store.beans.MeasureUnit;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.MeasureUnitDao;

/**
 * @author Esaie MUHASA
 *
 */
public class MeasureUnitTableModel extends DBEntityTableModel<MeasureUnit> {
	private static final long serialVersionUID = -9173640909234491297L;
	
	private static final String [] TITLES = {"N°", "Abbreviation", "Appelation complete", "Date d'enregistrement"};
	
	public MeasureUnitTableModel () {
		super(DAOFactory.getDao(MeasureUnitDao.class));
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
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				int index = rowIndex + 1;
				return (index < 10? "0":"")+index;
			case 1:
				return data.get(rowIndex).getShortName();
			case 2:
				return data.get(rowIndex).getFullName();
			case 3:
				return DEFAULT_DATE_TIME_FORMAT.format(data.get(rowIndex).getRecordingDate());
		}
		return "";
	}

}
