/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.Product;

/**
 * @author Esaie MUHASA
 *
 */
class ProductDaoSQL extends UtilSQL<Product> implements ProductDao {
	
	private final String [] 
			TABLE_FIELDS = { "id", "name", "description", "picture", "recordingDate", "lastUpdateDate" },
			UPDATEBLE_TABLE_FIELDS = {"name", "description", "picture", "lastUpdateDate" };

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
	String[] getUpdatebleFields() {
		return UPDATEBLE_TABLE_FIELDS;
	}

	@Override
	Object[] getOccurrenceValues(Product entity) {
		return new Object [] {
				entity.getId(),
				entity.getName(),
				entity.getDescription(),
				entity.getPicture(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}
	
	@Override
	Object[] getUpdatebleOccurrenceValues(Product entity) {
		return new Object [] {
				entity.getName(),
				entity.getDescription(),
				entity.getPicture(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}

	@Override
	protected Product instantiate() {
		return new Product();
	}

	@Override
	protected String getTableName() {
		return Product.class.getSimpleName();
	}
	
	@Override
	protected Product mapping(ResultSet result) throws SQLException {
		Product p = super.mapping(result);
		p.setName(result.getString("name"));
		p.setDescription(result.getString("description"));
		p.setPicture(result.getString("picture"));
		return p;
	}
	
}
