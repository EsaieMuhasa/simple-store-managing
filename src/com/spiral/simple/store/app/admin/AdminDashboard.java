/**
 * 
 */
package com.spiral.simple.store.app.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import com.spiral.simple.store.beans.BudgetRubric;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.Spends;
import com.spiral.simple.store.dao.BudgetRubricDao;
import com.spiral.simple.store.dao.CommandDao;
import com.spiral.simple.store.dao.CommandPaymentDao;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.PaymentPartDao;
import com.spiral.simple.store.dao.SpendsDao;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;
import com.trimeur.swing.chart.Axis;
import com.trimeur.swing.chart.ChartPanel;
import com.trimeur.swing.chart.CloudChartRender;
import com.trimeur.swing.chart.CloudChartRender.ChartRenderTranslationListener;
import com.trimeur.swing.chart.CloudChartRender.Interval;
import com.trimeur.swing.chart.DateAxis;
import com.trimeur.swing.chart.DefaultAxis;
import com.trimeur.swing.chart.DefaultCloudChartModel;
import com.trimeur.swing.chart.DefaultMaterialPoint;
import com.trimeur.swing.chart.DefaultPieModel;
import com.trimeur.swing.chart.DefaultPiePart;
import com.trimeur.swing.chart.DefaultPointCloud;
import com.trimeur.swing.chart.PiePanel;
import com.trimeur.swing.chart.PointCloud.CloudType;
import com.trimeur.swing.chart.tools.Utility;

/**
 * @author Esaie Muhasa
 * tabeau de board de l'administration de l'application
 */
public class AdminDashboard extends JPanel {
	private static final long serialVersionUID = -6628170417791066111L;

	private final JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
	private final PieChartPanel piePanel = new PieChartPanel();
	private final HistogrammPanel histogrammPanel = new HistogrammPanel();
	
	private final CurrencyDao currencyDao = DAOFactory.getDao(CurrencyDao.class);
	private final CommandPaymentDao commandPaymentDao = DAOFactory.getDao(CommandPaymentDao.class);
	private final SpendsDao  spendsDao = DAOFactory.getDao(SpendsDao.class);
	private final CommandDao commandDao = DAOFactory.getDao(CommandDao.class);
	private final BudgetRubricDao budgetRubricDao = DAOFactory.getDao(BudgetRubricDao.class);
	private final PaymentPartDao paymentDao = DAOFactory.getDao(PaymentPartDao.class);
	
	private final DAOListenerAdapter<Command> commandAdapter = new DAOListenerAdapter<Command>() {
		@Override
		public void onCreate(Command... data) {
			reloadChartRecipe();
		}

		@Override
		public void onUpdate(Command newState, Command oldState) {
			reloadChartRecipe();
		}

		@Override
		public void onDelete(Command... data) {
			reloadChartRecipe();
		}
	};
	
	private final DAOListenerAdapter<CommandPayment> paymentAdapter = new DAOListenerAdapter<CommandPayment>() {
		@Override
		public void onCreate(CommandPayment... data) {
			reloadChartRecipe();
		}

		@Override
		public void onUpdate(CommandPayment newState, CommandPayment oldState) {
			reloadChartRecipe();
		}

		@Override
		public void onDelete(CommandPayment... data) {
			reloadChartRecipe();
		}
	};
	
	private final DAOListenerAdapter<Spends> spendsAdapter = new DAOListenerAdapter<Spends>() {
		@Override
		public void onCreate(Spends... data) {
			reloadChartSpends();
		}

		@Override
		public void onUpdate(Spends newState, Spends oldState) {
			reloadChartSpends();
		}

		@Override
		public void onDelete(Spends... data) {
			reloadChartSpends();
		}
	};
	
	public AdminDashboard() {
		super(new BorderLayout());
		init();
		reload();
	}
	
	/**
	 * initialisation des composants graphiques
	 */
	private void init() {
		add(tabbed, BorderLayout.CENTER);
		
		tabbed.addTab("Etats ", new ImageIcon(Config.getIcon("pie")), piePanel);
		tabbed.addTab("Entrées/Sorties ", new ImageIcon(Config.getIcon("chart")), histogrammPanel);
		
		Currency [] currencies = null;
		if (currencyDao.countAll() != 0)
			currencies = currencyDao.findAll();
		
		piePanel.firstData(currencies);
		histogrammPanel.firstData(currencies);
	}
	
	/**
	 * ecoute des interfaces du DAO
	 */
	public void listeningDao() {
		commandPaymentDao.addBaseListener(paymentAdapter);
		spendsDao.addBaseListener(spendsAdapter);
		commandDao.addBaseListener(commandAdapter);
	}
	
	/**
	 * desabonnement aux interfaces du DAO
	 */
	public void unlisteningDao() {
		commandPaymentDao.removeBaseListener(paymentAdapter);
		spendsDao.removeBaseListener(spendsAdapter);
		commandDao.removeBaseListener(commandAdapter);
	}
	
	/**
	 * mis en jours des models des graphiques 
	 */
	public synchronized void reload () {
		histogrammPanel.reload();
		piePanel.reload();
	}
	
	/**
	 * mis en jour du modele du graphique de repartition des recettes,
	 * conformement aux donnees dans la BDD
	 */
	public synchronized void reloadChartRecipe () {
		histogrammPanel.reload();
		if(piePanel.getChartType() == PieChartType.RECIPE_CHART || piePanel.getChartType() == PieChartType.AVAILABLE_CHART)
			piePanel.reload();
	}
	
	/**
	 * mis en jours du modele du grahique de realisation des depenses
	 */
	public synchronized void reloadChartSpends () {
		if(piePanel.getChartType() == PieChartType.SPENDS_CHART)
			piePanel.reload();
	}
	
	/**
	 * @author Esaie Muhasa
	 * Enumeration des tyles des graphiques de type chart
	 */
	private enum PieChartType {
		RECIPE_CHART,
		SPENDS_CHART,
		AVAILABLE_CHART
	}

	/**
	 * @author Esaie Muhasa
	 * panel de visualisation du pie chart du liquidite disponible en caisse
	 */
	private class PieChartPanel extends JPanel {
		private static final long serialVersionUID = -1108138932826650587L;
		
		private final DefaultComboBoxModel<Currency> currencyModel = new DefaultComboBoxModel<>();
		private final DefaultPieModel pieModel = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModel);
		private final JComboBox<Currency> currencyBox = new  JComboBox<>(currencyModel);
		private final JCheckBox checkCurrency = new JCheckBox("Convertiseur", true);
		
		private final JRadioButton [] radios = {
				new JRadioButton("Recettes", true),
				new JRadioButton("Dépenses", false),
				new JRadioButton("Etat de la caisse", false)};
		private int chartType = 1;//1: pour un grahique des recette, 2: pour graphique des depenses
		private final ActionListener checkCurrencyActionListener = event -> reload();
		private final ItemListener currencyBoxListener = event-> onCurrencyItemChange(event);
		private final ActionListener radioListener = event -> {
			JRadioButton radio = (JRadioButton) event.getSource();
			chartType = Integer.parseInt(radio.getName());
			reload();
		};
		
		private final List<BudgetRubric> rubrics = new ArrayList<>();
		
		public PieChartPanel() {
			super(new BorderLayout());
			
			final JPanel 
				top = new JPanel(new BorderLayout()),
				left = new  JPanel(),
				radios = new JPanel(new FlowLayout(FlowLayout.LEFT));
			
			pieModel.setTitle("Montant disponible ne caisse");
			pieModel.setRealMaxPriority(true);
			piePanel.setBorderColor(Color.LIGHT_GRAY.brighter().brighter().brighter().brighter());
			
			currencyBox.setPreferredSize(new Dimension(200, 26));
			currencyBox.addItemListener(currencyBoxListener);
			checkCurrency.addActionListener(checkCurrencyActionListener);
			
			top.add(radios, BorderLayout.CENTER);
			top.add(left, BorderLayout.EAST);
			left.add(checkCurrency);
			left.add(currencyBox);
			left.setOpaque(false);
			
			top.setBackground(Color.LIGHT_GRAY);
			add(top, BorderLayout.NORTH);
			add(piePanel, BorderLayout.CENTER);
			
			//boutons radio de choix du graphique a selectionner
			radios.setOpaque(false);
			ButtonGroup group = new ButtonGroup();
			for (int i = 0; i < this.radios.length; i++) {
				JRadioButton radio = this.radios[i];
				radios.add(radio);
				group.add(radio);
				radio.setName(String.valueOf(i+1));
				radio.addActionListener(radioListener);
			}
			//==
		}
		
		/**
		 * chargement des donnees elementaire
		 * @param currencies
		 */
		public void firstData (Currency [] currencies) {
			if(currencies != null)
				for (Currency currency : currencies)
					currencyModel.addElement(currency);
			
			if (budgetRubricDao.countAll() != 0) {
				BudgetRubric [] brs = budgetRubricDao.findAll();
				for (BudgetRubric br : brs)
					rubrics.add(br);
			}
		}
		
		/**
		 * renvoe le tyle de graphique actuelement selectionee
		 * @return
		 */
		public PieChartType getChartType () {
			if(chartType == 1)
				return PieChartType.RECIPE_CHART;
			if (chartType == 2)
				return PieChartType.SPENDS_CHART;
			if (chartType == 3)
				return PieChartType.AVAILABLE_CHART;
			throw new RuntimeException("Impossible de determiner le type de graphique");
		}
		
		/**
		 * lors du changement de l'element selectionnee
		 * dans le combo box des devises prise en charge
		 * @param event
		 */
		private void onCurrencyItemChange (ItemEvent event) {
			if (event.getStateChange() != ItemEvent.SELECTED)
				return;
			
			reload();
		}
		
		/**
		 * rechargement des donnees du model du graphique
		 */
		private void reload () {
			if(currencyModel.getSize() == 0 || rubrics.size() == 0)
				return;
			
			List<DefaultPiePart> parts = new ArrayList<>();
			pieModel.removeAll();
			boolean currencyOnly = !checkCurrency.isSelected();
			Currency currency = currencyModel.getElementAt(currencyBox.getSelectedIndex());
			pieModel.setSuffix(currency.getSymbol());
			switch (chartType) {
				case 1:{//graphique des recettes
					for (int i = 0; i < rubrics.size(); i++) {
						BudgetRubric rubric = rubrics.get(i);
						double amount = paymentDao.getSumByRubric(rubric, currency, currencyOnly);
						DefaultPiePart part = new  DefaultPiePart(Utility.getColorAt(i), amount, rubric.toString());
						
						parts.add(part);
					}
					pieModel.setTitle("Répartition des recettes, de maniere globale");
				} break;
				case 2:{//graphique des depense
					for (int i = 0; i < rubrics.size(); i++) {
						BudgetRubric rubric = rubrics.get(i);
						double amount = spendsDao.getSumByRubric(rubric.getId(), currency, currencyOnly);
						DefaultPiePart part = new  DefaultPiePart(Utility.getColorAt(i), amount, rubric.toString());
						
						parts.add(part);
					}
					pieModel.setTitle("Répartition des dépenses");
				} break;
				default:{//graphique des etats diponible en caisse
					for (int i = 0; i < rubrics.size(); i++) {
						BudgetRubric rubric = rubrics.get(i);
						double spend = spendsDao.getSumByRubric(rubric.getId(), currency, currencyOnly);
						double recipe = paymentDao.getSumByRubric(rubric, currency, currencyOnly);
						double amount = recipe - spend;
						DefaultPiePart part = new  DefaultPiePart(Utility.getColorAt(i), amount, rubric.toString());
						
						parts.add(part);
					}
					pieModel.setTitle("Etat du liquidité en caisse");
				}break;
			}
			
			pieModel.addParts(parts.toArray(new DefaultPiePart[parts.size()]));
		}
		
	}

	/**
	 * @author Esaie Muhasa
	 * panel de visualisation des entrees/sorties
	 */
	private class HistogrammPanel extends JPanel {
		private static final long serialVersionUID = -7134623874177241934L;
		
		private DefaultAxis yAxis = new DefaultAxis();
		private DateAxis xAxis = new DateAxis();
		private Interval interval = new Interval(-28, 1);
		
		private final DefaultPointCloud cloudInput = new DefaultPointCloud("Recette", Utility.getColorAt(5), Utility.getColorAlphaAt(5), Utility.getColorAlphaAt(8).darker());
		private final DefaultPointCloud cloudOutput = new DefaultPointCloud("Depenses", Color.RED.darker(), Color.RED.darker().darker(), Color.RED.darker());
		private final DefaultCloudChartModel chartModel = new DefaultCloudChartModel(xAxis, yAxis);
		private final DefaultComboBoxModel<Currency> currencyModel = new  DefaultComboBoxModel<>();
		
		private final ChartPanel chartPanel = new ChartPanel(chartModel);
		private final JComboBox<Currency> currencyBox = new JComboBox<>(currencyModel);
		private final JCheckBox checkCurrency = new JCheckBox("Convertisseur", true);
		private final JLabel chartTitle = new JLabel();
		
		private final ChartRenderTranslationListener renderTranslation = new ChartRenderTranslationListener() {
			
			@Override
			public void onRequireTranslation(CloudChartRender source, Interval xInterval, Interval yInterval) {
				reload();
			}
			
			@Override
			public void onRequireTranslation(CloudChartRender source, Axis axis, Interval interval) {
				reload();
			}
		};
		
		/**
		 * l'activation du chackbox, signifie que tout les autres devise doivent etre convertie
		 * sa desactivation signifie que nous devons selectionner uniquement, les operations qui font
		 * reference a la devise actuelement selectionnee
		 */
		private final ActionListener checkCurrencyActionListener = event -> reload();
		
		/**
		 * lors de la selection d'un nouvelle devise, nous pouvons 
		 * * soit selectionner les operations qui font refence a la dite devise
		 * * soit selectionner touts les opeations en converitisants ceux-qui font reference aux autres devise
		 */
		private final ItemListener currencyBoxItemListener = event -> {
			if(event.getStateChange() != ItemEvent.SELECTED)
				return;
			
			reload();
		};
		
		public HistogrammPanel() {
			super(new BorderLayout());
			
			final JPanel 
				top = new  JPanel(new BorderLayout()),
				left = new JPanel();
			
			chartModel.addChart(cloudInput);
			chartModel.addChart(cloudOutput);
			
			cloudInput.setDefaultPointSize(0.8d);
			cloudOutput.setDefaultPointSize(0.8d);
			
			cloudInput.setType(CloudType.STICK_CHART);
			cloudOutput.setType(CloudType.STICK_CHART);
			
			currencyBox.setPreferredSize(new Dimension(200, 26));
			
			left.add(checkCurrency);
			left.add(currencyBox);
			left.setOpaque(false);
			top.setBackground(Color.LIGHT_GRAY);
			top.add(left, BorderLayout.EAST);
			top.add(chartTitle, BorderLayout.CENTER);
			
			add(top, BorderLayout.NORTH);
			add(chartPanel, BorderLayout.CENTER);
			
			chartPanel.getChartRender().setVerticalTranslate(false);
			chartPanel.getChartRender().addTranslationListener(renderTranslation);
			
			checkCurrency.addActionListener(checkCurrencyActionListener);
			currencyBox.addItemListener(currencyBoxItemListener);
		}
		
		/**
		 * initialisation des donnees elementaire
		 * @param currencies
		 */
		public void firstData(Currency [] currencies) {
			if(currencies != null)
				for (Currency currency : currencies)
					currencyModel.addElement(currency);
		}
		
		/**
		 * rechargement des donnees du model du graphique
		 */
		private void reload () {
			cloudInput.removePoints();
			cloudOutput.removePoints();
			if(currencyModel.getSize() == 0)
				return;
			
			Date min = UIComponentBuilder.fromDateAxisValue(interval.getMin());
			Date max = UIComponentBuilder.fromDateAxisValue(interval.getMax());
			
			Currency currency = currencyModel.getElementAt(currencyBox.getSelectedIndex());
			for (double i = interval.getMin(); i <= interval.getMax(); i += 1d) {
				Date date = UIComponentBuilder.fromDateAxisValue(i);
				double amount = commandPaymentDao.getSumByDate(date, currency, !checkCurrency.isSelected());
				double spends = -spendsDao.getSumByDate(date, currency, !checkCurrency.isSelected());
				
				DefaultMaterialPoint in = new DefaultMaterialPoint(Color.GREEN.darker());
				DefaultMaterialPoint out = new  DefaultMaterialPoint(Color.RED.darker());
				
				out.setX(i);
				out.setY(spends);
				out.setLabelX(CommandPayment.DATE_FORMAT.format(date));
				out.setLabelY(CommandPayment.DECIMAL_FORMAT.format(spends)+" "+currency.getShortName());
				
				in.setX(i);
				in.setY(amount);
				in.setLabelX(CommandPayment.DATE_FORMAT.format(date));
				in.setLabelY(CommandPayment.DECIMAL_FORMAT.format(amount)+" "+currency.getShortName());
				cloudInput.addPoint(in);
				cloudOutput.addPoint(out);
			}
			
			String title = String.format("Statistiques du %s au %s (en %s)", 
					CommandPayment.DATE_FORMAT.format(min), CommandPayment.DATE_FORMAT.format(max),
					currency.getFullName());
			yAxis.setMeasureUnit(currency.getSymbol());
			chartTitle.setText(title);
		}
		
	}

}
