/**
 * 
 */
package com.spiral.simple.store.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.spiral.simple.store.beans.DBEntity;
import com.spiral.simple.store.dao.DAOException.ErrorType;

/**
 * @author Esaie Muhasa
 *
 */
@SuppressWarnings(value = "unchecked")
abstract class UtilSQL <T extends DBEntity> implements DAOInterface<T>{
	
	public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	
	protected final DefaultDAOFactorySql daoFactory;
	protected final List<DAOBaseListener<T>> baseListeners = new ArrayList<>();
	protected final List<DAOProgressListener<T>> progressListeners = new ArrayList<>();
	protected final List<DAOErrorListener> errorListeners = new ArrayList<>();

	/**
	 * @param daoFactory
	 */
	public UtilSQL (DefaultDAOFactorySql daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	@Override
	public DAOFactory getFactory() {
		return daoFactory;
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
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee\n"+e.getMessage(), e);
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
			connection.setAutoCommit(false);
			fireOnPrepared(requestId, t.length + 1);

			create(connection, requestId, t);
			connection.commit();
			fireOnProgress(requestId, t.length+1, null);
			fireOnCreate(requestId, t);
		} catch (SQLException e) {
			DAOException err = new DAOException("Une erreur est survenue lors l'insersion des donnees dans la base de donnees \n"+e.getMessage(), e, ErrorType.ON_CREATE);
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
				fireOnUpdate(requestId, t, olds);
		} catch (SQLException e) {
			DAOException ex= new DAOException("Une erreur est survenue lors de la mis en jours\n"+e.getMessage(), e, ErrorType.ON_UPDATE);
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
			delete(connection, requestId, keys);
			connection.commit();
			fireOnDelete(requestId, data);
		} catch (SQLException e) {
			DAOException err = new DAOException("Une erreur est survenue lors de la suppression des donnees dans la base de donnees\n"+e.getMessage(), e, ErrorType.ON_DELETE);
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
		try (
				Connection connection = daoFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery("SELECT * FROM "+getViewName())
			) {
			return readData(result, "Aucunne donnee cartographiable dans la table/vue "+getViewName());
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee\n "+e.getMessage(), e);
		}
	}

	@Override
	public T[] findAll (int limit, int offset) throws DAOException {
		try (
				Connection connection = daoFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery("SELECT * FROM "+getViewName()+" LIMIT "+limit+" OFFSET "+offset)
			) {
			return readData(result, "Aucunne donnee cartographiable pour l'intervale choisie");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee\n"+e.getMessage(), e);
		}
	}

	@Override
	public T[] findAll (String... keys) throws DAOException {
		final Object [] params = new Object[keys.length];
		String in = "";
		
		for (int i = 0; i < keys.length; i++) {
			params[i] = keys[i];
			in += " ?,";
		}
		in = in.substring(0, in.length()-1);
		String sql =String.format("SELECT * FROM %s WHERE id IN (%s)", getViewName(), in);
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(sql, connection, false, params);
				ResultSet result = statement.executeQuery()
			) {
			return readData(result, "Aucunne donnee cartographiable pour le tableau des ID proposer");
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee\n"+e.getMessage(), e);
		}
	}

	@Override
	public void goFindAll(int requestId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goFindAll(int requestId, int limit, int offset) {
		// TODO Auto-generated method stub
		
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
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee\n"+e.getMessage(), e);
		}
		return count;
	}
	
	@Override
	public boolean checkAll(int offset) throws DAOException {
		try (
				Connection connection = daoFactory.getConnection();
				Statement statement =  connection.createStatement();
				ResultSet result = statement.executeQuery("SELECT id FROM "+getViewName()+" LIMIT 1 OFFSET "+offset)
			) {
			return (result.next());
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee\n"+e.getMessage(), e);
		}
	}

	@Override
	public void addBaseListener(DAOBaseListener<T> listener) {
		Objects.requireNonNull(listener);
		if(!baseListeners.contains(listener))
			baseListeners.add(listener);
		
	}

	@Override
	public void removeBaseListener(DAOBaseListener<T> listener) {
		Objects.requireNonNull(listener);
		baseListeners.remove(listener);
	}

	@Override
	public void removeProgressListener(DAOProgressListener<T> listener) {
		Objects.requireNonNull(listener);
		progressListeners.remove(listener);
	}

	@Override
	public void addProgressListener(DAOProgressListener<T> listener) {
		Objects.requireNonNull(listener);
		if(!progressListeners.contains(listener))
			progressListeners.add(listener);
	}
	
	@Override
	public void addErrorListener(DAOErrorListener listener) {
		Objects.requireNonNull(listener);
		if(!errorListeners.contains(listener))
			errorListeners.add(listener);
	}

	@Override
	public void removeErrorListener(DAOErrorListener listener) {
		Objects.requireNonNull(listener);
		errorListeners.remove(listener);
	}
	
	
	/**
	 * return the Date object represent a last timetemps at date day in method parameter
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	public static Date toMaxTimestampOfDay (Date date) throws DAOException{
		String date2str = DATE_FORMAT.format(date);
		Date maxDate = null;
		try {
			maxDate = DATE_TIME_FORMAT.parse(date2str+" 23:59:59");
		} catch (ParseException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return maxDate;
	}
	
	/**
	 * return the meddle timestemps of date day in method parameter
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	public static Date toMiddleTimestampOfDay (Date date) throws DAOException{
		String date2str = DATE_FORMAT.format(date);
		Date maxDate = null;
		try {
			maxDate = DATE_TIME_FORMAT.parse(date2str+" 12:00:00");
		} catch (ParseException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return maxDate;
	}
	
	/**
	 * return the min timestamps of date day in method parameter
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	public static Date toMinTimestampOfDay (Date date) throws DAOException{
		String date2str = DATE_FORMAT.format(date);
		Date minDate = null;
		try {
			minDate = DATE_TIME_FORMAT.parse(date2str+" 00:00:00");
		} catch (ParseException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return minDate;
	}

	/**
	 * Fetching for result set and mapping data in this result set.
	 * if result set is empty, DAOEzception was Up, and message in this exception is value 
	 * of second parameter of this method
	 * @param result
	 * @param message
	 * @return
	 * @throws SQLException
	 */
	protected T [] readData (ResultSet result, String message) throws SQLException {
		List<T> list = new ArrayList<>();

		while(result.next())
			list.add(mapping(result));
		if(list.isEmpty()) 
			throw new DAOException(message);
		T [] data = createArray(list.size());
		data = list.toArray(data);
		list.clear();
		return data;
	}
	
	protected T [] readData (ResultSet result) throws SQLException {
		return readData(result, "Aucunne donnee cartographiable pour la requette de selection");
	}
	
	/**
	 * utility method to execute SQL query to select data
	 * @param sqlQuery
	 * @param params
	 * @return
	 */
	protected T [] readData (String sqlQuery, Object...params) {
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(sqlQuery, connection, false, params);
				ResultSet result = statement.executeQuery()
			) {
			return readData(result);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la lecture des donnees dans la base de donnee.\n"+e.getMessage(), e);
		}
	}
	
	/**
	 * utility to execute SQL query to count occurrence
	 * @param sqlQuery : the SQL query must be the form <strong>SELECT COUNT(*) AS label FROM ... </strong>
	 * @param params
	 * @return
	 */
	protected int countData (String sqlQuery, Object...params) {
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(sqlQuery, connection, false, params);
				ResultSet result = statement.executeQuery()
			) {
			if(result.next())
				return result.getInt(1);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee\n"+e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * check if sqlQuery has data in database
	 * @param sqlQuery
	 * @param params
	 * @return
	 */
	protected boolean checkData (String sqlQuery, Object...params) {
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(sqlQuery, connection, false, params);
				ResultSet result = statement.executeQuery()
			) {
			return (result.next());
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee\n"+e.getMessage(), e);
		}
	}

	protected boolean check (String columnName, Object value) throws DAOException {
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(String.format("SELECT * FROM %s WHERE %s = ?", getViewName(), columnName), connection, false, value);
				ResultSet result = statement.executeQuery()
			) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la verification de l'existance des donnees dans la base de donnee\n"+e.getMessage(), e);
		}
	}
	
	protected boolean check (String columnName, Object value, String id) throws DAOException {
		try (
				Connection connection = daoFactory.getConnection();
				PreparedStatement statement = prepare(String.format("SELECT * FROM %s WHERE %s = ? AND id != ?", getViewName(), columnName), connection, false, value, id);
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
		String sql = "INSERT INTO "+getTableName()+" ( "+String.join(", ", labels)+" ) VALUES (";
		Date now = new Date();
		
		for (int j = 0; j < labels.length; j++)
			sql += " ?,";
		sql = sql.substring(0, sql.length() - 1)+")";
		
		for (int i = 0; i < t.length; i++) {
			if(t[i].getId() == null || t[i].getId().trim().isEmpty() || checkById(connection, t[i].getId()))
				t[i].setId(generateId(connection));
			
			if(t[i].getRecordingDate() == null)
				t[i].setRecordingDate(now);
			
			Object [] values = getOccurrenceValues(t[i]);			
			try (PreparedStatement statement = prepare(sql, connection, false, values)) {
				int status = statement.executeUpdate();
				if(status == 0)
					throw new DAOException("Auncun enregistrement n'a ete prise en compte", ErrorType.ON_CREATE);
			}
			fireOnProgress(requestId, i+1, t[i]);
		}
		
	}
	
	private String updateSqlQuery = null;
	synchronized void update (Connection connection, int requestId, T... t) throws DAOException, SQLException {
		Date now = new Date();
		if(updateSqlQuery == null) {			
			String [] labels = getUpdatebleFields();
			updateSqlQuery = "UPDATE "+getTableName()+" SET ";
			for (int i = 0; i < labels.length; i++)
				updateSqlQuery += labels[i] +"=?, ";
			
			updateSqlQuery = updateSqlQuery.trim();
			updateSqlQuery = updateSqlQuery.substring(0, updateSqlQuery.length() - 1)+" WHERE id = ?";
		}
		
		Object [] params = new Object[getUpdatebleFields().length+1];
		for (int i = 0; i < t.length; i++) {
			t[i].setLastUpdateDate(now);
			Object [] values = getUpdatebleOccurrenceValues(t[i]);
			for (int j = 0; j < values.length; j++)
				params[j] = values[j];
			
			params[params.length-1] = t[i].getId();
			try (PreparedStatement statement = prepare(updateSqlQuery, connection, false, params)) {
				int status = statement.executeUpdate();
				if(status == 0)
					throw new DAOException("Auncune modification n'a été prise en compte pour pour l'ID: "+t[i].getId(), ErrorType.ON_CREATE);
			}
		}
	}
	
	/**
	 * deletion of all occurrences owner of any keys
	 * @param connection
	 * @param requestId
	 * @param keys
	 * @throws DAOException
	 * @throws SQLException
	 */
	void delete (Connection connection, int requestId, String... keys) throws DAOException, SQLException {
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
				throw new DAOException("Aucune suppression n'a été fait dans la base de donnée");
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
	 * return the updateble field by table name
	 * @return
	 */
	String [] getUpdatebleFields () {
		return getTableFields();
	}
	
	/**
	 * return value of any column of table, order of value must check
	 * order returned by getTableFields() method 
	 * @param entity
	 * @return
	 */
	abstract Object [] getOccurrenceValues (T entity);
	
	/**
	 * return the updateble occurrence values
	 * @param entity
	 * @return
	 */
	Object [] getUpdatebleOccurrenceValues (T entity) {
		return getOccurrenceValues(entity);
	}
	
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
		t.initLastUpdateDate(result.getLong("lastUpdateDate"));
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
		for (int i = 0, count = baseListeners.size(); i < count; i++)
			baseListeners.get(i).onCreate(data);
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
	 * @param data
	 * @param old
	 */
	protected synchronized void fireOnUpdate(int requestId, T data, T old) {
		for (DAOProgressListener<T> ls : progressListeners)
			ls.onFinish(requestId, data);
		
		for (DAOBaseListener<T> ls : baseListeners)
			ls.onUpdate(data, old);
	}
	
	/**
	 * emit on update multiple occurrences in database table
	 * @param requestId
	 * @param data
	 * @param old
	 */
	protected synchronized void fireOnUpdate(int requestId, T [] data, T [] old) {
		for (DAOProgressListener<T> ls : progressListeners)
			ls.onFinish(requestId, data);
		
		for (DAOBaseListener<T> ls : baseListeners)
			ls.onUpdate(data, old);
	}
	
	/**
	 * emit event when error occurred in process
	 * @param requestId
	 * @param e
	 */
	protected synchronized void fireOnError (int requestId, DAOException e) {
		for (DAOErrorListener ls : errorListeners)
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
		
		try ( PreparedStatement statement = connection.prepareStatement(SQL_QUERY) ) {
			for (int i = 0; i < columnsValues.length; i++)
				statement.setObject(i+1, columnsValues[i]);
			statement.setString(columnsValues.length+1, idEntity);
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
