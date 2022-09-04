/**
 * 
 */
package com.spiral.simple.store.dao;

import com.spiral.simple.store.beans.DBEntity;

/**
 * @author Esaie MUHASA
 *
 */
@SuppressWarnings(value = "unchecked")
public abstract class DAOListenerAdapter<T extends DBEntity>
		implements DAOBaseListener<T>, DAOProgressListener<T>, DAOErrorListener {

	@Override
	public void onStart(int requestId) {}

	@Override
	public void onPrepared(int requestId, int max) {}

	@Override
	public void onProgress(int requestId, int current, T data) {}

	@Override
	public void onFinish(int requestId, T... data) {}

	@Override
	public void onError(int requestId, DAOException exception) {}

	@Override
	public void onCreate(T... data) {}

	@Override
	public void onUpdate(T newState, T oldState) {}

	@Override
	public void onUpdate(T[] newState, T[] oldState) {}

	@Override
	public void onDelete(T... data) {}

}
