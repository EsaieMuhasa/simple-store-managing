/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.dao.CommandDao;
import com.spiral.simple.store.dao.CommandPaymentDao;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
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
import com.trimeur.swing.chart.DefaultPointCloud;
import com.trimeur.swing.chart.PointCloud.CloudType;

/**
 * @author Esaie Muhasa
 *
 */
public class PanelDashboard extends JPanel implements ChartRenderTranslationListener{
	private static final long serialVersionUID = 4305735104729283008L;
	
	private final DefaultAxis xAxis = new DateAxis();
	private final DefaultAxis yAxis = new DefaultAxis();
	private final DefaultPointCloud points = new DefaultPointCloud("Vente journalière", Color.RED.darker());
	private final DefaultCloudChartModel chartModel = new DefaultCloudChartModel(xAxis, yAxis);
	private final DefaultComboBoxModel<Currency> currencyModel = new  DefaultComboBoxModel<>();
	private final Interval intervale = new Interval(-28, 1);
	
	private final ChartPanel chartPanel = new ChartPanel(chartModel);
	private final JComboBox<Currency> boxCurrency = new JComboBox<>(currencyModel);
	private final JLabel labelTitle = UIComponentBuilder.createH2("");
	
	private final CommandPaymentDao commandPaymentDao = DAOFactory.getDao(CommandPaymentDao.class);
	private final CurrencyDao currencyDao = DAOFactory.getDao(CurrencyDao.class);
	private final CommandDao commandDao = DAOFactory.getDao(CommandDao.class);
	
	private final ItemListener currencyItemListener = event -> onCurrencySelectionChange(event);
	
	private final DAOListenerAdapter<CommandPayment> paymentDaoAdapter = new  DAOListenerAdapter<CommandPayment>() {

		@Override
		public void onCreate(CommandPayment... data) {
			for (CommandPayment payment : data) {
				if(inChart(payment.getDate())) {
					reload();
					break;
				}
					
			}
		}

		@Override
		public void onUpdate(CommandPayment newState, CommandPayment oldState) {
			if(!inChart(newState.getDate()))
				return;
			
			reload();
		}
		
	};
	
	private final DAOListenerAdapter<Command>  commandDAOAdapter = new  DAOListenerAdapter<Command>() {

		@Override
		public void onCreate(Command... data) {
			if(!inChart(data[0].getDate()))
				return;
			reload();
		}

		@Override
		public void onUpdate(Command newState, Command oldState) {
			if(!inChart(newState.getDate()))
				return;
			reload();
		}

		@Override
		public void onDelete(Command... data) {
			if(!inChart(data[0].getDate()))
				return;
			reload();
		}
		
	};
	
	public PanelDashboard() {
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		add(chartPanel, BorderLayout.CENTER);
		
		//event DAO
		commandPaymentDao.addBaseListener(paymentDaoAdapter);
		commandDao.addBaseListener(commandDAOAdapter);
		//==
		
		final JPanel 
			top = new JPanel(new BorderLayout()),
			right = new JPanel();
		
		right.add(boxCurrency);
		right.setOpaque(false);
		boxCurrency.setPreferredSize(new Dimension(200, 25));
		
		top.add(labelTitle, BorderLayout.CENTER);
		top.add(right, BorderLayout.EAST);
		top.setBackground(Color.LIGHT_GRAY);
		top.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		add(top, BorderLayout.NORTH);
		
		points.setDefaultPointSize(0.7d);
		points.setType(CloudType.STICK_CHART);
		
		yAxis.setMeasureUnit(" $");
		chartPanel.getChartRender().addTranslationListener(this);
		chartPanel.getChartRender().setVerticalTranslate(false);
		
		if(currencyDao.countAll() != 0) {
			Currency [] currencies = currencyDao.findAll();
			for (Currency currency : currencies) 
				currencyModel.addElement(currency);
		}
		
		reload();
		chartModel.addChart(points);
		boxCurrency.addItemListener(currencyItemListener);
	}
	
	/**
	 * verifie si la date est dans l'intervale actuelement visualisee
	 * @param date
	 * @return
	 */
	private boolean inChart (Date date) {
		Date min = UIComponentBuilder.fromDateAxisValue(intervale.getMin());
		Date max = UIComponentBuilder.fromDateAxisValue(intervale.getMax());
		return UIComponentBuilder.inIntervale(min, max, date);
	}
	
	@Override
	public String getName() {
		return "Tableau de board";
	}
	
	/**
	 * lors du change de la devise, on recharge le graphique
	 * @param event
	 */
	private void onCurrencySelectionChange (ItemEvent event) {
		if(event.getStateChange() != ItemEvent.SELECTED)
			return;
		
		reload();
	}
	
	/**
	 * lecture des donnees du model du graphique
	 */
	private void reload() {
		points.removePoints();
		if(currencyModel.getSize() == 0)
			return;
		
		Date min = UIComponentBuilder.fromDateAxisValue(intervale.getMin());
		Date max = UIComponentBuilder.fromDateAxisValue(intervale.getMax());
		
		Currency currency = currencyModel.getElementAt(boxCurrency.getSelectedIndex());
		for (double i = intervale.getMin(); i <= intervale.getMax(); i += 1d) {
			Date date = UIComponentBuilder.fromDateAxisValue(i);
			double amount = commandPaymentDao.getSumByDate(date, currency, false);
			
			DefaultMaterialPoint point = new DefaultMaterialPoint(Color.RED.darker());
			point.setX(i);
			point.setY(amount);
			point.setLabelX(CommandPayment.DATE_FORMAT.format(date));
			point.setLabelY(CommandPayment.DECIMAL_FORMAT.format(amount)+" "+currency.getShortName());
			points.addPoint(point);
		}
		
		String title = String.format("Statistiques du %s au %s (en %s)", 
				CommandPayment.DATE_FORMAT.format(min), CommandPayment.DATE_FORMAT.format(max),
				currency.getFullName());
		yAxis.setMeasureUnit(currency.getSymbol());
		labelTitle.setText(title);
	}

	@Override
	public void onRequireTranslation(CloudChartRender source, Axis axis, Interval interval) {
		this.intervale.setInterval(interval);
		reload();
	}

	@Override
	public void onRequireTranslation(CloudChartRender source, Interval xInterval, Interval yInterval) {
		intervale.setInterval(xInterval);
		reload();
	}

}
