/**
 * 
 */
package com.spiral.simple.store.app.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.spiral.simple.store.app.models.InvoiceTableModel;
import com.spiral.simple.store.app.models.ReceivedTableModel;
import com.spiral.simple.store.beans.Client;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.dao.ClientDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.ExchangeRateDao;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie Muhasa
 *
 */
public class CommandView extends JComponent {
	private static final long serialVersionUID = 1802972774245239179L;
	
	private final List<CommandViewListener>  listeners = new ArrayList<>();
	
	private final InvoiceTableModel invoiceTableModel = new InvoiceTableModel();
	private final ReceivedTableModel receivedTableModel = new ReceivedTableModel();
	private final CustomTable invoiceTable = new CustomTable(invoiceTableModel);
	private final CustomTable receivedTable = new CustomTable(receivedTableModel);
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
	
	private final JLabel labelCommandNumber = UIComponentBuilder.createH3("");
	private final JLabel labelName = UIComponentBuilder.createH3("Nom: ");
	private final JLabel labelTelephone = UIComponentBuilder.createH3("Téléphone: ");
	private final JLabel labelCommandAmount = UIComponentBuilder.createH2("");
	private final JLabel labelPaymentAmount = UIComponentBuilder.createH2("");
	private final JLabel labelPaymentDebtAmount = UIComponentBuilder.createH2("");
	
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem [] popupOptions = {
			new JMenuItem("Livraison la commande", new ImageIcon(Config.getIcon("success"))),
			new JMenuItem("Editer la commande", new ImageIcon(Config.getIcon("edit"))),
			new JMenuItem("Supprimer la commande", new ImageIcon(Config.getIcon("close"))),
			new JMenuItem("Nouveau payement", new ImageIcon(Config.getIcon("new"))),
			new JMenuItem("Modifier l'identite du client", new ImageIcon(Config.getIcon("usredit")))
	};

	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) {
				popupMenu.show(CommandView.this, e.getX(), e.getY());
			}
		}
	};
	
	private final ActionListener pupupOptionListener = event -> onItemOption(event);
	private Command command;
	
	private final ExchangeRateDao exchangeRateDao = DAOFactory.getDao(ExchangeRateDao.class);
	private final ClientDao clientDao = DAOFactory.getDao(ClientDao.class);
	private final DAOListenerAdapter<Client> clientListenerAdapter = new DAOListenerAdapter<Client>() {

		@Override
		public void onUpdate(Client newState, Client oldState) {
			System.out.println(newState);
			System.out.println(oldState);
			if(command.getClient().equals(newState)){
				command.setClient(newState);
				updateClient();
			}
		}
	};

	/**
	 * 
	 */
	public CommandView() {
		super();
		setLayout(new BorderLayout());
		buildUI();
		addMouseListener(mouseAdapter);
		invoiceTable.addMouseListener(mouseAdapter);
		tabbedPane.addMouseListener(mouseAdapter);
		clientDao.addBaseListener(clientListenerAdapter);
		
		//pup up menu
		for (int i = 0; i < popupOptions.length; i++) {
			popupMenu.add(popupOptions[i]);
			popupOptions[i].addActionListener(pupupOptionListener);
			popupOptions[i].setName((i+1)+"");
			if(i == 2 || i == 3)
				popupMenu.addSeparator();
		}
		//==
	}
	
	/**
	 * updating client personal informations
	 */
	private void updateClient() {
		if(command == null || command.getClient() == null)
			return;
		labelName.setText("Noms: "+command.getClient().getNames());
		labelTelephone.setText("Téléphone: "+command.getClient().getTelephone());
	}
	
	/**
	 * dispose all resource used by command view.
	 * Disconnect command view on DAOs interfaces, and clear command view listeners
	 * and disconnection on base swing listener
	 */
	public void dispose () {
		invoiceTable.removeMouseListener(mouseAdapter);
		tabbedPane.removeMouseListener(mouseAdapter);
		clientDao.removeBaseListener(clientListenerAdapter);
		
		for (int i = 0; i < popupOptions.length; i++) 
			popupOptions[i].removeActionListener(pupupOptionListener);
	}
	
	/**
	 * changement de l'etant d'un element du menu
	 * @param index
	 * @param enabled
	 */
	private void setEnabledItemOption(int index, boolean enabled) {
		popupOptions[index].setEnabled(enabled);
	}

	/**
	 * on item of pop up menu selected
	 * @param event
	 */
	private void onItemOption(ActionEvent event) {
		if(listeners.size() == 0)
			return;
		
		JMenuItem item = (JMenuItem) event.getSource();
		int index = Integer.parseInt(item.getName());
		
		switch (index) {
			case 1://deliver command
				for (CommandViewListener ls : listeners)
					ls.onDeliveryRequest(command);
				break;
				
			case 2://update command
				for (CommandViewListener ls : listeners)
					ls.onUpdateRequest(command);
				break;
				
			case 3://delete command
				for (CommandViewListener ls : listeners)
					ls.onDeletionRequest(command);
				break;
				
			case 4://command payment
				for (CommandViewListener ls : listeners)
					ls.onPaymentRequest(command);
				break;
				
			case 5://update client name and telephone number
				for (CommandViewListener ls : listeners)
					ls.onClientUpdateRequest(command.getClient());
				break;
				
			default:
				break;
		}
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
		receivedTableModel.setCommand(command);
		receivedTableModel.daoReload();
		invoiceTableModel.daoReload();
		updateClient();
		
		if(command == null)
			return;
		
		labelPaymentAmount.setText(command.getCreditToString());
		labelPaymentDebtAmount.setText(command.getSoldToString());
		
		labelCommandNumber.setText("N° "+command.getNumber());
		labelCommandAmount.setText(command.getTotalToString());
		exchangeRateDao.processingCommandPayment(command);
		
		setEnabledItemOption(3, !command.isSuccessfullyPaid());
		labelCommandAmount.setForeground(command.isSuccessfullyPaid()? Color.GREEN.darker() : Color.RED.darker());
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
		
		final Box client = Box.createVerticalBox(), number = Box.createHorizontalBox();
		final JPanel top = new JPanel(new BorderLayout());
		
		number.add(Box.createHorizontalGlue());
		number.add(labelCommandNumber);
		labelCommandNumber.setHorizontalAlignment(JLabel.RIGHT);
		
		top.add(number, BorderLayout.NORTH);
		top.add(client, BorderLayout.CENTER);
		
		client.add(labelName);
		client.add(Box.createVerticalStrut(5));
		client.add(labelTelephone);
		client.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		client.setBackground(CustomTable.GRID_COLOR);
		client.setOpaque(true);
		
		final JPanel 
			invoiceBottom = new JPanel(new BorderLayout()),
			receivedBottom = new JPanel(new BorderLayout());
		
		final JScrollPane 
			scrollInvoice = new JScrollPane(invoiceTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
			scrollReceived = new JScrollPane(receivedTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		scrollInvoice.setBorder(null);
		scrollReceived.setBorder(null);
		
		panelInvoice.add(scrollInvoice, BorderLayout.CENTER);
		panelInvoice.add(invoiceBottom, BorderLayout.SOUTH);
		
		//bottom panel to show calculated total amount by command
		invoiceBottom.add(UIComponentBuilder.createH2("Total facture: "), BorderLayout.WEST);
		invoiceBottom.add(labelCommandAmount, BorderLayout.CENTER);
		invoiceBottom.setBackground(invoiceTable.getGridColor());
		invoiceBottom.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		labelCommandAmount.setHorizontalAlignment(JLabel.RIGHT);
		//==
		
		//bottom panel in tap to show received amount
		final JPanel 
			debs = new JPanel(new BorderLayout()),
			credits = new JPanel(new BorderLayout());
		
		debs.add(UIComponentBuilder.createH2("Dette: "), BorderLayout.WEST);
		credits.add(UIComponentBuilder.createH2("Total réçu: "), BorderLayout.WEST);
		debs.add(labelPaymentDebtAmount, BorderLayout.CENTER);
		credits.add(labelPaymentAmount, BorderLayout.CENTER);
		
		credits.setOpaque(false);
		debs.setOpaque(false);
		
		labelPaymentDebtAmount.setHorizontalAlignment(JLabel.RIGHT);
		labelPaymentAmount.setHorizontalAlignment(JLabel.RIGHT);
		
		receivedBottom.add(debs, BorderLayout.CENTER);
		receivedBottom.add(credits, BorderLayout.NORTH);
		receivedBottom.setBackground(invoiceTable.getGridColor());
		receivedBottom.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		//==
		
		panelSlip.add(scrollReceived, BorderLayout.CENTER);
		panelSlip.add(receivedBottom, BorderLayout.SOUTH);
		
		tabbedPane.addTab("Facture", panelInvoice);
		tabbedPane.addTab("Réçu", panelSlip);

		JPanel padding = new JPanel(new BorderLayout());
		
		padding.add(top, BorderLayout.NORTH);
		padding.add(tabbedPane, BorderLayout.CENTER);
		padding.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		add(padding, BorderLayout.CENTER);
		setBorder(BorderFactory.createLineBorder(invoiceTable.getGridColor()));
	}
	
	/**
	 * listening command view event
	 * @param listener
	 */
	public void addCommandViewListener (CommandViewListener listener) {
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	/**
	 * removing listener
	 * @param listener
	 */
	public void removeCommandViewListener (CommandViewListener listener) {
		listeners.remove(listener);
	}
	
	
	/**
	 * @author Esaie Muhasa
	 * interface to listening command view events
	 */
	public static interface CommandViewListener {
		
		/**
		 * when user need update command
		 * @param command
		 */
		void onUpdateRequest(Command command);
		
		/**
		 * when user need delete command in database
		 * @param command
		 */
		void onDeletionRequest(Command command);
		
		/**
		 * when user need update client identity
		 * @param client
		 */
		void onClientUpdateRequest(Client client);
		
		/**
		 * when user need sign command item as delivered
		 * @param command
		 */
		void onDeliveryRequest (Command command);
		
		/**
		 * on new payment of command
		 * @param command
		 */
		void onPaymentRequest(Command command);
	}

}
