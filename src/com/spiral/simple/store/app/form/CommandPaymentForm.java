/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.DBEntity;
import com.spiral.simple.store.dao.CommandPaymentDao;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOException;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.ExchangeRateDao;
import com.spiral.simple.store.swing.SimpleComboBox;
import com.spiral.simple.store.swing.SimpleDateField;
import com.spiral.simple.store.swing.SimpleTextField;

/**
 * @author Esaie Muhasa
 *
 */
public class CommandPaymentForm extends AbstractForm<CommandPayment> {
	private static final long serialVersionUID = 457761875478302753L;
	
	private final DefaultComboBoxModel<Currency> currencyModel = new DefaultComboBoxModel<>();
	private final SimpleTextField fieldAmount = new SimpleTextField(" Montant");
	private final SimpleComboBox<Currency> fieldCurrency = new SimpleComboBox<>("Devise", currencyModel);
	private final SimpleDateField fieldDate = new SimpleDateField("Date du payement");
	
	private final ItemListener currencyItemListener = event -> onCurrencyChange(event);
	private final ExchangeRateDao exchangeRateDao = DAOFactory.getDao(ExchangeRateDao.class);
	private final CurrencyDao currencyDao = DAOFactory.getDao(CurrencyDao.class);
	
	private CommandPayment payment;
	private String cause [] ;

	public CommandPaymentForm() {
		super(DAOFactory.getDao(CommandPaymentDao.class));
		
		setTitle("Payment de la commande");
		final Box fields = Box.createVerticalBox();
		final JPanel row = new JPanel(new GridLayout(1, 2));
		row.add(fieldAmount);
		row.add(fieldCurrency);
		
		fields.add(fieldDate);
		fields.add(Box.createVerticalStrut(10));
		fields.add(row);
		fields.add(Box.createVerticalGlue());
		
		getBody().add(fields, BorderLayout.CENTER);
		fieldCurrency.getField().addItemListener(currencyItemListener);
		setVisibilityButtonCancellation(true);
	}
	
	/**
	 * change visibility of date field
	 * @param visible
	 */
	public void setFieldDateVisible (boolean visible) {
		fieldDate.setVisible(visible);
	}
	
	@Override
	public void doReload() {
		super.doReload();
		fieldCurrency.getField().removeItemListener(currencyItemListener);
		currencyModel.removeAllElements();
		try {
			Currency []  currencies = currencyDao.findAll();
			for (Currency currency : currencies)
				currencyModel.addElement(currency);
		} catch (DAOException e) {
			fieldCurrency.setEnabled(false);
		}
		fieldCurrency.getField().addItemListener(currencyItemListener);
	}
	
	/**
	 * @return the payment
	 */
	public CommandPayment getPayment() {
		return payment;
	}

	/**
	 * @param payment the payment to set
	 */
	public void setPayment(CommandPayment payment) {
		this.payment = payment;
		if(payment != null){
			BigDecimal dec = new BigDecimal(payment.getAmount()).setScale(2, RoundingMode.HALF_UP);
			fieldAmount.getField().setText(dec.doubleValue()+"");
			
			if(payment.getCurrency() == null)
				return;
			
			for (int i = 0; i < currencyModel.getSize(); i++)
				if (payment.getCurrency().equals(currencyModel.getElementAt(i))) {
					fieldCurrency.getField().setSelectedIndex(i);
					return;
				}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		fieldAmount.setEnabled(enabled);
		fieldCurrency.setEnabled(enabled);
	}

	/**
	 * listening currency selection change
	 * when user need execute trading, it must click on OK in dialog box
	 * @param event
	 */
	private void onCurrencyChange(ItemEvent event) {
		Currency currency = (Currency) event.getItem();
		if(currency != null && event.getStateChange() == ItemEvent.DESELECTED) {
			Currency currency2 = currencyModel.getElementAt(fieldCurrency.getField().getSelectedIndex());
			if (!exchangeRateDao.checkByCurrencies(currency.getId(), currency2.getId())) 
				return;

			try {
				double unitPrice = Double.parseDouble(fieldAmount.getField().getText());
				String message = "Voulez-vous faire la conversion du "+DBEntity.DECIMAL_FORMAT.format(unitPrice)+" "+currency.getShortName()+""
						+ "\n en "+currency2.getShortName()+"? \n",
						title = "Conversion de "+currency.getShortName()+" en "+currency2.getShortName();
				int status = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
				
				if(status == JOptionPane.YES_OPTION){
					unitPrice = exchangeRateDao.convert(unitPrice, currency, currency2);
					BigDecimal decimal = new BigDecimal(unitPrice).setScale(2, RoundingMode.HALF_UP);
					fieldAmount.getField().setText(decimal+"");
				}
			} catch (NumberFormatException e) {}
		}
	}

	@Override
	protected void doCleanFields() {
		fieldAmount.getField().setText("0");
	}

	@Override
	protected void doValidate() {
		if (payment == null)
			payment = new CommandPayment();
		cause = null;
		payment.setCurrency(currencyModel.getElementAt(fieldCurrency.getField().getSelectedIndex()));
		try {
			double amount = Double.parseDouble(fieldAmount.getField().getText());
			payment.setAmount(amount);
			if(amount <= 0.0)
				cause = new String[]{"Le montant doit être une valeur numérique valide suppérieur à zéro"};
		} catch (NumberFormatException e) {
			cause = new String[]{"Le montant doit être une valeur numérique valide"};
		}
	}

	@Override
	protected boolean isAccept() {
		if(!fieldAmount.getField().getText().trim().isEmpty() && currencyModel.getSize() != 0) {
			try{
				double amount = Double.parseDouble(fieldAmount.getField().getText());
				return amount > 0.0;
			} catch (NumberFormatException e) {}
		}
		return false;
	}

	@Override
	protected String[] getRejectCause() {
		return cause;
	}

}
