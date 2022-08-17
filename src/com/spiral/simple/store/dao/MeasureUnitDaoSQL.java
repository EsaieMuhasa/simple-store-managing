/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.MeasureUnit;

/**
 * @author Esaie MUHASA
 *
 */
class MeasureUnitDaoSQL extends UtilSQL<MeasureUnit> implements MeasureUnitDao {
	
	private static final String [] TABLE_FIELDS = {"id", "shortName", "fullName", "recordingDate", "lastUpdateDate"};

	public MeasureUnitDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByShortName(String shortName) throws DAOException {
		return check("shortName", shortName);
	}

	@Override
	public boolean checkByFullName(String fullName) throws DAOException {
		return check("fullName", fullName);
	}

	@Override
	public boolean checkByShortName(String shortName, String id) throws DAOException {
		return check("shortName", shortName, id);
	}

	@Override
	public boolean checkByFullName(String fullName, String id) throws DAOException {
		return check("fullName", fullName, id);
	}

	@Override
	MeasureUnit[] createArray(int length) {
		return new MeasureUnit[length];
	}

	@Override
	String[] getTableFields() {
		return TABLE_FIELDS;
	}

	@Override
	Object[] getOccurrenceValues(MeasureUnit entity) {
		return new Object [] {
				entity.getId(),
				entity.getShortName(),
				entity.getFullName(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null
		};
	}

	@Override
	protected MeasureUnit instantiate() {
		return new MeasureUnit ();
	}

	@Override
	protected String getTableName() {
		return MeasureUnit.class.getSimpleName();
	}
	
	@Override
	protected MeasureUnit mapping(ResultSet result) throws SQLException {
		MeasureUnit m = super.mapping(result);
		m.setShortName(result.getString("shortName"));
		m.setFullName(result.getString("fullName"));
		return m;
	}

}
