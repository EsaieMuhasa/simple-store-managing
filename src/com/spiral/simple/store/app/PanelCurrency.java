/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.spiral.simple.store.app.form.CurrencyForm;
import com.spiral.simple.store.app.form.ExchangeRateForm;
import com.spiral.simple.store.app.models.ExchangeRateTableModel;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelCurrency extends JPanel {
	private static final long serialVersionUID = -659627479279912684L;
	
	private final DefaultListModel<Currency> currencyListModel = new DefaultListModel<>();
	private final JList<Currency> currencyList = new JList<>(currencyListModel);
	private final CurrencyWorkspace workspace = new CurrencyWorkspace();
	
	private final JButton btnNewCurrency = new JButton("Ajoute une dévise", new ImageIcon(Config.getIcon("new")));
	private final JButton btnNewExchangeRate = new JButton("Nouveau taux", new ImageIcon(Config.getIcon("events")));
	private final ActionListener btnNewCurrencyListener = event -> showCreateCurrencyForm();
	private final ActionListener btnNewExchangeRateListener = event -> showCreateExchangeRateForm();
	private JDialog currencyDialog;
	private JDialog exchangeRateDialog;
	
	private CurrencyForm currencyForm;
	private ExchangeRateForm exchangeRateForm;
	
	private final CurrencyDao currencyDao = DAOFactory.getDao(CurrencyDao.class);
	private final ListSelectionListener currencyListSelectionListener = event -> onListSelectionListener (event);

	public PanelCurrency() {
		super(new BorderLayout());
		init();
		btnNewCurrency.addActionListener(btnNewCurrencyListener);
		btnNewExchangeRate.addActionListener(btnNewExchangeRateListener);
		
		currencyList.addListSelectionListener(currencyListSelectionListener);
		currencyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	/**
	 * lors dela selection d'un item de la liste de devise deja sauvegarder dans la BDD (pour l'instant dans la liste de devise)
	 * @param event
	 */
	private void onListSelectionListener(ListSelectionEvent event) {
		Currency currency = currencyListModel.getElementAt(currencyList.getSelectedIndex());
		workspace.setCurrency(currency);
	}

	/**
	 * initialization of main components
	 */
	private void init() {
		final JPanel 
			left = new JPanel(new BorderLayout()),
			padding = new JPanel(new BorderLayout()),
			paddingCenter = new JPanel(new BorderLayout()),
			bottom = new JPanel();
		final JScrollPane scroll = new JScrollPane(currencyList);
		
		
		scroll.setBorder(null);
		bottom.add(btnNewCurrency);
		bottom.add(btnNewExchangeRate);
		bottom.setBackground(CustomTable.GRID_COLOR);
		
		left.setBorder(BorderFactory.createLineBorder(bottom.getBackground()));
		left.add(scroll, BorderLayout.CENTER);
		left.add(bottom, BorderLayout.SOUTH);
		
		padding.add(left, BorderLayout.CENTER);
		padding.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		paddingCenter.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		paddingCenter.add(workspace, BorderLayout.CENTER);
		
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, padding, paddingCenter);
		add(split, BorderLayout.CENTER);
		
		load();
	}
	
	/***
	 * chargement des donnees pour la premiere fois, depuis la BDD
	 */
	private void load () {
		if(currencyDao.countAll() == 0)
			return;
		
		Currency [] currencies = currencyDao.findAll();
		for (Currency currency : currencies) {
			currencyListModel.addElement(currency);
		}
	}
	
	/**
	 * utility method to show dialog container by currency form
	 */
	private void showCreateCurrencyForm() {
		createCurrencyDialog();
		
		currencyDialog.setTitle("Ajout d'une dévise monaitaire");
		currencyDialog.setVisible(true);
	}
	
	/**
	 * utility method to build and show dialog owner of exchange rate form
	 */
	private void showCreateExchangeRateForm () {
		createExchangeRateDialog();
		
		exchangeRateDialog.setTitle("Ajout d'un taux d'echange");
		exchangeRateDialog.setVisible(true);
	}
	
	/**
	 * utility method to initialize and manage single instance by currencyDialog
	 */
	private void createCurrencyDialog () {
		if(currencyDialog == null) {
			currencyDialog = new JDialog(MainWindow.getLastInstance(), "Création d'une dévise", true);
			currencyDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			currencyForm = new CurrencyForm();
			
			JPanel content = (JPanel) currencyDialog.getContentPane();
			content.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			content.add(currencyForm, BorderLayout.CENTER);
			
			currencyDialog.pack();
			currencyDialog.setSize(400, currencyDialog.getHeight());
			currencyDialog.setResizable(false);
		}
		currencyDialog.setLocationRelativeTo(MainWindow.getLastInstance());
	}

	/**
	 * utility method to initialize and manage single instance by exchangeRateDialog
	 * exchangeRateDialog is owner window by form to insert/update exchange rate by sales device
	 */
	private void createExchangeRateDialog () {
		if(exchangeRateDialog == null) {
			exchangeRateDialog = new JDialog(MainWindow.getLastInstance(), "Ajout d'un taux d'echange", true);
			exchangeRateDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			exchangeRateForm = new ExchangeRateForm();
			exchangeRateForm.reload();
			JPanel content = (JPanel) exchangeRateDialog.getContentPane();
			content.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			content.add(exchangeRateForm, BorderLayout.CENTER);
			
			exchangeRateDialog.pack();
			exchangeRateDialog.setSize(500, exchangeRateDialog.getHeight());
			exchangeRateDialog.setResizable(false);
		}
		exchangeRateDialog.setLocationRelativeTo(MainWindow.getLastInstance());
	}


	/**
	 * @author Esaie MUHASA
	 * panel to show description by currency and exchange rate by currency
	 */
	private class CurrencyWorkspace extends JPanel {
		private static final long serialVersionUID = 8659928962281195916L;
		
		private final ExchangeRateTableModel 
			availableTateTableModel = new ExchangeRateTableModel(),
			allRateTableModel = new ExchangeRateTableModel();
		
		private final JTabbedPane tabbed = new JTabbedPane();
		private final CustomTable 
			tableAvailable = new CustomTable(availableTateTableModel),
			tableAll = new CustomTable(allRateTableModel);
		
		public CurrencyWorkspace() {
			super(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
			
			init();
			add(tabbed, BorderLayout.CENTER);
			
			availableTateTableModel.setAvailableOnly(true);
			allRateTableModel.setAvailableOnly(false);
		}
		
		/**
		 * initialisation des composants graphiques
		 */
		private void init() {
			
			final JPanel 
				panel1 = new JPanel(new BorderLayout()),
				panel2 = new JPanel(new BorderLayout());
			
			final JScrollPane
				scroll1 = new JScrollPane(tableAvailable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
				scroll2 = new JScrollPane(tableAll, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			scroll1.setBorder(null);
			scroll2.setBorder(null);
			
			panel1.add(scroll1, BorderLayout.CENTER);
			panel2.add(scroll2, BorderLayout.CENTER);
			
			tabbed.addTab("Taux actuels ", new ImageIcon(Config.getIcon("success")), panel1);
			tabbed.addTab("Voir tout ", new ImageIcon(Config.getIcon("report")), panel2);
		}

		/**
		 * mutation du currency dans les table models
		 * @param currency the currency to set
		 */
		public void setCurrency(Currency currency) {
			availableTateTableModel.setCurrency(currency);
			allRateTableModel.setCurrency(currency);
		}

	}

}
