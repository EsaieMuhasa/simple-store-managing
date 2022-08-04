/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.spiral.simple.store.app.form.ProductForm;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelProducts extends JPanel {
	private static final long serialVersionUID = -4084647016832997773L;
	
	private JLabel title = UIComponentBuilder.createH1("Articles");
	private JTextField fieldSerch = new JTextField();
	private JButton btnAdd = new JButton("Nouveau produit", UIComponentBuilder.loadIcon("new"));
	
	private final ActionListener listenerBtnAdd = event -> createProduct(event);
	{
		btnAdd.addActionListener(listenerBtnAdd);
	}

	public PanelProducts() {
		super(new BorderLayout());
		
		
		add(createBody(), BorderLayout.CENTER);
		add(createHeader(), BorderLayout.NORTH);
		setBorder(UIComponentBuilder.EMPTY_BORDER_5);

	}
	
	@Override
	public String getName() {
		return getClass().getName();
	}
	
	/**
	 * init header components
	 * and listing mouse click to btnAdd
	 */
	private JPanel createHeader() {
		JPanel top = new JPanel(new GridLayout());
		top.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		Box box = Box.createHorizontalBox();
		
		box.add(fieldSerch);
		box.add(Box.createHorizontalStrut(10));
		box.add(btnAdd);
		
		top.add(title);
		top.add(box);
		return top;
	}
	
	private JPanel createBody () {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;
	}
	
	private void createProduct (ActionEvent event) {
		JDialog d = new JDialog(MainWindow.getLastInstance(), "Formulaire d'insersion des articles", true);
		d.setSize(850, 500);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setLocationRelativeTo(d.getOwner());
		
		JPanel c = (JPanel) d.getContentPane();
		d.add(new ProductForm(), BorderLayout.CENTER);
		c.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		d.setVisible(true);
	}

}
