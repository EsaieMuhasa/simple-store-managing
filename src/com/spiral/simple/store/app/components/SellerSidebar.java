/**
 * 
 */
package com.spiral.simple.store.app.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import com.spiral.simple.store.app.MainWindow;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandPayment;
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
import com.trimeur.swing.chart.DateAxis;
import com.trimeur.swing.chart.DefaultAxis;
import com.trimeur.swing.chart.DefaultCloudChartModel;
import com.trimeur.swing.chart.DefaultMaterialPoint;
import com.trimeur.swing.chart.DefaultPointCloud;
import com.trimeur.swing.chart.PointCloud.CloudType;

/**
 * @author Esaie Muhasa
 *
 */
public class SellerSidebar extends JPanel {
	private static final long serialVersionUID = -8618632784321077214L;
	
	public final SimpleDateFormat MONTH_NAME_FORMAT = new SimpleDateFormat("MMMM yyyy");
	
	private final JButton btnAddCommand =new JButton("Nouvelle commande", new ImageIcon(Config.getIcon("new")));
	private final JCalendar calendar = new JCalendar();

	
	private final DefaultComboBoxModel<Currency> cardsCurrencyBoxModel = new DefaultComboBoxModel<>();
	
	private final List<SellerSidebarListener> listeners = new ArrayList<>();
	private final PropertyChangeListener calendarListener = event -> onSelectedDateChange();
	private final PropertyChangeListener monthListener = event -> onSelectedMonthChange();
	
	private final StateContainer stateContainer = new  StateContainer();
	private final ChartContainer chartContainer = new ChartContainer();
	
	private final JLabel 
		titleCharts = UIComponentBuilder.createH2("Septembre"),
		labelYear = UIComponentBuilder.createH2("2022"),
		titleCards = UIComponentBuilder.createH2("28/08/2022");
	
	private final JCheckBox cardsCurrencyCheck = new JCheckBox("", true);
	private final JComboBox<Currency> cardsCurrencyBox = new JComboBox<>(cardsCurrencyBoxModel);
	
	{
		calendar.addPropertyChangeListener("calendar", calendarListener);
		calendar.getMonthChooser().addPropertyChangeListener("month", monthListener);
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
	
	private final ActionListener cardCurrencyActionListener = event -> onCardCheckCurrencyAction(event);
	
	private final ItemListener cardBoxCurrencyListener = event -> onCardBoxCurrencyChange(event);
	
	public SellerSidebar() {
		super(new BorderLayout(5, 5));
		
		final JPanel
			top = new JPanel(new BorderLayout()),
			center = new JPanel(new BorderLayout()),
			btn = new JPanel(new BorderLayout()),
			chart = new JPanel(new BorderLayout(5, 5)),
			cardsTitle = new JPanel(new GridLayout(1, 2, 5, 5)),
			chartTitle = new JPanel(new BorderLayout());
		
		btn.add(btnAddCommand, BorderLayout.CENTER);
		btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		
		top.add(calendar, BorderLayout.CENTER);
		top.add(btn, BorderLayout.NORTH);
		
		Box box = Box.createHorizontalBox();
		box.add(cardsCurrencyCheck);
		box.add(Box.createHorizontalStrut(5));
		box.add(cardsCurrencyBox);
		
		cardsCurrencyCheck.addActionListener(cardCurrencyActionListener);
		cardsCurrencyBox.addItemListener(cardBoxCurrencyListener);
		
		cardsTitle.add(titleCards);
		cardsTitle.add(box);
		cardsTitle.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		cardsTitle.setBackground(CustomTable.GRID_COLOR);
		
		center.add(cardsTitle, BorderLayout.NORTH);
		center.add(stateContainer, BorderLayout.CENTER);
		center.setBorder(BorderFactory.createLineBorder(cardsTitle.getBackground()));
		
		chartTitle.add(titleCharts, BorderLayout.CENTER);
		chartTitle.add(labelYear, BorderLayout.EAST);
		chartTitle.setBackground(CustomTable.GRID_COLOR);
		chartTitle.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		chart.add(chartTitle, BorderLayout.NORTH);
		chart.setBorder(BorderFactory.createLineBorder(cardsTitle.getBackground()));
		chart.add(chartContainer, BorderLayout.CENTER);
		
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
		cardsCurrencyBoxModel.removeAllElements();
		cardsCurrencyBox.setEnabled(cardsCurrencyCheck.isSelected());
		if(currencyDao.countAll() == 0)
			return;
		
		Currency []  cs = currencyDao.findAll();
		for (Currency currency : cs)
			cardsCurrencyBoxModel.addElement(currency);
		
		stateContainer.initTabs(cs);
		chartContainer.initCurrencies(cs);
		
		if(!cardsCurrencyCheck.isSelected())//doit-ton effectuer la conversion des devises directement???
			stateContainer.setSelectedCurrency(cardsCurrencyBoxModel.getElementAt(cardsCurrencyBox.getSelectedIndex()), calendar.getDate());
	}
	
	/**
	 * rechargeemnt de donnes depuis la bdd
	 * pour metre en jour l'UI.
	 * cette methode recharge le(s) modele(s) du/des graphique(s) et ceux des cards
	 */
	private synchronized void reload() {
		stateContainer.onDateChange(calendar.getDate());
		chartContainer.reload(calendar.getDate());
	}
	
	/**
	 * lors de changement d'etat de la boite de dialogie:
	 * si la boite de dialogue est maintenant selectionner, alors on fais directement la conversion de recette 
	 * pour la devise selectionner, sinon on le solde selon chaque devise dans un tabbed panel
	 * @param event
	 */
	private void onCardCheckCurrencyAction(ActionEvent event) {
		cardsCurrencyBox.setEnabled(cardsCurrencyCheck.isSelected());
		
		if(cardsCurrencyCheck.isSelected())
			stateContainer.setSelectedCurrency(cardsCurrencyBoxModel.getElementAt(cardsCurrencyBox.getSelectedIndex()), calendar.getDate());
		else
			stateContainer.onDateChange(calendar.getDate());
	}
	
	/**
	 * lors l'utilisateur decide change la devise
	 * @param event
	 */
	private void onCardBoxCurrencyChange(ItemEvent event) {
		if(event.getStateChange() != ItemEvent.SELECTED)
			return;
		
		stateContainer.setSelectedCurrency(cardsCurrencyBoxModel.getElementAt(cardsCurrencyBox.getSelectedIndex()), null);
	}
	
	/**
	 * when selected date change
	 */
	private void onSelectedDateChange () {
		String title = MONTH_NAME_FORMAT.format(calendar.getDate());
		title = title.substring(0, 1).toUpperCase() + title.substring(1, title.length());
		titleCharts.setText(title);
		for (SellerSidebarListener ls : listeners)
			ls.onDateChange(calendar.getDate());
	}
	
	/**
	 * lors du changemet d'un mois de l'annee
	 */
	private void onSelectedMonthChange () {
		chartContainer.reload(calendar.getDate());
		String title = MONTH_NAME_FORMAT.format(calendar.getDate());
		title = title.substring(0, 1).toUpperCase() + title.substring(1, title.length());
		titleCharts.setText(title);
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
			titleCards.setText("Le "+Command.DATE_FORMAT.format(date));
			removeAll();
			
			if(cardsCurrencyCheck.isSelected()) {
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
	 * containeur des graphiques
	 */
	private class ChartContainer extends JPanel {
		private static final long serialVersionUID = 1142782728896444128L;
		
		private final DateFormat 
			DATE_GET_MONTH_YEAR = new SimpleDateFormat("MM-yyyy"),
			DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy"); 
		
		private final DefaultAxis yAxis = new DefaultAxis("Montant", "$", "$");
		private final DateAxis xAxis = new DateAxis("Date", "date", "");
		private final DefaultCloudChartModel chartModel = new DefaultCloudChartModel(xAxis, yAxis);
		private final CloudChartRender chartRender = new CloudChartRender(chartModel);
		private final DefaultPointCloud cloud = new DefaultPointCloud(Color.RED.darker());
		
		private final JPanel panelCurrencies = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		private final ButtonGroup groupCurrencies =  new ButtonGroup(); 
		private final JCheckBox checkConverter = new JCheckBox("Convertir", true);
		private final List<JRadioButton> radios = new ArrayList<>();
		private final List<Currency> currencies = new ArrayList<>();
		private final ActionListener radiosAction = event -> onRadioAction(event);
		private final ActionListener checkConverterAction = event -> onCheckConverterAction (event);
		
		private boolean convert = false;
		
		public ChartContainer() {
			super(new  BorderLayout());
			init();
			
			cloud.setTitle("Vente journalière");
			cloud.setDefaultPointSize(0.9d);
			cloud.setType(CloudType.STICK_CHART);
			chartModel.addChart(cloud);
			checkConverter.addActionListener(checkConverterAction);
		}
		
		@Override
		public void doLayout() {
			super.doLayout();
			int h = getHeight();
			chartRender.setVisible(h > 80);
		}
		
		/**
		 * initialisation des composant graphique
		 */
		private void init() {
			final JPanel center = new JPanel(new BorderLayout()),
					bottom = new JPanel(new BorderLayout());
			
			center.add(chartRender, BorderLayout.CENTER);
			center.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			
			bottom.add(checkConverter, BorderLayout.WEST);
			bottom.add(panelCurrencies, BorderLayout.CENTER);
			
			bottom.setBackground(CustomTable.GRID_COLOR);
			panelCurrencies.setOpaque(false);
			
			add(bottom, BorderLayout.SOUTH);
			add(center, BorderLayout.CENTER);
		}
		
		/**
		 * prepation du panel de selection de la devise
		 * @param currencies
		 */
		private void initCurrencies (Currency [] currencies) {
			for (Currency currency : currencies) {
				JRadioButton radio = new JRadioButton(currency.getShortName(), true);
				radio.addActionListener(radiosAction);
				radios.add(radio);
				groupCurrencies.add(radio);
				panelCurrencies.add(radio);
				this.currencies.add(currency);
			}
		}
		
		/**
		 * lors du click sur un bouton ration
		 * @param event
		 */
		private void onRadioAction(ActionEvent event) {
			JRadioButton  radio = (JRadioButton) event.getSource();
			if(!radio.isSelected())
				return;
			
			Currency currency = currencies.get(radios.indexOf(radio));
			reload(calendar.getDate(), currency, convert);
		}
		
		/**
		 * lorsque l'utilisateur active/desactive la conversion automatique
		 * @param event
		 */
		private void onCheckConverterAction (ActionEvent event) {
			convert = !convert;
			reload (calendar.getDate());
		}
		
		/**
		 * de donnee du model du graphique.
		 * on charge directement des operations de mois de la date en parametre
		 * @param date
		 */
		private void reload (Date date) {
			JRadioButton radio = null;
			for (int i = 0; i < radios.size(); i++) {
				radio = radios.get(i);
				if (radio.isSelected())
					break;
			}
			
			Objects.requireNonNull(radio);
			Currency currency = currencies.get(radios.indexOf(radio));
			reload(calendar.getDate(), currency, convert);
		}

		/**
		 * mis en jours des models des graphiques
		 * @param date
		 */
		private void reload(Date date, Currency currency, final boolean currencyOnly) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			
			long maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			String minToString = "01-"+DATE_GET_MONTH_YEAR.format(date);
			//String maxToString = (maxDay < 10? "0":"")+maxDay+"-"+DATE_GET_MONTH_YEAR.format(date);
			
			try {
				Date min = DATE_FORMAT.parse(minToString);
				//Date max = DATE_FORMAT.parse(maxToString);
				cloud.setTitle("Vente journalière ( "+(currencyOnly? "Pour le " : "Conversion en ")+currency.getShortName()+")");
				yAxis.setMeasureUnit(currency.getSymbol());
				cloud.removePoints();
				
				for (long i = 0; i <= maxDay; i++) {
					long time = min.getTime() + (1000l * 60l * 60l * 24l * i);
					Date day = new Date(time);
					
					double amount = commandPaymentDao.getSoldByDate(day, currency, currencyOnly);
					DefaultMaterialPoint point = new DefaultMaterialPoint(Color.RED.darker());
					
					point.setX(UIComponentBuilder.toDateAxisValue(day));
					point.setLabelX(CommandPayment.DATE_FORMAT.format(day));
					point.setLabelY(CommandPayment.DECIMAL_FORMAT.format(amount)+" "+currency.getShortName());
					point.setY(amount);
					
					cloud.addPoint(point);
					cloud.setBackgroundColor(Color.RED.darker());
				}
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(MainWindow.getLastInstance(), "Une erreur est survenue lors de parsage de la date\n"+e.getMessage(),
						"Erreur parsage date", JOptionPane.ERROR_MESSAGE);
			}
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
