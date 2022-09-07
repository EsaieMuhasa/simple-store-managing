/**
 * 
 */
package com.spiral.simple.store.app.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.dao.CommandDao;
import com.spiral.simple.store.dao.CommandPaymentDao;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.ExchangeRateDao;
import com.spiral.simple.store.swing.Card;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.swing.DefaultCardModel;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;
import com.toedter.calendar.JCalendar;
import com.trimeur.swing.chart.CloudChartRender;
import com.trimeur.swing.chart.DefaultCloudChartModel;

/**
 * @author Esaie Muhasa
 *
 */
public class SellerSidebar extends JPanel {
	private static final long serialVersionUID = -8618632784321077214L;
	
	private final JButton btnAddCommand =new JButton("Nouvele commande", new ImageIcon(Config.getIcon("new")));
	private final JCalendar calendar = new JCalendar();

	private final DefaultCloudChartModel chartModel = new DefaultCloudChartModel();
	private final DefaultComboBoxModel<Currency> currencyBoxModel = new DefaultComboBoxModel<>();
	
	private final List<SellerSidebarListener> listeners = new ArrayList<>();
	private final PropertyChangeListener calendarListener = event -> onSelectedDateChange();
	private final StateContainer stateContainer = new  StateContainer();
	private final JLabel title = UIComponentBuilder.createH2("Synthèse du 28/08/2022");
	private final CloudChartRender chartRender = new CloudChartRender(chartModel);
	
	private final JCheckBox currencyCheck = new JCheckBox("", true);
	private final JComboBox<Currency> currencyBox = new JComboBox<>(currencyBoxModel);
	
	{
		calendar.addPropertyChangeListener("calendar", calendarListener);
		btnAddCommand.addActionListener(event -> {
			for (SellerSidebarListener ls : listeners)
				ls.onNewCommand();
		});
	}
	
	private final CommandDao commandDao = DAOFactory.getDao(CommandDao.class);
	private final CurrencyDao currencyDao = DAOFactory.getDao(CurrencyDao.class);
	private final CommandPaymentDao commandPaymentDao = DAOFactory.getDao(CommandPaymentDao.class);
	private final ExchangeRateDao exchangeRateDao = DAOFactory.getDao(ExchangeRateDao.class);
	
	private final DAOListenerAdapter<Command> commandListenerAdapter = new DAOListenerAdapter<Command>() {
		@Override
		public void onCreate(Command... data) {
			if(Command.DATE_FORMAT.format(data[0].getDate()).equals(Command.DATE_FORMAT.format(calendar.getDate()))) {
				reload();
			}
		}

		@Override
		public void onUpdate(Command newState, Command oldState) {
			if(Command.DATE_FORMAT.format(newState.getDate()).equals(Command.DATE_FORMAT.format(calendar.getDate()))) {
				reload();
			}
		}

		@Override
		public void onDelete(Command... data) {
			if(Command.DATE_FORMAT.format(data[0].getDate()).equals(Command.DATE_FORMAT.format(calendar.getDate()))) {
				reload();
			}
		}
	};
	
	private final ActionListener checkCurrencyChangeListener = event -> onCheckCurrenceChange(event);
	private final ItemListener boxCurrencyListener = event -> onBoxCurrencyChange(event);
	
	public SellerSidebar() {
		super(new BorderLayout(5, 5));
		
		final JPanel
			top = new JPanel(new BorderLayout()),
			center = new JPanel(new BorderLayout()),
			btn = new JPanel(new BorderLayout()),
			chart = new JPanel(new BorderLayout(5, 5)),
			titlePanel = new JPanel(new GridLayout(1, 2, 5, 5));
		
		btn.add(btnAddCommand, BorderLayout.CENTER);
		btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		
		top.add(calendar, BorderLayout.CENTER);
		top.add(btn, BorderLayout.NORTH);
		
		Box box = Box.createHorizontalBox();
		box.add(currencyCheck);
		box.add(Box.createHorizontalStrut(5));
		box.add(currencyBox);
		
		currencyCheck.addActionListener(checkCurrencyChangeListener);
		currencyBox.addItemListener(boxCurrencyListener);
		
		titlePanel.add(title);
		titlePanel.add(box);
		titlePanel.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		titlePanel.setBackground(CustomTable.GRID_COLOR);
		
		center.add(titlePanel, BorderLayout.NORTH);
		center.add(stateContainer, BorderLayout.CENTER);
		center.setBorder(BorderFactory.createLineBorder(titlePanel.getBackground()));
		
		chart.setBorder(BorderFactory.createLineBorder(titlePanel.getBackground()));
		chart.add(chartRender, BorderLayout.CENTER);
		
		add(top, BorderLayout.NORTH);
		add(chart, BorderLayout.CENTER);
		add(center, BorderLayout.SOUTH);
		setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		addSidebarListener(stateContainer);
		commandDao.addBaseListener(commandListenerAdapter);

		reloadCurrencies();
	}
	
	/**
	 * mis en jour du model du combo box qui contiens 
	 * les devises
	 */
	private void reloadCurrencies() {
		currencyBoxModel.removeAllElements();
		currencyBox.setEnabled(currencyCheck.isSelected());
		if(currencyDao.countAll() == 0)
			return;
		
		Currency []  cs = currencyDao.findAll();
		for (Currency currency : cs) 
			currencyBoxModel.addElement(currency);
		
		stateContainer.initTabs(cs);
		if(!currencyCheck.isSelected())
			stateContainer.setSelectedCurrency(currencyBoxModel.getElementAt(currencyBox.getSelectedIndex()), calendar.getDate());
	}
	
	/**
	 * rechargeemnt de donnes depuis la bdd
	 * pour metre enjour l'UI
	 */
	private synchronized void reload() {
		stateContainer.onDateChange(calendar.getDate());
	}
	
	/**
	 * lors de changement d'etat de la boite de dialogie:
	 * si la boite de dialogue est maintenant selectionner, alors on fais directement la conversion de recette 
	 * pour la devise selectionner, sinon on le solde selon chaque devise dans un tabbed panel
	 * @param event
	 */
	private void onCheckCurrenceChange(ActionEvent event) {
		currencyBox.setEnabled(currencyCheck.isSelected());
		
		if(currencyCheck.isSelected())
			stateContainer.setSelectedCurrency(currencyBoxModel.getElementAt(currencyBox.getSelectedIndex()), calendar.getDate());
		else
			stateContainer.onDateChange(calendar.getDate());
	}
	
	/**
	 * lors l'utilisateur decide change la devise
	 * @param event
	 */
	private void onBoxCurrencyChange(ItemEvent event) {
		if(event.getStateChange() != ItemEvent.SELECTED)
			return;
		
		stateContainer.setSelectedCurrency(currencyBoxModel.getElementAt(currencyBox.getSelectedIndex()), null);
	}
	
	/**
	 * when selected date change
	 */
	private void onSelectedDateChange () {
		for (SellerSidebarListener ls : listeners)
			ls.onDateChange(calendar.getDate());
	}
	
	/**
	 * change status button sender for creation new command request
	 * @param enabled
	 */
	public void setEnabledAddCommand (boolean enabled ) {
		btnAddCommand.setEnabled(enabled);
	}
	
	/**
	 * subscribe a new listener on side bar events
	 * @param listener
	 */
	public void addSidebarListener (SellerSidebarListener listener) {
		if(!listeners.contains(listener)){
			listeners.add(listener);
			listener.onDateChange(calendar.getDate());
		}
	}
	
	/**
	 * unsubscribe listener on side bar events
	 * @param listener
	 */
	public void removeSidebarListener (SellerSidebarListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * conteneur de cards pour visualiser les etats comptable de la date 
	 * actuelement selectionner 
	 * @author Esaie Muhasa
	 */
	private final class StateContainer extends JPanel implements SellerSidebarListener{
		private static final long serialVersionUID = 7858908555626412343L;
		
		private JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
		private StateItem item = new StateItem();

		public StateContainer() {
			super(new BorderLayout());
			item.setCurrencyOnly(false);
			add(item, BorderLayout.CENTER);
		}
		
		/**
		 * initialisation des tabulations pour chaque devise disponible
		 */
		public void initTabs (Currency [] currencies) {
			for (int i = 0; i < currencies.length; i++) {
				StateItem st = new StateItem();
				st.setCurrencyOnly(true);
				st.setCurrency(currencies[i]);
				
				tabbed.addTab(currencies[i].getShortName(), st);
			}
		}
		
		/**
		 * modification de la devise actuelement selectionne
		 * utiliser cette methode lors que l'utilisateur selection une velle devise
		 * @param currency
		 * @param date
		 */
		private void setSelectedCurrency (Currency currency, Date date) {
			Objects.requireNonNull(currency);
			removeAll();
			if(date != null)
				item.reloadCardModel(currency, date);
			else
				item.convertTo(currency);
			add(item, BorderLayout.CENTER);
			revalidate();
			repaint();
		}

		@Override
		public void onDateChange(Date date) {
			title.setText("Le "+Command.DATE_FORMAT.format(date));
			removeAll();
			
			if(currencyCheck.isSelected()) {
				item.setCurrentDate(date);
				add(item, BorderLayout.CENTER);
			} else {
				for (int i = 0; i < tabbed.getTabCount(); i++) {
					StateItem item = (StateItem) tabbed.getComponentAt(i);
					item.setCurrentDate(date);
				}
				add(tabbed, BorderLayout.CENTER);
			}
			revalidate();
			repaint();
		}

		@Override
		public void onNewCommand() {}
	}
	
	/**
	 * @author Esaie Muhasa
	 * Etat comptable.
	 */
	private final class StateItem extends JPanel {
		private static final long serialVersionUID = -756350261195314676L;
		
		private final DefaultCardModel<Double> modelInput = new DefaultCardModel<>(CustomTable.GRID_COLOR, Color.BLACK, Config.getIcon("btn-add"), "$");
		private final DefaultCardModel<Double> modelOutput = new DefaultCardModel<>(CustomTable.GRID_COLOR, Color.BLACK, Config.getIcon("btn-minus"), "$");
		
		private Date currentDate;
		private Currency currency;
		private boolean currencyOnly = true;//les autre devise doit-elle etre mie a l'ecart??
		
		public StateItem () {
			super(new BorderLayout());
			
			final Card cardInput = new Card(modelInput);
			final Card cardOutput = new Card(modelOutput);
			
			modelInput.setValue(0d);
			modelInput.setTitle("Recette");
			modelInput.setInfo("Vante total du jour, en");
			modelOutput.setInfo("Depenses en  ");
			
			modelOutput.setValue(0d);
			modelOutput.setTitle("Dépense");
			
			final Box box = Box.createVerticalBox();
			box.add(cardInput);
			box.add(Box.createVerticalStrut(10));
			box.add(cardOutput);
			
			add(box, BorderLayout.CENTER);
			setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		}
		
		/**
		 * Conversion de l'actuel montant en montant en parametre 
		 * @param currency
		 */
		public void convertTo(Currency currency) {
			if(this.currency == null || currency == null) {
				setCurrency(currency);
				return;
			}
			double amount = exchangeRateDao.convert(modelInput.getValue(), this.currency, currency, currentDate);
			modelInput.setValue(amount);
			this.currency = currency;
			modelInput.setSuffix(currency.getShortName());
			modelInput.setInfo("Vante total du jour, en "+currency.getFullName());
			modelOutput.setInfo("Dépenses en  "+currency.getFullName());
		}

		/**
		 * mis en jour des models des cars
		 */
		public void reloadCardModel() {
			if(currency == null || currentDate == null)
				return;
			
			double in = commandPaymentDao.getSoldByDate(currentDate, currency, currencyOnly);
			modelInput.setValue(in);
			
		}
		
		/**
		 * mis en jours des models des cards
		 * @param currency
		 * @param date
		 */
		public void reloadCardModel(Currency currency, Date date) {
			this.currency = currency;
			this.currentDate = date;
			
			modelInput.setSuffix(currency.getShortName());
			modelOutput.setSuffix(currency.getShortName());
			reloadCardModel();
		}

		/**
		 * @param currency the currency to set
		 */
		public void setCurrency(Currency currency) {
			if(currency == this.currency)
				return;
			
			this.currency = currency;
			if(currency == null)
				return;
			
			modelInput.setSuffix(currency.getShortName());
			modelOutput.setSuffix(currency.getShortName());
			modelInput.setInfo("Vante total du jour, en "+currency.getFullName());
			modelOutput.setInfo("Dépenses en  "+currency.getFullName());
			reloadCardModel();
		}

		/**
		 * @param currentDate the currentDate to set
		 */
		public void setCurrentDate(Date currentDate) {
			this.currentDate = currentDate;
			reloadCardModel();
		}

		/**
		 * @param currencyOnly the currencyOnly to set
		 */
		public void setCurrencyOnly(boolean currencyOnly) {
			if(this.currencyOnly == currencyOnly)
				return;
			this.currencyOnly = currencyOnly;
			reloadCardModel();
		}

	}
	
	/**
	 * @author Esaie Muhasa
	 * interface to listening seller side bar event
	 */
	public static interface SellerSidebarListener {
		
		/**
		 * Called on selected date change in JCalendar panel
		 * @param date
		 */
		void onDateChange (Date date);
		
		/**
		 * called when user click on add new command button
		 */
		void onNewCommand ();
		
	}
	
}
