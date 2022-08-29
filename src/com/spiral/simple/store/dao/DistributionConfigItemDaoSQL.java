/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.DistributionConfig;
import com.spiral.simple.store.beans.DistributionConfigItem;

/**
 * @author Esaie Muhasa
 *
 */
class DistributionConfigItemDaoSQL extends UtilSQL<DistributionConfigItem> implements DistributionConfigItemDao {

	private static final String[] FIELD_LABELS = {"id", "recordingDate", "lastUpdateDate", "owner", "rubric", "percent"};

	public DistributionConfigItemDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByKey(String configId, String rubricId) throws DAOException {
		return checkData("SELECT * FROM "+getTableName()+" WHERE config = ? AND rubric = ?", configId, rubricId);
	}

	@Override
	public DistributionConfigItem findByKey(String configId, String rubricId) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHERE config = ? AND rubric = ?", configId, rubricId)[0];
	}

	@Override
	public boolean checkByConfig(String configKey) throws DAOException {
		return check("confing", configKey);
	}

	@Override
	public DistributionConfigItem[] findByConfig(String configKey) throws DAOException {
		return readData("SELECT * FROM "+getTableName()+" WHERE config = ? AND rubric = ?", configKey);
	}

	@Override
	DistributionConfigItem[] createArray(int length) {
		return new DistributionConfigItem[length];
	}

	@Override
	String[] getTableFields() {
		return FIELD_LABELS;
	}

	@Override
	Object[] getOccurrenceValues(DistributionConfigItem entity) {
		return new Object[] {
				entity.getId(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null,
				entity.getOwner().getId(),
				entity.getRubric().getId(),
				entity.getPercent()
		};
	}
	
	@Override
	protected DistributionConfigItem mapping(ResultSet result) throws SQLException {
		DistributionConfigItem i = super.mapping(result);
		i.setOwner(new DistributionConfig());
		i.getOwner().setId(result.getString("owner"));
		i.setRubric(daoFactory.get(BudgetRubricDao.class).findById(result.getString("rubric")));
		i.setPercent(result.getDouble("percent"));
		return i;
	}

	@Override
	protected DistributionConfigItem instantiate() {
		return new DistributionConfigItem();
	}

	@Override
	protected String getTableName() {
		return DistributionConfigItem.class.getSimpleName();
	}

}
