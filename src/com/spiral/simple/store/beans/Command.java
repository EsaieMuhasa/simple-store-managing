/**
 * 
 */
package com.spiral.simple.store.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	/**
	 * all payment associate with this client command
	 */
	private final List<CommandPayment> payments = new ArrayList<>();
	
	/**
	 * collection of all sold of command.
	 * items in this list are associate on one currency.
	 * result in command payment item is sum of all payment item, has same currency 
	 */
	private final List<CommandPayment> credits = new ArrayList<>();
	

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
	
	/**
	 * remove payment at index, in command
	 * @param index
	 */
	public void removePaymentAt(int index) {
		payments.remove(index);
	}
	
	/**
	 * adding command payment
	 * @param payments
	 */
	public void addPayments(CommandPayment ...payments) {
		for (CommandPayment payment : payments) {
			if(!this.payments.contains(payment)) {
				this.payments.add(payment);
				payment.setCommand(this);
				if(payment.getId() == null)
					payment.setDate(date);
			}
		}
		sortCredits();
	}
	
	/**
	 * calculate
	 */
	private void sortCredits() {
		credits.clear();
		for (CommandPayment payment : payments) {
			boolean sum = false;
			for (CommandPayment in : credits) {
				if(payment.getCurrency().equals(in.getCurrency())) {
					in.sum(payment);
					sum = true;
					break;
				}
			}
			
			if (!sum){
				CommandPayment credit = new CommandPayment();
				credit.setCommand(this);
				credit.setDate(date);
				credit.setCurrency(payment.getCurrency());
				credit.sum(payment);
				credits.add(credit);
			}
		}
	}

	/**
	 * check if command payment by occurrence is in payment list
	 * @param currency
	 * @return
	 */
	public boolean hasPaymentByCurrency (Currency currency) {
		for (CommandPayment payment : payments)
			if(payment.getCurrency().equals(currency))
				return true;
		return false;
	}
	
	/**
	 * count all payment items associate at this client command
	 * @return
	 */
	public int countPayements() {
		return payments.size();
	}
	
	/**
	 * utility method to clear all payment,
	 * and merge credit to payment list
	 */
	public void creditsToPayments () {
		payments.clear();
		payments.addAll(credits);
	}
	
	/**
	 * remove payment in payments list
	 * @param payment
	 */
	public void removePayment(CommandPayment payment) {
		payments.remove(payment);
	}
	
	/**
	 * return command payment list
	 * @return
	 */
	public CommandPayment [] getPayments () {
		if(payments.size() == 0)
			return null;
		return payments.toArray(new CommandPayment[payments.size()]);
	}
	
	/**
	 * return credit by any support currency
	 * @return
	 */
	public CommandPayment [] getCredits () {
		if(credits.size() == 0)
			return null;
		return credits.toArray(new CommandPayment[credits.size()]);
	}
	
	/**
	 * convert credits content to string
	 * @return
	 */
	public String getCreditToString () {
		String c = "";
		for (CommandPayment credit : payments)
			c+= credit.toString() +" + ";
		c = c.trim();
		c = c.substring(0, c.length()-2);
		return c;
	}
	
	/**
	 * return total amount as string, to pay for this command
	 * @return
	 */
	public String getTotalToString () {
		Map<String, Double>  amounts = new HashMap<>();
		for (CommandItem item : items) {
			double amount = 0;
			if(amounts.containsKey(item.getCurrency().getShortName()))
				amount = amounts.get(item.getCurrency().getShortName());
			
			amount += item.getTotalPrice();
			amounts.put(item.getCurrency().getShortName(), amount);
		}
		String c = "";
		Set<String> keys = amounts.keySet();
		for (String key : keys)
			c += DECIMAL_FORMAT.format(amounts.get(key))+" "+key + " + ";
		
		if(c.length() > 3)
			c = c.substring(0, c.length() - 3);
		return c;
	}
	
}
