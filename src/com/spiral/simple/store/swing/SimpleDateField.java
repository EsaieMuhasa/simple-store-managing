package com.spiral.simple.store.swing;

import java.util.Date;

import com.toedter.calendar.JDateChooser;

public class SimpleDateField extends AbstractFormField {
	private static final long serialVersionUID = 3482698475958293605L;
	
	protected final JDateChooser field = new JDateChooser(new Date());

	/**
	 * default construct
	 */
	public SimpleDateField() {
		super();
		init();
	}
	
	/**
	 * construct to initialization text of field label
	 * @param label
	 */
	public SimpleDateField(String label) {
		super(label);
		init();
	}

	/**
	 * @return the field
	 */
	public JDateChooser getField() {
		return field;
	}

}
