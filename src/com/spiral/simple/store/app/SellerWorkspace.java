/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Date;

import javax.swing.JPanel;

import com.spiral.simple.store.app.components.SellerSidebar;
import com.spiral.simple.store.app.components.SellerSidebar.SellerSidebarListener;
import com.spiral.simple.store.beans.Command;

/**
 * @author Esaie Muhasa
 *
 */
public class SellerWorkspace extends JPanel implements SellerSidebarListener {
	private static final long serialVersionUID = -2474958501183133361L;
	
	private final SellerSidebar  sidebar = new SellerSidebar();
	private final JPanel center = new JPanel(new BorderLayout());
	
	private CommandDialog commandDialog;

	/**
	 * 
	 */
	public SellerWorkspace() {
		super(new BorderLayout());
		
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
		commandDialog.setSize(commandDialog.getWidth() + 100, 440);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewCommand() {
		showNewCommandDialog();
	}


}
