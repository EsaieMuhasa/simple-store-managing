/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.spiral.simple.store.app.form.AbstractForm;
import com.spiral.simple.store.app.form.FormListener;
import com.spiral.simple.store.app.form.ProductForm;
import com.spiral.simple.store.app.models.ProductTableModel;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.ProductDao;
import com.spiral.simple.store.dao.StockDao;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
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
	private ProductForm productForm;
	
	private final JPopupMenu popup = new JPopupMenu();
	private final JMenuItem options [] = {
			new JMenuItem("Modifier", new ImageIcon(Config.getIcon("edit"))),
			new JMenuItem("Supprimer", new ImageIcon(Config.getIcon("close"))),
			new JMenuItem("Fiche de stock", new ImageIcon(Config.getIcon("normalize"))),
	};
	
	private final StockDao stockDao = DAOFactory.getDao(StockDao.class);
	private final ProductDao productDao = DAOFactory.getDao(ProductDao.class);
	private final FormListener formListener = new  FormListener() {
		@Override
		public void onValidate(AbstractForm<?> form) {}
		
		@Override
		public void onRejetData(AbstractForm<?> form, String... causes) {}
		
		@Override
		public void onCancel(AbstractForm<?> form) {
			productForm.setProduct(null);
			disposeDialogProduct();
		}

		@Override
		public void onAcceptData(AbstractForm<?> form) {
			form.setEnabled(false);
			form.persist();
		}
	};
	
	private final MouseAdapter tableMouseAdapter = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			int index = table.getSelectedRow();
			if(!e.isPopupTrigger() || index == -1)
				return;
			
			Product product = tableModel.getRow(index);
			options[1].setEnabled(!stockDao.checkByProduct(product.getId()));
			popup.show(table, e.getX(), e.getY());
		}
	};
	
	private final WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			productForm.cancel();
		}
	};
	
	private final DAOListenerAdapter<Product> productListenerAdapter = new DAOListenerAdapter<Product>() {
		@Override
		public void onCreate(Product... data) {
			productForm.setEnabled(true);
			disposeDialogProduct();
		}

		@Override
		public void onUpdate(Product newState, Product oldState) {
			productForm.setEnabled(true);
			disposeDialogProduct();
		}
	};
	
	private final ActionListener optionsItemListener = event -> onItemOptionAction(event);

	public PanelProducts() {
		super(new BorderLayout());
		
		tableModel = new ProductTableModel(productDao);
		table = new CustomTable(tableModel);
		productDao.addBaseListener(productListenerAdapter);
		
		add(createBody(), BorderLayout.CENTER);
		add(createHeader(), BorderLayout.NORTH);
		setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		for (JMenuItem item : options){
			item.addActionListener(optionsItemListener);
			popup.add(item);
			if(item == options[1])
				popup.addSeparator();
		}
		
		table.addMouseListener(tableMouseAdapter);
		tableModel.reload();
		int [] cols = {0, 1};
		
		for (int col : cols) {
			table.getColumnModel().getColumn(col).setWidth(50);
			table.getColumnModel().getColumn(col).setMaxWidth(50);
			table.getColumnModel().getColumn(col).setResizable(false);
		}
		
	}
	
	/**
	 * action de click sur un element du popup menu
	 * @param event
	 */
	private void onItemOptionAction(ActionEvent event) {
		Product product = tableModel.getRow(table.getSelectedRow());
		if(event.getSource() == options[0]) {
			updateProduct(product);
		} else if (event.getSource() == options[1]) {
			String message = "Voulez-vous vraiment supprimer \n\""+product.toString()+"\"";
			message += " de la liste des produit?";
			int status = JOptionPane.showConfirmDialog(MainWindow.getLastInstance(), message, "Suppression d'un produit", JOptionPane.YES_NO_CANCEL_OPTION);
			if(status == JOptionPane.YES_OPTION)
				productDao.delete(ProductForm.DEFAULT_ON_PERSIST_REQUEST_ID, product.getId());
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
		createDialogProduct();
		productForm.setProduct(new Product());;
		dialogFormProduct.setLocationRelativeTo(dialogFormProduct.getOwner());
		dialogFormProduct.setVisible(true);
	}
	
	/**
	 * utilitaire de demande de mise en jour d'un produit.
	 * @param product
	 */
	public void updateProduct (Product product) {
		createDialogProduct();
		
		Product p = productDao.findById(product.getId());
		productForm.setProduct(p);
		dialogFormProduct.setLocationRelativeTo(dialogFormProduct.getOwner());
		dialogFormProduct.setVisible(true);
	}
	
	/**
	 * utilitaire de cration de la boite de dialogue parent du formualire 
	 * d'insertion/modification d'un produit
	 */
	private void createDialogProduct () {
		if(dialogFormProduct == null) {
			dialogFormProduct = new JDialog(MainWindow.getLastInstance(), "Formulaire d'insersion des articles", true);
			productForm = new ProductForm();
			productForm.addFormListener(formListener);
			dialogFormProduct.setSize(850, 500);
			dialogFormProduct.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialogFormProduct.addWindowListener(windowAdapter);
			dialogFormProduct.setResizable(false);
			dialogFormProduct.add(productForm, BorderLayout.CENTER);
			JPanel c = (JPanel) dialogFormProduct.getContentPane();
			c.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		}
	}
	
	/**
	 * liberation des resources utiliser par la boite de 
	 * dialogue parent du formulaire d'insersion/edition d'un produit
	 */
	private void disposeDialogProduct() {
		dialogFormProduct.setVisible(false);
		dialogFormProduct.dispose();
	}

}
