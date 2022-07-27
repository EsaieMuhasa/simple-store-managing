/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.DBEntity;

/**
 * @author Esaie MUHASA
 * listener of creation, updating and deletion events
 */
@SuppressWarnings("unchecked")
public interface DAOBaseListener <T extends DBEntity> {
	
	/**
	 * event emitted after new instance(s) created in database
	 * @param data
	 */
	void onCreate (T... data);
	
	/**
	 * event emitted after updating of entity
	 * @param newState, new state of entity
	 * @param oldState old state of entity
	 */
	void onUpdate (T newState, T oldState);
	
	/**
	 * event emitted after entity collection updating
	 * @param newState
	 * @param oldState
	 */
	void onUpdate (T [] newState, T [] oldState);
	
	/**
	 * emitted on all deletion
	 * @param entity
	 */
	void onDelete (T... data);

}
