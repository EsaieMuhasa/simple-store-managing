/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.spiral.simple.store.beans.Client;

/**
 * @author Esaie Muhasa
 *
 */
class ClientDaoSQL extends UtilSQL<Client> implements ClientDao {
	private static final String [] FIELDS_LABELS = {"id", "recordingDate", "lastUpdateDate", "names", "telephone"};

	public ClientDaoSQL(DefaultDAOFactorySql daoFactory) {
		super(daoFactory);
	}

	@Override
	public boolean checkByTelephone(String telephone) throws DAOException {
		return check("telephone", telephone);
	}

	@Override
	public Client findByTelephone(String telephone) throws DAOException {
		return find("telephone", telephone);
	}

	@Override
	Client[] createArray(int length) {
		return new Client [length];
	}

	@Override
	String[] getTableFields() {
		return FIELDS_LABELS;
	}

	@Override
	Object[] getOccurrenceValues(Client entity) {
		return new Object[] {
				entity.getId(),
				entity.getRecordingDate().getTime(),
				entity.getLastUpdateDate() != null? entity.getLastUpdateDate().getTime() : null,
				entity.getNames(),
				entity.getTelephone()
		};
	}
	
	@Override
	protected Client mapping(ResultSet result) throws SQLException {
		Client c = super.mapping(result);
		c.setNames(result.getString("names"));
		c.setTelephone(result.getString("telephone"));
		return c;
	}

	@Override
	protected Client instantiate() {
		return new Client();
	}

	@Override
	protected String getTableName() {
		return Client.class.getSimpleName();
	}

}
