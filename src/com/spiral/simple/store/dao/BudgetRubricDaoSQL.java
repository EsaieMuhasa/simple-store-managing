/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.BudgetRubric;

/**
 * @author Esaie MUHASA
 *
 */
public class BudgetRubricDaoSQL extends UtilSQL<BudgetRubric> implements BudgetRubricDao {
	
	private static final String [] TABLE_FIELDS = {"id", "label", "description", "recordingDate", "lastUpdateDate"};

	public BudgetRubricDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByLabel(String label) throws DAOException {
		return check("label", label);
	}

	@Override
	public boolean checkByLabel(String label, String id) throws DAOException {
		return check("label", label, id);
	}

	@Override
	BudgetRubric[] createArray(int length) {
		return new BudgetRubric[length];
	}

	@Override
	String[] getTableFields() {
		return TABLE_FIELDS;
	}

	@Override
	Object[] getOccurrenceValues(BudgetRubric entity) {
		return new Object[] {
				entity.getId(),
				entity.getLabel(),
				entity.getDescription(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}

	@Override
	protected BudgetRubric instantiate() {
		return new BudgetRubric();
	}

	@Override
	protected String getTableName() {
		return BudgetRubric.class.getSimpleName();
	}

}
