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

	/**
	 * 
	 */
	public CommandPayment() {
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

}
