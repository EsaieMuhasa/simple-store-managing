/**
 * 
 */
package com.spiral.simple.store.app.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.spiral.simple.store.app.MainWindow;
import com.spiral.simple.store.app.form.AbstractForm;
import com.spiral.simple.store.app.form.FormListener;
import com.spiral.simple.store.app.form.SpendsForm;
import com.spiral.simple.store.app.models.SpendsTableModel;
import com.spiral.simple.store.beans.Spends;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.SpendsDao;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie Muhasa
 *
 */
public class PanelSpends extends JPanel {
	private static final long serialVersionUID = -1551276937438934800L;
	private final JButton btnAdd = new JButton("Nouvelle dépense", new ImageIcon(Config.getIcon("new")));
	private final SpendsTableModel tableModel = new SpendsTableModel();
	private final CustomTable table = new CustomTable(tableModel);
	
	private ActionListener actionListener = event -> showCreateSpends();
	
	private final SpendsDao spendsDao = DAOFactory.getDao(SpendsDao.class);
	private final DAOListenerAdapter<Spends> spendsAdapter = new DAOListenerAdapter<Spends>() {

		@Override
		public void onCreate(Spends... data) {
			disposeDialog();
			JOptionPane.showMessageDialog(MainWindow.getLastInstance(), "Succes d'enregistrement du depense",
					"Alert", JOptionPane.INFORMATION_MESSAGE);
		}

		@Override
		public void onUpdate(Spends newState, Spends oldState) {
			disposeDialog();
			JOptionPane.showMessageDialog(MainWindow.getLastInstance(), "Succes d'enregistrement des\nmodification de la depense",
					"Alert", JOptionPane.INFORMATION_MESSAGE);
		}
	};
	
	private JDialog dialog;
	private SpendsForm form;
	private FormListener formListener;
	private WindowAdapter windowAdapter;

	/**
	 * 
	 */
	public PanelSpends() {
		super(new BorderLayout());
		
		init();
		btnAdd.addActionListener(actionListener);
	}
	
	/**
	 * initialisation des composants graphiques
	 */
	private void init() {
		final JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		final JPanel center = new JPanel(new BorderLayout());
		
		JScrollPane scroll = new JScrollPane(table, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		center.add(scroll, BorderLayout.CENTER);

		top.add(btnAdd);
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		
		spendsDao.addBaseListener(spendsAdapter);
		tableModel.reload();
	}
	
	/**
	 * desactivation dela boite de dialogue
	 */
	private void disposeDialog() {
		dialog.setVisible(false);
		form.setSpends(null);
		dialog.dispose();
	}
	
	/**
	 * contruction de la boite de dialogue de d'insertion/mis en jours d'une
	 * depense
	 */
	private void createDialog () {
		if(dialog != null)
			return;
		
		form= new SpendsForm();
		dialog = new JDialog(MainWindow.getLastInstance(), "", true);
		formListener = new FormListener() {
			
			@Override
			public void onValidate(AbstractForm<?> form) {}
			
			@Override
			public void onRejetData(AbstractForm<?> form, String... causes) {}
			
			@Override
			public void onCancel(AbstractForm<?> form) {
				disposeDialog();
			}
			
			@Override
			public void onAcceptData(AbstractForm<?> form) {
				form.persist();
			}
		};
		
		windowAdapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int response = JOptionPane.showConfirmDialog(MainWindow.getLastInstance(),
						"Voulez-vous vraiment annuler cette opération", "Annulation de l'opération", JOptionPane.YES_NO_OPTION);
				if(response == JOptionPane.OK_OPTION)
					disposeDialog();
			}
		};
		
		JPanel content = (JPanel) dialog.getContentPane();
		content.add(form, BorderLayout.CENTER);
		content.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		form.addFormListener(formListener);
		form.doReload();
		
		dialog.pack();
		dialog.setSize(620, 520);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(windowAdapter);
	}
	
	/**
	 * affiche de la boite de dialogue d'enregistrement d'une nouvelle depense
	 */
	private void showCreateSpends () {
		createDialog();
		dialog.setLocationRelativeTo(MainWindow.getLastInstance());
		dialog.setTitle("Enregistrement d'une dépense");
		dialog.setVisible(true);
	}

}
