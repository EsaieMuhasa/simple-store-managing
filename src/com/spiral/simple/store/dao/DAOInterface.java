/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.DBEntity;

/**
 * @author Esaie Muhasa
 *
 */
@SuppressWarnings(value="unchecked")
public interface DAOInterface <T extends DBEntity> {
	
	/**
	 * Verification of id in database table
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	boolean checkById (String id) throws DAOException;
	
	/**
	 * Create occurrences in database for all objects in array passed at this method parameter
	 * @param requestId
	 * @param t
	 */
	void create (int requestId, T... t);
	
	/**
	 * update occurrences in database.
	 * value of field id of any object in array, has must exist in database.
	 * @param requestId
	 * @param t
	 */
	void update (int requestId, T... t);
	
	/**
	 * definitive deletion of occurrence in database
	 * definitive deletion of occurrences owner of any primary keys in array
	 * passed by this method parameter
	 * @param requestId
	 * @param keys
	 */
	void delete (int requestId, String... keys);
	
	/**
	 * return occurrence owner of primary key in method parameter
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	T findById (String id) throws DAOException;
	
	/**
	 * find all occurrence owner of id in array
	 * @param keys
	 * @return
	 * @throws DAOException
	 */
	T[] findAll(String... keys) throws DAOException;
	
	/**
	 * return all data in database table
	 * @return
	 * @throws DAOException
	 */
	T[] findAll() throws DAOException;
	
	/**
	 * request find all form table
	 * process is run in new thread.
	 * result is emitted in progress listener event type.
	 * make you sure to subscribed 
	 * @param requestId
	 */
	void goFindAll(int requestId);
	
	/**
	 * send request to find interval of data in database table
	 * process is run in new thread. response is emitted in progress listener event type.
	 * @param requestId
	 * @param limit
	 * @param offset
	 */
	void goFindAll (int requestId, int limit, int offset);
	
	/**
	 * return data interval of concerned table in database
	 * @param limit max occurrence to select
	 * @param offset number of occurrences to skip
	 * @return
	 * @throws DAOException
	 */
	T[] findAll(int limit, int offset) throws DAOException;
	
	/**
	 * return count occurrence in database table
	 * @return
	 * @throws DAOException
	 */
	int countAll() throws DAOException;
	
	/**
	 * subscribe new listener.
	 * listener must intersect creation, update and deletion events 
	 * @param listener
	 */
	void addBaseListener (DAOBaseListener<T> listener);
	
	/**
	 * remove listener if that subscribed  
	 * @param listener
	 */
	void removeBaseListener (DAOBaseListener<T> listener);
	
	/**
	 * unsubscribe progress listener
	 * @param listener
	 */
	void removeProgressListener(DAOProgressListener<T> listener);
	
	/**
	 * Subscribe new progress listener
	 * @param listener
	 */
	void addProgressListener(DAOProgressListener<T> listener);

}
