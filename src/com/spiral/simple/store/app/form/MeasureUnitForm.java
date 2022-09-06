/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;

import javax.swing.Box;

import com.spiral.simple.store.beans.MeasureUnit;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.MeasureUnitDao;
import com.spiral.simple.store.swing.SimpleTextField;

/**
 * @author Esaie MUHASA
 *
 */
public class MeasureUnitForm extends AbstractForm<MeasureUnit> {
	private static final long serialVersionUID = 3035110348378957150L;

	private final MeasureUnitDao measureUnitDao;
	
	private final SimpleTextField fieldShortName = new SimpleTextField("Abreviation");
	private final SimpleTextField fieldFullName = new SimpleTextField("Appelation complete");
	
	private boolean accept;
	private String [] rejectCause;

	public MeasureUnitForm() {
		super(DAOFactory.getDao(MeasureUnitDao.class));
		measureUnitDao = DAOFactory.getDao(MeasureUnitDao.class);
		
		build();
	}
	
	
	/**
	 * building form, field components
	 */
	private void build() {
		Box rows = Box.createVerticalBox();

		rows.add(Box.createHorizontalStrut(10));
		rows.add(fieldShortName);

		rows.add(fieldFullName);
		
		getBody().add(rows, BorderLayout.CENTER);
	}

	@Override
	protected void doCleanFields() {
		fieldFullName.getField().setText("");
		fieldShortName.getField().setText("");
	}


	@Override
	protected void doValidate() {
		String cause = "";
		MeasureUnit m = new MeasureUnit();
		
		m.setShortName(fieldShortName.getField().getText().trim());
		m.setFullName(fieldFullName.getField().getText().trim());
		
		if(cause != "")
			rejectCause = cause.split(";");
		else 
			rejectCause = null;
		
		accept = rejectCause == null;
		
		if (accept)
			measureUnitDao.create(DEFAULT_ON_PERSIST_REQUEST_ID, m);
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
