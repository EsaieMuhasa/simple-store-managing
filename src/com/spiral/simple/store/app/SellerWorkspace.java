/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.spiral.simple.store.app.components.CommandView;
import com.spiral.simple.store.app.components.SellerSidebar;
import com.spiral.simple.store.app.components.SellerSidebar.SellerSidebarListener;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.dao.CommandDao;
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
	 */
	private void showNewCommandDialog() {
		if (commandDialog != null){
			commandDialog.setLocationRelativeTo(MainWindow.getLastInstance());
			commandDialog.setCommand(new Command());
			commandDialog.setVisible(true);
			return;
		}
		
		EventQueue.invokeLater(() -> {
			buildCommandDialog();
			commandDialog.setLocationRelativeTo(MainWindow.getLastInstance());
			commandDialog.setCommand(new Command());
			commandDialog.setVisible(true);
		});

	}

	@Override
	public void onDateChange(Date date) {
		labelTitle.setText("Le "+Command.DATE_FORMAT.format(date));
		commands.loadByDate(date);
	}

	@Override
	public void onNewCommand() {
		showNewCommandDialog();
	}
	
	
	/**
	 * panel to show command by selected date in side bar
	 * @author Esaie Muhasa
	 */
	protected static class GridCommand extends JPanel {
		private static final long serialVersionUID = -5526962605839654031L;
		
		private final CommandDao commandDao = DAOFactory.getDao(CommandDao.class);
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
			removeAll();
			if(commandDao.checkByDate(selectedDate)) {
				Command [] commands = commandDao.findByDate(selectedDate, limit, offset);
				for (Command command : commands) {
					CommandView view = new CommandView();
					view.setCommand(command);
					add(view);
				}
			}
			revalidate();
			repaint();
		}
		
	}


}
