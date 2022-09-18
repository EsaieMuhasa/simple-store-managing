/**
 * 
 */
package com.spiral.simple.store.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Esaie MUHASA
 *
 */
public class DistributionConfig extends DBEntity{

	private static final long serialVersionUID = 6488059592608684622L;
	
	private List<DistributionConfigItem> items = new ArrayList<>();

	public DistributionConfig() {
		super();
	}

	/**
	 * @return the status of configuration
	 */
	public boolean isEnabled() {
		return lastUpdateDate == null && recordingDate != null;
	}
	
	/**
	 * adding items in configuration
	 * @param items
	 */
	public void addItems (DistributionConfigItem ...items) {
		for (DistributionConfigItem item : items) 
			this.items.add(item);
	}
	
	/**
	 * remove item in configuration
	 * @param item
	 */
	public void removeItem (DistributionConfigItem item) {
		items.remove(item);
	}
	
	/**
	 * remove all configuration item
	 */
	public void removeAll () {
		items.clear();
	}
	
	/**
	 * return items associate with this configuration
	 * @return
	 */
	public DistributionConfigItem[] getItems () {
		return items.toArray(new DistributionConfigItem[items.size()]);
	}
	
	@Override
	public String toString() {
		return "Du "+DATE_FORMAT.format(recordingDate)+" "+(lastUpdateDate != null? "au "+DATE_FORMAT.format(lastUpdateDate) : "jusqu'aujourd'hui");
	}

}
