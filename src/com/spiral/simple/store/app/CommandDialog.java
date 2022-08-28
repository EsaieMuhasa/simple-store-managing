/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.MeasureUnit;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.swing.CaptionnablePanel;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.swing.SimpleComboBox;
import com.spiral.simple.store.swing.SimpleDateField;
import com.spiral.simple.store.swing.SimpleTextField;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie Muhasa
 * 
 * Frame facilitant l'execution d'une commande.
 */
public class CommandDialog extends JDialog {
	private static final long serialVersionUID = 7993991908409205256L;
	
	private final DefaultListModel<Product> listModelProduct=  new DefaultListModel<>();
	private final JList<Product> listProdut = new JList<>(listModelProduct);
	private final JTextField fieldSearchProdut = new  JTextField();
	
	private final JButton btnValidate = new JButton("Valider la commande", new ImageIcon(Config.getIcon("success")));
	private final JButton btnCancel = new JButton("Annuler la commande", new ImageIcon(Config.getIcon("close")));
	private final JButton btnPrint = new JButton("Imprimer la facture", new ImageIcon(Config.getIcon("print")));
	
	private final PanelFieldsCommand fieldsCommand = new PanelFieldsCommand();
	private final CustomTable itemTable = new CustomTable();
	
	private final WindowAdapter windowAdapter = new WindowAdapter() {
		
		@Override
		public void windowClosing(WindowEvent e) {
			doCancel();
		}
	};
	
	/**
	 * default construct
	 */
	public CommandDialog() {
		super(MainWindow.getLastInstance(), "Réalisation d'une commande", true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(windowAdapter);
		btnCancel.addActionListener(event -> doCancel());
		btnPrint.addActionListener(event ->  doPrintInvoice());
		btnValidate.addActionListener(event -> doValidate());
		
		initViews();
	}
	
	/**
	 * initialization of UI components
	 */
	private void initViews() {
		JPanel 
			contentPanel = (JPanel) getContentPane(),
			leftPanel = new JPanel(new BorderLayout(5, 5)),
			centerPanel = new JPanel(new BorderLayout(5, 5)),
			center = new JPanel(new BorderLayout()),
			bottomPanel = new JPanel();
		
		bottomPanel.add(btnCancel);
		bottomPanel.add(btnValidate);
		bottomPanel.setBackground(itemTable.getGridColor());
		
		JScrollPane 
			scrollList = new JScrollPane(listProdut),
			scrollTable = new JScrollPane(itemTable);
		
		centerPanel.add(fieldsCommand, BorderLayout.NORTH);
		centerPanel.add(scrollTable, BorderLayout.CENTER);
		centerPanel.add(btnPrint, BorderLayout.SOUTH);
		
		scrollList.setBorder(null);
		scrollTable.setBorder(null);
		
		leftPanel.setBorder(BorderFactory.createLineBorder(itemTable.getGridColor()));
		leftPanel.add(fieldSearchProdut, BorderLayout.NORTH);
		leftPanel.add(scrollList, BorderLayout.CENTER);
		
		JPanel 
			leftPadding = new JPanel(new BorderLayout()),
			rigthPadding = new JPanel(new BorderLayout());
		
		leftPadding.add(leftPanel, BorderLayout.CENTER);
		leftPadding.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		rigthPadding.add(centerPanel, BorderLayout.CENTER);
		rigthPadding.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPadding, rigthPadding);

		center.add(split, BorderLayout.CENTER);
		center.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		contentPanel.add(center, BorderLayout.CENTER);
		contentPanel.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * canceling operation
	 */
	private void doCancel() {
		int status = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment annuler la commande?", "Annullation de la commande", JOptionPane.YES_NO_OPTION);
		if(status != JOptionPane.OK_OPTION)
			return;
		
		
		setVisible(false);
		dispose();
	}
	
	/**
	 * utility method calling tools to print invoice
	 */
	private void doPrintInvoice () {
		
	}
	
	/**
	 * method to validate command
	 * -first we must validate command items
	 * -second we validate command payment
	 */
	private void doValidate () {
		
	}

	public void load() {
		// TODO Auto-generated method stub
		
	}
	
	
	/**
	 * @author Esaie Muhasa
	 * container of all text field in command form
	 */
	private static class PanelFieldsCommand extends JPanel {
		private static final long serialVersionUID = -6841650272514637580L;
		
		private final DefaultComboBoxModel<Product> productModel = new DefaultComboBoxModel<>(); 
		private final DefaultComboBoxModel<MeasureUnit> measureUnitModel = new DefaultComboBoxModel<>();
		private final DefaultComboBoxModel<Currency> currencyModel = new DefaultComboBoxModel<>();
		
		private final JButton btnValidate = new JButton("Valider", new ImageIcon(Config.getIcon("success")));
		private final SimpleDateField fieldCommandDate = new SimpleDateField("");
		
		private final SimpleTextField fieldClientName = new SimpleTextField(" Noms");
		private final SimpleTextField fieldClientTelephone = new SimpleTextField(" Numéro de téléphone");
		
		private final SimpleComboBox<Product> fieldItemProduct = new SimpleComboBox<>(" Produit", productModel);
		private final SimpleComboBox<MeasureUnit> fieldQuantityUnit = new SimpleComboBox<>("Unité de mesure", measureUnitModel);
		private final SimpleComboBox<Currency> fieldCurrency = new SimpleComboBox<>("Devise", currencyModel);
		private final SimpleTextField fieldItemQuantity =  new SimpleTextField(" Quantité");
		private final SimpleTextField fieldItemUnitPrice = new SimpleTextField(" Prix unitaire");
		
		public PanelFieldsCommand() {
			super(new BorderLayout());
			init();
		}
		
		/**
		 * building UI components
		 */
		private void init() {
			final Box 
				box = Box.createVerticalBox(),
				client = Box.createVerticalBox(),
				item = Box.createVerticalBox();
			
			final JPanel 
				bottom = new JPanel(),
				rowQuantity = new JPanel(new GridLayout(1, 2, 5, 5)),
				rowUnitPrice = new JPanel(new GridLayout(1, 2, 5, 5));
			
			client.add(fieldClientTelephone);
			client.add(fieldClientName);
			
			rowQuantity.add(fieldItemQuantity);
			rowQuantity.add(fieldQuantityUnit);
			
			rowUnitPrice.add(fieldItemUnitPrice);
			rowUnitPrice.add(fieldCurrency);
			
			item.add(fieldItemProduct);
			item.add(rowQuantity);
			item.add(rowUnitPrice);
			
			bottom.add(btnValidate);
			
			box.add(new CaptionnablePanel("Date du jour", fieldCommandDate));
			box.add(Box.createVerticalStrut(5));
			box.add(new CaptionnablePanel("Client", client));
			box.add(Box.createVerticalStrut(5));
			box.add(new CaptionnablePanel("Elément de la commande", item));
			box.add(Box.createVerticalStrut(5));
			box.add(bottom);
			
			box.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			add(box, BorderLayout.CENTER);
			setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
			
		}
		
	}


}
