/**
 * 
 */
package com.spiral.simple.store.beans;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Esaie MUHASA
 * default base database entity
 */
public abstract class DBEntity implements Serializable{

	private static final long serialVersionUID = 6959053409534426415L;
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.##");
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * 
	 */
	protected String id;
	
	/**
	 * date of record occurrence
	 */
	protected Date recordingDate;
	
	/**
	 * the last date and time of occurrence update
	 */
	protected Date lastUpdateDate;

	/**
	 * 
	 */
	public DBEntity() {
		super();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the recordingDate
	 */
	public Date getRecordingDate() {
		return recordingDate;
	}

	/**
	 * @param recordingDate the recordingDate to set
	 */
	public void setRecordingDate(Date recordingDate) {
		this.recordingDate = recordingDate;
	}
	
	/**
	 * 
	 * @param timestamp
	 * @return
	 */
	protected Date buildDate (long timestamp) {
		Date date = null;
		if(timestamp <= 1000)
			timestamp = 0;
		
		if(timestamp != 0)
			date = new Date(timestamp);
		
		return date;
	}
	
	public void initRecordingDate (long date) {
		recordingDate = buildDate(date);
	}
	
	public void initLastUpdateDate (long date) {
		lastUpdateDate = buildDate(date);
	}

	/**
	 * @return the lastUpdateDate
	 */
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	/**
	 * @param lastUpdateDate the lastUpdateDate to set
	 */
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

}
