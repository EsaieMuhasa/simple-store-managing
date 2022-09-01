/**
 * 
 */
package com.spiral.simple.store.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Esaie MUHASA
 *
 */
public class Command extends DBEntity {

	private static final long serialVersionUID = 5158875958000658833L;
	
	private Date date;
	private boolean delivered;
	private Client client;
	
	/**
	 * all item associate with this client command
	 */
	private final List<CommandItem> items = new ArrayList<>();
	private final List<CommandPayment> payments = new ArrayList<>();

	/**
	 * 
	 */
	public Command() {
		super();
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the delivered
	 */
	public boolean isDelivered() {
		return delivered;
	}

	/**
	 * @param delivered the delivered to set
	 */
	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}
	
	/**
	 * adding command items
	 * @param items
	 */
	public void addItem (CommandItem...items) {
		for (CommandItem item : items){
			this.items.add(item);
			item.setCommand(this);
		}
	}
	
	/**
	 * remove item in this command
	 * @param item
	 */
	public void removeItem (CommandItem item) {
		items.remove(item);
	}
	
	/**
	 * remove item at index, in command
	 * @param index
	 */
	public void removeItemAt (int index) {
		items.remove(index);
	}
	
	/**
	 * remove all items in command
	 */
	public void removeItems () {
		items.clear();
	}
	
	/**
	 * get all item in client command
	 * @return
	 */
	public CommandItem [] getItems () {
		if(items.size() == 0)
			return null;
		
		return items.toArray(new CommandItem[items.size()]);
	}
	
	/**
	 * count item of client command
	 * @return
	 */
	public int countItems () {
		return items.size();
	}
	
	public void removePaymentAt(int index) {
		
	}

}
