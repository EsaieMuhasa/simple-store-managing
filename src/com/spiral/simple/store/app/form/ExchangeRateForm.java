/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.ExchangeRate;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.ExchangeRateDao;
import com.spiral.simple.store.swing.CaptionnablePanel;
import com.spiral.simple.store.swing.SimpleComboBox;

/**
 * @author Esaie MUHASA
 *
 */
public class ExchangeRateForm extends AbstractForm<ExchangeRate>  {
	private static final long serialVersionUID = 844318113662435943L;
	
	private static final int DEFAULT_REQUEST_ID = 0xFF0001;
	
	private final DefaultComboBoxModel<Currency>
		currency1ComboModel = new DefaultComboBoxModel<>(),
		currency2ComboModel = new DefaultComboBoxModel<>();
	
	private final SimpleComboBox<Currency> currency1ComboBox = new SimpleComboBox<>("Devise de depart", currency1ComboModel);
	
	private final JTextField fieldCurrency1 = new JTextField();
	private final JTextField fieldCurrency2 = new JTextField();
	private final JComboBox<Currency> currency2ComboBox = new JComboBox<>(currency2ComboModel);
	
	private final ItemListener currency1ComboBoxListener = event -> onCurrency1ItemSelectionChange(event);
	private boolean accept = false;
	private String [] rejectCause;
	
	private final ExchangeRateDao exchangeRateDao;
	private final CurrencyDao currencyDao;
	
	public ExchangeRateForm() {
		super(DAOFactory.getDao(ExchangeRateDao.class));
		exchangeRateDao = DAOFactory.getDao(ExchangeRateDao.class);
		currencyDao = DAOFactory.getDao(CurrencyDao.class);
		build();
		currency1ComboBox.getField().addItemListener(currency1ComboBoxListener);
	}
	
	/**
	 * initialization of graphics components
	 */
	private void build() {
		Box rows = Box.createVerticalBox();
		JPanel cols = new JPanel(new GridLayout(1,3, 5, 5));
		
		cols.add(fieldCurrency1);
		cols.add(fieldCurrency2);
		cols.add(currency2ComboBox);
		
		rows.add(currency1ComboBox);
		rows.add(Box.createVerticalStrut(10));
		rows.add(new CaptionnablePanel("Taux d'echanage", cols));
		
		getBody().add(rows, BorderLayout.CENTER);
		fieldCurrency1.setEditable(false);
		fieldCurrency1.setHorizontalAlignment(JTextField.RIGHT);
	}
	
	@Override
	public void doReload() {
		super.doReload();
		if ( currencyDao.countAll() != 0 ) {
			Currency [] currencies = currencyDao.findAll();
			
			for (Currency c : currencies) {
				currency1ComboModel.addElement(c);
				currency2ComboModel.addElement(c);
			}
		}
	}

	@Override
	protected void doCleanFields() {
		Currency c1 = currency1ComboModel.getElementAt(currency1ComboBox.getField().getSelectedIndex());
		fieldCurrency1.setText("1 "+c1.getShortName());
		fieldCurrency2.setText("1");
	}
	
	/**
	 * listener of currency1 combo box
	 * @param event
	 */
	private void onCurrency1ItemSelectionChange(ItemEvent event) {
		if(event.getStateChange() != ItemEvent.SELECTED)
			return;
		Currency c1 = currency1ComboModel.getElementAt(currency1ComboBox.getField().getSelectedIndex());
		fieldCurrency1.setText("1 "+c1.getShortName());
	}

	@Override
	protected void doValidate() {
		String cause = "";
		
		ExchangeRate rate = new ExchangeRate();
		
		Currency c1 = currency1ComboModel.getElementAt(currency1ComboBox.getField().getSelectedIndex());
		Currency c2 = currency2ComboModel.getElementAt(currency2ComboBox.getSelectedIndex());
		
		rate.setCurrency1(c1);
		rate.setCurrency2(c2);
		rate.setRate(Double.parseDouble(fieldCurrency2.getText().trim()));
		rate.setStartTime(new Date());
		
		if (cause != "")
			rejectCause = cause.split(";");
		else
			rejectCause = null;
		accept = rejectCause == null;
		
		if(accept)
			exchangeRateDao.create(DEFAULT_REQUEST_ID, rate);
	}

	@Override
	protected boolean isAccept() {
		return accept;
	}

	@Override
	protected String[] getRejectCause() {
		return rejectCause;
	}

}
