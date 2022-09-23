/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import com.spiral.simple.store.beans.BudgetRubric;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.Spends;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.PaymentPartDao;
import com.spiral.simple.store.dao.SpendsDao;
import com.spiral.simple.store.swing.SimpleComboBox;
import com.spiral.simple.store.swing.SimpleDateField;
import com.spiral.simple.store.swing.SimpleTextField;

/**
 * @author Esaie Muhasa
 *
 */
public class SpendsForm extends AbstractForm<Spends> {
	private static final long serialVersionUID = 5972135310016077655L;
	
	private final SpendsDao spendsDao = DAOFactory.getDao(SpendsDao.class);
	private final PaymentPartDao paymentPartDao = DAOFactory.getDao(PaymentPartDao.class);
	
	private final DefaultComboBoxModel<Currency> currencyModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<BudgetRubric> rubricModel = new DefaultComboBoxModel<>();
	
	private final SimpleTextField fieldAmount = new SimpleTextField("Montant");
	private final SimpleComboBox<Currency> fieldCurrency = new SimpleComboBox<>("Devise", currencyModel);
	private final SimpleComboBox<BudgetRubric> fieldRubric = new SimpleComboBox<>("Rubric budgetaire", rubricModel);
	private final SimpleDateField fieldDate = new SimpleDateField("Date du jour");
	
	private String [] cause=null;
	private Spends spends;
	
	public SpendsForm() {
		super(DAOFactory.getDao(SpendsDao.class));
		init();
	}
	
	
	/**
	 * 
	 */
	private void init() {
		JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
		
		panel.add(fieldDate);
		panel.add(fieldRubric);
		panel.add(fieldAmount);
		panel.add(fieldCurrency);
		
		getBody().add(panel, BorderLayout.CENTER);
	}
	
	@Override
	public void persist() {
		if(spends == null)
			return;
		
		if(spends.getId() == null)
			spendsDao.create(DEFAULT_ON_PERSIST_REQUEST_ID, spends);
		else
			spendsDao.update(DEFAULT_ON_PERSIST_REQUEST_ID, spends);
	}

	/**
	 * @return the spends
	 */
	public Spends getSpends() {
		return spends;
	}

	/**
	 * @param spends the spends to set
	 */
	public void setSpends(Spends spends) {
		this.spends = spends;
	}

	@Override
	protected void doCleanFields() {
		fieldAmount.reset();
		fieldDate.reset();
	}

	@Override
	protected void doValidate() {
		if(spends == null)
			spends = new Spends();
		
		String error = "";
		cause = null;
		
		BudgetRubric rubric = rubricModel.getElementAt(fieldRubric.getField().getSelectedIndex());
		Currency currency = currencyModel.getElementAt(fieldCurrency.getField().getSelectedIndex());
		
		spends.setCurrency(currency);
		spends.setRubric(rubric);
		
		//validation du montant
		try {
			spends.setAmount(Double.parseDouble(fieldAmount.getField().getText().trim()));
			if(spends.getAmount() <= 0)
				error += "Entrez une valeur supperieur a zero;";
			else {
				double availableAmount = paymentPartDao.getSumByRubric(rubric, currency, true) - spendsDao.getSumByRubric(rubric.getId(), currency, true);
				if(spends.getAmount() > availableAmount)
					error += "Le solde du compte est insulfisant;";
			}
		} catch (NumberFormatException e) {
			error += "Entez le montant au format valide;";
		}
		
		if(!error.trim().isEmpty())
			cause = error.split(";");
		
	}

	@Override
	protected boolean isAccept() {
		return cause == null || cause.length == 0;
	}

	@Override
	protected String[] getRejectCause() {
		return cause;
	}

}
