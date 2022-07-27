/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.spiral.simple.store.beans.DBEntity;
import com.spiral.simple.store.dao.DAOException.ErrorType;

/**
 * @author Esaie Muhasa
 *
 */
@SuppressWarnings(value = "unchecked")
abstract class UtilSQL <T extends DBEntity> implements DAOInterface<T>{
	
	protected final DefaultDAOFactorySql daoFactory;
	protected final List<DAOBaseListener<T>> baseListeners = new ArrayList<>();
	protected final List<DAOProgressListener<T>> progressListeners = new ArrayList<>();

	/**
	 * @param daoFactory
	 */
	public UtilSQL (DefaultDAOFactorySql daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	@Override
	public boolean checkById(String id) throws DAOException {
		return check("id", id);
	}
	
	public boolean checkById (Connection connection, String id) {
		try (
				PreparedStatement statement = prepare(String.format("SELECT * FROM %s WHERE id = ?", getViewName()), connection, false, id);
				ResultSet result = statement.executeQuery()
			) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee", e);
		}
	}
	
	public String generateId (Connection connection) throws DAOException{
		String id = null;
		do {
			id = UUID.randomUUID().toString().toUpperCase();
		} while (checkById(connection, id));
		return id.toString().toUpperCase();
	}

	@Override
	public void create (int requestId, T... t) {
		new Thread(() -> doCreate(requestId, t)).start();
	}

	@Override
	public void update (int requestId, T... t) {
		new Thread(() -> doUpdate(requestId, t)).start();
	}

	@Override
	public void delete (int requestId, String...keys) {
		new Thread(() -> doDelete(requestId, keys)).start();
	}
	
	
	/**
	 * utility method to insert data in database table
	 * @param requestId
	 * @param t
	 */
	protected final synchronized void doCreate (int requestId, T... t) {
		try (Connection connection = daoFactory.getConnection()) {
			fireOnStart(requestId);
			Date now = new Date();
			connection.setAutoCommit(false);
			fireOnPrepared(requestId, t.length + 1);
			for (int i = 0; i < t.length; i++) {
				if(t[i].getId() == null || t[i].getId().trim().isEmpty() || checkById(connection, t[i].getId()))
					t[i].setId(generateId(connection));
				
				if(t[i].getRecordingDate() == null)
					t[i].setRecordingDate(now);
				
				fireOnProgress(requestId, i+1, t[i]);
			}
			
			create(connection, requestId, t);
			connection.commit();
			fireOnProgress(requestId, t.length+1, null);
			fireOnCreate(requestId, t);
		} catch (SQLException e) {
			DAOException err = new DAOException("Une erreur est survenue lors l'insersion des donnees dans la base de donnees", e, ErrorType.ON_CREATE);
			fireOnError(requestId, err);
		}
	}

	/**
	 * utility method to update occurrences in database table
	 * @param requestId
	 * @param t
	 */
	protected final synchronized void doUpdate (int requestId, T... t) {
		try (Connection connection = daoFactory.getConnection()) {
			fireOnStart(requestId);
			Date now = new Date();
			String [] keys = new String [t.length];
			
			connection.setAutoCommit(false);
			fireOnPrepared(requestId, t.length + 1);
			for (int i = 0; i < t.length; i++) {
				if(t[i].getId() == null || t[i].getId().trim().isEmpty() || !checkById(connection, t[i].getId()))
					throw new DAOException("impossible de poursuivre la mis en jour car l'identifiant d'une occurence n'est pas reconue "
							+ "dans la base de donnee", ErrorType.ON_UPDATE);

				t[i].setLastUpdateDate(now);
				keys[i] = t[i].getId();
				fireOnProgress(requestId, i+1, t[i]);
			}
			
			T [] olds = findAll(keys);
			
			update(connection, requestId, t);
			fireOnProgress(requestId, t.length+1, null);
			connection.commit();
			if(t.length == 1)
				fireOnUpdate(requestId, t[0], olds[0]);
			else
				fireOnUpdate(requestId, olds, t);
		} catch (SQLException e) {
			DAOException ex= new DAOException("Une erreur est survenue lors de la mis en jours", e, ErrorType.ON_UPDATE);
			fireOnError(requestId, ex);
		} catch (DAOException e) {
			fireOnError(requestId, e);
		}
	}
	
	protected final synchronized void doDelete (int requestId, String...keys) {		
		try (Connection connection = daoFactory.getConnection()) {
			fireOnStart(requestId);
			T [] data = createArray(keys.length);
			connection.setAutoCommit(false);
			fireOnPrepared(requestId, keys.length);
			for (int i = 0; i < keys.length; i++) {
				if (!checkById(connection, keys[i]))
					throw new DAOException("Impossible de poursuivre l'operation de supression car "
							+ "l'ID: "+keys[i]+" est inconnue dans la base de donnee", ErrorType.ON_DELETE);
				
				data[i] = find(connection, "id", keys[i]);
				fireOnProgress(requestId, i+1, data[i]);
			}
			delete(connection, keys);
			connection.commit();
			fireOnDelete(requestId, data);
		} catch (SQLException e) {
			DAOException err = new DAOException("Une erreur est survenue lors de la suppression des donnees dans la base de donnees", e, ErrorType.ON_DELETE);
			fireOnError(requestId, err);
		} catch (DAOException e) {
			fireOnError(requestId, e);
		}
	}
	

	@Override
	public T findById (String id) throws DAOException {
		return find("id", id);
	}

	@Override
	public T[] findAll() throws DAOException {
		T [] data = null;
		try (
				Connection connection = daoFactory.getConnection();
				Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet result = statement.executeQuery("SELECT * FROM "+getViewName())
			) {
			int count  = result.last()? result.getRow() : 0;

			if(count != 0) {
				result.beforeFirst();
				data = createArray(count);
				while(result.next())
					data[result.getRow()-1] = mapping(result);
					
			} else 
				throw new DAOException("Aucunne donnee cartographiable pour l'intervale choisie");
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee", e);
		}
		return data;
	}

	@Override
	public T[] findAll (int limit, int offset) throws DAOException {
		T [] data = null;
		try (
				Connection connection = daoFactory.getConnection();
				Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet result = statement.executeQuery("SELECT * FROM "+getViewName()+" LIMIT "+limit+" OFFSET "+offset)
			) {
			int count  = result.last()? result.getRow() : 0;

			if(count != 0) {
				result.beforeFirst();
				data = createArray(count);
				while(result.next())
					data[result.getRow()-1] = mapping(result);
					
			} else 
				throw new DAOException("Aucunne donnee cartographiable pour l'intervale choisie");
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee", e);
		}
		return data;
	}

	@Override
	public int countAll() throws DAOException {
		int count = 0;
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement =  connection.prepareStatement("SELECT COUNT(*) AS nombre FROM "+getViewName());
				ResultSet result = statement.executeQuery()
			) {
			if(result.next())
				count = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee", e);
		}
		return count;
	}
	
	@Override
	public void addBaseListener(DAOBaseListener<T> listener) {
		if(!baseListeners.contains(listener))
			baseListeners.add(listener);
		
	}

	@Override
	public void removeBaseListener(DAOBaseListener<T> listener) {
		baseListeners.remove(listener);
	}

	@Override
	public void removeProgressListener(DAOProgressListener<T> listener) {
		progressListeners.remove(listener);
	}

	@Override
	public void addProgressListener(DAOProgressListener<T> listener) {
		if(!progressListeners.contains(listener))
			progressListeners.add(listener);
	}

	protected boolean check (String columnName, Object value) throws DAOException {
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(String.format("SELECT * FROM %s WHERE %s = ?", getViewName(), columnName), connection, false, value);
				ResultSet result = statement.executeQuery()
			) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee", e);
		}
	}
	
	protected T find (Connection connection, String columnName, Object value) throws DAOException, SQLException{
		T data = null;
		try (
				PreparedStatement statement = prepare(String.format("SELECT * FROM %s WHERE "+columnName+"= ?", getViewName()), connection, false, value);
				ResultSet result = statement.executeQuery()
			) {
			if (result.next()) 
				data = mapping(result);
			else 
				throw new DAOException("Aucune donnee identifier par "+columnName+" :>> "+value+" << dans la table "+getViewName(), ErrorType.ON_SELECT);
		}
		return data;
	}
	
	protected T find (String columnName, Object value) throws DAOException {
		T data = null;
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(String.format("SELECT * FROM %s WHERE "+columnName+"= ?", getViewName()), connection, false, value);
				ResultSet result = statement.executeQuery()
			) {
			if (result.next()) 
				data = mapping(result);
			else 
				throw new DAOException("Aucune donnee identifier par "+columnName+" : "+value+" dans la table "+getViewName());
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee", e);
		}
		return data;
	}
	
	/**
	 * insert new occurrences in table
	 * @param connection
	 * @param requestId
	 * @param t
	 * @throws DAOException
	 * @throws SQLException
	 */
	synchronized void create (Connection connection, int requestId, T... t) throws DAOException, SQLException  {
		String [] labels = getTableFields();
		String sql = "INSERT INTO "+getTableName()+" ( "+String.join(", ", labels)+" ) VALUES";
		Object [] values = new Object[labels.length * t.length]; 
		
		for (int i = 0; i < t.length; i++) {
			Object [] subValues = getOccurrenceValues(t[i]);
			
			String sub = "";
			for (int j = 0; j < subValues.length; j++) {
				sub += " ?,";
				values[ (i*labels.length) + j] = subValues[j];
			}
			sub = sub.substring(0, sub.length() - 1);
			sql += " ("+sub+"),";
		}
		
		sql = sql.substring(0, sql.length() -1);
		
		try (PreparedStatement statement = prepare(sql, connection, false, values)) {
			int status = statement.executeUpdate();
			if(status == 0)
				throw new DAOException("Auncun enregistrement n'a ete prise en compte", ErrorType.ON_CREATE);
		}
		
	}
	
	private String updateSqlQuery = null;
	synchronized void update (Connection connection, int requestId, T... t) throws DAOException, SQLException {
		
		if(updateSqlQuery == null) {			
			String [] labels = getTableFields();
			updateSqlQuery = "UPDATE "+getTableName()+" SET ( ";
			for (int i = 0; i < labels.length; i++)
				updateSqlQuery += labels[i] +"= ?,";
			
			updateSqlQuery = updateSqlQuery.substring(0, updateSqlQuery.length() - 1)+" ) WHERE id = ?";
		}
		
		for (int i = 0; i < t.length; i++) {
			Object [] values = getOccurrenceValues(t[i]);
			try (PreparedStatement statement = prepare(updateSqlQuery, connection, false, values)) {
				int status = statement.executeUpdate();
				if(status == 0)
					throw new DAOException("Auncun enregistrement n'a ete prise en compte pour pour l'id "+t[i].getId(), ErrorType.ON_CREATE);
			}
		}
	}
	
	void delete (Connection connection, String... keys) throws DAOException, SQLException {
		String where = "";
		for (int i = 0; i < keys.length; i++) 
			where += " ?,";
		where = where.substring(0, where.length()-1);
		
		final String sql = "DELETE FROM "+getTableName()+" WHERE id IN("+where+")";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			for (int i = 0; i < keys.length; i++)
				statement.setString(i+1, keys[i]);
			
			int status  = statement.executeUpdate();
			if (status == 0)
				throw new DAOException("Aucune suppression n'a ete fait dans la base de donnee");
		}
	}
	
	/**
	 * create empty array by parameter value length
	 * @param length
	 * @return
	 */
	abstract T [] createArray (int length) ;
	
	/**
	 * return column names of table
	 * @return
	 */
	abstract String [] getTableFields ();
	
	/**
	 * return value of any column of table, order of value must check
	 * order returned by getTableFields() method 
	 * @param entity
	 * @return
	 */
	abstract Object [] getOccurrenceValues (T entity);
	
	/**
	 * map entity in result set
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	protected T mapping (ResultSet result) throws SQLException {
		T t = instantiate();
		t.setId(result.getString("id"));
		t.initRecordingDate(result.getLong("recordingDate"));
		long last = result.getLong("lastUpdateDate");
		if(last != 0)
			t.initLastUpdateDate(last);
		return t;
	}
	
	/**
	 * create new instance by entity class managed by current DAO
	 * @return
	 */
	protected abstract T instantiate ();
	
	/**
	 * return table name in schema database
	 * @return
	 */
	protected abstract String getTableName () ;
	
	/**
	 * the default behavior by this method, is append a string V_ to table name,
	 * if hasView method return true value
	 * @return
	 */
	protected String getViewName () {
		String view = getTableName();
		if(hasView())
			view = "V_"+view;
		return view;
	}
	
	/**
	 * this table has material view in database??
	 * @return
	 */
	protected boolean hasView() {
		return false;
	}
	
	/**
	 * @param entity
	 */
	protected synchronized void fireOnCreate (int requestId, T... data) {
		for (DAOBaseListener<T> ls : baseListeners)
			ls.onCreate(data);
	}
	
	/**
	 * emit deletion event
	 * @param requestId
	 * @param data
	 */
	protected synchronized void fireOnDelete (int requestId, T... data) {
		for (DAOProgressListener<T> ls : progressListeners)
			ls.onFinish(requestId, data);
		
		for (DAOBaseListener<T> ls : baseListeners)
			ls.onDelete(data);
	}
	
	/**
	 * emit on update single occurrence in database table
	 * @param requestId
	 * @param old
	 * @param data
	 */
	protected synchronized void fireOnUpdate(int requestId, T old, T data) {
		for (DAOProgressListener<T> ls : progressListeners)
			ls.onFinish(requestId, data);
		
		for (DAOBaseListener<T> ls : baseListeners)
			ls.onUpdate(old, data);
	}
	
	/**
	 * emit on update multiple occurrences in database table
	 * @param requestId
	 * @param old
	 * @param data
	 */
	protected synchronized void fireOnUpdate(int requestId, T [] old, T [] data) {
		for (DAOProgressListener<T> ls : progressListeners)
			ls.onFinish(requestId, data);
		
		for (DAOBaseListener<T> ls : baseListeners)
			ls.onUpdate(old, data);
	}
	
	/**
	 * emit event when error occurred in process
	 * @param requestId
	 * @param e
	 */
	protected synchronized void fireOnError (int requestId, DAOException e) {
		for (DAOProgressListener<T> ls : progressListeners)
			ls.onError(requestId, e);
	}
	
	/**
	 * emit event on start long process
	 * @param requestId
	 */
	protected synchronized void fireOnStart (int requestId) {
		for (DAOProgressListener<T> ls : progressListeners)
			ls.onStart(requestId);
	}
	
	/**
	 * emit on finish prepared processing
	 * @param requestId
	 * @param max
	 */
	protected synchronized void fireOnPrepared(int requestId, int max) {
		for (DAOProgressListener<T> ls : progressListeners)
			ls.onPrepared(requestId, max);
	}
	
	/**
	 * emit on process progression
	 * @param requestId
	 * @param current
	 * @param data
	 */
	protected synchronized void fireOnProgress (int requestId, int current, T data) {
		for (DAOProgressListener<T> ls : progressListeners)
			ls.onProgress(requestId, current, data);
	}
	
	/**
	 * Prepare a SQL statement
	 * @param SQL_QUERY query to prepare
	 * @param connection a connection on database
	 * @param autoGeneratedKeys when we need generated ID by database
	 * @param objects data to prepare in SQL query
	 * @return
	 * @throws SQLException
	 */
	protected synchronized static PreparedStatement prepare (String SQL_QUERY, Connection connection, boolean autoGeneratedKeys, Object...objects) throws SQLException{
		PreparedStatement statement = connection.prepareStatement(SQL_QUERY, autoGeneratedKeys? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
		for (int i = 0; i < objects.length; i++)
			statement.setObject(i+1, objects[i]);
		return statement;
	}
	
	/**
	 * prepare read only statement
	 * fetch mode two directions is enabled
	 * @param SQL_QUERY
	 * @param connection
	 * @param objects
	 * @return
	 * @throws SQLException
	 */
	protected synchronized static PreparedStatement prepareReadOnly (String SQL_QUERY, Connection connection, Object...objects) throws SQLException{
		PreparedStatement statement = connection.prepareStatement(SQL_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		for (int i = 0; i < objects.length; i++)
			statement.setObject(i+1, objects[i]);
		return statement;
	}
	
	/**
	 * Insersion des donnee dans une table de la base de donnee
	 * @param tableName le nom de la table
	 * @param columnsNames un tableau des noms de colones
	 * @param columnsValues
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected synchronized void insertInTable(String [] columnsNames, Object [] columnsValues) throws SQLException, DAOException{
		try ( Connection connection = daoFactory.getConnection() ) {
			insertInTable(connection, columnsNames, columnsValues);
		} 
	}
	
	protected synchronized void insertInTable(String [] columnsNames, Object [][] columnsValues) throws SQLException, DAOException{
		
	}
	
	/**
	 * insert data in table. Connection on database must initialized above.
	 * we prefer this method when we need execute a query in transaction 
	 * @param connection
	 * @param columnsNames
	 * @param columnsValues
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected synchronized void insertInTable(Connection connection, String [] columnsNames, Object [] columnsValues) throws SQLException, DAOException{
		String 
			SQL_QUERY = "INSERT INTO "+getTableName()+" (",
			SQL_SUITE=" VALUES (";
		for (int i=0; i<columnsNames.length; i++) {
			SQL_QUERY += " "+columnsNames[i]+",";
			SQL_SUITE += " ?,";
		}
		
		SQL_QUERY = SQL_QUERY.substring(0, SQL_QUERY.length()-1)+" )";
		SQL_SUITE = SQL_SUITE.substring(0, SQL_SUITE.length()-1)+" )";
		
		SQL_QUERY += SQL_SUITE;
		try (PreparedStatement statement = prepare(SQL_QUERY, connection, true, columnsValues)) {
			int status = statement.executeUpdate();
			if(status == 0)
				throw new DAOException("Aucune occurence enregistrer. Veiller ré-éssayer svp!");
		}
	}
	
	/**
	 * utility method to generate and execute SQL query to update occurrence in database.
	 * @param connection
	 * @param columnsNames
	 * @param columnsValues
	 * @param idEntity
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected synchronized void updateInTable(Connection connection, String [] columnsNames, Object [] columnsValues, String idEntity) throws SQLException, DAOException{
		String SQL_QUERY = "UPDATE "+getTableName()+" SET";
		for (int i=0; i<columnsNames.length; i++) 
			SQL_QUERY += " "+columnsNames[i]+"=?,";
		
		SQL_QUERY = SQL_QUERY.substring(0, SQL_QUERY.length()-1) + " WHERE id=?";
		
		try ( PreparedStatement statement = prepare(SQL_QUERY, connection, false, columnsValues, idEntity) ) {
			int statut=statement.executeUpdate();
			if(statut == 0 )
				throw new DAOException("Aucune mise ajours n'a été effectuer. Veiller re-essayer svp!");
		}
		
	}
	
	/**
	 * update occurrence in database table
	 * @param columnsNames columns name list to update
	 * @param columnsValues column list value 
	 * @param idEntity entityId in table
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected synchronized void updateInTable(String [] columnsNames, Object [] columnsValues, String idEntity) throws SQLException, DAOException{
		try ( Connection connection = daoFactory.getConnection() ) {
			updateInTable(connection, columnsNames, columnsValues, idEntity);
		}
	}
	

	/**
	 * release connection
	 * @param connection
	 */
	protected void close (Connection connection) {
		if (connection!=null) {
			try {
				connection.close();
			} catch (SQLException e) {}
		}
	}
	
	
	/**
	 * release statement resource
	 * @param statement
	 */
	protected void close (Statement statement) {
		if(statement!=null) {
			try {
				statement.close();
			} catch (SQLException e) {}
		}
	}
	
	/**
	 * release result set resource
	 * @param result
	 */
	protected void close (ResultSet result) {
		if (result!=null) {
			try {
				result.close();
			} catch (SQLException e) {}
		}
	}
	
	/**
	 * release prepared statement resource
	 * @param statement
	 */
	protected void close (PreparedStatement statement) {
		if(statement!=null) {
			try {
				statement.close();
			} catch (SQLException e) {}
		}
	}

}