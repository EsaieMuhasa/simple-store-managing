/**
 * 
 */
package com.spiral.simple.store.app.models;

import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.dao.DAOInterface;

/**
 * @author Esaie MUHASA
 *
 */
public class ProductTableModel extends DBEntityTableModel<Product> {
	private static final long serialVersionUID = 1252963400358605240L;
	
	private final String [] TITLES = {"N°", "Photo", "Nom du produit", "Date d'enrgistrement"};
	
	public ProductTableModel(DAOInterface<Product> daoInterface) {
		super(daoInterface);
	}
	
	@Override
	public String getColumnName(int column) {
		if(column < TITLES.length)
			return TITLES[column];
		return super.getColumnName(column);
	}

	@Override
	public int getColumnCount() {
		return TITLES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0 : {
				int index = rowIndex+1;
				return (index < 10? "0":"")+index;
			}
			case 1 : {
				if(data.get(rowIndex).getPicture() != null)
					return data.get(rowIndex).getImage();
			}break;
			case 2 : return data.get(rowIndex).getName();
			case 3 : return DEFAULT_DATE_TIME_FORMAT.format(data.get(rowIndex).getRecordingDate());
		}
		return "";
	}

}
