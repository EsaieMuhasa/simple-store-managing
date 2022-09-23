/**
 * 
 */
package com.spiral.simple.store.app.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.spiral.simple.store.app.MainWindow;
import com.spiral.simple.store.app.form.SpendsForm;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie Muhasa
 *
 */
public class PanelSpends extends JPanel {
	private static final long serialVersionUID = -1551276937438934800L;
	private final JButton btnAdd = new JButton("Nouvelle dépense", new ImageIcon(Config.getIcon("new")));
	
	private ActionListener actionListener = event -> showCreateSpends();
	
	private JDialog dialog;
	private SpendsForm form;

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

		top.add(btnAdd);
		add(top, BorderLayout.NORTH);
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
		JPanel content = (JPanel) dialog.getContentPane();
		content.add(form, BorderLayout.CENTER);
		content.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		dialog.pack();
		dialog.setSize(dialog.getWidth() * 2, dialog.getHeight());
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
