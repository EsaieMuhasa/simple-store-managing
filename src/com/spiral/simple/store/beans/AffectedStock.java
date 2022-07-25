/**
 * 
 */
package com.spiral.simple.store.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class AffectedStock extends DBEntity {
	private static final long serialVersionUID = -8540484825590004432L;
	
	private CommandItem item;
	private Stock stock;
	private double quantity;
	private double unitPrice;

	/**
	 * 
	 */
	public AffectedStock() {
		super();
	}

	/**
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the unitPrice
	 */
	public double getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the item
	 */
	public CommandItem getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(CommandItem item) {
		this.item = item;
	}

	/**
	 * @return the stock
	 */
	public Stock getStock() {
		return stock;
	}

	/**
	 * @param stock the stock to set
	 */
	public void setStock(Stock stock) {
		this.stock = stock;
	}

}
