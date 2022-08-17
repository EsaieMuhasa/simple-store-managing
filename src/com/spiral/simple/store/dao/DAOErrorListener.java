/**
 * 
 */
package com.spiral.simple.store.dao;

/**
 * @author Esaie MUHASA
 *
 */
public interface DAOErrorListener {
	
	/**
	 * emit if error occurred in process
	 * @param requestId
	 * @param exception
	 */
	void onError (int requestId, DAOException exception);
}
