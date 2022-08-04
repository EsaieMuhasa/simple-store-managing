/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.Product;

/**
 * @author Esaie MUHASA
 *
 */
class ProductDaoSQL extends UtilSQL<Product> implements ProductDao {
	
	private final String [] TABLE_FIELDS = { "id", "name", "description", "picture", "recordingDate", "lastUpdateDate" };

	public ProductDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public Product[] search(String value) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Product[] createArray(int length) {
		return new Product[length];
	}

	@Override
	String[] getTableFields() {
		return TABLE_FIELDS;
	}

	@Override
	Object[] getOccurrenceValues(Product entity) {
		Object [] data = {
				entity.getId(),
				entity.getName(),
				entity.getDescription(),
				entity.getPicture(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
		return data;
	}

	@Override
	protected Product instantiate() {
		return new Product();
	}

	@Override
	protected String getTableName() {
		return Product.class.getSimpleName();
	}
	
}
