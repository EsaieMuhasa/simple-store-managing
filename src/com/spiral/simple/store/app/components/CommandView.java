/**
 * 
 */
package com.spiral.simple.store.app.components;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.spiral.simple.store.app.models.InvoiceTableModel;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie Muhasa
 *
 */
public class CommandView extends JPanel {
	private static final long serialVersionUID = 1802972774245239179L;
	
	private final InvoiceTableModel invoiceTableModel = new InvoiceTableModel();
	private final CustomTable invoiceTable = new CustomTable(invoiceTableModel);
	private final JTabbedPane tabbedPane = new JTabbedPane();
	
	private final JLabel labelName = UIComponentBuilder.createH2("Nom: ");
	private final JLabel labelTelephone = UIComponentBuilder.createH2("Téléphone: ");
	
	private Command command;

	/**
	 * 
	 */
	public CommandView() {
		super(new BorderLayout());
		buildUI();
		
		JPanel padding = new JPanel(new BorderLayout());
		
		padding.add(tabbedPane, BorderLayout.CENTER);
		padding.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		add(padding, BorderLayout.CENTER);
		setBorder(BorderFactory.createLineBorder(invoiceTable.getGridColor()));
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
		invoiceTableModel.setCommand(command);
		invoiceTableModel.daoReload();
	}

	/**
	 * @return the invoiceTableModel
	 */
	public InvoiceTableModel getInvoiceTableModel() {
		return invoiceTableModel;
	}

	/**
	 * utility method to build all UI components
	 */
	private void buildUI() {
		final JPanel panelInvoice = new JPanel(new BorderLayout(5, 5));
		final JPanel panelSlip = new JPanel(new BorderLayout());
		
		final Box client = Box.createVerticalBox();
		client.add(labelName);
		client.add(Box.createVerticalStrut(5));
		client.add(labelTelephone);
		
		final JScrollPane scroll = new JScrollPane(invoiceTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(null);
		
		panelInvoice.add(client, BorderLayout.NORTH);
		panelInvoice.add(scroll, BorderLayout.CENTER);
		
		tabbedPane.addTab("Facture", panelInvoice);
		tabbedPane.addTab("Réçu", panelSlip);
	}

}
