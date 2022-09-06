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

import com.spiral.simple.store.beans.helper.Money;

/**
 * @author Esaie MUHASA
 *
 */
public class Command extends DBEntity {

	private static final long serialVersionUID = 5158875958000658833L;
	
	private Date date;
	private boolean delivered;
	private Client client;
	private int number;//the slip number
	private boolean deleted;//when command is temporary delete
	private transient boolean successfullyPaid = false;
	
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
	private transient final List<CommandPayment> credits = new ArrayList<>();
	
	/**
	 * suppresion temporaire des elements d'une commande.
	 * ce champs est utiliser lors dela modification d'une commande, car si l'utilisateur demande la
	 * supression d'un element de la commande, celle-ci n'est pas fait imediatement.
	 * Les element de cette liste serons definitivement supprimer lors de la sauvegarde definitive des modifications
	 */
	private transient final List<CommandItem> tmpDeletion = new ArrayList<>();
	

	/**
	 * 
	 */
	public Command() {
		super();
		successfullyPaid = false;
	}

	/**
	 * @return the successfullyPaid
	 */
	public boolean isSuccessfullyPaid() {
		return successfullyPaid;
	}

	/**
	 * @param successfullyPaid the successfullyPaid to set
	 */
	public void setSuccessfullyPaid(boolean successfullyPaid) {
		this.successfullyPaid = successfullyPaid;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
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
		for (CommandItem item : items) {
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
		if(item.getId() != null)
			tmpDeletion.add(item);
	}
	
	/**
	 * remove item at index, in command
	 * @param index
	 */
	public void removeItemAt (int index) {
		CommandItem item  =  items.remove(index);
		if(item.getId() != null)
			tmpDeletion.add(item);
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
	 * count in tmp deletion list
	 * @return
	 */
	public int countTmpDeletion () {
		return tmpDeletion.size();
	}
	
	/**
	 * return command item in tmp deletion list
	 * @return
	 */
	public CommandItem [] getTmpDeletion () {
		return tmpDeletion.toArray(new CommandItem[tmpDeletion.size()]);
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
	 * remove all payment associate with this client command
	 */
	public void removePayments () {
		payments.clear();
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
	 * return sold by command to string
	 * @return
	 */
	public String getSoldToString() {
		String s = "0 USD";
		return s;
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
	
	/**
	 * renvoie la collection des montant totals qui doivent etre payer pour 
	 * satisfaire la dite command
	 * @return
	 */
	public Money [] getTotalMoney () {
		Map<String, Money>  amounts = new HashMap<>();
		for (CommandItem item : items) {
			Money m = null;
			if(amounts.containsKey(item.getCurrency().getShortName()))
				m = amounts.get(item.getCurrency().getShortName());
			else
				m = new Money(0, item.getCurrency());
			
			double amount = item.getTotalPrice() + m.getAmount();
			m.setAmount(amount);
			amounts.put(item.getCurrency().getShortName(), m);
		}
		Set<String> keys = amounts.keySet();
		Money [] moneys = new Money[keys.size()];
		int index = 0;
		for (String key : keys)
			moneys[index++] = amounts.get(key);
		return moneys;
	}
	
	
	/**
	 * renvoie un tableau des montants deja payemer par le client
	 * @return
	 */
	public Money [] getPaidMoney() {
		if(credits.size() == 0)
			return null;
		
		Money [] moneys = new Money[credits.size()];
		for (int i = 0; i < moneys.length; i++) {
			moneys[i] = new Money(credits.get(i).getAmount(), credits.get(i).getCurrency());
		}
		return moneys;
	}
	
	
	/**
	 * counting of item currencies
	 * @return
	 */
	public int countItemCurrencies () {
		Map<String, Double>  amounts = new HashMap<>();
		for (CommandItem item : items) {
			double amount = 0;
			if(amounts.containsKey(item.getCurrency().getShortName()))
				amount = amounts.get(item.getCurrency().getShortName());
			
			amount += item.getTotalPrice();
			amounts.put(item.getCurrency().getShortName(), amount);
		}
		return amounts.size();
	}
	
	/**
	 * count of payments currencies 
	 * (count of credit currencies)
	 * @return
	 */
	public int countPaymentCurrencies () {
		return credits.size();
	}

	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
