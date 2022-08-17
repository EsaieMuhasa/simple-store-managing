/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;

import javax.swing.Box;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.swing.SimpleTextField;

/**
 * @author Esaie MUHASA
 *
 */
public class CurrencyForm extends AbstractForm<Currency> {
	private static final long serialVersionUID = -3164767131421197597L;
	
	public static final int CREATION_REQUEST_ID = 0xFF0002;
	
	private final SimpleTextField fieldSymbol = new SimpleTextField("Symbole");
	private final SimpleTextField fieldShortName = new SimpleTextField("Abreviation");
	private final SimpleTextField fieldFullName = new SimpleTextField("Appelation complete");
	
	private boolean accept;
	private String [] rejectCause;
	private final CurrencyDao currencyDao;

	public CurrencyForm() {
		super(DAOFactory.getDao(CurrencyDao.class));
		currencyDao = DAOFactory.getDao(CurrencyDao.class);
		build();
	}
	
	/**
	 * building form, field components
	 */
	private void build() {
		Box rows = Box.createVerticalBox();
		Box cols = Box.createHorizontalBox();
		
		cols.add(fieldSymbol);
		cols.add(Box.createHorizontalStrut(10));
		cols.add(fieldShortName);
		
		rows.add(cols);
		rows.add(fieldFullName);
		
		getBody().add(rows, BorderLayout.CENTER);
	}

	@Override
	protected void doCleanFields() {
		fieldFullName.getField().setText("");
		fieldShortName.getField().setText("");
		fieldSymbol.getField().setText("");
	}

	@Override
	protected void doValidate() {
		String cause = "";
		Currency c = new Currency();
		
		c.setShortName(fieldShortName.getField().getText().trim());
		c.setFullName(fieldFullName.getField().getText().trim());
		c.setSymbol(fieldSymbol.getField().getText().trim());
		
		if(cause != "")
			rejectCause = cause.split(";");
		else 
			rejectCause = null;
		
		accept = rejectCause == null;
		
		if (accept)
			currencyDao.create(CREATION_REQUEST_ID, c);
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
