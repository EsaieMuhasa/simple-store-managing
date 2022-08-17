/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.DBEntity;

/**
 * @author Esaie MUHASA
 *
 */
public interface DAOProgressListener <T extends DBEntity> {
	
	/**
	 * emit on task start
	 * @param requestId
	 */
	void onStart (int requestId);
	
	/**
	 * emit on prepared finished (when max count of task is determinate)
	 * @param requestId request id is identifier of task. id bust
	 * @param max
	 */
	void onPrepared (int requestId, int max);
	
	/**
	 * emit on progress increment 
	 * @param requestId
	 * @param current count of task executed
	 * @param data
	 */
	void onProgress (int requestId, int current, T data);
	
	/**
	 * emit on process finished
	 * @param requestId
	 * @param data
	 */
	@SuppressWarnings(value="unchecked")
	void onFinish (int requestId, T... data);

}
