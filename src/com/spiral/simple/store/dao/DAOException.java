package com.spiral.simple.store.dao;

/**
 * 
 * @author esaie
 *
 */
public class DAOException extends RuntimeException {
	private static final long serialVersionUID = -6136766942041571215L;
	
	/**
	 * @author Esaie MUHASA
	 * enumeration of error bind on CRUD operations
	 */
	public static enum ErrorType {
		ON_CREATE,
		ON_UPDATE,
		ON_DELETE,
		ON_SELECT
	};
	
	private ErrorType type;

	public DAOException(String message) {
		super(message);
	}
	
	public DAOException(String message, ErrorType type) {
		super(message);
		this.type = type;
	}

	public DAOException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public DAOException(Throwable cause, ErrorType type) {
		super(cause.getMessage(), cause);
		this.type = type;
	}

	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DAOException(String message, Throwable cause, ErrorType type) {
		super(message, cause);
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public ErrorType getType() {
		return type;
	}

}
