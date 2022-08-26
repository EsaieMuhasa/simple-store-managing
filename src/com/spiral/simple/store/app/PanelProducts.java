/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
	
	/**
	 * utilitaire de demande d'ouverture du frame qui contiens le formulaire d'insersion 
	 * d'un nouveau produit
	 */
	public void addProduct () {
		createProduct();
	}
	
	private void createProduct () {
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
