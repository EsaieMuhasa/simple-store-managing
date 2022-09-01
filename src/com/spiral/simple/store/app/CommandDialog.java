/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretListener;

import com.spiral.simple.store.app.form.AbstractForm;
import com.spiral.simple.store.app.form.CommandPaymentForm;
import com.spiral.simple.store.app.form.FormListener;
import com.spiral.simple.store.app.models.InvoiceTableModel;
import com.spiral.simple.store.beans.AffectedStock;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandItem;
import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.DBEntity;
import com.spiral.simple.store.beans.MeasureUnit;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.dao.CommandDao;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.ExchangeRateDao;
import com.spiral.simple.store.dao.MeasureUnitDao;
import com.spiral.simple.store.dao.ProductDao;
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
	
	private final DefaultComboBoxModel<Product> productModel = new DefaultComboBoxModel<>(); 
	private final DefaultComboBoxModel<MeasureUnit> measureUnitModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<Currency> currencyModel = new DefaultComboBoxModel<>();
	private final InvoiceTableModel tableModel = new InvoiceTableModel();
	
	private final PanelFieldsCommand fieldsCommand = new PanelFieldsCommand();
	private final CustomTable itemTable = new CustomTable(tableModel);
	
	private final MeasureUnitDao measureUnitDao = DAOFactory.getDao(MeasureUnitDao.class);
	private final ProductDao productDao = DAOFactory.getDao(ProductDao.class);
	private final CommandDao commandDao = DAOFactory.getDao(CommandDao.class);
	private final CurrencyDao currencyDao = DAOFactory.getDao(CurrencyDao.class);
	private final ExchangeRateDao exchangeRateDao = DAOFactory.getDao(ExchangeRateDao.class);
	
	private final WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			doCancel();
		}
	};
	
	private final MouseAdapter listProductMouseAdapter = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			
			if(e.getClickCount() == 2) {
				Product product = listModelProduct.getElementAt(listProdut.getSelectedIndex());
				fieldsCommand.fieldItemProduct.getField().removeItemListener(fieldsCommand.productItemListener);
				fieldsCommand.fieldCurrency.getField().removeItemListener(fieldsCommand.currencyItemListener);
				CommandItem item = null;
				if(productModel.getIndexOf(product) == -1){
					productModel.addElement(product);
					item = tableModel.createByProduct(product);
					if (item.getCurrency() == null)
						item.setCurrency(currencyModel.getElementAt(fieldsCommand.fieldCurrency.getField().getSelectedIndex()));
					else {
						/* pour le currency dans le command item pointe vers la meme reference
						 * que ceux du model du combo box des occurences
						 */
						for (int j = 0; j < currencyModel.getSize(); j++) {
							if(item.getCurrency().equals(currencyModel.getElementAt(j))) {
								item.setCurrency(currencyModel.getElementAt(j));
								break;
							}
						}
					}
				} else 
					item = tableModel.findByProduct(product);
				
				if(item.countStock() != 0) {
					AffectedStock a = item.getStockAt(0);
					for (int j = 0; j < measureUnitModel.getSize(); j++) {
						if (measureUnitModel.getElementAt(j).getId().equals(a.getStock().getMeasureUnit().getId())) {
							fieldsCommand.fieldQuantityUnit.getField().setSelectedIndex(j);
							break;
						}
					}
				}
				
				fieldsCommand.fieldQuantityUnit.setEnabled(false);
				fieldsCommand.fieldItemProduct.getField().setSelectedItem(product);
				fieldsCommand.fieldItemQuantity.getField().setText(item.getQuantity()+"");
				fieldsCommand.fieldItemUnitPrice.getField().setText(item.getUnitPrice()+"");
				fieldsCommand.fieldCurrency.getField().setSelectedItem(item.getCurrency());
				fieldsCommand.fieldItemProduct.getField().addItemListener(fieldsCommand.productItemListener);
				fieldsCommand.fieldCurrency.getField().addItemListener(fieldsCommand.currencyItemListener);
			}
		}
	};
	
	private final DAOListenerAdapter<Product> productListenerAdapter = new  DAOListenerAdapter<Product>() {};
	private final DAOListenerAdapter<Currency> currecyListenerAdapter = new DAOListenerAdapter<Currency>() {};
	private final DAOListenerAdapter<MeasureUnit> measureUnitListenerAdapter = new DAOListenerAdapter<MeasureUnit>();
	private final DAOListenerAdapter<Command> commandListenerAdapter = new DAOListenerAdapter<Command>() {};
	
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
		
		//listening DAOs events
		measureUnitDao.addBaseListener(measureUnitListenerAdapter);
		productDao.addBaseListener(productListenerAdapter);
		commandDao.addBaseListener(commandListenerAdapter);
		currencyDao.addBaseListener(currecyListenerAdapter);
		//==
		
		initViews();
		
		listProdut.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listProdut.addMouseListener(listProductMouseAdapter);
	}
	
	/**
	 * mutation of command, in command models
	 * @param command
	 */
	public void setCommand (Command command) {
		tableModel.setCommand(command);
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
		itemTable.setShowVerticalLines(false);
		
		JScrollPane 
			scrollList = new JScrollPane(listProdut),
			scrollTable = new JScrollPane(itemTable);
		
		final JPanel 
			panelTable = new JPanel(new BorderLayout()),
			panelFields = new JPanel(new BorderLayout()),
			tableBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		centerPanel.add(panelFields, BorderLayout.WEST);
		centerPanel.add(panelTable, BorderLayout.CENTER);
		
		panelFields.add(fieldsCommand, BorderLayout.NORTH);
		panelFields.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
		panelTable.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
		panelTable.add(scrollTable, BorderLayout.CENTER);
		panelTable.add(tableBottom, BorderLayout.SOUTH);
		tableBottom.add(btnPrint);
		
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
		if(tableModel.getRowCount() != 0) {
			int status = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment annuler la commande?", "Annullation de la commande", JOptionPane.YES_NO_OPTION);
			if(status != JOptionPane.OK_OPTION)
				return;
		}
		fieldsCommand.setEnabled(false);
		tableModel.setCommand(null);
		setVisible(false);
		fieldsCommand.dispose();
		fieldsCommand.setEnabled(true);
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

	/**
	 * loading data
	 */
	public void load() {
		if(measureUnitDao.countAll() != 0) {
			measureUnitModel.removeAllElements();
			MeasureUnit [] units = measureUnitDao.findAll();
			for (MeasureUnit unit : units)
				measureUnitModel.addElement(unit);
		}
		
		if(productDao.countAll() != 0) {
			listModelProduct.removeAllElements();
			Product [] products = productDao.findAll();
			for (Product product : products)
				listModelProduct.addElement(product);
		}
		
		if(currencyDao.countAll() != 0) {
			currencyModel.removeAllElements();
			Currency [] currencies = currencyDao.findAll();
			for (Currency currency : currencies) 
				currencyModel.addElement(currency);
		}
	}
	
	
	/**
	 * @author Esaie Muhasa
	 * container of all text field in command form
	 */
	private class PanelFieldsCommand extends JPanel implements FormListener{
		private static final long serialVersionUID = -6841650272514637580L;

		private final SimpleDateField fieldCommandDate = new SimpleDateField("");
		
		private final SimpleTextField fieldClientName = new SimpleTextField(" Noms");
		private final SimpleTextField fieldClientTelephone = new SimpleTextField(" Numéro de téléphone");
		
		private final SimpleComboBox<Product> fieldItemProduct = new SimpleComboBox<>(" Produit", productModel);
		private final SimpleComboBox<MeasureUnit> fieldQuantityUnit = new SimpleComboBox<>("Unité de mesure", measureUnitModel);
		private final SimpleComboBox<Currency> fieldCurrency = new SimpleComboBox<>("Devise", currencyModel);
		private final SimpleTextField fieldItemQuantity =  new SimpleTextField(" Quantité");
		private final SimpleTextField fieldItemUnitPrice = new SimpleTextField(" Prix unitaire");
		
		private final DefaultListModel<CommandPayment> listPaymentModel = new DefaultListModel<>();
		private final JTabbedPane tabbedPane = new JTabbedPane();
		private final CommandPaymentForm paymentForm = new CommandPaymentForm();
		private final JList<CommandPayment> listPayment = new JList<>(listPaymentModel);
		
		private final ItemListener productItemListener =  event -> onProductSelectionChange(event);
		private final ItemListener currencyItemListener = event -> onCurrencySelectionChange(event);
		
		//payment panel
		private final CardLayout paymentLayout = new CardLayout();
		private final JPanel paymentPanel = new JPanel(paymentLayout);
		private final JButton btnNewPayment = new JButton("Nouveau payement", new ImageIcon(Config.getIcon("new")));
		//==
		
		private final CaretListener caretQuantityListener = event -> {
			if(productModel.getSize() == 0)
				return;
			
			Product product = productModel.getElementAt(fieldItemProduct.getField().getSelectedIndex());
			if(!tableModel.checkByProduct(product))
				return;
			
			CommandItem item = tableModel.findByProduct(product);
			if(!fieldItemQuantity.getField().getText().trim().isEmpty()) {
				try {
					double quantity = Double.parseDouble(fieldItemQuantity.getField().getText());					
					item.setQuantity(quantity);
					tableModel.repaintRow(item);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(CommandDialog.this, "La quantité doit être une valeur numérique valide.", "Erreur: valeur numérique invalide", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		private final CaretListener caretUnitPriceListener = event -> {
			if(productModel.getSize() == 0)
				return;
			
			Product product = productModel.getElementAt(fieldItemProduct.getField().getSelectedIndex());
			if(!tableModel.checkByProduct(product))
				return;
			
			CommandItem item = tableModel.findByProduct(product);
			if(!fieldItemUnitPrice.getField().getText().trim().isEmpty()) {
				try {
					double unitPrice = Double.parseDouble(fieldItemUnitPrice.getField().getText());					
					item.setUnitPrice(unitPrice);
					tableModel.repaintRow(item);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(CommandDialog.this, "La prix unitaire doit être une valeur numérique valide.", "Erreur: valeur numérique invalide", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		public PanelFieldsCommand() {
			super(new BorderLayout());
			init();
			initPaymentPanel();
			
			fieldItemProduct.getField().addItemListener(productItemListener);
			fieldItemQuantity.getField().addCaretListener(caretQuantityListener);
			fieldItemUnitPrice.getField().addCaretListener(caretUnitPriceListener);
			paymentForm.addFormListener(this);
			paymentForm.doReload();
		}
		
		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			fieldClientName.setEnabled(enabled);
			fieldClientTelephone.setEnabled(enabled);
			fieldItemProduct.setEnabled(enabled);
			fieldItemUnitPrice.setEnabled(enabled);
			fieldItemQuantity.setEnabled(enabled);
		}

		/**
		 * clear all text fields and disposable combo box
		 */
		private void dispose() {
			fieldClientName.getField().setText("");;
			fieldClientTelephone.getField().setText("");
			fieldItemUnitPrice.getField().setText("");
			fieldItemQuantity.getField().setText("");
			fieldsCommand.fieldItemProduct.getField().removeItemListener(fieldsCommand.productItemListener);
			productModel.removeAllElements();
			fieldsCommand.fieldItemProduct.getField().addItemListener(fieldsCommand.productItemListener);
		}
		
		/**
		 * validate product after update command item
		 * @param product
		 */
		private void validateItem (Product product) {

			try {
				if(product == null)
					return;
				CommandItem item = tableModel.findByProduct(product);
				if(!fieldItemQuantity.getField().getText().trim().isEmpty()) {
					double quantity = Double.parseDouble(fieldItemQuantity.getField().getText());					
					item.setQuantity(quantity);
				}
				double unitPrice = Double.parseDouble(fieldItemUnitPrice.getField().getText());
				Currency currency = currencyModel.getElementAt(fieldCurrency.getField().getSelectedIndex());
				
				item.setUnitPrice(unitPrice);
				item.setCurrency(currency);
				
				tableModel.repaintRow(item);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(CommandDialog.this, e.getMessage(), "Erreur: valeur numérique invalide", JOptionPane.ERROR_MESSAGE);
			} catch (RuntimeException e) {
				System.out.println(e.getStackTrace()[0]+": "+e.getMessage());
			}
		}
		
		/**
		 * listening of selection item product change, in product combo box
		 * @param event
		 */
		private void onProductSelectionChange (ItemEvent event) {
			Product product = (Product) event.getItem();
			if(event.getStateChange() == ItemEvent.DESELECTED){//validation de l'element de- selectionner
				validateItem(product);
			} else {
				//chargement de donnee pour l'element selectionner
				if(product == null || !tableModel.checkByProduct(product))
					return;
				
				CommandItem item = tableModel.findByProduct(product);
				fieldItemQuantity.getField().setText(item.getQuantity()+"");
				fieldItemUnitPrice.getField().setText(item.getUnitPrice()+"");
				fieldCurrency.getField().setSelectedItem(item.getCurrency());
			}
		}
		
		/**
		 * when change currency,
		 * we ask question, so he is prefer execute trading of currency
		 * @param event
		 */
		private void onCurrencySelectionChange (ItemEvent event) {
			Currency currency = (Currency) event.getItem();
			if(currency != null && event.getStateChange() == ItemEvent.DESELECTED) {
				Currency currency2 = currencyModel.getElementAt(fieldCurrency.getField().getSelectedIndex());
				if (!exchangeRateDao.checkByCurrencies(currency.getId(), currency2.getId())) 
					return;
				
				double unitPrice = Double.parseDouble(fieldItemUnitPrice.getField().getText());
				String message = "Voulez-vous faire la conversion du "+DBEntity.DECIMAL_FORMAT.format(unitPrice)+" "+currency.getShortName()+""
						+ "\n en "+currency2.getShortName()+"? \n",
						title = "Conversion de "+currency.getShortName()+" en "+currency2.getShortName();
				int status = JOptionPane.showConfirmDialog(CommandDialog.this, message, title, JOptionPane.YES_NO_OPTION);
				
				if(status == JOptionPane.YES_OPTION){
					unitPrice = exchangeRateDao.convert(unitPrice, currency, currency2);
					BigDecimal decimal = new BigDecimal(unitPrice).setScale(2, RoundingMode.HALF_UP);
					fieldItemUnitPrice.getField().setText(decimal.doubleValue()+"");
				}
				
				Product product = productModel.getElementAt(fieldItemProduct.getField().getSelectedIndex()); 
				if(!tableModel.checkByProduct(product))
					return;
				CommandItem item = tableModel.findByProduct(product);
				item.setCurrency(currency2);
			}
		}
		
		/**
		 * building UI components
		 */
		private void init() {
			final JPanel panelCommand = new JPanel(new BorderLayout());
			
			final Box 
				box = Box.createVerticalBox(),
				client = Box.createVerticalBox(),
				item = Box.createVerticalBox();
			
			final JPanel 
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
			
			box.add(new CaptionnablePanel("Date du jour", fieldCommandDate));
			box.add(Box.createVerticalStrut(5));
			box.add(new CaptionnablePanel("Client", client));
			box.add(Box.createVerticalStrut(5));
			
			box.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			panelCommand.add(item, BorderLayout.NORTH);
			
			tabbedPane.addTab("Commande", panelCommand);
			tabbedPane.addTab("Payement", paymentPanel);
			
			add(box, BorderLayout.NORTH);
			add(tabbedPane, BorderLayout.CENTER);
		}
		
		/**
		 * initialization of UI component of payment panel
		 */
		private void initPaymentPanel() {
			JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT)),
					description = new JPanel(new BorderLayout());
			
			final JScrollPane scroll = new JScrollPane(listPayment);
			scroll.setBorder(null);
			
			top.add(btnNewPayment);
			description.add(top, BorderLayout.NORTH);
			description.add(scroll, BorderLayout.CENTER);
			description.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			paymentPanel.add(description, "description");
			paymentPanel.add(paymentForm, "form");
			
			paymentLayout.addLayoutComponent(description, "description");
			paymentLayout.addLayoutComponent(paymentForm, "form");
			
			btnNewPayment.addActionListener(event -> {
				paymentLayout.show(paymentPanel, "form");
			});
			
			paymentForm.setFieldDateVisible(false);
		}

		@Override
		public void onValidate(AbstractForm<?> form) {}

		@Override
		public void onAcceptData(AbstractForm<?> form) {
			paymentLayout.show(paymentPanel, "description");
			
		}

		@Override
		public void onRejetData(AbstractForm<?> form, String... causes) {
			//JOptionPane.showMessageDialog(CommandDialog.this, causes[0], "Erreur du montant", JOptionPane.ERROR_MESSAGE);
		}

		@Override
		public void onCancel(AbstractForm<?> form) {
			paymentLayout.show(paymentPanel, "description");
		}
	}

}
