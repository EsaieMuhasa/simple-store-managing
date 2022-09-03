/**
 * 
 */
package com.spiral.simple.store.app.models;

import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.dao.CommandPaymentDao;
import com.spiral.simple.store.dao.DAOFactory;

/**
 * @author Esaie Muhasa
 *
 */
public class ReceivedTableModel extends DBEntityTableModel<CommandPayment> {
	private static final long serialVersionUID = -3035824201951776524L;
	
	private static final String [] TITLES = {"N°", "Date", "amount"};
	
	private final CommandPaymentDao commandPaymentDao;
	private Command command;
	
	public ReceivedTableModel() {
		super(DAOFactory.getDao(CommandPaymentDao.class));
		commandPaymentDao = DAOFactory.getDao(CommandPaymentDao.class);
	}
	
	@Override
	public synchronized void reload() {
		data.clear();
		
		if(command != null && command.countPayements() != 0){
			CommandPayment [] payments = command.getPayments();
			for (CommandPayment payment : payments)
				data.add(payment);
		}
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		if(column < TITLES.length)
			return TITLES[column];
		return super.getColumnName(column);
	}
	
	/**
	 * force reloading payment by command id DAO
	 */
	public synchronized void daoReload() {
		data.clear();
		if(command != null && commandPaymentDao.checkByCommand(command.getId())) {
			CommandPayment[] payments = commandPaymentDao.findByCommand(command.getId());
			for (CommandPayment payment : payments)
				data.add(payment);
			
			command.removePayments();
			command.addPayments(payments);
		}
		fireTableDataChanged();
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
		if(command == this.command)
			return;
		
		this.command = command;
		reload();
	}

	@Override
	public int getColumnCount() {
		return TITLES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return (data.get(rowIndex).getNumber());
			case 1: return DEFAULT_DATE_FORMAT.format(data.get(rowIndex).getDate());
			case 2: return data.get(rowIndex).toString();
		}
		return "";
	}

}
