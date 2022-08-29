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
public class CommandItem extends DBEntity {
	private static final long serialVersionUID = -880889777646589531L;
	
	private Command command;
	private Product product;
	private double quantity;
	private DistributionConfig config;
	
	private final List<AffectedStock> stocks = new ArrayList<>();

	/**
	 * 
	 */
	public CommandItem() {
		super();
	}

	/**
	 * @return the command
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(Command command) {
		this.command = command;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
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
	 * @return the configuration binded command item
	 */
	public DistributionConfig getConfig() {
		return config;
	}

	/**
	 * @param config the configuration to set
	 */
	public void setConfig(DistributionConfig config) {
		this.config = config;
	}
	
	/**
	 * adding new affected stock
	 * @param stocks
	 */
	public void addStock (AffectedStock ...stocks) {
		for (AffectedStock stock : stocks){
			this.stocks.add(stock);
			stock.setItem(this);
		}
	}
	
	/**
	 * return table of affected stock by this client command item
	 * @return
	 */
	public AffectedStock [] getStocks () {
		if(stocks.size() == 0)
			return null;
		return stocks.toArray(new AffectedStock[stocks.size()]);
	}
	
	public AffectedStock getStockAt (int index) {
		return stocks.get(index);
	}
	
	/**
	 * remove affected stock
	 * @param stock
	 */
	public void removeStock (AffectedStock stock) {
		stocks.remove(stock);
	}
	
	/**
	 * removing stock affected at index
	 * @param index
	 */
	public void removeStockAt (int index) {
		stocks.remove(index);
	}
	
	/**
	 * removing all stock affected by item command
	 */
	public void removeStocks () {
		stocks.clear();
	}

}
