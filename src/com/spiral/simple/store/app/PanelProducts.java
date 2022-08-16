/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.spiral.simple.store.app.form.ProductForm;
import com.spiral.simple.store.app.models.ProductTableModel;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.ProductDao;
import com.spiral.simple.store.swing.CustomTable;
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
	
	private ProductTableModel tableModel;
	private CustomTable table;
	
	private JDialog dialogFormProduct;

	public PanelProducts() {
		super(new BorderLayout());
		
		tableModel = new ProductTableModel(DAOFactory.getDao(ProductDao.class));
		table = new CustomTable(tableModel);
		
		add(createBody(), BorderLayout.CENTER);
		add(createHeader(), BorderLayout.NORTH);
		setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		tableModel.reload();
		
		int [] cols = {0, 1};
		
		for (int col : cols) {
			table.getColumnModel().getColumn(col).setWidth(50);
			table.getColumnModel().getColumn(col).setMaxWidth(50);
			table.getColumnModel().getColumn(col).setResizable(false);
		}
		
	}
	
	@Override
	public String getName() {
		return "Articles";
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
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(null);
		panel.add(scroll, BorderLayout.CENTER);
		return panel;
	}
	
	private void createProduct (ActionEvent event) {
		if(dialogFormProduct == null) {
			dialogFormProduct = new JDialog(MainWindow.getLastInstance(), "Formulaire d'insersion des articles", true);
			dialogFormProduct.setSize(850, 500);
			dialogFormProduct.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialogFormProduct.add(new ProductForm(), BorderLayout.CENTER);
			JPanel c = (JPanel) dialogFormProduct.getContentPane();
			c.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		}
		dialogFormProduct.setLocationRelativeTo(dialogFormProduct.getOwner());
		dialogFormProduct.setVisible(true);
	}

}
