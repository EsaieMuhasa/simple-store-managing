/**
 * 
 */
package com.spiral.simple.store.beans;

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

}
