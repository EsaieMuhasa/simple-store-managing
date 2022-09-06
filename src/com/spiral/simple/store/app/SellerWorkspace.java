/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.spiral.simple.store.app.components.CommandView;
import com.spiral.simple.store.app.components.CommandView.CommandViewListener;
import com.spiral.simple.store.app.components.SellerSidebar;
import com.spiral.simple.store.app.components.SellerSidebar.SellerSidebarListener;
import com.spiral.simple.store.app.form.AbstractForm;
import com.spiral.simple.store.app.form.ClientForm;
import com.spiral.simple.store.app.form.FormListener;
import com.spiral.simple.store.beans.Client;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.dao.ClientDao;
import com.spiral.simple.store.dao.CommandDao;
import com.spiral.simple.store.dao.CommandPaymentDao;
import com.spiral.simple.store.dao.DAOException;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
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
		
		private final GridLayout gridLayout = new GridLayout(2, 2, 10, 10);
		private final JPanel content = new JPanel(gridLayout);
		private List<CommandView> commandViews = new ArrayList<>();
		private Date selectedDate;
		private int limit;
		private int offset;
		
		private ClientForm form;
		private JDialog formDialog;
		private WindowAdapter formDialogWindowAdapter;
		private FormListener formListener;
		private final ClientDao clientDao = DAOFactory.getDao(ClientDao.class);
		private final DAOListenerAdapter<Client> clientListenerAdapter = new DAOListenerAdapter<Client>() {
			@Override
			public void onError(int requestId, DAOException exception) {
				if(requestId  != ClientForm.DEFAULT_ON_PERSIST_REQUEST_ID)
					return;
				
				//JOptionPane.showMessageDialog(MainWindow.getLastInstance(), exception.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}

			@Override
			public void onUpdate(Client newState, Client oldState) {				
				JOptionPane.showMessageDialog(MainWindow.getLastInstance(), "Seccess d'enregistrement des modifications\n"+newState.toString(), "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		};
		
		private final DAOListenerAdapter<Command> commandListenerAdapter = new DAOListenerAdapter<Command>() {
			@Override
			public void onCreate(Command... data) {
				for (Command command : data) {
					addCommandAt(command, 0);
					revalidate();
					repaint();
				}
			}

			@Override
			public void onDelete(Command... data) {
				for (Command command : data) {
					for (int i=0, count = commandViews.size(); i < count; i++) {
						CommandView view = commandViews.get(i);
						if(view.getCommand().getId().equals(command.getId())) {
							content.remove(view);
							content.revalidate();
							content.repaint();
							view.dispose();
							return;
						}
					}
				}
			}
		};
		
		public GridCommand() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
			clientDao.addBaseListener(clientListenerAdapter);
			clientDao.addErrorListener(clientListenerAdapter);
			commandDao.addBaseListener(commandListenerAdapter);
			
			content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			
			JScrollPane scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setBorder(null);
			add(scroll, BorderLayout.CENTER);
		}
		
		@Override
		public void doLayout() {
			int width =  getWidth();
			int rows = gridLayout.getRows(), cols = gridLayout.getColumns();
			
			if(width <= 400) {
				rows = commandViews.size();
				cols = 1;
			} else if (width >= 700 && width <= 900) {
				rows = (commandViews.size() / 2) + (commandViews.size() % 2);
				cols = 2;
			} else if (width > 900) {
				rows = (commandViews.size() / 3) + (commandViews.size() % 3 != 0? 1 : 0);
				cols = 3;
			}
			
//				int size = width / cols;
//				Dimension dim = new Dimension(size, size);
//				for (CommandView view : commandViews){
//					view.setPreferredSize(dim);
//					view.setMaximumSize(dim);
//				}
			
			gridLayout.setRows(rows);
			gridLayout.setColumns(cols);
			super.doLayout();
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
			content.removeAll();
			if(commandDao.checkByDate(selectedDate)) {
				Command [] commands = commandDao.findByDate(selectedDate, limit, offset);
				for (Command command : commands)
					addCommand(command);
			}
			content.revalidate();
			content.repaint();
		}
		
		/**
		 * ajout d'une command sur le panel.
		 * creation dela view
		 * @param command
		 */
		private void addCommand (Command command) {
			CommandView view = new CommandView();
			view.setCommand(command);
			view.addCommandViewListener(this);
			content.add(view);
			commandViews.add(view);			
		}
		
		/**
		 * insersion d'une command a l'index
		 * @param command
		 * @param index
		 */
		private void addCommandAt (Command command, int index) {
			CommandView view = new CommandView();
			view.setCommand(command);
			view.addCommandViewListener(this);
			content.add(view, index);
			commandViews.add(index, view);
		}
		
		/**
		 * disposing client form,
		 * and closing/hiding formDialog
		 */
		private void disposeClientForm() {
			formDialog.setVisible(false);
			form.setClient(null);
			formDialog.dispose();
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
				try {
					commandDao.moveToTrash(command.getId());
					JOptionPane.showMessageDialog(MainWindow.getLastInstance(), "Success de suppression de la commande", "Information", JOptionPane.INFORMATION_MESSAGE);
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(MainWindow.getLastInstance(), e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		@Override
		public void onClientUpdateRequest(Client client) {
			if (form == null) {
				form = new ClientForm(clientDao);
				formDialog = new JDialog(MainWindow.getLastInstance(), "Modification de l'identite d'un client", true);
				
				formDialogWindowAdapter = new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						disposeClientForm();
					}
				};
				
				formListener = new FormListener() {
					
					@Override
					public void onValidate(AbstractForm<?> form) {}
					
					@Override
					public void onRejetData(AbstractForm<?> form, String... causes) {}
					
					@Override
					public void onCancel(AbstractForm<?> form) {
						System.out.println("cancel");
						disposeClientForm();
					}
					
					@Override
					public void onAcceptData(AbstractForm<?> form) {
						form.persist();
						disposeClientForm();
					}
				};
				
				form.addFormListener(formListener);
				
				JPanel content = (JPanel) formDialog.getContentPane();
				content.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
				content.add(form, BorderLayout.CENTER);
				formDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				formDialog.addWindowListener(formDialogWindowAdapter);
				formDialog.pack();
				formDialog.setSize(400, formDialog.getHeight());
				formDialog.setResizable(false);
			}
			
			Client copy = clientDao.findById(client.getId());
			form.setClient(copy);
			formDialog.setTitle("Client: "+copy.toString());
			
			formDialog.setLocationRelativeTo(MainWindow.getLastInstance());
			formDialog.setVisible(true);
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
