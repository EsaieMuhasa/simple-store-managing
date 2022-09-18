/**
 * 
 */
package com.spiral.simple.store.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class CommandPayment extends CashMoney {

	private static final long serialVersionUID = 1340171519317359710L;
	
	private Command command;
	private DistributionConfig config;//configuration auquel l'operation est liee
	private int number;//payment number

	/**
	 * 
	 */
	public CommandPayment() {
		super();
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
	
	@Override
	public String toString() {
		return String.format("%s %s", DECIMAL_FORMAT.format(amount), currency.getShortName());
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

}
