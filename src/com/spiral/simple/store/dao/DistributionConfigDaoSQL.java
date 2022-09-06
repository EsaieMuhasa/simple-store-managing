/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spiral.simple.store.beans.DistributionConfig;
import com.spiral.simple.store.beans.DistributionConfigItem;
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
		for (DistributionConfig config : t){
			DistributionConfigItem [] items = config.getItems();
			for (DistributionConfigItem item : items) {
				if (item.getPercent() <= 0.0)
					config.removeItem(item);
			}
			((DistributionConfigItemDaoSQL) daoFactory.get(DistributionConfigItemDao.class)).create(connection, requestId, config.getItems());
		}
	}
	
	@Override
	public void toggle(int requestId, DistributionConfig config) {
		Thread t = new Thread(() -> doToggle(requestId, config));
		t.start();
	}
	
	/**
	 * synchronized toggle configuration utility method
	 * @param requestId
	 * @param config
	 */
	private synchronized void doToggle(int requestId, DistributionConfig config) {
		try (Connection connection = daoFactory.getConnection()) {
			connection.setAutoCommit(false);
			if(config.getId() == null || config.getId().trim().isEmpty()) {//nouvelle configuration,
				if(checkAvailableByProduct(config.getProduct().getId())){//nouvons archiver l'enciene si elle existe
					Date now = new Date();
					DistributionConfig old = findAvailableByProduct(config.getProduct().getId());
					old.setLastUpdateDate(now);
					updateInTable(connection, new String[] {"lastUpdateDate"}, new Object[] {now.getTime()}, old.getId());
					config.setRecordingDate(now);
				}
				
				create(connection, requestId, config);
				fireOnCreate(requestId, config);
			} else {//mis en jour de ladite configuration
				/*
				 * => separation des occurences:
				 * 	-a creer
				 * 	-a modifier
				 * 	-a supprimer
				 */
				
				DistributionConfigItem [] items = config.getItems();
				
				List<DistributionConfigItem> itemToCreate = new ArrayList<>();
				List<DistributionConfigItem> itemToUpdate = new ArrayList<>();
				List<DistributionConfigItem> itemToDelete = new ArrayList<>();
				
				for (int i = 0; i < items.length; i++) {
					DistributionConfigItem item = items[i];
					if (item.getId() != null && !item.getId().trim().isEmpty()) {//create or delete
						if(item.getPercent() <= 0.0){
							itemToDelete.add(item);
							config.removeItem(item);
						} else 
							itemToUpdate.add(item);
					} else {
						if (item.getPercent() > 0.0)
							itemToCreate.add(item);
					}
				}
				
				if (!itemToCreate.isEmpty())
					((DistributionConfigItemDaoSQL) daoFactory.get(DistributionConfigItemDao.class)).create(connection, requestId, itemToCreate.toArray(new DistributionConfigItem[itemToCreate.size()]));
				
				if (!itemToUpdate.isEmpty())
					((DistributionConfigItemDaoSQL) daoFactory.get(DistributionConfigItemDao.class)).update(connection, requestId, itemToUpdate.toArray(new DistributionConfigItem[itemToUpdate.size()]));
				
				if (!itemToDelete.isEmpty()){
					String [] keys = new String[itemToDelete.size()];
					for(int i = 0; i < itemToDelete.size(); i++)
						keys[i] = itemToDelete.get(i).getId();
						
					((DistributionConfigItemDaoSQL) daoFactory.get(DistributionConfigItemDao.class)).delete(connection, requestId, keys);
				}
				
				fireOnUpdate(requestId, null, config);
			}
			connection.commit();
		} catch (SQLException e) {
			DAOException d = new DAOException("Une erreur est survenue lors de la communication avec le systhe de stockage des donnees. \n"+e.getMessage(), e);
			fireOnError(requestId, d);
		} catch (DAOException e) {
			fireOnError(requestId, e);
		}
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
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null,
				entity.getProduct().getId()
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
