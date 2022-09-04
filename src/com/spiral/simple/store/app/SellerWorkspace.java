/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.spiral.simple.store.app.components.CommandView;
import com.spiral.simple.store.app.components.CommandView.CommandViewListener;
import com.spiral.simple.store.app.components.SellerSidebar;
import com.spiral.simple.store.app.components.SellerSidebar.SellerSidebarListener;
import com.spiral.simple.store.beans.Client;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.dao.CommandDao;
import com.spiral.simple.store.dao.CommandItemDao;
import com.spiral.simple.store.dao.CommandPaymentDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie Muhasa
 *
 */
public class SellerWorkspace extends JPanel implements SellerSidebarListener {
	private static final long serialVersionUID = -2474958501183133361L;
	
	private final SellerSidebar  sidebar = new SellerSidebar();
	private final JPanel center = new JPanel(new BorderLayout());
	private final JLabel labelTitle = UIComponentBuilder.createH1("Le "+Command.DATE_FORMAT.format(new Date()));
	private final JLabel labelCount = UIComponentBuilder.createH1("0 commande");
	
	private final CommandDao commandDao = DAOFactory.getDao(CommandDao.class);
	private final CommandItemDao commandItemDao = DAOFactory.getDao(CommandItemDao.class);
	private final CommandPaymentDao commandPaymentDao = DAOFactory.getDao(CommandPaymentDao.class);
	
	private final GridCommand commands = new  GridCommand();
	private CommandDialog commandDialog;

	/**
	 * 
	 */
	public SellerWorkspace() {
		super(new BorderLayout());
		
		final JPanel header = new JPanel(new BorderLayout());
		header.setBackground(CustomTable.GRID_COLOR);
		header.add(labelTitle, BorderLayout.CENTER);
		header.add(labelCount,BorderLayout.EAST);
		header.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		center.add(header, BorderLayout.NORTH);
		center.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		center.add(commands, BorderLayout.CENTER);
		
		add(sidebar, BorderLayout.EAST);
		add(center, BorderLayout.CENTER);
		
		sidebar.addSidebarListener(this);
	}
	
	@Override
	public String getName() {
		return "Espace de vente";
	}
	
	/**
	 * utility method to build JDialog frame,
	 * UI manager for sell form
	 */
	private synchronized void buildCommandDialog() {
		sidebar.setEnabledAddCommand(false);
		commandDialog = new CommandDialog();
		commandDialog.load();
		commandDialog.pack();
		commandDialog.setSize(commandDialog.getWidth() + 100, 475);
//		commandDialog.setResizable(false);
		sidebar.setEnabledAddCommand(true);
	}
	
	/**
	 * request to show command dialog
	 * @param command
	 */
	private void showCommandDialog(Command command) {
		Command com = null;
		if(command.getId() != null) {
			com = commandDao.findById(command.getId());
			com.addItem(commandItemDao.findByCommand(command.getId()));
			if(commandPaymentDao.checkByCommand(command.getId()))
				com.addPayments(commandPaymentDao.findByCommand(command.getId()));
		} else 
			com = command;
		
		if (commandDialog == null)
			buildCommandDialog();
		
		commandDialog.setLocationRelativeTo(MainWindow.getLastInstance());
		commandDialog.setCommand(com);
		commandDialog.setVisible(true);
	}

	@Override
	public void onDateChange(Date date) {
		labelTitle.setText("Le "+Command.DATE_FORMAT.format(date));
		int nombre = commandDao.countByDate(date);
		String txt = nombre+" Commande"+(nombre>1? "s": "");
		labelCount.setText(txt);
		commands.loadByDate(date);
		
	}

	@Override
	public void onNewCommand() {
		showCommandDialog(new Command());
	}
	
	
	/**
	 * panel to show command by selected date in side bar
	 * @author Esaie Muhasa
	 */
	protected class GridCommand extends JPanel implements CommandViewListener{
		private static final long serialVersionUID = -5526962605839654031L;
		
		private List<CommandView> commandViews = new ArrayList<>();
		private Date selectedDate;
		private int limit;
		private int offset;
		
		public GridCommand() {
			super(new GridLayout(2, 2, 10, 10));
			setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		}
		
		/**
		 * loading all command by date 
		 * @param date
		 */
		public void loadByDate (Date date) {
			this.selectedDate = date;
			limit = 20;
			offset = 0;
			reload();
		}
		
		/**
		 * reload data
		 */
		private void reload() {
			for (CommandView view : commandViews) 
				view.dispose();
			
			commandViews.clear();
			removeAll();
			if(commandDao.checkByDate(selectedDate)) {
				Command [] commands = commandDao.findByDate(selectedDate, limit, offset);
				for (Command command : commands) {
					CommandView view = new CommandView();
					view.setCommand(command);
					view.addCommandViewListener(this);
					add(view);
					commandViews.add(view);
				}
			}
			revalidate();
			repaint();
		}
		
		//==============================================
		//override by command view listener interface
		//===============================================

		@Override
		public void onUpdateRequest(Command command) {
			showCommandDialog(command);
		}

		@Override
		public void onDeletionRequest(Command command) {
			String message = "Vous êtes sur le point de supprimer la commande N°"+command.getNumber();
			message += "\neffectuer en date du "+Command.DATE_FORMAT.format(command.getDate())+" par \""+command.getClient();
			message += "\"\nN.B: Cette opération est ireversible";
			
			int status = JOptionPane.showConfirmDialog(MainWindow.getLastInstance(), message, "Suppresion d'une command", JOptionPane.YES_NO_OPTION);
			if(status == JOptionPane.YES_OPTION) {
				
			}
		}

		@Override
		public void onClientUpdateRequest(Client client) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDeliveryRequest(Command command) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPaymentRequest(Command command) {
			// TODO Auto-generated method stub
			
		}
		
	}


}
