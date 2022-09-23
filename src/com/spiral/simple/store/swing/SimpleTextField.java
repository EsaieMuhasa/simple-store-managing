package com.spiral.simple.store.swing;

import javax.swing.JTextField;

public class SimpleTextField extends AbstractFormField {
	private static final long serialVersionUID = 3482698475958293605L;

	private final JTextField field = new JTextField();

	/**
	 * default construct
	 */
	public SimpleTextField() {
		super("");
		init();
	}
	
	/**
	 * construct to initialization text of field label
	 * @param label
	 */
	public SimpleTextField(String label) {
		super(label);
		init();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		field.setEnabled(enabled);
	}

	/**
	 * @return the field
	 */
	public JTextField getField() {
		return field;
	}
	
	@Override
	public void reset() {
		field.setText("");
	}
}
