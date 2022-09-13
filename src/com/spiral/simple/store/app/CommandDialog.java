/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import com.spiral.simple.store.beans.Client;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandItem;
import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.DBEntity;
import com.spiral.simple.store.beans.MeasureUnit;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.dao.ClientDao;
import com.spiral.simple.store.dao.CommandDao;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOException;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.ExchangeRateDao;
import com.spiral.simple.store.dao.MeasureUnitDao;
import com.spiral.simple.store.dao.ProductDao;
import com.spiral.simple.store.dao.StockDao;
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
	
	public static final int DAO_REQUEST_ID = 0x007700;
	
	private final DefaultListModel<Product> listModelProduct=  new DefaultListModel<>();
	private final JList<Product> listProduct = new JList<>(listModelProduct);
	private final JTextField fieldSearchProdut = new  JTextField();
	
	private final JButton btnValidate = new JButton("Valider", new ImageIcon(Config.getIcon("success")));
	private final JButton btnCancel = new JButton("Annuler", new ImageIcon(Config.getIcon("close")));
	private final JButton btnPrintInvoice = new JButton("Facture", new ImageIcon(Config.getIcon("print")));
	private final JButton btnPrintSlip = new JButton("Réçu", new ImageIcon(Config.getIcon("print")));
	
	private final DefaultComboBoxModel<Product> productModel = new DefaultComboBoxModel<>(); 
	private final DefaultComboBoxModel<MeasureUnit> measureUnitModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<Currency> currencyModel = new DefaultComboBoxModel<>();
	private final InvoiceTableModel tableModel = new InvoiceTableModel();
	private final JLabel labelPaymentMoney = UIComponentBuilder.createH2("Réçu: ");//le montant total deja recu
	private final JLabel labelPaymentTotal = UIComponentBuilder.createH2("Total: ");//le montant total qui doit etre payer
	private final JLabel labelPaymentDebt = UIComponentBuilder.createH2("Reste: ");//le reste pour la command
	
	private final PanelFieldsCommand fieldsCommand = new PanelFieldsCommand();
	private final CustomTable itemTable = new CustomTable(tableModel);
	
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem [] itemOptions = {
			new JMenuItem("Modiffier", new ImageIcon(Config.getIcon("edit"))),
			new JMenuItem("Supprimer", new ImageIcon(Config.getIcon("close")))
	}; 
	
	private final MeasureUnitDao measureUnitDao = DAOFactory.getDao(MeasureUnitDao.class);
	private final ProductDao productDao = DAOFactory.getDao(ProductDao.class);
	private final StockDao stockDao = DAOFactory.getDao(StockDao.class);
	private final CommandDao commandDao = DAOFactory.getDao(CommandDao.class);
	private final CurrencyDao currencyDao = DAOFactory.getDao(CurrencyDao.class);
	private final ExchangeRateDao exchangeRateDao = DAOFactory.getDao(ExchangeRateDao.class);
	private final ClientDao clientDao = DAOFactory.getDao(ClientDao.class);
	
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
				Product product = listModelProduct.getElementAt(listProduct.getSelectedIndex());
				fieldsCommand.fieldItemProduct.getField().removeItemListener(fieldsCommand.productItemListener);
				fieldsCommand.fieldCurrency.getField().removeItemListener(fieldsCommand.currencyItemListener);
				fieldsCommand.fieldQuantityUnit.getField().removeItemListener(fieldsCommand.measureUnitListenenr);
				CommandItem item = null;
				if(productModel.getIndexOf(product) == -1){
					/*
					 * pour satisfaire les histoires de reference lors dela modification d'une commande
					 * nous devons verifier si le produit existe dans le model du combo box
					 */
					for(int i = 0; i < productModel.getSize(); i++) {
						if(product.equals(productModel.getElementAt(i))) {
							item = tableModel.findByProduct(productModel.getElementAt(i));
							break;
						}
					}
					
					if(item == null) {						
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
					}
				} else 
					item = tableModel.findByProduct(product);
				
				/*
				 * les stocks d'un produits sont succeptible d'avoir plusieur unite de menuse.
				 * lors le personnalisation de l'element de la commande, le choix de l'unite de mesure determine le(s) stock(s)
				 * qui seront crediter
				 */
				measureUnitModel.removeAllElements();
				MeasureUnit [] units = item.getStocksUnit();
				if(item.countStock() != 0) {//manipulation de l'unite de mesure
					for (MeasureUnit unit : units) {
						measureUnitModel.addElement(unit);
						if(item.getMeasureUnit() == null || unit.equals(item.getMeasureUnit()))
							fieldsCommand.fieldQuantityUnit.getField().setSelectedItem(unit);
					}
					fieldsCommand.fieldQuantityUnit.setEnabled(true);
				} else 
					fieldsCommand.fieldQuantityUnit.setEnabled(false);
				
				fieldsCommand.fieldItemProduct.getField().setSelectedItem(product);
				fieldsCommand.fieldItemQuantity.getField().setText(item.getQuantity()+"");
				fieldsCommand.fieldItemUnitPrice.getField().setText(item.getUnitPrice()+"");
				fieldsCommand.fieldCurrency.getField().setSelectedItem(item.getCurrency());
				
				fieldsCommand.fieldItemProduct.getField().addItemListener(fieldsCommand.productItemListener);
				fieldsCommand.fieldCurrency.getField().addItemListener(fieldsCommand.currencyItemListener);
				fieldsCommand.fieldQuantityUnit.getField().addItemListener(fieldsCommand.measureUnitListenenr);
				
				revalidateCommand();
			}
		}
	};
	
	private final MouseAdapter invoiceTableMouseAdapter = new MouseAdapter() {//trigger pop up to show option of command item table
		@Override
		public void mouseReleased(MouseEvent e) {
			if(!e.isPopupTrigger() || itemTable.getSelectedRow() == -1)
				return;
			
			popupMenu.show(itemTable, e.getX(), e.getY());
		}
	};//==
	
	private final ActionListener itemInvoiceOptionActionListener = event -> {//listening item menu by pop up menu
		Command  command = tableModel.getCommand();
		CommandItem item = tableModel.getRow(itemTable.getSelectedRow());
		Product product = item.getProduct();

		if(event.getSource() == itemOptions[0]) {//update command item
			fieldsCommand.fieldItemProduct.getField().setSelectedItem(product);
		} else {//remove command item				
			String message = "Voulez-vous vraiment retirer "
					+ "\n"+product+" de la commande?";
			int status = JOptionPane.showConfirmDialog(CommandDialog.this, message, "Sppression de l'item", JOptionPane.YES_NO_OPTION);
			if (status == JOptionPane.YES_OPTION) {
				command.removeItem(item);
				tableModel.reload();
				fieldsCommand.fieldItemProduct.getField().removeItem(product);
			}
		}
	};//===
	
	private final DAOListenerAdapter<Product> productListenerAdapter = new  DAOListenerAdapter<Product>() {
		@Override
		public void onCreate(Product... data) {
			for (Product product : data)
				listModelProduct.addElement(product);
		}

		@Override
		public void onUpdate(Product newState, Product oldState) {
			for (int i = 0; i < listModelProduct.getSize(); i++)
				if(listModelProduct.getElementAt(i).getId().equals(newState.getId())){
					listModelProduct.setElementAt(newState, i);
					return;
				}
		}

		@Override
		public void onDelete(Product... data) {
			for (Product product : data) 
				for (int i = 0; i < listModelProduct.getSize(); i++) 
					if(listModelProduct.getElementAt(i).getId().equals(product.getId())){
						listModelProduct.removeElementAt(i);
						break;
					}
		}
	};
	
	private final DAOListenerAdapter<Currency> currecyListenerAdapter = new DAOListenerAdapter<Currency>() {
		@Override
		public void onCreate(Currency... data) {
			for (Currency currency : data) 
				currencyModel.addElement(currency);
		}

		@Override
		public void onUpdate(Currency newState, Currency oldState) {
			for (int i = 0; i < currencyModel.getSize(); i++)
				if(currencyModel.getElementAt(i).equals(newState)) {
					currencyModel.removeElementAt(i);
					currencyModel.insertElementAt(newState, i);
					return;
				}
		}

		@Override
		public void onDelete(Currency... data) {
			for (Currency currency : data)
				for (int i = 0; i < currencyModel.getSize(); i++) 
					if(currencyModel.getElementAt(i).equals(currency)) {
						currencyModel.removeElementAt(i);
						break;
					}
		}
	};
	
	private final DAOListenerAdapter<MeasureUnit> measureUnitListenerAdapter = new DAOListenerAdapter<MeasureUnit>() {
		@Override
		public void onCreate(MeasureUnit... data) {
			for (MeasureUnit m : data)
				measureUnitModel.addElement(m);
		}

		@Override
		public void onUpdate(MeasureUnit newState, MeasureUnit oldState) {
			for (int i = 0; i < measureUnitModel.getSize(); i++)
				if(measureUnitModel.getElementAt(i).equals(newState)) {
					measureUnitModel.removeElementAt(i);
					measureUnitModel.insertElementAt(newState, i);
					return;
				}
		}

		@Override
		public void onDelete(MeasureUnit... data) {
			for (MeasureUnit m : data)
				for (int i = 0; i < measureUnitModel.getSize(); i++) 
					if(measureUnitModel.getElementAt(i).equals(m)) {
						measureUnitModel.removeElementAt(i);
						break;
					}
		}
	};
	
	private final DAOListenerAdapter<Command> commandListenerAdapter = new DAOListenerAdapter<Command>() {
		@Override
		public void onCreate(Command... data) {
			cancel();
			String message = "Enregistrement de la commande fait avec succès";
			JOptionPane.showMessageDialog(MainWindow.getLastInstance(), message, "Enregistrement de la commande", JOptionPane.INFORMATION_MESSAGE);
		}

		@Override
		public void onUpdate(Command newState, Command oldState) {
			cancel();
			String message = "Enregistrement des modifications\n de la commande fait avec succès";
			JOptionPane.showMessageDialog(MainWindow.getLastInstance(), message, "Enregistrement des modifications", JOptionPane.INFORMATION_MESSAGE);
		}

		@Override
		public void onError(int requestId, DAOException exception) {
			exception.printStackTrace();
		}
		
	};
	
	private String [] invalidityReason;//tableau contenant les raison d'invalidite de la commande
	
	/**
	 * default construct
	 */
	public CommandDialog() {
		super(MainWindow.getLastInstance(), "Réalisation d'une commande", true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(windowAdapter);
		btnCancel.addActionListener(event -> doCancel());
		btnPrintInvoice.addActionListener(event ->  doPrintInvoice());
		btnValidate.addActionListener(event -> doValidate());
		
		//listening DAOs events
		measureUnitDao.addBaseListener(measureUnitListenerAdapter);
		productDao.addBaseListener(productListenerAdapter);
		commandDao.addBaseListener(commandListenerAdapter);
		commandDao.addErrorListener(commandListenerAdapter);
		currencyDao.addBaseListener(currecyListenerAdapter);
		//==
		
		initViews();
		
		listProduct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listProduct.addMouseListener(listProductMouseAdapter);
		itemTable.addMouseListener(invoiceTableMouseAdapter);
		
		for (JMenuItem item : itemOptions) {
			popupMenu.add(item);
			item.addActionListener(itemInvoiceOptionActionListener);
		}
	}
	
	/**
	 * mutation of command, in command models
	 * @param command
	 */
	public void setCommand (Command command) {
		tableModel.setCommand(command);
		if(command.getClient() == null) 
			command.setClient(new Client());
		
		if(command.getDate() == null)
			command.setDate(new Date());
		
		fieldsCommand.initFields(command);
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
		
		listProduct.setFixedCellHeight(40);
		listProduct.setFont(new Font("Arial", Font.BOLD, 12));
		listProduct.setCellRenderer(new ProductCellRender());
		
		JScrollPane 
			scrollList = new JScrollPane(listProduct),
			scrollTable = new JScrollPane(itemTable);
		
		final JPanel 
			panelTable = new JPanel(new BorderLayout()),
			panelFields = new JPanel(new BorderLayout());
		
		centerPanel.add(panelFields, BorderLayout.WEST);
		centerPanel.add(panelTable, BorderLayout.CENTER);
		
		panelFields.add(fieldsCommand, BorderLayout.NORTH);
		panelFields.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
		panelTable.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
		panelTable.add(scrollTable, BorderLayout.CENTER);
		
		//the panel below table view of invoice
		final Box tableBottom = Box.createVerticalBox();
		final Box box = Box.createHorizontalBox();//pour les boutons
		final JPanel rows = new JPanel(new GridLayout(3, 1, 5, 5));
		
		rows.add(labelPaymentTotal);
		rows.add(labelPaymentMoney);
		rows.add(labelPaymentDebt);
		rows.setBackground(itemTable.getGridColor());
		
		box.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		box.add(Box.createHorizontalGlue());
		box.add(btnPrintInvoice);
		box.add(Box.createHorizontalStrut(5));
		box.add(btnPrintSlip);
		tableBottom.add(rows, BorderLayout.EAST);
		tableBottom.add(box, BorderLayout.CENTER);
		panelTable.add(tableBottom, BorderLayout.SOUTH);
		//==
		
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
		cancel();
	}
	
	private void cancel() {
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
	 * utilitaire d'initialisation dela commande.
	 * cette method erecupere la commande sauvegarder dans le tableModel de 
	 * elemenet de ladite command, et y ajoute les informations concernant le client
	 * @return
	 */
	private Command initCommand () {
		Command command = tableModel.getCommand();
		Client client = command.getClient();
		
		Date date = fieldsCommand.fieldCommandDate.getField().getDate();
		String telephone = fieldsCommand.fieldClientTelephone.getField().getText().trim();
		String names = fieldsCommand.fieldClientName.getField().getText().trim();
		
		client.setTelephone(telephone.isEmpty()? null : telephone);
		client.setNames(names.isEmpty()? null : names);
		
		if (telephone != null && client.getId() == null && clientDao.checkByTelephone(client.getTelephone()))
			command.setClient(clientDao.findByTelephone(client.getTelephone()));
		
		command.setDate(date == null? new Date() : date);
		return command;
	}
	
	/**
	 * method to validate command
	 * -first we must validate command items
	 * -second we validate command payment
	 */
	private void doValidate () {
		Command command = initCommand();
		if (!isValid(command, command.getId() == null)) {
			JOptionPane.showMessageDialog(this, invalidityReason, "Echec d'enregistremnt", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(command.getId() == null)
			commandDao.create(DAO_REQUEST_ID, command);
		else 
			commandDao.update(DAO_REQUEST_ID, command);
	}
	
	/**
	 * validation de la commande.
	 * Le message d'invaliditee son conserver dans un tableau
	 * @param command
	 * @param onCreate
	 * @return
	 */
	private boolean isValid (Command command, boolean onCreate) {
		String message = "";
		
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			CommandItem item = tableModel.getRow(i);

			if(!item.isValidable()) {
				message += "Le stocks disponible ( en "+item.getMeasureUnit().getShortName()+") pour le produit "+item.getProduct().getName()+" sont insufisante;";
			}
			
		}
		
		if(message.isEmpty())
			invalidityReason = null;
		else
			invalidityReason = message.split(";");
		return message.isEmpty();
	}
	
	/**
	 * cette methode demande la revalidation dela commande.
	 * Re-verifie les quantites requise pour la satisfaction des elements de la commande
	 * conformement aux stock disponible
	 */
	private void revalidateCommand () {
		Command command = initCommand();
		boolean valid = isValid(command, command.getId() == null);
		btnValidate.setEnabled(valid);
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
		
		if(stockDao.checkAvailable()) {
			listModelProduct.removeAllElements();
			Product [] products = productDao.findByAvailableStock();
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
	 * rendue des produits dans la liste des produits
	 */
	private static class ProductCellRender extends DefaultListCellRenderer {
		private static final long serialVersionUID = -7511388178715292366L;
		
		private static final Area ROUND_AREA = new Area(new Rectangle2D.Float(5, 5, 30, 30));
		static {
			ROUND_AREA.subtract(new Area(new Ellipse2D.Float(5, 5, 30, 30)));
		}
		
		private Product product;
		private boolean isSelected;
		
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			product = (Product) value;
			this.isSelected = isSelected;
			setFont(list.getFont());
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
		
		@Override
		protected void paintComponent(Graphics graphics) {
			if (product != null) {
				Graphics2D g = (Graphics2D) graphics;
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
				Image img = product.getImage().getImage();
				
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(img, 5, 5, 30, 30, null);
				g.setColor(getBackground());
				g.fill(ROUND_AREA);
				
				FontMetrics metrics = g.getFontMetrics();
				g.setFont(getFont());
				g.setColor(isSelected? Color.WHITE : Color.BLACK);
				
				g.drawString(product.getName(), 40, (getHeight()/2) + (metrics.getHeight()/4));
				
				g.setColor(Color.DARK_GRAY);
				g.drawLine(30, 30, getWidth()-10, 30);
				return;
			} else
				super.paintComponent(graphics);
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
		private final ItemListener measureUnitListenenr = event -> onMeasureUnitSelectionChange(event);
		
		//payment panel
		private final JPanel paymentPanel = new JPanel(new BorderLayout());
		private final JPanel paymentDescription = new JPanel(new BorderLayout());
		private final JButton btnNewPayment = new JButton("Nouveau payement", new ImageIcon(Config.getIcon("new")));
		//==
		
		//items by pop up menu options by listPayment
		private final JMenuItem [] itemOptionsListPayment = {
				new JMenuItem("Supprimer"),
				new JMenuItem("Modifier")
		};
		private final JPopupMenu listPaymentPopupMenu = new JPopupMenu();
		
		private final CaretListener caretQuantityListener = event -> {
			if(productModel.getSize() == 0)
				return;
			
			Product product = productModel.getElementAt(fieldItemProduct.getField().getSelectedIndex());
			if(!tableModel.checkByProduct(product))
				return;
			
			CommandItem item = tableModel.findByProduct(product);
			double quantity = 0;
			if(!fieldItemQuantity.getField().getText().trim().isEmpty()) {
				try {
					quantity = Double.parseDouble(fieldItemQuantity.getField().getText());					
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(CommandDialog.this, "La quantité doit être une valeur numérique valide.", "Erreur: valeur numérique invalide", JOptionPane.ERROR_MESSAGE);
				}
			} else 
				quantity = 0;
			
			item.setQuantity(quantity);
			tableModel.repaintRow(item);
			updateLabelTotalAmount();
			revalidateCommand();
		};
		
		private final CaretListener caretUnitPriceListener = event -> {
			if(productModel.getSize() == 0)
				return;
			
			Product product = productModel.getElementAt(fieldItemProduct.getField().getSelectedIndex());
			if(!tableModel.checkByProduct(product))
				return;
			
			CommandItem item = tableModel.findByProduct(product);
			double price = 0;
			if(!fieldItemUnitPrice.getField().getText().trim().isEmpty()) {
				try {
					price = Double.parseDouble(fieldItemUnitPrice.getField().getText());					
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(CommandDialog.this, "La prix unitaire doit être une valeur numérique valide.", "Erreur: valeur numérique invalide", JOptionPane.ERROR_MESSAGE);
				}
			} else 
				price = 0;
			
			item.setUnitPrice(price);
			tableModel.repaintRow(item);
			updateLabelTotalAmount();
			revalidateCommand();
		};
		
		private final MouseAdapter listPaymentMouseAdapter = new MouseAdapter() {//listening mouse on JList payment to show pop up menu options
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && listPaymentModel.getSize() != 0 && listPayment.getSelectedIndex() != -1) {
					listPaymentPopupMenu.show(listPayment, e.getX(), e.getY());
				}
			}
		};
		
		private final ActionListener itemPopupActionListener = event -> {//listening action on any item by JList of payment pop pup menu
			CommandPayment p = listPaymentModel.getElementAt(listPayment.getSelectedIndex());
			if(event.getSource() == itemOptionsListPayment[0]) {//suppression
				String message = "Es-tu sûr de vouloir supprimer ce payement??\n-> "+p.toString();
				int status = JOptionPane.showConfirmDialog(CommandDialog.this, message, "Suppression du payement", JOptionPane.YES_NO_OPTION);
				if(status == JOptionPane.YES_OPTION) {
					tableModel.getCommand().removePayment(p);
					listPaymentModel.removeElement(p);
				}
			} else if(event.getSource() == itemOptionsListPayment[1]) {// modification
				paymentForm.setPayment(p);
				showPaymentForm();
			}
		};
		
		public PanelFieldsCommand() {
			super(new BorderLayout());
			init();
			initPaymentPanel();
			
			fieldItemProduct.getField().addItemListener(productItemListener);
			fieldItemQuantity.getField().addCaretListener(caretQuantityListener);
			fieldItemUnitPrice.getField().addCaretListener(caretUnitPriceListener);
			listPayment.addMouseListener(listPaymentMouseAdapter);
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
		
		private void updateLabelTotalAmount () {
			labelPaymentTotal.setText("Total: "+tableModel.getCommand().getTotalToString());
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
			listPaymentModel.removeAllElements();
		}
		
		/**
		 * permet de charge les information contenue la commande dans les composants graphiques
		 * @param command
		 */
		public void initFields (Command command) {
			productModel.removeAllElements();
			
			if(command != null) {
				CommandPayment [] payments = command.getPayments();
				if(payments != null) {
					for (CommandPayment payment : payments) {
						listPaymentModel.addElement(payment);
					}
				}
				
				Client client = command.getClient();
				fieldClientName.getField().setText(client.getNames());
				fieldClientTelephone.getField().setText(client.getTelephone());
				
				for (int i = 0; i < tableModel.getRowCount(); i++) {
					CommandItem item = command.getItemAt(i);
					productModel.addElement(item.getProduct());
				}
				labelPaymentMoney.setText("Réçu: "+tableModel.getCommand().getCreditToString());
			}
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
			updateLabelTotalAmount();
		}
		
		/**
		 * utility method to show payment formulaire
		 */
		private void showPaymentForm () {
			paymentPanel.removeAll();
			paymentPanel.add(paymentForm, BorderLayout.CENTER);
			paymentPanel.revalidate();
			paymentPanel.repaint();
		}
		
		/**
		 * utility method to showing payments descriptions by current command
		 */
		private void showPaymentDescription() {
			paymentPanel.removeAll();
			paymentPanel.add(paymentDescription, BorderLayout.CENTER);
			paymentPanel.revalidate();
			paymentPanel.repaint();
		}
		
		/**
		 * when change currency,
		 * we ask question, so user prefer execute trading of currency
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
				updateLabelTotalAmount();
			}
		}
		
		/**
		 * lors de la commande d'un produit dont les stock disponible ne sont pas vendue au meme unite de mesure,
		 * l'utililsateur a la posiblite de changer l'unite de mesure. en fonction de l'unite de mesure,
		 * le stock qui sera debiter est determiné
		 * @param event
		 */
		private void onMeasureUnitSelectionChange (ItemEvent event) {
			if(event.getStateChange() != ItemEvent.SELECTED || measureUnitModel.getSize() == 0 || productModel.getSize() == 0)
				return;
			
			Product product = productModel.getElementAt(fieldItemProduct.getField().getSelectedIndex());
			if(!tableModel.checkByProduct(product))
				return;
			
			CommandItem item = tableModel.findByProduct(product);
			item.dispatchQantityTo(measureUnitModel.getElementAt(fieldQuantityUnit.getField().getSelectedIndex()));
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
			JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			
			final JScrollPane scroll = new JScrollPane(listPayment);
			scroll.setBorder(null);
			
			top.add(btnNewPayment);
			
			paymentDescription.add(top, BorderLayout.NORTH);
			paymentDescription.add(scroll, BorderLayout.CENTER);
			paymentDescription.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			paymentPanel.add(paymentDescription, BorderLayout.CENTER);
			
			btnNewPayment.addActionListener(event -> {
				showPaymentForm();
			});
			
			paymentForm.setFieldDateVisible(false);
			
			for (JMenuItem item : itemOptionsListPayment) {
				listPaymentPopupMenu.add(item);
				item.addActionListener(itemPopupActionListener);
			}
		}

		@Override
		public void onValidate(AbstractForm<?> form) {}

		@Override
		public void onAcceptData(AbstractForm<?> form) {
			
			final CommandPayment payment = paymentForm.getPayment(), newPay = new CommandPayment();
			tableModel.getCommand().addPayments(payment);
			tableModel.getCommand().creditsToPayments();
			
			listPaymentModel.removeAllElements();
			newPay.setAmount(0);
			paymentForm.setPayment(newPay);
			showPaymentDescription();
			
			CommandPayment [] payments = tableModel.getCommand().getPayments();
			for (CommandPayment p : payments)
				listPaymentModel.addElement(p);
			
			labelPaymentMoney.setText("Réçu: "+tableModel.getCommand().getCreditToString());
		}

		@Override
		public void onRejetData(AbstractForm<?> form, String... causes) {
			//JOptionPane.showMessageDialog(CommandDialog.this, causes[0], "Erreur du montant", JOptionPane.ERROR_MESSAGE);
		}

		@Override
		public void onCancel(AbstractForm<?> form) {
			showPaymentDescription();
		}
	}

}
