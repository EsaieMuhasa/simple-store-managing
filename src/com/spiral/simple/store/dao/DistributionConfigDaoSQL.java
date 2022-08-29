/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.DistributionConfig;
import com.spiral.simple.store.beans.Product;

/**
 * @author Esaie Muhasa
 *
 */
class DistributionConfigDaoSQL extends UtilSQL<DistributionConfig> implements DistributionConfigDao {

	private static final String[] FIELDS_LABELS = {"id", "recordingDate", "lastUpdateDate", "product"};

	public DistributionConfigDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByProduct(String productKey) throws DAOException {
		return check("product", productKey);
	}

	@Override
	public boolean checkAvailableByProduct(String productKey) throws DAOException {
		return checkData("SELECT * FROM "+getTableName()+" WHERE product = ? AND lastUpdateDate IS NULL LIMIT 1 OFFSET 0", productKey);
	}

	@Override
	public DistributionConfig findAvailableByProduct(String productKey) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHERE product = ? AND lastUpdateDate IS NULL LIMIT 1 OFFSET 0", productKey)[0];
	}

	@Override
	public DistributionConfig[] findByProduct(String productKey) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHERE product = ?", productKey);
	}
	
	@Override
	synchronized void create(Connection connection, int requestId, DistributionConfig... t) throws DAOException, SQLException {
		super.create(connection, requestId, t);
		for (DistributionConfig config : t)
			((DistributionConfigItemDaoSQL) daoFactory.get(DistributionConfigItemDao.class)).create(connection, requestId, config.getItems());
	}

	@Override
	DistributionConfig[] createArray(int length) {
		return new DistributionConfig[length];
	}

	@Override
	String[] getTableFields() {
		return FIELDS_LABELS;
	}

	@Override
	Object[] getOccurrenceValues(DistributionConfig entity) {
		return new Object[] {
				entity.getId(),
				entity.getProduct().getId(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}
	
	@Override
	protected DistributionConfig mapping(ResultSet result) throws SQLException {
		DistributionConfig c = super.mapping(result);
		c.setProduct(new Product());
		c.getProduct().setId(result.getString("product"));
		return c;
	}

	@Override
	protected DistributionConfig instantiate() {
		return new DistributionConfig();
	}

	@Override
	protected String getTableName() {
		return DistributionConfig.class.getSimpleName();
	}

}
